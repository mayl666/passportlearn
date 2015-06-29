package com.sogou.upd.passport.oauth2.openresource.hystrix;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.utils.ConnectHttpClient;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.request.OAuthClientRequest;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponseFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: nahongxu
 * Date: 15-6-16
 * Time: 下午4:14
 * To change this template use File | Settings | File Templates.
 */
public class HystrixQQAuthMethod extends ConnectHttpClient {

    private static final Logger log = LoggerFactory.getLogger(HystrixQQAuthMethod.class);

    /**
     * 执行http请求，get或post
     */
    public static <T extends OAuthClientResponse> T execute(OAuthClientRequest request, Map<String, String> headers,
                                                            String requestMethod, Class<T> responseClass,
                                                            HttpRequestBase req) throws OAuthProblemException {

        InputStream in = null;
        String url = "";
        //性能分析
        StopWatch stopWatch = new Slf4JStopWatch(prefLogger);
        try {
            String responseBody = "";
            url = request.getLocationUri();
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
        }catch (SocketException ske) {
            log.warn("HystrixQQAuthMethod socked error");
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe) {
                }
            }
            return null;
        }
        catch (Exception e) {
            log.warn("[HystrixQQAuthMethod] Execute Http Request Exception! RequestBody:" + request.getBody(), e);
            stopWatch(stopWatch, url, "failed");
            throw new OAuthProblemException(ErrorUtil.HTTP_CLIENT_REQEUST_FAIL);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("[HystrixQQAuthMethod] Close input stream IOException!", e);
                    stopWatch(stopWatch, url, "failed");
                    throw new OAuthProblemException(ErrorUtil.HTTP_CLIENT_REQEUST_FAIL);
                }
            }
        }
    }
}
