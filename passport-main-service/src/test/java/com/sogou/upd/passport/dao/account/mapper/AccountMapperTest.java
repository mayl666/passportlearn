package com.sogou.upd.passport.dao.account.mapper;

import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.dao.account.AccountMapper;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import com.sogou.upd.passport.service.account.generator.PwdGenerator;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.inject.Inject;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-4-8
 * Time: 上午11:22
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = {"classpath:spring-config-test.xml"})
public class AccountMapperTest extends AbstractJUnit4SpringContextTests {

    @Inject
    private AccountMapper accountMapper;

    private static final String MOBILE = "13937153065";
    private static final String PASSWORD = "fanya";
    private static final String PASSPORT_ID = PassportIDGenerator.generator(MOBILE, AccountTypeEnum.PHONE.getValue());
    private static final String IP = "127.0.0.1";
    private static final int PROVIDER = AccountTypeEnum.PHONE.getValue();
    private static final int STATUS = 1;
    private static final int VERSION = 1;
    private static final int USER_ID = 99;

    /**
     * 测试保存用户
     */
    @Test
    public void testSaveAccount() throws SystemException {
        Account account = new Account();
        account.setMobile(MOBILE);
        account.setPasswd(PwdGenerator.generatorPwdSign(PASSWORD));
        account.setAccountType(PROVIDER);
        account.setPassportId(PASSPORT_ID);
        account.setRegIp(IP);
        account.setRegTime(new Date());
        account.setStatus(STATUS);
        account.setVersion(VERSION);
        int row = accountMapper.saveAccount(account);
        System.out.println(row);
    }

    /**
     * 测试根据passportId获取Account
     */
    @Test
    public void testGetAccountByPassportId() {
        Account account = accountMapper.getAccountByPassportId(PASSPORT_ID);
        if (account != null) {
            System.out.println("获取成功...");
        } else {
            System.out.println("获取失败!!!");
        }
    }

    /**
     * 测试根据手机号码获取Account
     */
    @Test
    public void testGetAccountByMobile() {
        Account account = accountMapper.getAccountByMobile(MOBILE);
        if (account != null) {
            System.out.println("获取成功...");
        } else {
            System.out.println("获取失败!!!");
        }
    }

    /**
     * 测试根据userId获取Account
     */
    @Test
    public void testGetAccountByUserId() {
        Account account = accountMapper.getAccountByUserId(USER_ID);
        if (account != null) {
            System.out.println("获取成功...");
        } else {
            System.out.println("获取失败!!!");
        }
    }

    /**
     * 测试根据userId获取passportId
     */
    @Test
    public void testGetPassportIdByUserId() {
        String passportId = accountMapper.getPassportIdByUserId(USER_ID);
        if (passportId != null) {
            System.out.println("获取成功--->" + passportId);
        } else {
            System.out.println("获取失败!!!");
        }
    }

    /**
     * 测试根据passprotId获取userId
     */
    @Test
    public void testGetUserIdByPassportId() {
        long userId = accountMapper.getUserIdByPassportId(PASSPORT_ID);
        if (userId != 0) {
            System.out.println("获取成功--->" + userId);
        } else {
            System.out.println("获取失败!!!");
        }
    }

    /**
     * 测试修改用户
     */
    @Test
    public void testUpdateAccount() throws SystemException {
        Account account = new Account();
        account.setId(USER_ID);
        account.setMobile(MOBILE);
        account.setPasswd(PwdGenerator.generatorPwdSign(PASSWORD));
        account.setAccountType(PROVIDER);
        account.setPassportId(PASSPORT_ID);
        account.setRegIp(IP);
        account.setRegTime(new Date());
        account.setStatus(STATUS);
        account.setVersion(VERSION);
        int row = accountMapper.updateAccount(account);
        System.out.println(row);
    }
}
