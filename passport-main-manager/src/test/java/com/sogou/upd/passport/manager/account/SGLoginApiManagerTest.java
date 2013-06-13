package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * Created with IntelliJ IDEA. User: chenjiameng Date: 13-5-15 Time: 下午4:31 To change this template use
 * File | Settings | File Templates.
 */
@ContextConfiguration(locations = {"classpath:spring-config-test.xml"})
public class SGLoginApiManagerTest extends AbstractJUnit4SpringContextTests {
    @Autowired
    private LoginApiManager sgLoginApiManager;

    private static final String PASSPORT_ID = "13552848876@sohu.com";
    private static final String NEW_MOBILE = "13800000000";
    private static final String EMAIL = "Binding123@163.com";
    private static final String NEW_EMAIL = "hujunfei1986@126.com";
    private static final String QUESTION = "Secure question";
    private static final String NEW_QUESTION = "New secure question";
    private static final String ANSWER = "Secure answer";
    private static final String NEW_ANSWER = "New secure answer";

    private static final int clientId = 1100;
    @Test
    public void testAuthUser() {
        try {
            AuthUserApiParams authUserParameters = new AuthUserApiParams();
            authUserParameters.setUserid("13126693178");
            authUserParameters.setClient_id(clientId);
            authUserParameters.setPassword(Coder.encryptMD5("123456"));
            authUserParameters.setUsertype(1);
            Result result = sgLoginApiManager.webAuthUser(authUserParameters);
            System.out.println("testAuthUser:"+result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
