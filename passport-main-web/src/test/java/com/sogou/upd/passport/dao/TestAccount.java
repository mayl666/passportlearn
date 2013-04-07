package com.sogou.upd.passport.dao;

import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.dao.account.AccountMapper;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountAuth;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-4-7
 * Time: 下午3:29
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = {"classpath:spring-config.xml"})
public class TestAccount {
    @Inject
    private AccountService accountService;

    /**
     * 测试手机账号是否存在
     */
    @Test
    public void testCheckIsRegisterAccount() {
        String mobile = "13545210241";
        String passwd = "123456";
        String passportId = PassportIDGenerator.generator(mobile, AccountTypeEnum.PHONE.getValue());
        Account account = new Account();
        account.setMobile(mobile);
        account.setPasswd(passwd);
        account.setPassportId(passportId);
        boolean as = false;
        try {
            System.out.println("-----开始查询------");
//            as = accountService.checkIsRegisterAccount(account);
            System.out.println(as);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        if (as == true) {
            System.out.println("用户没有注册过，允许注册！");
        } else {
            System.out.println("用户已经注册过，不允许再次注册！");
        }
    }

    /**
     * 测试用户正式注册
     */
    @Test
    public void testUserRegister() {
        String account = "13545210241";
        String passwd = "123456";
        String ip = "192.168.0.1";
        Account a = new Account();
        a.setMobile(account);
        a.setPasswd(passwd);
        a.setRegIp(ip);
        a.setAccountType(AccountTypeEnum.PHONE.getValue());
//        long l = accountService.userRegister(a);
//        System.out.println(l);
    }

    /**
     * 测试更新用户状态表
     */
    @Test
    public void testUpdateAccountAuth() {
        String accessToken = "123456";
        String refreshToken = "jfdroe";
        long accessValidTime = 30;
        long refreshValidTime = 30;
        AccountAuth aa = new AccountAuth();
        aa.setId(1);
        aa.setUserId(3);
        aa.setRefreshToken(refreshToken);
        aa.setAccessToken(accessToken);
        aa.setAccessValidTime(accessValidTime);
        aa.setRefreshValidTime(refreshValidTime);
//        int row = accountService.updateAccountAuth(aa);
//        System.out.println(row);
    }
}
