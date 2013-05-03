package com.sogou.upd.passport.oauth2.openresource.request;

import com.sogou.upd.passport.oauth2.openresource.parameters.SinaOAuth;

/**
 * sina微博 API请求的通用参数
 *
 * @author shipengzhi(shipengzhi@sogou-inc.com)
 */
public class SinaAbstractAPIRequest extends OAuthClientRequest {

    public SinaAbstractAPIRequest(String url) {
        super(url);
    }

    /**
     * 新浪OpenAPI调用的公共参数
     */
    public static class SinaCommonParamsBuilder extends OAuthClientRequestBuilder {

        public SinaCommonParamsBuilder(String url) {
            super(url);
        }

        /**
         * 新浪分配给passport的appKey，即sina.thirdPartyKey
         */
        public SinaCommonParamsBuilder setSource(String source) {
            this.parameters.put(SinaOAuth.SOURCE, source == null ? null : source);
            return this;
        }

        /**
         * sina微博用户id
         */
        public SinaCommonParamsBuilder setUid(String uid) {
            this.parameters.put(SinaOAuth.UID, uid == null ? null : uid);
            return this;
        }
    }
}
