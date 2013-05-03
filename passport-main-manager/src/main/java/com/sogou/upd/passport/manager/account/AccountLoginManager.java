package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.oauth2.authzserver.request.OAuthTokenASRequest;

/**
 * 手机号登录，邮箱登录
 * User: mayan
 * Date: 13-4-15
 * Time: 下午4:33
 * To change this template use File | Settings | File Templates.
 */
public interface AccountLoginManager {

    public Result authorize(OAuthTokenASRequest oauthRequest);

}
