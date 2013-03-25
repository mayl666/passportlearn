package com.sogou.upd.passport.service.account.impl;

import com.sogou.upd.passport.dao.account.AccountDao;
import com.sogou.upd.passport.dao.account.AccountMapper;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * User: mayan
 * Date: 13-3-22
 * Time: 下午3:38
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AccountServiceImpl implements AccountService {
    @Inject
    private AccountDao accountDao;

    @Override
    public boolean findUserRegisterIsOrNot(Account account) {
        accountDao.findUserRegisterIsOrNot(account);
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void userRegister(Account account) {
        accountDao.userRegister(account);
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
