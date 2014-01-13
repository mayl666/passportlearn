package com.sogou.upd.passport.manager.api.connect.form.proxy;

/**
 * 所有第三方开放平台公共参数类
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-1-10
 * Time: 下午6:43
 * To change this template use File | Settings | File Templates.
 */
public class BaseConnectParams {

    private String access_token = "access_token"; //第三方开放平台公用认证参数

    public String getAccess_token() {
        return access_token;
    }
}
