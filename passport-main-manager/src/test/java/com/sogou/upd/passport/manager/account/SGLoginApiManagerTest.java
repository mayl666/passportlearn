package com.sogou.upd.passport.manager.account;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import junit.framework.Assert;
import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: chenjiameng Date: 13-5-15 Time: 下午4:31 To change this template use
 * File | Settings | File Templates.
 */
//@Ignore
@ContextConfiguration(locations = {"classpath:spring-config-test.xml"})
public class SGLoginApiManagerTest extends AbstractJUnit4SpringContextTests {
    @Autowired
    private LoginApiManager sgLoginApiManager;

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


    /**
     * 用于生成 sginf sgrdig
     */
    @Test
    public void testGetSGCookie() {
        String userid = "happytest0814@sogou.com";
        int client_id = 1120;
        String refnick = "测试";

        CookieApiParams params = new CookieApiParams(userid, client_id, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, refnick);

        Result result = sgLoginApiManager.getCookieInfo(params);

        System.out.println("==============sginf :" + result.getModels().get("sginf"));
        System.out.println("==============sgrdig :" + result.getModels().get("sgrdig"));


    }


    /**
     * 用于生成 ver=5 cookie: passport、ppinfo、ppinf、pprdig
     */
    @Test
    public void testVer5SGCookie() {

        //生成种搜狗域下的 ver=5 的 cookie:passport、ppinfo、ppinf、pprdig

        String userid = "happytest0814@sogou.com";
        int client_id = 1120;
        String refnick = "测试0821";

        CookieApiParams cookieApiParams = new CookieApiParams(userid, client_id, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, refnick);
        Result result = sgLoginApiManager.getSGCookieInfoForAdapter(cookieApiParams);
        System.out.println("========= passport=" + result.getModels().get("passport"));
        System.out.println("========= ppinfo=" + result.getModels().get("ppinfo"));
        System.out.println("========= ppinf=" + result.getModels().get("ppinf"));
        System.out.println("========= pprdig=" + result.getModels().get("pprdig"));

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


    @Test
    public void testStrBlackList() {

        //nanajiaozixian1@sogou.com ~ nanajiaozixian99@sogou.com

        StringBuffer blacklist = new StringBuffer("0 0 30");
        blacklist.append("\r\n").append("nanajiaozixian1@sogou.com").append("\r\n");
        blacklist.append("nanajiaozixian1@sogou.com").append("\r\n");
        blacklist.append("nanajiaozixian1@sogou.com").append("\r\n");
        blacklist.append("nanajiaozixian1@sogou.com").append("\r\n");
        blacklist.append("nanajiaozixian1@sogou.com").append("\r\n");
        blacklist.append("nanajiaozixian1@sogou.com").append("\r\n");
        System.out.println("=========================");
        System.out.println(blacklist.toString());
        System.out.println("=========================");


        System.out.println("unix timestamp " + System.currentTimeMillis());
    }


}
