package com.sogou.upd.passport.web.internal.account;

import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.web.BaseActionTest;
import com.sogou.upd.passport.web.JUnitActionBase;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * User: mayan
 * Date: 13-6-7 Time: 下午5:48
 */
public class LoginApiControllerTest extends BaseActionTest {



    @Test
    public void testAuthemailuser() throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        int clientId = 1014;
        String serverSecret = "6n$gFQf<=Az_3MZb#W?4&LCm~)Qhm{";
        long ct = System.currentTimeMillis();
        String code = ManagerHelper.generatorCode("sohukankan000@sogou.com", clientId, serverSecret, ct) ;
        System.out.println("code:" + code);
        params.put("client_id", String.valueOf(clientId));
        params.put("password", "123123");
        params.put("userid","sohukankan000@sogou.com");
        params.put("createip", "192.168.1.1");
        params.put("code", code);
        params.put("ct", String.valueOf(ct));
        String result = sendPost("http://10.11.211.152:8090/internal/account/authemailuser", params);
        System.out.println(result);
    }
}
