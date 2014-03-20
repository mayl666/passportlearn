package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import com.sogou.upd.passport.service.account.generator.PwdGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: liuling Date: 13-4-7 Time: 下午4:09 To change this template use
 * File | Settings | File Templates.
 */
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class AccountServiceTest extends AbstractJUnit4SpringContextTests {

    @Inject
    private AccountServiceForDelete accountServiceForDelete;
    @Inject
    private MobilePassportMappingServiceForDelete mobilePassportMappingServiceForDelete;

    private static final String MOBILE = "18511531063";
    private static final String NEW_MOBILE = "13800000000";
    private static final String PASSWORD = "111111";
    private static final String PASSPORT_ID1 = "13552848876@sohu.com";
    private static final
    String PASSPORT_ID_PHONE = PassportIDGenerator.generator(MOBILE, AccountTypeEnum.PHONE.getValue());
    private static final String IP = "127.0.0.1";
    private static final int PROVIDER = AccountTypeEnum.PHONE.getValue();
    private static final String SOGOU = "liuling@sogou.com";
    private static final String EMAIL = "loveerin9460@163.com";
    private static final int PROVIDER_EMAIL = AccountTypeEnum.EMAIL.getValue();

    /**
     * 初始化手机用户账号，并验证是否插入正确
     */
    @Test
    public void testInitialPhoneAccount() throws Exception {
        Account account = accountServiceForDelete.queryAccountByPassportId(PASSPORT_ID_PHONE);
        if (account != null) {
            boolean flag = accountServiceForDelete.deleteAccountByPassportId(PASSPORT_ID_PHONE);
            Assert.assertTrue(flag);
            boolean flagDelete = mobilePassportMappingServiceForDelete.deleteMobilePassportMapping(MOBILE);
            Assert.assertTrue(flagDelete);
        }
        String PASSWORD_CRYPT = PwdGenerator.generatorStoredPwd(PASSWORD, true);
        account = accountServiceForDelete.initialAccount(MOBILE, PASSWORD_CRYPT, true, IP, PROVIDER);
        Assert.assertNotNull(account);
        Assert.assertEquals(PASSPORT_ID_PHONE, account.getPassportId());
        String passportId = mobilePassportMappingServiceForDelete.queryPassportIdByMobile(MOBILE);
        Assert.assertNotNull(passportId);
        Assert.assertEquals(PASSPORT_ID_PHONE, passportId);
        Assert.assertEquals(passportId, account.getPassportId());
    }


    /**
     * 验证插入表中的内容是否与预定值相等
     *
     * @throws Exception
     */
    @Test
    public void QueryAccountByPassportId() throws Exception {
        Account account = accountServiceForDelete.queryAccountByPassportId(PASSPORT_ID_PHONE);
        Assert.assertNotNull(account);
        Assert.assertEquals(PASSPORT_ID_PHONE, account.getPassportId());
    }

    /**
     * 发送激活邮件至注册邮箱,自测需要在action里调用，在service中测试会提示找不到.vm文件
     */
    @Test
    public void testSendEmail() throws Exception {
        String mail = "erinbeals2012@gmail.com";
        String PASSWORD_CRYPT = PwdGenerator.generatorStoredPwd(PASSWORD, true);
        boolean isSuccess = false;
        try {
            isSuccess = accountServiceForDelete.sendActiveEmail(mail, PASSWORD_CRYPT, CommonConstant.SGPP_DEFAULT_CLIENTID, "127.0.0.1", null);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        Assert.assertTrue(isSuccess);
    }

    /**
     * 初始化搜狗账号
     *
     * @throws Exception
     */
    @Test
    public void testInitialSogouAccount() throws Exception {
        Account account = accountServiceForDelete.queryAccountByPassportId(SOGOU);
        if (account != null) {
            boolean flag = accountServiceForDelete.deleteAccountByPassportId(SOGOU);
            Assert.assertTrue(flag);
        }
        String PASSWORD_CRYPT = PwdGenerator.generatorStoredPwd(PASSWORD, true);
        account = accountServiceForDelete.initialAccount(SOGOU, PASSWORD_CRYPT, true, IP, PROVIDER_EMAIL);
        Assert.assertNotNull(account);
        Assert.assertEquals(SOGOU, account.getPassportId());
        Assert.assertNotNull(account);
    }

    /**
     * 测试根据用户名获取手机用户对象
     */
    @Test
    public void testQueryPhoneByPassportId() {
        Account account = accountServiceForDelete.queryAccountByPassportId(PASSPORT_ID_PHONE);
        Assert.assertNotNull(account);
        String passportId = mobilePassportMappingServiceForDelete.queryPassportIdByMobile(MOBILE);
        Assert.assertNotNull(passportId);
        Assert.assertEquals(passportId, account.getPassportId());
    }

    /**
     * 测试根据用户名获取手机用户对象
     */
    @Test
    public void testQuerySogouByPassportId() {
        Account account = accountServiceForDelete.queryAccountByPassportId(SOGOU);
        Assert.assertNotNull(account);
        Assert.assertEquals(SOGOU, account.getPassportId());
    }

    /**
     * 测试根据用户名获取手机用户对象
     */
    @Test
    public void testNotInitWebAccount() {
        Account account = accountServiceForDelete.initialWebAccount(EMAIL, IP);
        Assert.assertNull(account);
    }


    @Test
    public void testInitWebAccountToCache() {
//        accountServiceForDelete.initialAccountToCache();
    }


    /**
     * 测试验证账号的有效性，是否为正常用户
     */
    @Test
    public void testVerifyAccountVaild() {
        Account account = accountServiceForDelete.queryNormalAccount(PASSPORT_ID_PHONE);
        Assert.assertNotNull(account);
    }

    /**
     * 测试验证用户名密码是否正确
     */
    @Test
    public void testVerifyUserPwdVaild() {
        Result result = accountServiceForDelete.verifyUserPwdValid("tinkame_test@sogou.com", "123456", true);
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void testVerifyUserPwdValidByPasswordType() {
        try {
            Account account_0 = new Account();
            account_0.setId(1);
            account_0.setAccountType(1);
            account_0.setPassportId("tinkame_test@sogou.com");
            account_0.setPasswordType("0");
            account_0.setPassword("123456");
            Result result = accountServiceForDelete.verifyUserPwdValidByPasswordType(account_0, "123456", false);
            Assert.assertTrue(result.isSuccess());

            Account account_1 = new Account();
            account_1.setId(1);
            account_1.setAccountType(1);
            account_1.setPassportId("tinkame_test@sogou.com");
            account_1.setPasswordType("1");
            account_1.setPassword(Coder.encryptMD5("123456"));
            Result result_1 = accountServiceForDelete.verifyUserPwdValidByPasswordType(account_1, "123456", false);
            Assert.assertTrue(result_1.isSuccess());

            Account account_2 = new Account();
            account_2.setId(1);
            account_2.setAccountType(1);
            account_2.setPassportId("tinkame_test@sogou.com");
            account_2.setPasswordType("2");
            account_2.setPassword(PwdGenerator.generatorStoredPwd("123456", true));
            Result result_2 = accountServiceForDelete.verifyUserPwdValidByPasswordType(account_2, "123456", true);
            Assert.assertTrue(result_2.isSuccess());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 测试重置密码
     */
    @Test
    public void testResetPassword() {
        Account account = accountServiceForDelete.queryNormalAccount(PASSPORT_ID_PHONE);
        boolean flag = accountServiceForDelete.resetPassword(account, PASSWORD, true);
        if (flag != false) {
            System.out.println("重置成功...");
        } else {
            System.out.println("重置失败!!!");
        }
    }

    /**
     * 测试修改绑定手机
     */
    @Test
    public void testModifyMobile() {
        Account account = accountServiceForDelete.queryAccountByPassportId(PASSPORT_ID1);
        boolean flag = accountServiceForDelete.modifyMobile(account, NEW_MOBILE);
        if (flag == true) {
            System.out.println("修改成功：" + accountServiceForDelete.queryAccountByPassportId(PASSPORT_ID1).getMobile());
        } else {
            System.out.println("修改失败");
        }
        accountServiceForDelete.modifyMobile(account, account.getMobile());
    }
}
