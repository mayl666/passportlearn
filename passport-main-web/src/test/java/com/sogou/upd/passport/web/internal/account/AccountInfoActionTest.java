package com.sogou.upd.passport.web.internal.account;

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
        String uniqname = "年后";
        int clientId = 1110;
        String serverSecret = "FqMV=*S:y^s0$FlwyW>xZ8#A4bQ2Hr";
        long ct = System.currentTimeMillis();
        String code = ManagerHelper.generatorCodeGBK(uniqname.toString(), clientId, serverSecret, ct) ;
        System.out.println("code:" + code);
        params.put("client_id", String.valueOf(clientId));
        params.put("uniqname", uniqname);
        params.put("code", code);
        params.put("ct", String.valueOf(ct));
        String result = sendPost("http://10.11.211.152:8090/internal/account/checkuniqname", params);
        System.out.println(result);
    }


    @Test
    public void testUpdateUserinfo() throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        String uniqname = "dass大发112d";

        String userId="mayan@sogou.com";

        int clientId = 1110;
        String serverSecret = "FqMV=*S:y^s0$FlwyW>xZ8#A4bQ2Hr";
        long ct = System.currentTimeMillis();
        String code = ManagerHelper.generatorCode(userId, clientId, serverSecret, ct) ;
        System.out.println("code:" + code);
        params.put("client_id", String.valueOf(clientId));
        params.put("userid", userId);
        params.put("uniqname", uniqname);
        params.put("birthday","2012-02-05") ;
        params.put("personalId","110108198305051414");
        params.put("modifyip","192.168.0.1");
//        params.put("province","121212");
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

        String userId="57A738AE5409E7C72EB3245D88B526B2@qq.sohu.com";

        String serverSecret = "FqMV=*S:y^s0$FlwyW>xZ8#A4bQ2Hr";
        long ct = System.currentTimeMillis();
        String code = ManagerHelper.generatorCode(userId, clientId, serverSecret, ct) ;
        System.out.println("code:" + code);
        params.put("client_id", String.valueOf(clientId));
        params.put("userid",userId);
//        params.put("fields","avatarurl,personalid,province,city,username,sec_mobile,sec_email,sec_ques") ;
        params.put("fields","avatarurl") ;
        params.put("imagesize","30,55");
        params.put("modifyip","192.168.1.1");
//
        params.put("code", code);
        params.put("ct", String.valueOf(ct));
        String result = sendPost("http://10.11.196.173:8090/internal/account/userinfo", params);
        System.out.println(result);
    }

    //修改单台服务器的hystrix开关
    @Test
    public void testSwitchHystrix() throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        int clientId = 1110;
        String serverSecret = "FqMV=*S:y^s0$FlwyW>xZ8#A4bQ2Hr";
        long ct = System.currentTimeMillis();
        String globalEnabled="true";
        String qqHystrixEnabled="true";
        String kafkaHystrixEnabled="true";
        String userid="nahongxu@sogou.com";

        String code = ManagerHelper.generatorCode(userid, clientId, serverSecret, ct) ;
        System.out.println("code:" + code);
        params.put("userid",userid);
        params.put("client_id", String.valueOf(clientId));
        params.put("code", code);
        params.put("ct", String.valueOf(ct));
        params.put("globalEnabled",globalEnabled);
        params.put("qqHystrixEnabled",qqHystrixEnabled);
        params.put("kafkaHystrixEnabled",kafkaHystrixEnabled);
//        String result = sendPost("http://10.136.24.136:8090/internal/hystrix/switch", params);
        String result = sendPost("http://10.136.24.105:8090/internal/hystrix/switch", params);
        System.out.println(result);

    }
}

