package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.result.Result;

/**
 * 账户安全相关
 * User: mayan
 * Date: 13-4-15
 * Time: 下午4:30
 * To change this template use File | Settings | File Templates.
 */
public interface AccountSecureManager {
    /**
     * 发送短信验证码
     * @param mobile
     * @param clientId
     * @return
     */
    public Result sendMobileCode(String mobile,int clientId) ;
}
