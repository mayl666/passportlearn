package com.sogou.upd.passport.manager.account.impl;

import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.AccountManager;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-3-19
 * Time: 下午5:06
 * To change this template use File | Settings | File Templates.
 */
@Component("accountManager")
public class AccountManagerImpl implements AccountManager {

    @Autowired
    private AccountService accountService;

    @Override
    public Account queryNormalAccount(String passportId) throws Exception {
        return accountService.queryAccountByPassportId(passportId);
    }

    @Override
    public Account queryAccountByPassportId(String passportId) throws ServiceException {
        return accountService.queryAccountByPassportId(passportId);  //To change body of implemented methods use File | Settings | File Templates.
    }


}
