package com.sogou.upd.passport.web.internal.account;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.web.BaseActionTest;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * User: mayan
 * Date: 13-6-7 Time: 下午5:48
 */
public class LoginApiControllerTest extends BaseActionTest {

    public static String httpUrl= "http://10.11.196.173:8090";
    public static String httpUrl_test= "http://10.11.211.152:8090";

    public static  int clientId = 1100;
    public static  String serverSecret = "yRWHIkB$2.9Esk>7mBNIFEcr:8\\[Cv";



    @Test
    public void testAuthemailuser() throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        int clientId = 1014;
        String serverSecret = "6n$gFQf<=Az_3MZb#W?4&LCm~)Qhm{";
        long ct = System.currentTimeMillis();
        String code = ManagerHelper.generatorCode("tinkame_test@sogou.com", clientId, serverSecret, ct) ;
        System.out.println("code:" + code);
        params.put("client_id", String.valueOf(clientId));
        params.put("password", Coder.encryptMD5("123456"));
        params.put("userid","tinkame_test@sogou.com");
        params.put("code", code);
        params.put("ct", String.valueOf(ct));
        String result = sendPost(httpUrl_test+"/internal/account/authemailuser", params);
        System.out.println("testAuthemailuser_reslut:"+result);
    }

    @Test
    public void testAuthuser() throws IOException {
        Map<String, String> params = getAuthuserParam("tinkame_test@sogou.com");
        String result = sendPost(httpUrl+"/internal/account/authuser", params);
        System.out.println(result);
    }

    public Map getAuthuserParam(String passportId){
        Map<String, String> params = new HashMap<String, String>();
        long ct = System.currentTimeMillis();
        String code = ManagerHelper.generatorCode(passportId, clientId, serverSecret, ct) ;
        System.out.println("code:" + code);
        params.put("client_id", String.valueOf(clientId));
//        params.put("password", Coder.encryptMD5("123456"));
        params.put("userid","sohukankan000@sogou.com");
        params.put("code", code);
        params.put("ct", String.valueOf(ct));
        return params;
    }
}
