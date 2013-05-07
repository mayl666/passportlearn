package com.sogou.upd.passport.oauth2.openresource.http;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.request.OAuthClientRequest;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponseFactory;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

public class HttpClient4 {

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

    private static org.apache.http.client.HttpClient client;

    private static final Logger log = LoggerFactory.getLogger(HttpClient4.class);

    static {
        client = WebClientDevWrapper.wrapClient(new DefaultHttpClient());
    }

    /**
     * 执行http请求，get或post
     */
    public static <T extends OAuthClientResponse> T execute(OAuthClientRequest request, Map<String, String> headers,
                                                            String requestMethod, Class<T> responseClass) throws OAuthProblemException {

        InputStream in = null;
        try {
            URI location = new URI(request.getLocationUri());
            HttpRequestBase req = null;
            String responseBody = "";

            if (!Strings.isNullOrEmpty(requestMethod) && OAuth.HttpMethod.POST.equals(requestMethod)) {
                req = new HttpPost(location);
                HttpEntity entity = new StringEntity(request.getBody());
                ((HttpPost) req).setEntity(entity);
            } else {
                req = new HttpGet(location);
            }
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    req.setHeader(header.getKey(), header.getValue());
                }
            }

            HttpResponse response = client.execute(req);
            Header contentTypeHeader = null;
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                in = entity.getContent();
                responseBody = EntityUtils.toString(entity, CommonConstant.DEFAULT_CONTENT_CHARSET);
                contentTypeHeader = entity.getContentType();
            }
            String contentType = null;
            if (contentTypeHeader != null) {
                contentType = contentTypeHeader.toString();
            }

            return OAuthClientResponseFactory.createCustomResponse(responseBody, contentType, response.getStatusLine()
                    .getStatusCode(), responseClass);

        } catch (URISyntaxException e) {
            // URL表达式错误
            log.error("[HttpClient4] URL syntax error :", e);
            throw new OAuthProblemException(ErrorUtil.HTTP_CLIENT_REQEUST_FAIL);
        } catch (OAuthProblemException e) {
            throw e;
        } catch (Exception e) {
            log.error("[HttpClient4] Execute Http Request Exception!", e);
            throw new OAuthProblemException(ErrorUtil.HTTP_CLIENT_REQEUST_FAIL);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("[HttpClient4] Close input stream IOException!", e);
                    throw new OAuthProblemException(ErrorUtil.HTTP_CLIENT_REQEUST_FAIL);
                }
            }
        }
    }

    /**
     * 采用get方法，根据图片url获取图片，
     */
    public static Pair<Integer, Pair<String, byte[]>> executeImgUrl(String url) {

        InputStream in = null;
        try {
            URI location = new URI(url);
            HttpRequestBase req = null;
            byte[] responseBody = null;

            req = new HttpGet(location);

            HttpResponse response = client.execute(req);

            Header contentTypeHeader = null;
            int code = 0;
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                in = entity.getContent();
                code = response.getStatusLine().getStatusCode();
                responseBody = EntityUtils.toByteArray(entity);
                contentTypeHeader = entity.getContentType();
            }
            String contentType = null;
            if (contentTypeHeader != null) {
                contentType = contentTypeHeader.toString();
            }

            Pair<String, byte[]> content = Pair.of(contentType, responseBody);
            return Pair.of(code, content);

        } catch (URISyntaxException e) {
            // URL表达式错误
            log.error("[HttpClient4] URL syntax error :", e);
            return Pair.of(0, null);
        } catch (Exception e) {
            log.error("[HttpClient4] Request image url fail:", e);
            return Pair.of(0, null);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("[HttpClient4] Close httpClient4 IOExceptionl fail:", e);
                }
            }
        }
    }

    public void shutdown() {
        if (client != null) {
            ClientConnectionManager connectionManager = client.getConnectionManager();
            if (connectionManager != null) {
                connectionManager.shutdown();
            }
        }
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
