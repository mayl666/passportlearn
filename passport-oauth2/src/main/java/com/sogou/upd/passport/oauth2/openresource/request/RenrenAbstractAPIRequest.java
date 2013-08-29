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
        }

    }

}
