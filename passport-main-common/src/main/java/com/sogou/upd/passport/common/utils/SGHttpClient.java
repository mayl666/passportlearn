package com.sogou.upd.passport.common.utils;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.HystrixConstant;
import com.sogou.upd.passport.common.hystrix.HystrixConfigFactory;
import com.sogou.upd.passport.common.hystrix.HystrixQQCommand;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import com.sogou.upd.passport.common.parameter.HttpTransformat;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-5-29
 * Time: 上午10:25
 */
public class SGHttpClient {

    protected static final HttpClient httpClient;
    private static final Logger hystrixLogger = LoggerFactory.getLogger("hystrixLogger");
    /**
     * 最大连接数
     */
    protected final static int MAX_TOTAL_CONNECTIONS = 1500;
    /**
     * 获取连接的最大等待时间
     */
    protected final static int WAIT_TIMEOUT = 3000;
    /**
     * 每个路由最大连接数
     */
    protected final static int MAX_ROUTE_CONNECTIONS = 500;
    /**
     * 读取超时时间
     */
    protected final static int READ_TIMEOUT = 3000;

    /**
     * http返回成功的code
     */
    protected final static int RESPONSE_SUCCESS_CODE = 200;

    /**
     * 超过500ms的请求定义为慢请求
     */
    protected final static int SLOW_TIME = 500;

    protected static final Logger prefLogger = LoggerFactory.getLogger("httpClientTimingLogger");

    static {
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, WAIT_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, READ_TIMEOUT);
        httpClient = WebClientDevWrapper.wrapClient(new DefaultHttpClient());
    }

    /**
     * 执行http请求，并将返回结果从HttpTransformat转换为java bean
     *
     * @param requestModel 请求参数
     * @param transformat  返回值的类型
     * @param type         要得到的对象的类
     * @param <T>          泛型最终得到的bean类型
     * @return
     */
    public static <T> T executeBean(RequestModel requestModel, HttpTransformat transformat, java.lang.Class<T> type) {
        String value = executeStr(requestModel).trim();
        T t = null;
        switch (transformat) {
            case json:
                t = JsonUtil.jsonToBean(value, type);
                break;
            case xml:
                t = XMLUtil.xmlToBean(value, type);
                break;
        }
        ArrayList list = new ArrayList();
        list.iterator();
        return t;
    }

    /**
     * 执行请求操作，返回服务器返回内容
     *
     * @param requestModel
     * @return
     */
    public static String executeStr(RequestModel requestModel) {
        HttpEntity httpEntity = execute(requestModel);
        try {
            String charset = EntityUtils.getContentCharSet(httpEntity);
            if (StringUtil.isBlank(charset)) {
                charset = CommonConstant.DEFAULT_CHARSET;
            }
            String value = EntityUtils.toString(httpEntity, charset);
            if (!StringUtil.isBlank(value)) {
                value = value.trim();
            }
            return value;
        } catch (IOException | ParseException e) {
            throw new RuntimeException("http request error ", e);
        }
    }

    /**
     * 对外提供的执行请求的方法，主要添加了性能log
     *
     * @param requestModel
     * @return
     */
    public static HttpEntity execute(RequestModel requestModel) {
        //性能分析
        StopWatch stopWatch = new Slf4JStopWatch(prefLogger);
        try {
            HttpEntity httpEntity = executePrivate(requestModel);
            stopWatch(stopWatch, requestModel.getUrl(), "success");
            return httpEntity;
        } catch (Exception e) {
            if (requestModel != null) {
                stopWatch(stopWatch, requestModel.getUrl(), "failed");
            } else {
                stopWatch(stopWatch, "requestModel is null", "failed");
            }
            throw new RuntimeException("http request error ", e);
        }
    }

    public static Header[] executeHeaders(RequestModel requestModel) {
        if (requestModel == null) {
            throw new NullPointerException("requestModel 不能为空");
        }
        HttpRequestBase httpRequest = getHttpRequest(requestModel);
        StopWatch stopWatch = new Slf4JStopWatch(prefLogger);
        try {
            HttpParams params = httpClient.getParams();
            params.setParameter(ClientPNames.HANDLE_REDIRECTS, false);
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            Header[] headers = httpResponse.getAllHeaders();
            stopWatch(stopWatch, requestModel.getUrl(), "success");
            return headers;
        } catch (IOException e) {
            stopWatch(stopWatch, requestModel.getUrl(), "failed");
            throw new RuntimeException("http request error ", e);
        }
    }

    /**
     * 执行请求并返回请求结果
     *
     * @param requestModel
     * @return
     */
    protected static HttpEntity executePrivate(RequestModel requestModel) {
        if (requestModel == null) {
            throw new NullPointerException("requestModel 不能为空");
        }
        HttpRequestBase httpRequest = getHttpRequest(requestModel);

        //对QQapi调用hystrix
//        hystrixLogger.warn("SGHttpClient executePrivate:invoke hystrix...");
        String hystrixQQurl = HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_QQ_URL);
        Boolean hystrixGlobalEnabled = Boolean.parseBoolean(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_GLOBAL_ENABLED));
        Boolean hystrixQQHystrixEnabled = Boolean.parseBoolean(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_QQ_HYSTRIX_ENABLED));
