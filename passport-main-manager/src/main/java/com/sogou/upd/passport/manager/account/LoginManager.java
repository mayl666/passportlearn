package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.WebLoginParameters;
import com.sogou.upd.passport.oauth2.authzserver.request.OAuthTokenASRequest;

/**
 * 手机号登录，邮箱登录
 * User: mayan
 * Date: 13-4-15
 * Time: 下午4:33
 */
public interface LoginManager {

    public Result authorize(OAuthTokenASRequest oauthRequest);

    public Result accountLogin(WebLoginParameters parameters,String ip,String scheme);

    /**
     * 获取username登陆的时候是否需要登陆验证码
     * 目前策略如果连续3次登陆失败就需要输入验证码
     * 或者IP超过一个量就输入验证码
     * @param username
     * @param ip
     * @return
     */
    public boolean needCaptchaCheck(String client_id, String username, String ip);
}
