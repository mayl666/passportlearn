package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieApiParams;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieUrlApiParams;
import com.sogou.upd.passport.service.account.generator.PwdGenerator;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 下午2:28
 */

public class ProxyLoginApiManagerImplTest extends BaseTest {

    private static final int clientId = 1100;

    @Autowired
    private LoginApiManager proxyLoginApiManager;

    @Test
    public void testAuthUser() {
        try {
            AuthUserApiParams authUserParameters = new AuthUserApiParams();
            authUserParameters.setUserid("shipengzhi1986@sogou.com");
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
        createCookieApiParams.setUserid(userid);
        createCookieApiParams.setIp(modifyIp);
        Result result = proxyLoginApiManager.createCookie(createCookieApiParams);
        System.out.println(result);
    }

    @Test
    public void testBuildCreateCookieUrl(){
        CreateCookieUrlApiParams createCookieUrlApiParams=new CreateCookieUrlApiParams();
        createCookieUrlApiParams.setUserid(userid);
        createCookieUrlApiParams.setRu("https://account.sogou.com/login/success");
        createCookieUrlApiParams.setPersistentcookie(1);
        Result result = proxyLoginApiManager.buildCreateCookieUrl(createCookieUrlApiParams);
        System.out.println(result);
    }
}
