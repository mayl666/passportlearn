package com.sogou.upd.passport.service.account.impl;

import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.dao.account.AccountDao;
import com.sogou.upd.passport.common.math.PassportIDGenerator;
import com.sogou.upd.passport.common.parameter.AccountStatusEnum;
import com.sogou.upd.passport.dao.account.AccountMapper;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.print.DocFlavor;
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
        return accountDao.checkIsRegisterAccount(account);
    }

    @Override
    public void userRegister(Account account) {
        accountDao.userRegister(account);
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void userRegiterDetail(String mobile, String passwd, String regIp,String smsCode) {
        int accountType = AccountTypeEnum.PHONE.getValue();
        String passportId = PassportIDGenerator.generator(mobile, accountType);
        int status = AccountStatusEnum.REGULAR.getValue();
        int version = Account.NEW_ACCOUNT_VERSION;
        Account account = new Account(0, passportId, passwd, mobile, new Date(), regIp, status, version, accountType);
        accountDao.userRegister(account);
        //TODO add insert smsCode into app table
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
        a.setMobile(account);
        // TODO add dao implement，return userid
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long initialConnectAccount(String account, String ip, int provider) {
        return initialAccount(account, null, ip, provider);
    }
}
