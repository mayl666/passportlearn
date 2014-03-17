package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
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
    private static final String PASSWORD = "liuling8";
    private static final String PASSPORT_ID1 = "13552848876@sohu.com";
    private static final
    String PASSPORT_ID = PassportIDGenerator.generator(MOBILE, AccountTypeEnum.PHONE.getValue());
    private static final String IP = "127.0.0.1";
    private static final int PROVIDER = AccountTypeEnum.PHONE.getValue();

    /**
     * 测试初始化非第三方用户账号
     */
    @Test
    public void testInitialAccount() throws Exception {
        Account account = accountService.initialAccount(MOBILE, PASSWORD, true, IP, PROVIDER);
        Assert.assertNotNull(account);
    }

    /**
     * 测试根据用户名获取Account对象
     */
    @Test
    public void testQueryAccountByPassportId() {
        Account account = accountService.queryAccountByPassportId(PASSPORT_ID);
        if (account == null) {
            System.out.println("获取不成功!!!");
        } else {
            System.out.println("获取成功..");
        }
    }

    /**
     * 测试验证账号的有效性，是否为正常用户
     */
    @Test
    public void testVerifyAccountVaild() {
        Account account = accountService.queryNormalAccount(PASSPORT_ID);
        if (account != null) {
            System.out.println("用户存在...");
        } else {
            System.out.println("用户不存在!!!");
        }
    }

    /**
     * 测试验证用户名密码是否正确
     */
    @Test
    public void testVerifyUserPwdVaild() {
//        Result result = accountService.verifyUserPwdVaild(MOBILE, PASSWORD, true);
//        if (result.isSuccess()) {
//            System.out.println("正确...");
//        } else {
//            System.out.println("不正确!!!");
//        }
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
