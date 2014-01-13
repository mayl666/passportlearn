package com.sogou.upd.passport.manager.api.connect.form.proxy;

import com.sogou.upd.passport.manager.api.connect.form.QQBaseConnectParams;

/**
 * QQ邮箱平台参数类
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-1-10
 * Time: 下午6:30
 * To change this template use File | Settings | File Templates.
 */
public class MailConnectParams extends QQBaseConnectParams {
    private String openapi_url = "/user/get_mail_count";
    private String platform = "mail";
    private String signature = "sig";

    public String getOpenapi_url() {
        return openapi_url;
    }

    public String getPlatform() {
        return platform;
    }

    public String getSignature() {
        return signature;
    }
}
