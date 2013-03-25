package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.model.account.Account;

/**
 * User: mayan
 * Date: 13-3-22
 * Time: 下午3:38
 * To change this template use File | Settings | File Templates.
 */
public interface AccountService {

    public boolean findUserRegisterIsOrNot(Account account);

    public void userRegister(Account account);
}
