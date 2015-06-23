package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.WapSmsCodeLoginParams;

/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 15-6-8
 * Time: 上午11:31
 */
public interface SmsCodeLoginManager {

    /**
     * 下发短信校验码
     *
     * @param mobile
     * @param client_id
     * @param token
     * @param captcha
     * @return
     */
    public Result sendSmsCode(final String mobile, final int client_id, final String token, final String captcha);

    /**
     * 短信登录
     *
     * @param smsCodeLoginParams
     * @param ip
     * @return
     */
    public Result smsCodeLogin(WapSmsCodeLoginParams smsCodeLoginParams, String ip);

    /**
     * 判断wap端应用是否需要输入验证码
     *
     * @param client_id
     * @param mobile
     * @param ip
     * @return
     */
    public boolean needCaptchaCheck(String client_id, String mobile, String ip);


    /**
     * 手机短信登录 ，登录成功
     *
     * @param username
     * @param ip
     * @param passportId
     * @param clientId
     */
    public void doAfterLoginSuccess(final String username, final String ip, final String passportId, final int clientId);

    /**
     * 手机短信登录，失败记录
     *
     * @param mobile
     * @param ip
     * @param errCode
     */
    public void doAfterLoginFailed(final String mobile, final String ip, String errCode);
}
