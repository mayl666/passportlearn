package com.sogou.upd.passport.web.account.controller;

import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.web.BaseActionTest;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: mayan
 * Date: 13-6-25 Time: 下午5:33
 */
//@ContextConfiguration(locations = "classpath:spring-config-test.xml")
@Ignore
public class AccountInfoActionTest extends BaseActionTest {

    @Test
    public void testCheckUniqName() throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        String uniqname = "枕着玩";
        int clientId = 1110;
        String serverSecret = "FqMV=*S:y^s0$FlwyW>xZ8#A4bQ2Hr";
        long ct = System.currentTimeMillis();
        String code = ManagerHelper.generatorCodeGBK(uniqname.toString(), clientId, serverSecret, ct) ;
        System.out.println("code:" + code);
        params.put("client_id", String.valueOf(clientId));
        params.put("uniqname", uniqname);
        params.put("code", code);
        params.put("ct", String.valueOf(ct));
        String result = sendPost("http://127.0.0.1/internal/account/checkuniqname", params);
        System.out.println(result);
    }


    @Test
    public void testUpdateUserinfo() throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        String uniqname = "dasf答大发";
        int clientId = 1110;
        String serverSecret = "FqMV=*S:y^s0$FlwyW>xZ8#A4bQ2Hr";
        long ct = System.currentTimeMillis();
        String code = ManagerHelper.generatorCode("pqmagic20061@sohu.com", clientId, serverSecret, ct) ;
        System.out.println("code:" + code);
        params.put("client_id", String.valueOf(clientId));
        params.put("userid","pqmagic20061@sohu.com");
        params.put("uniqname", uniqname);
        params.put("modifyip","192.168.1.1");
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
        String code = ManagerHelper.generatorCode("pqmagic20061@sohu.com", clientId, serverSecret, ct) ;
        System.out.println("code:" + code);
        params.put("client_id", String.valueOf(clientId));
        params.put("userid","pqmagic20061@sohu.com");
        params.put("fields","uniqname") ;
        params.put("modifyip","192.168.1.1");
        params.put("code", code);
        params.put("ct", String.valueOf(ct));
        String result = sendPost("http://127.0.0.1/internal/account/userinfo", params);
        System.out.println(result);
    }
}

