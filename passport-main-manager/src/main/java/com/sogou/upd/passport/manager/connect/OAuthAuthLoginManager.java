package com.sogou.upd.passport.manager.connect;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.ConnectLoginParams;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.dataobject.OAuthTokenDO;
import com.sogou.upd.passport.oauth2.openresource.request.OAuthAuthzClientRequest;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthAuthzClientResponse;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthSinaSSOBindTokenRequest;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthSinaSSOTokenRequest;

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
     * SSO-SDK第三方账户登录接口
     *
     * @param oauthRequest Sina微博采用SSO-SDK，OAuth2登录授权成功后的响应结果对象
     * @param provider     第三方平台
     * @param ip           登录的ip
     * @return Result格式的返回值
     */
    public Result connectSSOLogin(OAuthSinaSSOTokenRequest oauthRequest, int provider, String ip);

    /**
     * 第三方账户登录接口
     *
     * @param connectLoginParams OAuth2登录授权请求参数
     * @param provider           第三方平台
     * @param ip                 登录的ip
     * @return Result格式的返回值
     */
    public OAuthAuthzClientRequest buildConnectLoginRequest(ConnectLoginParams connectLoginParams, ConnectConfig connectConfig,
                                                            String uuid, int provider, String ip) throws OAuthProblemException;

    public OAuthTokenDO buildConnectCallbackResponse(HttpServletRequest req, String connectType, int provider) throws OAuthProblemException;
}
