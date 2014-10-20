package com.sogou.upd.passport.oauth2.openresource.http;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.HttpConstant;
import com.sogou.upd.passport.common.utils.ConnectHttpClient;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.request.OAuthClientRequest;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponseFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class HttpClient4 extends ConnectHttpClient {

    private static final Logger log = LoggerFactory.getLogger(HttpClient4.class);

    /**
     * 执行http请求，get或post
     */
    public static <T extends OAuthClientResponse> T execute(OAuthClientRequest request, Map<String, String> headers,
                                                            String requestMethod, Class<T> responseClass) throws OAuthProblemException {

        InputStream in = null;
        //性能分析
        StopWatch stopWatch = new Slf4JStopWatch(prefLogger);
        URI location;
        String url = "";
        try {
            location = new URI(request.getLocationUri());
            url = request.getLocationUri();
        } catch (URISyntaxException e) {
            // URL表达式错误
            log.error("[HttpClient4] URL syntax error :", e);
            throw new OAuthProblemException(ErrorUtil.HTTP_CLIENT_REQEUST_FAIL);
        }

        try {
            HttpRequestBase req;
            String responseBody = "";
            if (!Strings.isNullOrEmpty(requestMethod) && HttpConstant.HttpMethod.POST.equals(requestMethod)) {
                req = new HttpPost(location);
                HttpEntity entity = new StringEntity(request.getBody());
                ((HttpPost) req).setEntity(entity);
            } else {
                req = new HttpGet(location);
                if (url.indexOf("?") > 0) {
                    url = url.substring(0, url.indexOf("?"));
                }
            }
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    req.setHeader(header.getKey(), header.getValue());
                }
            }

            HttpResponse response = httpClient.execute(req);
            Header contentTypeHeader = null;
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                in = entity.getContent();
                responseBody = EntityUtils.toString(entity, CommonConstant.DEFAULT_CHARSET);
                contentTypeHeader = entity.getContentType();
            }
            String contentType = null;
            if (contentTypeHeader != null) {
                contentType = contentTypeHeader.toString();
            }
            stopWatch(stopWatch, url, "success");
            return OAuthClientResponseFactory.createCustomResponse(responseBody, contentType, response.getStatusLine()
                    .getStatusCode(), responseClass);
        } catch (OAuthProblemException e) {
            stopWatch(stopWatch, url, "failed");
            throw e;
        } catch (Exception e) {
            log.warn("[HttpClient4] Execute Http Request Exception! RequestBody:" + request.getBody(), e);
            stopWatch(stopWatch, url, "failed");
            throw new OAuthProblemException(ErrorUtil.HTTP_CLIENT_REQEUST_FAIL);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("[HttpClient4] Close input stream IOException!", e);
                    stopWatch(stopWatch, url, "failed");
                    throw new OAuthProblemException(ErrorUtil.HTTP_CLIENT_REQEUST_FAIL);
                }
            }
        }
    }

}
