package com.sogou.upd.passport.dao.account.mapper;

import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.dao.account.AccountMapper;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import com.sogou.upd.passport.service.account.generator.PwdGenerator;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-4-8
 * Time: 上午11:22
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = true)
@Transactional
@ContextConfiguration(locations = {"classpath:spring-config-test.xml"})
public class AccountMapperTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Inject
    private AccountMapper accountMapper;

    private static final String MOBILE = "13671940927";
    private static final String PASSWORD = "fanlixiao";
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
    @Transactional
    @Rollback(true)
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
        Assert.assertTrue(row == 1);
    }

    /**
     * 测试根据passportId获取Account
     */
    @Test
    public void testGetAccountByPassportId() {
        Account account = accountMapper.getAccountByPassportId(PASSPORT_ID);
        Assert.assertNotNull(account);
    }

    /**
     * 测试根据手机号码获取Account
     */
    @Test
    public void testGetAccountByMobile() {
        Account account = accountMapper.getAccountByMobile(MOBILE);
        Assert.assertNotNull(account);
    }

    /**
     * 测试根据userId获取Account
     */
    @Test
    public void testGetAccountByUserId() {
        Account account = accountMapper.getAccountByUserId(USER_ID);
        Assert.assertNotNull(account);
    }

    /**
     * 测试根据userId获取passportId
     */
    @Test
    public void testGetPassportIdByUserId() {
        String passportId = accountMapper.getPassportIdByUserId(USER_ID);
        Assert.assertNotNull(passportId);
    }

    /**
     * 测试根据passprotId获取userId
     */
    @Test
    public void testGetUserIdByPassportId() {
        long userId = accountMapper.getUserIdByPassportId(PASSPORT_ID);
        Assert.assertTrue(userId != 0);
    }

    /**
     * 测试修改用户
     */
    @Rollback(true)
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
        Assert.assertTrue(row == 1);
    }
}
