package com.sogou.upd.passport.manager.connect;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.connect.AfterAuthParams;
import com.sogou.upd.passport.manager.form.connect.ConnectLoginParams;
import com.sogou.upd.passport.manager.form.connect.ConnectLoginRedirectParams;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;

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
     * 构造第三方用户OAuth授权接口URL
     *
     * @param connectLoginParams OAuth2登录授权请求参数
     * @param provider           第三方平台
     * @param ip                 登录的ip
     * @return
     */
    public String buildConnectLoginURL(ConnectLoginParams connectLoginParams, int provider, String ip,
                                       String httpOrHttps, String usearAgent) throws OAuthProblemException;

    /**
     * 处理第三方登录授权回调
     *
     * @param req
     * @param providerStr
     * @return
     */
    public Result handleConnectCallback(ConnectLoginRedirectParams redirectParams, HttpServletRequest req, String providerStr, String httpOrHttps);

    /**
     * 处理第三方SSO登录授权回调
     *
     * @param params
     * @return
     */
    public Result handleSSOAfterauth(HttpServletRequest req, AfterAuthParams params, String providerStr, String ip);

}
