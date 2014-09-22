package com.sogou.upd.passport.oauth2.openresource.request;

import com.sogou.upd.passport.oauth2.openresource.parameters.WeiXinOAuth;

/**
 * 微信API请求的通用参数
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-8-20
 * Time: 下午7:07
 * To change this template use File | Settings | File Templates.
 */
public class WeiXinAbstractAPIRequest extends OAuthClientRequest {

    protected WeiXinAbstractAPIRequest(String url) {
        super(url);
    }

    /**
     * 微信 OpenAPI调用的公共参数
     */
    public static class WeiXinCommonParamsBuilder extends OAuthClientRequestBuilder {

        public WeiXinCommonParamsBuilder(String url) {
            super(url);
        }

        /**
         * 微信分配给passport的appSecret
         */
        public WeiXinCommonParamsBuilder setAppSecret(String appSecret) {
            this.parameters.put(WeiXinOAuth.APP_SECRET, appSecret == null ? null : appSecret);
            return this;
        }

        /**
         * 微信用户id
         */
        public WeiXinCommonParamsBuilder setOpenid(String openid) {
            this.parameters.put(WeiXinOAuth.OPENID, openid == null ? null : openid);
            return this;
        }

        /**
         * 返回格式，(不调用该方法，默认为json）
         */
        public WeiXinCommonParamsBuilder setFormat(String format) {
            this.parameters.put(WeiXinOAuth.FORMAT, format == null ? null : format);
            return this;
        }
    }
}
