package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.oauth2.authzserver.request.OAuthTokenASRequest;

/**
 * 手机号登录，邮箱登录
 * User: mayan
 * Date: 13-4-15
 * Time: 下午4:33
 */
public interface OAuth2AuthorizeManager {

    /**
     * T3项目手机账号登录
     *
     * @param oauthRequest
     * @return
     */
    public Result authorize(OAuthTokenASRequest oauthRequest);

    /**
     * 浏览器PC/移动客户端登录
     *
     * @param oauthRequest
     * @return
     */
    public Result oauth2Authorize(OAuthTokenASRequest oauthRequest, AppConfig appConfig);

}
