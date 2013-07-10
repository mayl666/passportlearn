package com.sogou.upd.passport.common.utils;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import com.sogou.upd.passport.common.parameter.HttpTransformat;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;

import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-5-29
 * Time: 上午10:25
 */
public class SGHttpClient {


    private static final HttpClient httpClient;



    /**
     * 最大连接数
     */
    private final static int MAX_TOTAL_CONNECTIONS = 500;
    /**
     * 获取连接的最大等待时间
     */
    private final static int WAIT_TIMEOUT = 10000;
    /**
     * 每个路由最大连接数
     */
    private final static int MAX_ROUTE_CONNECTIONS = 200;
    /**
     * 读取超时时间
     */
    private final static int READ_TIMEOUT = 10000;

    /**
     * http返回成功的code
     */
    private final static int RESPONSE_SUCCESS_CODE = 200;

    /**
     * 超过500ms的请求定义为慢请求
     */
    private final static int SLOW_TIME = 500;

    private static final Logger prefLogger = LoggerFactory.getLogger("httpClientTimingLogger");

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SGHttpClient.class);

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
                charset = CommonConstant.DEFAULT_CONTENT_CHARSET;
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

    /**
     * 执行请求并返回请求结果
     *
     * @param requestModel
     * @return
     */
    private static HttpEntity executePrivate(RequestModel requestModel) {
        if (requestModel == null) {
            throw new NullPointerException("requestModel 不能为空");
        }
        HttpRequestBase httpRequest = getHttpRequest(requestModel);
        try {
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            int responseCode = httpResponse.getStatusLine().getStatusCode();
            //302如何处理
            if (responseCode == RESPONSE_SUCCESS_CODE) {
                return httpResponse.getEntity();
            }
            String params = EntityUtils.toString(requestModel.getRequestEntity(), CommonConstant.DEFAULT_CONTENT_CHARSET);
            throw new RuntimeException("http response error code: " + responseCode + " url:" + requestModel.getUrl() + " params:" + params);
        } catch (IOException e) {
            throw new RuntimeException("http request error ", e);
        }
    }

    /**
     * 根据请求的参数构造HttpRequestBase
     *
     * @param requestModel
     * @return
     */
    private static HttpRequestBase getHttpRequest(RequestModel requestModel) {
        HttpRequestBase httpRequest = null;
        HttpMethodEnum method = requestModel.getHttpMethodEnum();
        switch (method) {
            case GET:
                httpRequest = new HttpGet(requestModel.getUrl());
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
    private static void stopWatch(StopWatch stopWatch, String tag, String message) {
//        if (logger.isInfoEnabled()) {
//            stopWatch.stop(tag, message);
//        } else {
//            if ("failed".equals(message + "") || stopWatch.getElapsedTime() >= SLOW_TIME) {
//                stopWatch.stop(tag, message);
//            }
//        }
        //无论什么情况都记录下所有的请求数据
        if(stopWatch.getElapsedTime() >= SLOW_TIME){
            tag+="(slow)";
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
                registry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
                registry.register(new Scheme("https", 443, ssf));
                ThreadSafeClientConnManager mgr = new ThreadSafeClientConnManager(registry);


                HttpParams params = base.getParams();
                mgr.setMaxTotal(MAX_TOTAL_CONNECTIONS);
                mgr.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);
                HttpConnectionParams.setConnectionTimeout(params, WAIT_TIMEOUT);
                HttpConnectionParams.setSoTimeout(params, READ_TIMEOUT);

                return new DefaultHttpClient(mgr, params);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }


}
