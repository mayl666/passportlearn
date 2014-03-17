package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.math.RSA;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import com.sogou.upd.passport.manager.api.account.impl.SGLoginApiManagerImpl;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: chenjiameng Date: 13-5-15 Time: 下午4:31 To change this template use
 * File | Settings | File Templates.
 */
@ContextConfiguration(locations = {"classpath:spring-config-test.xml"})
public class SGLoginApiManagerTest extends AbstractJUnit4SpringContextTests {
    @Autowired
    private LoginApiManager sgLoginApiManager;

    private static final int clientId = 1100;

    @Test
    public void testAuthUser() {
        try {
            AuthUserApiParams authUserParameters = new AuthUserApiParams();
            authUserParameters.setUserid("13126693178@sohu.com");
            authUserParameters.setClient_id(clientId);
            authUserParameters.setPassword(Coder.encryptMD5("123456"));
            authUserParameters.setUsertype(1);
            Result result = sgLoginApiManager.webAuthUser(authUserParameters);
            System.out.println("testAuthUser:" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetCookieInfo() {
        String userid = "31680D6A6A65D32BF1E929677E78DE29@qq.sohu.com";
        int client_id = 1120;
//        String uniqname = "跳刀的兔子";
        String refnick = "跳刀的兔子";
        // userid, client_id, ru, ip, uniqname, refnick
        CookieApiParams cookieApiParams = new CookieApiParams(userid, client_id, "", "", "", refnick);
        Result result = sgLoginApiManager.getCookieInfo(cookieApiParams);
        System.out.println("sginf: " + result.getModels().get("sginf"));
        System.out.println("sgrdig: " + result.getModels().get("sgrdig"));
    }


    @Test
    public void testVerifyCookie() {
        String passportId = "CFBF0F59AE1029AF7C2F9F1CD4827F96@qq.sohu.com";
        int client_id = 1120;
        CookieApiParams cookieApiParams = new CookieApiParams(passportId, client_id, "", "", "", "");
        Result result = sgLoginApiManager.getCookieInfo(cookieApiParams);
        if (result.isSuccess()) {
            Map map = result.getModels();
            String sginf = (String) map.get("sginf");
            String sgrdig = (String) map.get("sgrdig");
            RequestModel requestModel = new RequestModel("http://10.11.195.95/");
            requestModel.addHeader("Cookie", "sginf=" + sginf + ";" + "sgrdig=" + sgrdig);
            try {
                String response = SGHttpClient.executeStr(requestModel);
                Assert.assertEquals(passportId, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Assert.assertTrue(false);
        }
    }


}
