package com.sogou.upd.passport.web.account.action;

import com.sogou.upd.passport.web.BaseActionTest;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: mayan Date: 13-6-25 Time: 下午5:33 To change this template use
 * File | Settings | File Templates.
 */
//@ContextConfiguration(locations = "classpath:spring-config-test.xml")
@Ignore
public class RegActionTest extends BaseActionTest {

    @Test
    public void testReg() throws IOException {
        String localUrl = "/web/reguser/";
        Map<String, String> params = new HashMap<>();
        params.put("client_id", "1100");
        params.put("new_email", "dasdasdasdasdasd1");
        params.put("old_email", "asasasa");
        params.put("password", "asasasa");

//      params.put("new_email", "212121");
//      params.put("old_email", "dasdasdasdasda@sogou.com");
//      params.put("client_id", "1100");
//      params.put("passport_id","dasdasdasds")  ;
//        params.put("captcha", "GVD2X");
//        params.put("newpwd", "212121");
//        params.put("password", "dasdasdasdasda@sogou.com");
//        params.put("client_id", "1100");

        String result  = sendPost("http://account.sogou.com/web/reguser", params);
        System.out.println(result);
    }
    @Test
    public void testUpdatePWD() throws IOException {
        String localUrl = "/web/reguser/";
        Map<String, String> params = new HashMap<>();
        params.put("newpwd", "212121");
        params.put("password", "dasdasdasdasda@sogou.com");
        params.put("client_id", "1100");
//        params.put("captcha", "GVD2X");
        String result  = sendPost("http://account.sogou.com/web/sendemail", params);
        System.out.println(result);
    }


    @Test
    public void testCheckusername() throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("username", "fsdf3ffds");
//    params.put("client_id", "1100");
        String result  = sendGet("http://account.sogou.com/web/account/checkusername", params);
        System.out.println(result);
    }
}