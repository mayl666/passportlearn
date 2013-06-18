package com.sogou.upd.passport.oauth2.openresource.http;

import com.sogou.upd.passport.common.HttpConstant;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
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

        return HttpClient4.execute(request, headers, requestMethod, responseClass);
    }

}
