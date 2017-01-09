package com.sogou.upd.passport.oauth2.openresource.request;

import com.sogou.upd.passport.oauth2.openresource.parameters.QQOAuth;

/**
 * Passport访问开放平台OAuth请求基类
 * @author shipengzhi
 */
public class QQUnionIdRequest extends QQAbstractAPIRequest {

    public QQUnionIdRequest(String url) {
        super(url);
    }
    
    /**
     * QQ用户类API调用的请求参数
     */
    public static class QQUnionIdBuilder extends QQCommonParamsBuilder {
        
        public QQUnionIdBuilder(String url) {
            super(url);
    
            this.parameters.put(QQOAuth.UNIONID, "1");
        }
        
        
    }

}
