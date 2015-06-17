package com.sogou.upd.passport.oauth2.openresource.http;

import com.google.common.base.Strings;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.sogou.upd.passport.common.HttpConstant;
import com.sogou.upd.passport.common.HystrixConstant;
import com.sogou.upd.passport.common.hystrix.HystrixConfigFactory;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.hystrix.HystrixQQAuthCommand;
import com.sogou.upd.passport.oauth2.openresource.request.OAuthClientRequest;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;

import java.util.HashMap;
import java.util.Map;

public class OAuthHttpClient {

    /**
     * 默认为GET
     */
    public static <T extends OAuthClientResponse> T execute(OAuthClientRequest request, Class<T> responseClass)
            throws OAuthProblemException {

        return execute(request, HttpConstant.HttpMethod.GET, responseClass);
    }

    public static <T extends OAuthClientResponse> T execute(OAuthClientRequest request, String requestMethod,
                                                            Class<T> responseClass) throws OAuthProblemException {

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpConstant.HeaderType.CONTENT_TYPE, HttpConstant.ContentType.URL_ENCODED);

        //对QQapi调用hystrix
        String hystrixQQurl = HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_QQ_URL);
        Boolean hystrixGlobalEnabled = Boolean.parseBoolean(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_GLOBAL_ENABLED));
        Boolean hystrixQQHystrixEnabled = Boolean.parseBoolean(HystrixConfigFactory.getProperty(HystrixConstant.PROPERTY_QQ_HYSTRIX_ENABLED));

        if (hystrixGlobalEnabled && hystrixQQHystrixEnabled) {
            String oAuthUrl = request.getLocationUri();
            if (!Strings.isNullOrEmpty(oAuthUrl) && oAuthUrl.contains(hystrixQQurl)) {
                try {
                    return revokeHystrixOAuthQQ(request, requestMethod, responseClass, headers);
                } catch (Exception e) {
                    throw new OAuthProblemException(e.getMessage());
                }
            }

        }

        return HttpClient4.execute(request, headers, requestMethod, responseClass);
    }

    //调用HystrixQQOAuthCommand
    public static <T extends OAuthClientResponse> T revokeHystrixOAuthQQ(OAuthClientRequest request, String requestMethod, Class<T> responseClass, Map<String, String> headers) throws Exception {

        HystrixQQAuthCommand hystrixQQOAuthCommand = null;
        String fallbackReason = "";
        String url = request.getLocationUri();
        try {
            hystrixQQOAuthCommand = new HystrixQQAuthCommand(request, requestMethod, responseClass, headers);
            T hystrixResponse = (T) hystrixQQOAuthCommand.execute();
            if (null == hystrixResponse) {
                if (hystrixQQOAuthCommand != null) {
                    fallbackReason = hystrixQQOAuthCommand.getFallbackReason();
                }
                throw new OAuthProblemException(fallbackReason + ",url=" + url);
            }

            return hystrixResponse;
        } catch (HystrixRuntimeException he) {
            if (hystrixQQOAuthCommand != null) {
                hystrixQQOAuthCommand.abortHttpRequest();
            }
            throw new OAuthProblemException("HystrixQQAuthCommand "+HystrixConstant.FALLBACK_REASON_CANNOT_FALLBACK + ",url=" + url);
        }

    }
}
