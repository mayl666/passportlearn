package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.dao.BaseDAOTest;
import com.sogou.upd.passport.model.account.Account;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-4-17 Time: 下午4:32 To change this template
 * use File | Settings | File Templates.
 */
public class AccountDAOTest extends BaseDAOTest {

    @Autowired
    private AccountDAO accountDAO;

    @Before
    public void init() {
//        Account account = new Account();
//        account.setPassportId(PASSPORT_ID);
//        account.setMobile(MOBILE);
//        try {
//            account.setPasswd(PwdGenerator.generatorStoredPwd(PASSWORD,true));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        account.setAccountType(ACCOUNT_TYPE);
//        account.setRegIp(IP);
//        account.setRegTime(new Date());
//        account.setStatus(STATUS);
//        account.setVersion(VERSION);
//        int row = accountDAO.insertAccount(account.getPassportId(), account);
//        Assert.assertTrue(row != 0);
    }

    @After
    public void end() {
        int row = accountDAO.deleteAccountByPassportId(PASSPORT_ID);
        Assert.assertTrue(row != 0);
    }

    /**
     * 测试单条记录查询
     */
    @Test
    public void testGetAccountByPassportId() {
//        Account account = accountDAO.getAccountByPassportId(PASSPORT_ID);
//        Assert.assertTrue(account != null);
        String passportid = "abc'; drop table account";
        Account account = accountDAO.getAccountByPassportId(passportid);
        Assert.assertTrue(account != null);
    }

    /**
     * 测试修改用户
     */
    @Test
    public void testModifyPassword() {
        String NEW_PASSWORD="11111";
        String PASSPORT_ID = "dsdsdsds@sogou.com ';drop table account";
        int row = accountDAO.updatePassword(NEW_PASSWORD, PASSPORT_ID);
        Assert.assertTrue(row == 1);
    }

    @Test
    public void testModifyMobile() {
        int row = accountDAO.updateMobile(null, PASSPORT_ID);
        Assert.assertTrue(row == 1);
        Account account = accountDAO.getAccountByPassportId(PASSPORT_ID);
        Assert.assertTrue(account.getMobile() == null);
        row = accountDAO.updateMobile(MOBILE, PASSPORT_ID);
        Assert.assertTrue(row == 1);
        account = accountDAO.getAccountByPassportId(PASSPORT_ID);
        Assert.assertTrue(account.getMobile().equals(MOBILE));
    }
}
