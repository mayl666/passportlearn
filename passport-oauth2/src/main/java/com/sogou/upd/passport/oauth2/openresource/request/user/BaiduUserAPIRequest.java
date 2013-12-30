package com.sogou.upd.passport.oauth2.openresource.request.user;

import com.sogou.upd.passport.oauth2.openresource.parameters.BaiduOAuth;
import com.sogou.upd.passport.oauth2.openresource.parameters.RenrenOAuth;
import com.sogou.upd.passport.oauth2.openresource.request.BaiduAbstractAPIRequest;
import com.sogou.upd.passport.oauth2.openresource.request.RenrenAbstractAPIRequest;

public class BaiduUserAPIRequest extends BaiduAbstractAPIRequest {

    public BaiduUserAPIRequest(String url) {
        super(url);
    }

    /**
     * Renren用户类API调用的请求参数
     */
    public static class BaiduUserAPIBuilder extends BaiduCommonParamsBuilder {

        public BaiduUserAPIBuilder(String url) {
            super(url);
        }

    }

}
