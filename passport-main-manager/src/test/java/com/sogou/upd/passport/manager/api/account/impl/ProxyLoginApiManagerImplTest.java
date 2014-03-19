package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.*;
import junit.framework.Assert;
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
            authUserParameters.setUserid("tinkame302@sohu.com");
            authUserParameters.setClient_id(clientId);
            authUserParameters.setPassword(Coder.encryptMD5("123456"));
            Result result = proxyLoginApiManager.webAuthUser(authUserParameters);
            Assert.assertEquals("0", result.getCode());
            System.out.println(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetCookieInfo() {
        String userId = "testliu94608@sogou.com";
        try {
            CookieApiParams cookieApiParams = new CookieApiParams();
            cookieApiParams.setUserid(userId);
            cookieApiParams.setIp("200.0.98.23");
            Result result = proxyLoginApiManager.getCookieInfo(cookieApiParams);
//            Map<String, Object> map = result.getModels();
//            List<Map<String, String>> listString = (List<Map<String, String>>) map.get("data");
//            Map<String, String> mapString = new HashMap<String, String>();
//            for (int i = 0; i < listString.size(); i++) {
//                String key = listString.get(i).get("name").toString();
//                String value = listString.get(i).get("value").toString();
//                mapString.put(key,value);
//            }
            System.out.println(result.toString());
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
        System.out.println("result:"+result);
    }
    @Test
    public void testgetSHCookieValue() {
       try {
           String userid =  "大大大31231@focus.cn";
//           String utfUserId = new String(userid.getBytes(),"gbk");
           CookieApiParams cookieApiParams = new CookieApiParams();
           cookieApiParams.setUserid(userid);
           cookieApiParams.setClient_id(1044);
           cookieApiParams.setRu("https://account.sogou.com/");
           cookieApiParams.setTrust(CookieApiParams.IS_ACTIVE);
           cookieApiParams.setPersistentcookie(String.valueOf(1));

           //TODO sogou域账号迁移后cookie生成问题
           Result getCookieValueResult = proxyLoginApiManager.getCookieInfo(cookieApiParams);
           System.out.println(getCookieValueResult.toString());
       }catch (Exception ex){

       }

    }

    @Test
    public void testGetCookieValue() {
        CreateCookieUrlApiParams createCookieUrlApiParams = new CreateCookieUrlApiParams();
        createCookieUrlApiParams.setUserid("shipengzhi1986@sogou.com");
        createCookieUrlApiParams.setRu(CommonConstant.DEFAULT_CONNECT_REDIRECT_URL);
        createCookieUrlApiParams.setPersistentcookie(1);
        createCookieUrlApiParams.setDomain("sogou.com");
        Result result = proxyLoginApiManager.getCookieInfoWithRedirectUrl(createCookieUrlApiParams);
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
