package com.sogou.upd.passport.web.account.controller;

import com.sogou.upd.passport.web.BaseActionTest;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-24 Time: 下午2:15 To change this template use
 * File | Settings | File Templates.
 */
@Ignore
//@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class AccountSecureControllerTest extends BaseActionTest {

    @Test
    public void testQuery() throws IOException {
        String localUrl = "/web/secure/query";
        Map<String, String> params = new HashMap<>();
        params.put("username", "hujunfei1986@163.com");
        params.put("client_id", "999");
        params.put("token", "4126d1b357ff49c7a5714c13d767d66a");
        params.put("captcha", "GVD2X");
        String result  = sendPost("http://account.sogou.com/api/secure/query", params);
        System.out.println(result);
//        System.out.println(result.getStatusText());
//         System.out.println(result.getData());
    }

    @Test
    public void testSendEmailReset() throws IOException {
        String localUrl = "/api/findpwd/sendbemail";
        Map<String, String> params = new HashMap<>();
        params.put("passport_id", "hujunfei1986@163.com");
        params.put("client_id", "999");
        String result = sendPost("http://account.sogou.com/api/findpwd/sendremail", params);
        /*Result result  = sendPostLocal(localUrl, params);
        System.out.println(result.getMessage());*/
        System.out.println(result);
    }

    @Test
    public void testEmailReset() throws Exception {
        String localUrl = "/api/findpwd/email";
        Map<String, String> params = new HashMap<>();
        params.put("passport_id", "hujunfei1986@163.com");
        params.put("client_id", "999");
        String passwdSign = DigestUtils.md5Hex("222222".getBytes());
        params.put("password", passwdSign);
        params.put("scode", "c7a7c37beef63f542975b137e4b8314c");
        String result  = sendPostLocal(localUrl, params);
        System.out.println(result);
    }

    @Test
    public void testSendSms() throws Exception {
        String url = "/api/sendsms";
        Map<String, String> params = new HashMap<>();
        params.put("username", "hujunfei1986@163.com");
        params.put("client_id", "999");
        params.put("mode", "2");
        params.put("module", "3");
        String result = sendPostLocal(url, params);
        System.out.println(result);
    }

    @Test
    public void testCheckSmsReset() throws Exception {
        String url = "/api/findpwd/checksms";
        Map<String, String> params = new HashMap<>();
        params.put("passport_id", "hujunfei1986@163.com");
        params.put("client_id", "999");
        params.put("smscode", "99875");
        String result = sendPostLocal(url, params);
        System.out.println(result);
    }

    @Test
    public void testMobileReset() throws Exception {
        String url = "/api/findpwd/mobile";
        Map<String, String> params = new HashMap<>();
        params.put("passport_id", "hujunfei1986@163.com");
        params.put("client_id", "999");
        params.put("scode", "6647bec044bb446fd20cd186358fe173");
        params.put("password", DigestUtils.md5Hex("111111".getBytes()));
        String result = sendPostLocal(url, params);
        System.out.println(result);

    }

    @Test
    public void testCheckAnswer() throws Exception {
        String url = "/api/findpwd/checkanswer";
        Map<String, String> params = new HashMap<>();
        params.put("passport_id", "hujunfei1986@163.com");
        params.put("client_id", "999");
        params.put("answer", "day");
        params.put("token", "3c81bb228cf848dcac7a664edc141040");
        params.put("captcha", "G2XCH");
        String result = sendPostLocal(url, params);
        System.out.println(result);
    }

    @Test
    public void testAnswerReset() throws Exception {
        String url = "/api/findpwd/ques";
        Map<String, String> params = new HashMap<>();
        params.put("passport_id", "hujunfei1986@163.com");
        params.put("client_id", "999");
        params.put("password", DigestUtils.md5Hex("222222".getBytes()));
        params.put("scode", "e54cc4bc09739ade3e746ad892305227");
        String result = sendPostLocal(url, params);
        System.out.println(result);
    }

    @Test
    public void testSendEmailForBind() throws Exception {
        String url = "/api/bind/sendemail";
        Map<String, String> params = new HashMap<>();
        params.put("passport_id", "hujunfei1986@163.com");
        params.put("client_id", "999");
        params.put("password", "222222");
        params.put("new_email", "hujunfei@sogou-inc.com");
        String result = sendPostLocal(url, params);
        System.out.println(result);
    }


    @Test
    public void testSendSmsNew() throws Exception {
        String url = "/api/sendsms";
        Map<String, String> params = new HashMap<>();
        params.put("username", "180XXXXXXXX"); // 填写待绑定新手机号
        params.put("client_id", "999");
        params.put("mode", "3");
        params.put("module", "3");
        String result = sendPostLocal(url, params);
        System.out.println(result);
    }


    @Test
    public void testModifyQues() throws Exception {
        String url = "/api/bind/ques";
        Map<String, String> params = new HashMap<>();
        params.put("passport_id", "hujunfei1986@163.com");
        params.put("client_id", "999");
        params.put("password", DigestUtils.md5Hex("111111".getBytes()));
        params.put("new_ques", "day");
        params.put("new_answer", "birthday");
        String result = sendPostLocal(url, params);
        System.out.println(result);
    }


}
