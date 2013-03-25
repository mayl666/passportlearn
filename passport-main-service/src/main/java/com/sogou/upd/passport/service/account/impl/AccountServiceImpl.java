package com.sogou.upd.passport.service.account.impl;

import com.sogou.upd.passport.dao.account.AccountDao;
import com.sogou.upd.passport.common.math.PassportIDGenerator;
import com.sogou.upd.passport.common.parameter.AccountStatusEnum;
import com.sogou.upd.passport.dao.account.AccountMapper;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;

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
    public boolean checkIsRegisterAccount(Account account) {
        accountDao.checkIsRegisterAccount(account);
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void userRegister(Account account) {
        accountDao.userRegister(account);
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Account checkIsRegisterAccount(Account account) {
        return accountMapper.checkIsRegisterAccount(account);
    }

    @Override
    public long initialAccount(String account, String pwd, String ip, int provider) {
        Account a = new Account();
        a.setPassportId(PassportIDGenerator.generator(account, provider));
        a.setPasswd(pwd);
        a.setRegTime(new Date());
        a.setRegIp(ip);
        a.setAccountType(provider);
        a.setStatus(AccountStatusEnum.REGULAR.getValue());
        a.setVersion(Account.NEW_ACCOUNT_VERSION);
        // TODO add dao implement，return userid
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long initialConnectAccount(String account, String ip, int provider) {
        return initialAccount(account, null, ip, provider);
    }
}
