package com.sogou.upd.passport.oauth2.openresource.request.user;

import com.sogou.upd.passport.oauth2.openresource.parameters.QQOAuth;
import com.sogou.upd.passport.oauth2.openresource.request.QQAbstractAPIRequest;

/**
 * 用户类的第三方API请求参数构造类
 * 包括接口：
 * 1.获取用户信息
 *
 * @author shipengzhi(shipengzhi@sogou-inc.com)
 */
public class QQUserAPIRequest extends QQAbstractAPIRequest {

    public QQUserAPIRequest(String url) {
        super(url);
    }

    /**
     * QQ用户类API调用的请求参数
     */
    public static class QQUserAPIBuilder extends QQAbstractAPIRequest.QQCommonParamsBuilder {

        public QQUserAPIBuilder(String url) {
            super(url);
        }

        /**
         * API返回的数据格式(可选)
         */
        public QQUserAPIBuilder setFormat(String format) {
            this.parameters.put(QQOAuth.FORMAT, format == null ? null : format);
            return this;
        }

    }

}
