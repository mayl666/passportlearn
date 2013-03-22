package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.model.account.Account;

/**
 * User: mayan
 * Date: 13-3-22
 * Time: 下午3:42
 * To change this template use File | Settings | File Templates.
 */
public interface AccountMapper {
    /**
     * 根据传入的参数，手机号码和密码,查询该手机是否已经注册
     * @return
     */
    public Account findUserRegisterIsOrNot(Account account);

    /**
     * 验证合法，用户注册
     * @param account
     */
    public void userRegister(Account account);


}
