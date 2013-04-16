package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.oauth2.authzserver.request.OAuthTokenRequest;

/**
 * 手机号登录，邮箱登录
 * User: mayan
 * Date: 13-4-15
 * Time: 下午4:33
 * To change this template use File | Settings | File Templates.
 */
public interface AccountLoginManager {
    //todo 返回Result
    public String authorize(OAuthTokenRequest oauthRequest) throws SystemException;

}
