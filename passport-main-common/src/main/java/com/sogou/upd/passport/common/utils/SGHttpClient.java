package com.sogou.upd.passport.common.utils;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.model.RequestModel;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
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
import org.springframework.beans.factory.annotation.Autowired;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Map;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-5-29
 * Time: 上午10:25
 */
public class SGHttpClient {


    private static HttpClient httpClient;

    /**
     * 最大连接数
     */
    public final static int MAX_TOTAL_CONNECTIONS = 500;
    /**
     * 获取连接的最大等待时间
     */
    public final static int WAIT_TIMEOUT = 10000;
    /**
     * 每个路由最大连接数
     */
    public final static int MAX_ROUTE_CONNECTIONS = 200;
    /**
     * 读取超时时间
     */
    public final static int READ_TIMEOUT = 10000;

    private final static int RESPONSE_SUCCESS_CODE = 200;

    static {
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, WAIT_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, READ_TIMEOUT);
        httpClient = WebClientDevWrapper.wrapClient(new DefaultHttpClient());
    }


    /**
     * 获取
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
            String result = EntityUtils.toString(httpEntity, charset);
            return result;
        } catch (IOException e) {
            throw new RuntimeException("http request error ", e);
        } catch (ParseException e) {
            throw new RuntimeException("http request error ", e);
        }
    }

    /**
     * 执行请求并返回请求结果
     *
     * @param requestModel
     * @return
     */
    public static HttpEntity execute(RequestModel requestModel) {
        if (requestModel == null) {
            throw new NullPointerException("requestModel 不能为空");
        }
        HttpRequestBase httpRequest = getHttpRequest(requestModel);
        try {
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            int responseCode = httpResponse.getStatusLine().getStatusCode();
            if (responseCode == RESPONSE_SUCCESS_CODE) {
                return httpResponse.getEntity();
            }
            throw new RuntimeException("http response error code: " + responseCode + " url:" + requestModel.getUrl());
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
                httpRequest = new HttpPost(requestModel.getUrl());
                break;
            case PUT:
                httpRequest = new HttpPut(requestModel.getUrl());
                break;
            case DELETE:
                httpRequest = new HttpDelete(requestModel.getUrl());
                break;
        }
        httpRequest.setParams(requestModel.getHttpParams());
        httpRequest.setHeaders(requestModel.getHeaders());
        return httpRequest;
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
