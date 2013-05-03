package com.sogou.upd.passport.oauth2.openresource.request.user;

import com.sogou.upd.passport.oauth2.openresource.parameters.SinaOAuth;
import com.sogou.upd.passport.oauth2.openresource.request.SinaAbstractAPIRequest;

/**
 * 用户类的第三方API请求参数构造类
 * 包括接口：
 * 1.获取用户信息
 *
 * @author shipengzhi(shipengzhi@sogou-inc.com)
 */
public class SinaUserAPIRequest extends SinaAbstractAPIRequest {

    public SinaUserAPIRequest(String url) {
        super(url);
    }

    /**
     * 新浪用户类API调用的请求参数
     */
    public static class SinaUserAPIBuilder extends SinaAbstractAPIRequest.SinaCommonParamsBuilder {

        public SinaUserAPIBuilder(String url) {
            super(url);
        }

        /**
         * 需要查询的用户昵称(可选)
         */
        public SinaUserAPIBuilder setScreenName(String format) {
            this.parameters.put(SinaOAuth.SCREEN_NAME, format == null ? null : format);
            return this;
        }

    }

}
