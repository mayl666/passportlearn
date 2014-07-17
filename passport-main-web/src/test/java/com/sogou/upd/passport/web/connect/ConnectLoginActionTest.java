package com.sogou.upd.passport.web.connect;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.web.BaseActionTest;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * User: mayan@sogou-inc.com
 * Date: 13-11-29
 * Time: 下午3:22
 */
@Ignore
public class ConnectLoginActionTest extends BaseActionTest {

    @Test
    public void testUpdatePWD() throws IOException {
        Map<String, String> params = Maps.newHashMap();
        params.put("provider", "qq");
        params.put("ru", "http://account.sogou.com/connect/callback/qq");
        params.put("client_id", "1100");
//        params.put("captcha", "GVD2X");
        String result  = sendPost("http://account.sogou.com/connect/login", params);
//        System.out.println(result);
    }





}
