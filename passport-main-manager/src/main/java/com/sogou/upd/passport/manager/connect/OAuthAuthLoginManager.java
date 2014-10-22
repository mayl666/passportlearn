package com.sogou.upd.passport.manager.connect;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.connect.AfterAuthParams;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-4-16
 * Time: 下午5:21
 * To change this template use File | Settings | File Templates.
 */
public interface OAuthAuthLoginManager {

    /**
     * 处理第三方登录授权回调
     * @param req
     * @param providerStr
     * @param ru
     * @param type
     * @return
     */
    public Result handleConnectCallback(HttpServletRequest req, String providerStr, String ru, String type,String httpOrHttps);

    /**
     * 处理第三方SSO登录授权回调
     * @param params
     * @return
     */
    public Result handleSSOAfterauth(HttpServletRequest req, AfterAuthParams params, String providerStr);

}
