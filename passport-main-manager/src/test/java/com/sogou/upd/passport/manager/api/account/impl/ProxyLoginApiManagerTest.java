package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieApiParams;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieUrlApiParams;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 下午2:28
 */

public class ProxyLoginApiManagerTest extends BaseTest {

    private static final int clientId = 1100;

    @Autowired
    private LoginApiManager proxyLoginApiManager;

    @Test
    public void testAuthUser() {
        try {
            AuthUserApiParams authUserParameters = new AuthUserApiParams();
            authUserParameters.setUserid("13621009174");
            authUserParameters.setClient_id(clientId);
            authUserParameters.setPassword(Coder.encryptMD5("spz1986411"));
            Result result = proxyLoginApiManager.webAuthUser(authUserParameters);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateCookie(){
        CreateCookieApiParams createCookieApiParams=new CreateCookieApiParams();
        createCookieApiParams.setUserid(passportId);
        createCookieApiParams.setIp(modifyIp);
        Result result = proxyLoginApiManager.createCookie(createCookieApiParams);
        System.out.println(result);
    }

    @Test
    public void testBuildCreateCookieUrl(){
        CreateCookieUrlApiParams createCookieUrlApiParams=new CreateCookieUrlApiParams();
        createCookieUrlApiParams.setUserid(passportId);
        createCookieUrlApiParams.setRu("http://ie.sogou.com");
        Result result = proxyLoginApiManager.buildCreateCookieUrl(createCookieUrlApiParams);
        System.out.println(result);
    }
}
