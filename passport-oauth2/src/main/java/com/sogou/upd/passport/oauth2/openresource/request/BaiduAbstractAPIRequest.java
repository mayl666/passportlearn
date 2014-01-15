package com.sogou.upd.passport.oauth2.openresource.request;

/**
 * baidu微博 API请求的通用参数
 *
 * @author shipengzhi(shipengzhi@sogou-inc.com)
 */
public class BaiduAbstractAPIRequest extends OAuthClientRequest {

    public BaiduAbstractAPIRequest(String url) {
        super(url);
    }

    /**
     * renren OpenAPI调用的公共参数
     */
    public static class BaiduCommonParamsBuilder extends OAuthClientRequestBuilder {

        public BaiduCommonParamsBuilder(String url) {
            super(url);
        }

    }

}
