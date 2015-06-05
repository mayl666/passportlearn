package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.result.Result;

/**
 * 短信校验码登录
 * User: chengang
 * Date: 15-6-4
 * Time: 下午3:02
 */
public interface SmsCodeLoginService {


    /**
     * 短信登录，生成短信校验码
     *
     * @param mobile
     * @param clientId
     * @return
     */
    public Result createSmsCode(final String mobile, final int clientId);

    /**
     * 短信登录，验证短信校验码
     *
     * @param mobile
     * @param smsCode
     * @param clientId
     * @return
     */
    public Result checkSmsCode(final String mobile, final String smsCode, final int clientId);

}
