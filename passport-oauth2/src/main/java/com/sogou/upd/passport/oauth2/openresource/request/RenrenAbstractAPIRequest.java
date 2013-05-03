package com.sogou.upd.passport.oauth2.openresource.request;

import com.sogou.upd.passport.oauth2.openresource.parameters.RenrenOAuth;

/**
 * renren API请求的通用参数
 *
 * @author shipengzhi(shipengzhi@sogou-inc.com)
 */
public class RenrenAbstractAPIRequest extends OAuthClientRequest {

    public RenrenAbstractAPIRequest(String url) {
        super(url);
    }

    /**
     * renren OpenAPI调用的公共参数
     */
    public static class RenrenCommonParamsBuilder extends OAuthClientRequestBuilder {

        public RenrenCommonParamsBuilder(String url) {
            super(url);
            this.parameters.put(RenrenOAuth.VERSION, RenrenOAuth.V1);
            this.parameters.put(RenrenOAuth.FORMAT, RenrenOAuth.JSON);
        }

        /**
         * 签名认证
         */
        public RenrenCommonParamsBuilder setSign(String sign) {
            this.parameters.put(RenrenOAuth.SIGN, sign == null ? null : sign);
            return this;
        }

        /**
         * 方法名
         */
        public RenrenCommonParamsBuilder setMethod(String method) {
            this.parameters.put(RenrenOAuth.METHOD, method == null ? null : method);
            return this;
        }

        /**
         * 版本号，(不调用该方法，默认为1.0）
         */
        public RenrenCommonParamsBuilder setVersion(String version) {
            this.parameters.put(RenrenOAuth.VERSION, version == null ? null : version);
            return this;
        }

        /**
         * 返回格式，(不调用该方法，默认为json）
         */
        public RenrenCommonParamsBuilder setFormat(String format) {
            this.parameters.put(RenrenOAuth.FORMAT, format == null ? null : format);
            return this;
        }

    }

}
