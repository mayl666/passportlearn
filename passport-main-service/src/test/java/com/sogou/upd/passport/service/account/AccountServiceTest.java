package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.CommonConstant;
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
    private AccountService accountService;

    private static final String MOBILE = "13545210241";
    private static final String NEW_MOBILE = "13800000000";
    private static final String PASSWORD = "111111";
    private static final String PASSPORT_ID1 = "13552848876@sohu.com";
    private static final
    String PASSPORT_ID = PassportIDGenerator.generator(MOBILE, AccountTypeEnum.PHONE.getValue());
    private static final String IP = "127.0.0.1";
    private static final int PROVIDER = AccountTypeEnum.PHONE.getValue();

    private static final String SOGOU = "liuling@sogou.com";
    private static final int PROVIDER_EMAIL = AccountTypeEnum.EMAIL.getValue();

    /**
     * 测试初始化非第三方用户账号
     */
    @Test
    public void testInitialPhoneAccount() throws Exception {
        Account account = accountService.initialAccount(MOBILE, PASSWORD, true, IP, PROVIDER);
        Assert.assertNotNull(account);
    }

    /**
     * 发送激活邮件至注册邮箱,自测需要在action里调用，在service中测试会提示找不到.vm文件
     */
    @Test
    public void testSendEmail() {
        String mail = "erinbeals2012@gmail.com";
        boolean isSuccess = false;
        try {
            isSuccess = accountService.sendActiveEmail(mail, PASSWORD, CommonConstant.SGPP_DEFAULT_CLIENTID, "127.0.0.1", null);
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
        Account account = accountService.initialAccount(SOGOU, PASSWORD, true, IP, PROVIDER_EMAIL);
        Assert.assertNotNull(account);
    }

    /**
     * 测试根据用户名获取Account对象
     */
    @Test
    public void testQueryAccountByPassportId() {
        Account account = accountService.queryAccountByPassportId(PASSPORT_ID);
        Assert.assertNotNull(account);

    }

    /**
     * 测试验证账号的有效性，是否为正常用户
     */
    @Test
    public void testVerifyAccountVaild() {
        Account account = accountService.queryNormalAccount(PASSPORT_ID);
        Assert.assertNotNull(account);
    }

    /**
     * 测试验证用户名密码是否正确
     */
    @Test
    public void testVerifyUserPwdVaild() {
        Result result = accountService.verifyUserPwdValid("tinkame_test@sogou.com", "123456", true);
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void testVerifyUserPwdValidByPasswordType() {
//        Result result = accountService.verifyUserPwdValidByPasswordType("tinkame_test@sogou.com", "123456", true);
//        Assert.assertTrue(result.isSuccess());
    }


    /**
     * 测试重置密码
     */
    @Test
    public void testResetPassword() {
        Account account = accountService.queryNormalAccount(PASSPORT_ID);
        boolean flag = accountService.resetPassword(account, PASSWORD, true);
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
        Account account = accountService.queryAccountByPassportId(PASSPORT_ID1);
        boolean flag = accountService.modifyMobile(account, NEW_MOBILE);
        if (flag == true) {
            System.out.println("修改成功：" + accountService.queryAccountByPassportId(PASSPORT_ID1).getMobile());
        } else {
            System.out.println("修改失败");
        }
        accountService.modifyMobile(account, account.getMobile());
    }
}
