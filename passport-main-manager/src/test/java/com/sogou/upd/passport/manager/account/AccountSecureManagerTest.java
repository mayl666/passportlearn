package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.parameter.AccountDomainEnum;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-15 Time: 下午4:31 To change this template use
 * File | Settings | File Templates.
 */
@ContextConfiguration(locations = {"classpath:spring-config-test.xml"})
public class AccountSecureManagerTest extends AbstractJUnit4SpringContextTests {
    @Autowired
    private AccountSecureManager accountSecureManager;

    private static final String PASSPORT_ID = "13552848876@sohu.com";
    private static final String NEW_MOBILE = "13800000000";
    private static final String EMAIL = "Binding123@163.com";
    private static final String NEW_EMAIL = "hujunfei1986@126.com";
    private static final String QUESTION = "Secure question";
    private static final String NEW_QUESTION = "New secure question";
    private static final String ANSWER = "Secure answer";
    private static final String NEW_ANSWER = "New secure answer";

    @Test
    public void testAll() {

    }

    /* ------------------------------------修改密保Begin------------------------------------ */
    public void testModify() {

    }

    public void testModifyEmail() {

    }

    /* ------------------------------------修改密保End------------------------------------ */

    /* ------------------------------------修改密保End------------------------------------ */

    public void testResetPwd() {

    }

    public void testResetPwdByEmail() {

    }

    /* ------------------------------------修改密保End------------------------------------ */

}
