package com.sogou.upd.passport.oauth2.openresource.request;

import com.sogou.upd.passport.oauth2.openresource.parameters.QQOAuth;

/**
 * QQAPI请求的通用参数
 *
 * @author shipengzhi(shipengzhi@sogou-inc.com)
 */
public class QQAbstractAPIRequest extends OAuthClientRequest {

    public QQAbstractAPIRequest(String url) {
        super(url);
    }

    /**
     * QQ OpenAPI调用的公共参数
     */
    public static class QQCommonParamsBuilder extends OAuthClientRequestBuilder {

        public QQCommonParamsBuilder(String url) {
            super(url);
        }

        /**
         * QQ分配给passport的appKey，即qq.thirdPartyKey
         */
        public QQCommonParamsBuilder setOauth_Consumer_Key(String oauth_consumer_key) {
            this.parameters.put(QQOAuth.OAUTH_CONSUMER_KEY, oauth_consumer_key == null ? null : oauth_consumer_key);
            return this;
        }

        /**
         * QQ用户id
         */
        public QQCommonParamsBuilder setOpenid(String openid) {
            this.parameters.put(QQOAuth.OPENID, openid == null ? null : openid);
            return this;
        }

        /**
         * 返回格式，(不调用该方法，默认为json）
         */
        public QQCommonParamsBuilder setFormat(String format) {
            this.parameters.put(QQOAuth.FORMAT, format == null ? null : format);
            return this;
        }
    }
}
