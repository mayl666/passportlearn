package com.sogou.upd.passport.oauth2.openresource.request.user;

import com.sogou.upd.passport.oauth2.openresource.parameters.WeiXinOAuth;
import com.sogou.upd.passport.oauth2.openresource.request.WeiXinAbstractAPIRequest;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-8-20
 * Time: 下午8:20
 * To change this template use File | Settings | File Templates.
 */
public class WeiXinUserAPIRequest extends WeiXinAbstractAPIRequest {

    public WeiXinUserAPIRequest(String url) {
        super(url);
    }

    /**
     * 微信用户类API调用的请求参数
     */
    public static class WeiXinUserAPIBuilder extends WeiXinCommonParamsBuilder {

        public WeiXinUserAPIBuilder(String url) {
            super(url);
        }

        /**
         * API返回的数据格式(可选)
         */
        public WeiXinUserAPIBuilder setFormat(String format) {
            this.parameters.put(WeiXinOAuth.FORMAT, format == null ? null : format);
            return this;
        }

    }
}
