package com.sogou.upd.passport.web.test.account.controller;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.service.account.generator.PwdGenerator;
import com.sogou.upd.passport.web.test.BaseActionTest;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-24 Time: 下午2:15 To change this template use
 * File | Settings | File Templates.
 */
//@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class AccountSecureControllerTest extends BaseActionTest {

    @Test
    public void testLogin() throws Exception {
        String url = "/api/login";
        Map<String, String> params = new HashMap<>();
        params.put("passport_id", "hujunfei1986@163.com");
        params.put("client_id", "999");
        params.put("password", "222222");
        Result result = sendPostLocal(url, params);
        System.out.println(result == null ? "Wrong": result.getStatusText());
    }

    @Test
    public void testQuery() throws IOException {
        String localUrl = "/api/query";
        Map<String, String> params = new HashMap<>();
        params.put("username", "hujunfei1986@163.com");
        params.put("client_id", "999");
        params.put("token", "1750fa01d201433fb4d64b36f1efc6a5");
        params.put("captcha", "KPH8R");
        Result result  = sendPostLocal(localUrl, params);
//        System.out.println(result.getStatusText());
//         System.out.println(result.getData());
    }

    @Test
    public void testSendEmailReset() throws IOException {
        String localUrl = "/api/findpwd/sendbemail";
        Map<String, String> params = new HashMap<>();
        params.put("passport_id", "hujunfei1986@163.com");
        params.put("client_id", "999");
        Result result  = sendPostLocal(localUrl, params);
        System.out.println(result.getStatusText());
        System.out.println(result.getData());
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
        Result result  = sendPostLocal(localUrl, params);
        System.out.println(result.getStatusText());
        System.out.println(result.getData());
    }


}
