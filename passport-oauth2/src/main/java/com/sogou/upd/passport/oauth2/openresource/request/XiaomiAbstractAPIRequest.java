package com.sogou.upd.passport.oauth2.openresource.request;

import com.sogou.upd.passport.oauth2.openresource.parameters.XiaomiOAuth;

/**
 * XIAOMIAPI请求的通用参数
 */
public class XiaomiAbstractAPIRequest extends OAuthClientRequest {

    public XiaomiAbstractAPIRequest(String url) {
        super(url);
    }

    /**
     * Xiaomi OpenAPI调用的公共参数
     */
    public static class XiaomiCommonParamsBuilder extends OAuthClientRequestBuilder {

        public XiaomiCommonParamsBuilder(String url) {
            super(url);
        }

        /**
         * XIAOMI分配给passport的appid
         */
        public XiaomiCommonParamsBuilder setClientId(Long clientId) {
            this.parameters.put(XiaomiOAuth.CLIENTID, clientId == null ? null : clientId);
            return this;
        }

        /**
         * 小米用户access_token
         */
        public XiaomiCommonParamsBuilder setToken(String token) {
            this.parameters.put(XiaomiOAuth.TOKEN, token == null ? null : token);
            return this;
        }
    }
}
