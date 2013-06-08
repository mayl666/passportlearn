package com.sogou.upd.passport.manager.proxy.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.proxy.account.LoginApiManager;
import com.sogou.upd.passport.manager.proxy.account.form.AuthUserApiParams;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 下午2:28
 */

public class AccountLoginManagerTest extends BaseTest {

    private static final int clientId = 1100;

    @Autowired
    private LoginApiManager proxyLoginApiManager;

    @Test
    public void testAuthUser() {
        try {
            AuthUserApiParams authUserParameters = new AuthUserApiParams();
            authUserParameters.setPassport_id("13621009174@sohu.com");
            authUserParameters.setClient_id(clientId);
            authUserParameters.setPassword(Coder.encryptMD5("spz1986411"));
            Result result = proxyLoginApiManager.webAuthUser(authUserParameters);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
