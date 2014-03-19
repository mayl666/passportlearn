package com.sogou.upd.passport.dao.account;

import com.sogou.upd.passport.common.parameter.AccountStatusEnum;
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
        account.setPassportId(PASSPORTID_SOGOU);
        account.setMobile(MOBILE);
        try {
            account.setPassword(PwdGenerator.generatorStoredPwd(PASSWORD, true));
        } catch (Exception e) {
            e.printStackTrace();
        }
        account.setRegIp(IP);
        account.setRegTime(new Date());
        account.setFlag(String.valueOf(AccountStatusEnum.REGULAR.getValue()));
        account.setPasswordType(String.valueOf(CRYPT_PASSWORD_TYPE));
        account.setAccountType(MAIL_ACCOUNT_TYPE);
        account.setUniqname("fdfd");
        account.setAvatar("fdfetrtrt");
        int row = accountDAO.insertOrUpdateAccount(account.getPassportId(), account);
        Assert.assertTrue(row == 1);
    }

    /**
     * 测试单条记录查询
     */
    @Test
    public void testGetAccountByPassportId() {
        Account account = accountDAO.getAccountByPassportId(PASSPORTID_SOGOU);
        Assert.assertTrue(account != null);

    }

    /**
     * 测试修改用户
     */
    @Test
    public void testModifyPassword() {
        String newPassword = null;
        try {
            newPassword = PwdGenerator.generatorStoredPwd(NEW_PASSWORD, true);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        int row = accountDAO.updatePassword(newPassword, PASSPORTID_SOGOU);
        Assert.assertTrue(row == 1);
    }


    @Test
    public void testModifyMobile() {
        String newMobile = "13621009174";
        accountDAO.updateMobile(newMobile, PASSPORTID_SOGOU);
        Account account = accountDAO.getAccountByPassportId(PASSPORTID_SOGOU);
        Assert.assertTrue(account.getMobile().equals(newMobile));
    }

    @Test
    public void end() {
        int row = accountDAO.deleteAccountByPassportId(PASSPORTID_SOGOU);
        Assert.assertTrue(row == 1);
    }
}
