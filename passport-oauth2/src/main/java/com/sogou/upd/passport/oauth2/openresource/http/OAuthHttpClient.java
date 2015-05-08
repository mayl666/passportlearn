package com.sogou.upd.passport.oauth2.openresource.http;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.HttpConstant;
import com.sogou.upd.passport.common.HystrixConstant;
import com.sogou.upd.passport.common.hystrix.HystrixConfigFactory;
import com.sogou.upd.passport.common.utils.ErrorUtil;
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
                T hystrixResponse=(T) new HystrixQQAuthCommand(request, requestMethod, responseClass, headers).execute();
                if(null == hystrixResponse)  {
                    throw new OAuthProblemException(ErrorUtil.ERR_CODE_OAUTH_HYSTRIX_ERROR,"oauth hystrix excute failed");
                }
                return hystrixResponse;
            }
        }

        return HttpClient4.execute(request, headers, requestMethod, responseClass);
    }

}
