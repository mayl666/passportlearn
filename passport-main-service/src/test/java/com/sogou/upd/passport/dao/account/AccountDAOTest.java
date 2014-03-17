package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.dao.BaseDAOTest;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.generator.PwdGenerator;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Created with IntelliJ IDEA. User: shipengzhi Date: 13-4-17 Time: 下午4:32 To change this template
 * use File | Settings | File Templates.
 */
public class AccountDAOTest extends BaseDAOTest {

    @Autowired
    private AccountDAO accountDAO;

    //    @Before
    @Test
    public void init() {
        Account account = new Account();
        account.setPassportId(PASSPORT_ID);
        account.setMobile(MOBILE);
        try {
            account.setPassword(PwdGenerator.generatorStoredPwd(PASSWORD, true));
        } catch (Exception e) {
            e.printStackTrace();
        }
        account.setRegIp(IP);
        account.setRegTime(new Date());
        account.setFlag(FLAG);
        account.setPasswordType(PWDTYPE);
        account.setAccountType(ACCOUNT_TYPE);
        account.setUniqname("fdfd");
        account.setAvatar("fdfetrtrt");
        int row = accountDAO.insertOrUpdateAccount(account.getPassportId(), account);
        System.out.println(row);
//        Assert.assertTrue(row == 1);
    }

//    /**
//     * 测试单条记录查询
//     */
//    @Test
//    public void testGetAccountByPassportId() {
//        Account account = accountDAO.getAccountByPassportId(PASSPORT_ID);
//        Assert.assertTrue(account != null);
//
//    }
//
//    /**
//     * 测试修改用户
//     */
//    @Test
//    public void testModifyPassword() {
//        int row = accountDAO.updatePassword(NEW_PASSWORD, PASSPORT_ID);
//        Assert.assertTrue(row == 1);
//    }
//
//
//    @Test
//    public void testModifyMobile() {
//        String newMobile = "13621009174";
//        accountDAO.updateMobile(newMobile, PASSPORT_ID);
//        Account account = accountDAO.getAccountByPassportId(PASSPORT_ID);
//        Assert.assertTrue(account.getMobile().equals(newMobile));
//    }
//
//    @After
//    public void end() {
//        int row = accountDAO.deleteAccountByPassportId(PASSPORT_ID);
//        Assert.assertTrue(row == 1);
//    }
}
