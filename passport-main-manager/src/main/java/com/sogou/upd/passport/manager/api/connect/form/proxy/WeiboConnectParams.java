package com.sogou.upd.passport.manager.api.connect.form.proxy;

import com.sogou.upd.passport.manager.api.connect.form.QQBaseConnectParams;

/**
 * QQ微博平台参数类
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-1-10
 * Time: 下午6:30
 * To change this template use File | Settings | File Templates.
 */
public class WeiboConnectParams extends QQBaseConnectParams {
    private String openapi_url = "/v3/update/get_num"; //第三方真实的接口url
    private String platform = "weibo";
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
