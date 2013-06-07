package com.sogou.upd.passport.manager.connect;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthSinaSSOBindTokenRequest;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-4-16
 * Time: 下午5:21
 * To change this template use File | Settings | File Templates.
 */
public interface OAuthAuthBindManager {

    /**
     * SSO-SDK第三方账户绑定接口
     *
     * @param oauthRequest Sina微博采用SSO-SDK，OAuth2登录授权成功后的响应结果对象
     * @param provider     第三方平台
     * @return Result格式的返回值
     */
    public Result connectSSOBind(OAuthSinaSSOBindTokenRequest oauthRequest, int provider);

}
