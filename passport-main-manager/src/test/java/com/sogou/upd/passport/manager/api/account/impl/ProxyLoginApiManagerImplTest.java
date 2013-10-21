package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.AppAuthTokenApiParams;
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

public class ProxyLoginApiManagerImplTest extends BaseTest {

    private static final int clientId = 1100;

    @Autowired
    private LoginApiManager proxyLoginApiManager;

    @Test
    public void testAuthUser() {
        try {
            AuthUserApiParams authUserParameters = new AuthUserApiParams();
            authUserParameters.setUserid("apptest1@sogou.com");
            authUserParameters.setClient_id(clientId);
            authUserParameters.setPassword(Coder.encryptMD5("111111"));
            Result result = proxyLoginApiManager.webAuthUser(authUserParameters);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAppAuth() throws Exception {
        AppAuthTokenApiParams params = new AppAuthTokenApiParams();
        params.setClient_id(1120);
        params.setToken("54b4c49bfdb3321a5ffea8358c7ec08b");
        params.setCode("23b442b3c93c059b5510b6230d85f070");
        params.setType(2);
        params.setCt(1160703204);
        Result result = proxyLoginApiManager.appAuthToken(params);
        System.out.println(result);
    }

    @Test
    public void testCreateCookie() {
        CreateCookieApiParams createCookieApiParams = new CreateCookieApiParams();
        createCookieApiParams.setUserid(userid);
        createCookieApiParams.setIp(modifyIp);
        Result result = proxyLoginApiManager.createCookie(createCookieApiParams);
        System.out.println(result);
    }

    @Test
    public void testGetCookieValue() {
        CreateCookieUrlApiParams createCookieUrlApiParams = new CreateCookieUrlApiParams();
        createCookieUrlApiParams.setUserid("shipengzhi1986@sogou.com");
        createCookieUrlApiParams.setRu("https://account.sogou.com/login/success");
        createCookieUrlApiParams.setPersistentcookie(1);
        createCookieUrlApiParams.setDomain("sogou.com");
        Result result = proxyLoginApiManager.getCookieValue(createCookieUrlApiParams);
        System.out.println(result);
    }

    @Test
    public void testBuildCreateCookieUrl() {
        CreateCookieUrlApiParams createCookieUrlApiParams = new CreateCookieUrlApiParams();
        createCookieUrlApiParams.setUserid(userid);
        createCookieUrlApiParams.setRu("https://account.sogou.com/login/success");
        createCookieUrlApiParams.setPersistentcookie(1);
        Result result = proxyLoginApiManager.buildCreateCookieUrl(createCookieUrlApiParams, false, true);
        System.out.println(result);
    }
}
