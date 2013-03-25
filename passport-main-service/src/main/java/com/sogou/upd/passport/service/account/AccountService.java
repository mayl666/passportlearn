package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.model.account.Account;

/**
 * User: mayan
 * Date: 13-3-22
 * Time: 下午3:38
 * To change this template use File | Settings | File Templates.
 */
public interface AccountService {
    /**
     * 检查此用户是否注册过，从用户账号表查
     * @param account
     * @return
     */
    public Account checkIsRegisterAccount(Account account);

    /**
     * 初始化非第三方用户账号
     * @param account
     * @param pwd
     * @param ip
     * @param provider
     * @return
     */
    public long initialAccount(String account, String pwd, String ip, int provider);

    /**
     * 初始化第三方用户账号
     * @param account
     * @param ip
     * @param provider
     * @return
     */
    public long initialConnectAccount(String account, String ip, int provider);

}
