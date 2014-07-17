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
public class MobileActionTest extends BaseActionTest {


    @Test
    public void testGetUserInfo() throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        String userid = "1747428841@sina.sohu.com";
        int clientId = 2007;
        String serverSecret = "udj0D>~Ez`:%Zbj`wFfh8mW`lB[{(]";
        String openid = "1747428841@sina.sohu.com";
//        long ct = System.currentTimeMillis();
        String ct = "1381915491000";
        String code = ManagerHelper.generatorCodeGBK(userid, clientId, serverSecret, Long.parseLong(ct)) ;
        System.out.println("code:" + code);
        params.put("client_id", String.valueOf(clientId));
        params.put("userid", userid);
        params.put("code", code);
        params.put("ct", String.valueOf(ct));
        params.put("openid",openid);
        String result = sendPost("http://10.11.196.173:8090/internal/connect/users/info", params);
        System.out.println(params);

        System.out.println(result);
    }

    class UsernamePwdMapping {
        private String username;
        private String pwd;

        public UsernamePwdMapping(String username, String pwd) {
            this.username = username;
            this.pwd = pwd;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPwd() {
            return pwd;
        }

        public void setPwd(String pwd) {
            this.pwd = pwd;
        }
    }
}

