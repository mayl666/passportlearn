package com.sogou.upd.passport.web.account.action;

import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.web.BaseActionTest;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: mayan
 * Date: 13-6-25 Time: 下午5:33
 */
//@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class AccountInfoActionTest extends BaseActionTest {

    @Test
    public void testCheckUniqName() throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        String uniqname = "日日";
        int clientId = 1110;
        String serverSecret = "FqMV=*S:y^s0$FlwyW>xZ8#A4bQ2Hr";
        long ct = System.currentTimeMillis();
        String code = ManagerHelper.generatorCodeGBK(uniqname.toString(), clientId, serverSecret, ct) ;
        System.out.println("code:" + code);
        params.put("client_id", String.valueOf(clientId));
        params.put("uniqname", uniqname);
        params.put("code", code);
        params.put("ct", String.valueOf(ct));
        String result = sendPost("http://10.11.196.173:8090/internal/account/checkuniqname", params);
        System.out.println(result);
    }


    @Test
    public void testUpdateUserinfo() throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        String uniqname = "dasfds大发";
        int clientId = 1110;
        String serverSecret = "FqMV=*S:y^s0$FlwyW>xZ8#A4bQ2Hr";
        long ct = System.currentTimeMillis();
        String code = ManagerHelper.generatorCode("pqmagic20061@sohu.com", clientId, serverSecret, ct) ;
        System.out.println("code:" + code);
        params.put("client_id", String.valueOf(clientId));
        params.put("userid", "pqmagic20061@sohu.com");
        params.put("uniqname", uniqname);
        params.put("birthday","2012-02-05") ;
        params.put("personalId","110108198305051414");
        params.put("modifyip","192.168.0.1");
        params.put("city","320501");
        params.put("gender","1");
        params.put("code", code);
        params.put("ct", String.valueOf(ct));
        String result = sendPost("http://127.0.0.1/internal/account/updateuserinfo", params);
        System.out.println(result);
    }

    @Test
    public void testGetUserinfo() throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        int clientId = 1110;
        String serverSecret = "FqMV=*S:y^s0$FlwyW>xZ8#A4bQ2Hr";
        long ct = System.currentTimeMillis();

        String passportId="lovemd@sohu.com";
        String code = ManagerHelper.generatorCode(passportId, clientId, serverSecret, ct) ;
        System.out.println("code:" + code);
        params.put("client_id", String.valueOf(clientId));
        params.put("userid",passportId);
        params.put("fields","uniqname,personalid,province,city,username,sec_mobile,sec_email,sec_ques") ;
        params.put("modifyip","192.168.1.1");
//
        params.put("code", code);
        params.put("ct", String.valueOf(ct));
        String result = sendPost("http://10.16.139.157:8090/internal/account/userinfo", params);
        System.out.println(result);
    }
}

