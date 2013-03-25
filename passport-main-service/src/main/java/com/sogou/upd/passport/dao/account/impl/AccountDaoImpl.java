package com.sogou.upd.passport.dao.account.impl;

import com.sogou.upd.passport.dao.account.AccountDao;
import com.sogou.upd.passport.dao.account.AccountMapper;
import com.sogou.upd.passport.model.account.Account;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-3-22
 * Time: 下午4:35
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class AccountDaoImpl implements AccountDao {

    @Inject
    public AccountMapper accountMapper;

    @Override
    public boolean checkIsRegisterAccount(Account account) {
        Account accountResult = accountMapper.checkIsRegisterAccount(account);
        //返回true时，表示没有查到已存在用户，可以注册；返回false，表示用户已存在，不可注册
        return accountResult == null ? true : false;
    }

    @Override
    public void userRegister(Account account) {
        accountMapper.userRegister(account);
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