//        int qqDelay=Integer.parseInt(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_QQ_DELAY));
//        try {
//            Thread.sleep(qqDelay);
//        }   catch (Exception e){
//            e.printStackTrace();
//        }

        if (hystrixGlobalEnabled && hystrixQQHystrixEnabled) {
            String qqUrl = requestModel.getUrl();
//            hystrixLogger.warn("SGHttpClient hystrix url:" + qqUrl);
            if (!Strings.isNullOrEmpty(qqUrl) && qqUrl.contains(hystrixQQurl)) {
                return new HystrixQQCommand(requestModel, httpClient).execute();
            }
        }

        InputStream in = null;
        try {
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            in = httpResponse.getEntity().getContent();
            int responseCode = httpResponse.getStatusLine().getStatusCode();
            //302如何处理
            if (responseCode == RESPONSE_SUCCESS_CODE) {
                return httpResponse.getEntity();
            }
            String params = EntityUtils.toString(requestModel.getRequestEntity(), CommonConstant.DEFAULT_CHARSET);
            String result = EntityUtils.toString(httpResponse.getEntity(), CommonConstant.DEFAULT_CHARSET);
            throw new RuntimeException("http response error code: " + responseCode + " url:" + requestModel.getUrl() + " params:" + params + "  result:" + result);
        } catch (Exception e) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe) {
                }
            }
            throw new RuntimeException("http request error ", e);
        }
    }

    /**
     * 根据请求的参数构造HttpRequestBase
     *
     * @param requestModel
     * @return
     */
    protected static HttpRequestBase getHttpRequest(RequestModel requestModel) {

        HttpRequestBase httpRequest = null;
        HttpMethodEnum method = requestModel.getHttpMethodEnum();
        switch (method) {
            case GET:
                httpRequest = new HttpGet(requestModel.getUrlWithParam());
                break;
            case POST:
                HttpPost httpPost = new HttpPost(requestModel.getUrl());
                httpPost.setEntity(requestModel.getRequestEntity());
                httpRequest = httpPost;
                break;
            case PUT:
                HttpPut httpPut = new HttpPut(requestModel.getUrl());
                httpPut.setEntity(requestModel.getRequestEntity());
                httpRequest = httpPut;
                break;
            case DELETE:
                httpRequest = new HttpDelete(requestModel.getUrl());
                break;
        }
        httpRequest.setHeaders(requestModel.getHeaders());
        return httpRequest;
    }

    /**
     * 记录性能log的规则
     *
     * @param stopWatch
     * @param tag
     * @param message
     */
    protected static void stopWatch(StopWatch stopWatch, String tag, String message) {
        //无论什么情况都记录下所有的请求数据
        if (stopWatch.getElapsedTime() >= SLOW_TIME) {
            tag += "(slow)";
        }
        stopWatch.stop(tag, message);
    }

    /*
 * 避免HttpClient的”SSLPeerUnverifiedException: peer not authenticated”异常
 * 不用导入SSL证书
 */
    public static class WebClientDevWrapper {

        public static org.apache.http.client.HttpClient wrapClient(org.apache.http.client.HttpClient base) {
            try {
                SSLContext ctx = SSLContext.getInstance("TLS");
                X509TrustManager tm = new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    }

                    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    }
                };
                ctx.init(null, new TrustManager[]{tm}, null);
                SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                SchemeRegistry registry = new SchemeRegistry();
                registry.register(new Scheme(CommonConstant.HTTP, 80, PlainSocketFactory.getSocketFactory()));
                registry.register(new Scheme(CommonConstant.HTTPS, 443, ssf));
                ThreadSafeClientConnManager mgr = new ThreadSafeClientConnManager(registry);

                HttpParams params = base.getParams();
                mgr.setMaxTotal(MAX_TOTAL_CONNECTIONS);
                mgr.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);
                HttpClientParams.setCookiePolicy(params, CookiePolicy.IGNORE_COOKIES); //忽略header里的cookie，解决ResponseProcessCookies(134): Invalid cookie header
                HttpConnectionParams.setConnectionTimeout(params, WAIT_TIMEOUT);
                HttpConnectionParams.setSoTimeout(params, READ_TIMEOUT);
                //当应用程序希望降低网络延迟并提高性能时，它们可以关闭Nagle算法
                // 加了以后没看到明显优化
//                HttpConnectionParams.setTcpNoDelay(params, true);
                // "旧连接"检查,为了确保该“被重用”的连接确实有效，会在重用之前对其进行有效性检查。这个检查大概会花费15-30毫秒。关闭该检查举措，会稍微提升传输速度
                // 加了这个配置以后，响应明显变慢，TIME_WAIT增多，所以不要用
//                HttpConnectionParams.setStaleCheckingEnabled(params, false);

                // "持续握手",遭到服务器拒绝应答的情况下，如果发送整个请求体，则会大大降低效率。此时，可以先发送部分请求进行试探，如果服务器愿意接收，则继续发送请求体。
                // 仅HTTP 1.1支持，所以不要用
//                HttpProtocolParams.setUseExpectContinue(params, true);

                return new DefaultHttpClient(mgr, params);
            } catch (Exception ex) {
                return null;
            }
        }
    }


}
