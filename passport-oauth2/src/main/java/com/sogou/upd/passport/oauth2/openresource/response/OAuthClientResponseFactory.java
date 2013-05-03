package com.sogou.upd.passport.oauth2.openresource.response;

import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.common.utils.OAuthUtils;

/**
 * OAuth Response 工厂类
 *
 * @author shipengzhi(shipengzhi@sogou-inc.com)
 */
public class OAuthClientResponseFactory {

    @SuppressWarnings("unchecked")
    public static <T extends OAuthClientResponse> T createCustomResponse(String body, String contentType,
                                                                         int responseCode, Class<T> clazz) throws OAuthProblemException {

        T resp = (T) OAuthUtils.instantiateClassWithParameters(clazz, null, null);
        resp.init(body, contentType, responseCode);
        return (T) resp;
    }

}
