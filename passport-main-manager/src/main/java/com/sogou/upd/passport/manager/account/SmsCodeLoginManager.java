package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.form.SmsCodeLoginParams;

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
    public Result smsCodeLogin(SmsCodeLoginParams smsCodeLoginParams, String ip);
}
