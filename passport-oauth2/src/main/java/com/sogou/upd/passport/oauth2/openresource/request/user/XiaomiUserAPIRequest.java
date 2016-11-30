package com.sogou.upd.passport.oauth2.openresource.request.user;

import com.sogou.upd.passport.oauth2.openresource.request.XiaomiAbstractAPIRequest;

/**
 * Created by nahongxu on 2015/11/18.
 */
public class XiaomiUserAPIRequest extends XiaomiAbstractAPIRequest {
    public XiaomiUserAPIRequest(String url){
        super(url);
    }
    /**
     * xiaomi用户类API调用的请求参数
     */
    public static class XiaomiUserAPIBuilder extends XiaomiAbstractAPIRequest.XiaomiCommonParamsBuilder {

        public XiaomiUserAPIBuilder(String url) {
            super(url);
        }


    }
}
