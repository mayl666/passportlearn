package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.AppAuthTokenApiParams;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieUrlApiParams;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 下午2:28
 */
//@Ignore
public class ProxyLoginApiManagerImplTest extends BaseTest {

    private static final int clientId = 1100;

    @Autowired
    private LoginApiManager proxyLoginApiManager;

    @Test
    public void testAuthUser() {
        try {
            AuthUserApiParams authUserParameters = new AuthUserApiParams();
            authUserParameters.setUserid("lxy790458144@game.sohu.com");
            authUserParameters.setClient_id(1044);
            authUserParameters.setPassword(Coder.encryptMD5("ChenLiu1314!!"));
            Result result = proxyLoginApiManager.webAuthUser(authUserParameters);
            System.out.println(result);
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
            cookieApiParams.setTrust(1);
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
            System.out.println("result:"+result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
