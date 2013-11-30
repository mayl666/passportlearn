package com.sogou.upd.passport.web.connect;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.utils.URLBuilderUtil;
import com.sogou.upd.passport.web.BaseActionTest;
import com.sogou.upd.passport.web.JUnitActionBase;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * User: mayan@sogou-inc.com
 * Date: 13-11-29
 * Time: 下午3:22
 */
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

    /* ------------------------- 第三方登录相关 ------------------------- */

    public String genThirdLoginURL(String thirdParty, String ru, String cb) {
        String url = "https://account.sogou.com/connect/login";
        String enru = "";
        try {
            cb = URLEncoder.encode(cb, "GBK");
            enru = ru + "?cb=" + cb;
            enru = URLEncoder.encode(enru, "GBK");
        } catch (Exception e) {
        }
        return URLBuilderUtil.addParameters(url, "provider", thirdParty, "client_id", "1100", "ru",
                enru);
    }



}
