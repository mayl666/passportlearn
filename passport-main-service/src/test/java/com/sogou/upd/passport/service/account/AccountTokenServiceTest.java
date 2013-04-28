package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-4-8
 * Time: 上午10:30
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class AccountTokenServiceTest extends AbstractJUnit4SpringContextTests {

    @Inject
    private AccountTokenService accountAuthService;

    private static final String REFRESH_TOKEN = "X124c4TlPMYa9Fiw1TC0pKLhI8_47b_ZqghatW3-QgIn4P915y97snN38QsVOK0MPplbTnwcUa2-34kMzgYaNhkTDbGp5Hb1LkfUkSkb_7M";
    private static final String INSTANCE_ID = "rer4543546576879htyh56njliuling1";
    private static final String MOBILE = "13545210241";
    private static final int CLIENT_ID_INT = 1001;
    private static final String PASSPORT_ID = PassportIDGenerator.generator(MOBILE, AccountTypeEnum.PHONE.getValue());
    private static final long USER_ID = 88;

    /**
     * 测试验证refresh_token的合法性
     */
    @Test
    public void testVerifyRefreshToken() {
        AccountToken accountAuth = accountAuthService.verifyRefreshToken(REFRESH_TOKEN, INSTANCE_ID);
        if (accountAuth != null) {
            System.out.println("合法...");
        } else {
            System.out.println("不合法!!!");
        }
    }

    /**
     * 测试初始化账号授权信息
     */
    @Test
    public void testInitialAccountAuth() throws Exception {
        AccountToken
                accountAuth = accountAuthService.initialAccountToken(PASSPORT_ID, CLIENT_ID_INT, INSTANCE_ID);
        if (accountAuth != null) {
            System.out.println("初始化成功...");
        } else {
            System.out.println("初始化不成功!!!");
        }
    }

    /**
     * 测试更新用户状态信息
     */
    @Test
    public void testUpdateAccountAuth() throws Exception {
        AccountToken
                accountAuth = accountAuthService.updateAccountToken(PASSPORT_ID, CLIENT_ID_INT, INSTANCE_ID);
        if (accountAuth != null) {
            System.out.println("插入auth表成功...");
        } else {
            System.out.println("插入auth不成功!!!");
        }
    }

    /**
     * 测试异步更新某用户其它状态信息
     */
    @Test
    public void testAsynUpdateAccountAuthBySql() {
        accountAuthService.asynbatchUpdateAccountToken(MOBILE, CLIENT_ID_INT);
    }
}
