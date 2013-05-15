package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.WebLoginParameters;
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

    public Result accountLogin(WebLoginParameters parameters);

    /**
     * 获取passportId登陆的时候是否需要登陆验证码
     * 目前策略如果连续3次登陆失败就需要输入验证码
     * @param passportId
     * @return
     */
    public boolean loginNeedCaptcha(String passportId);
}
