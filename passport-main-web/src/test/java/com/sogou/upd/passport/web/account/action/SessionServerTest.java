package com.sogou.upd.passport.web.account.action;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.model.httpclient.RequestModelJSON;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.parameter.HttpTransformat;
import com.sogou.upd.passport.common.utils.HttpClientUtil;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.common.utils.SessionServerUtil;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.web.test.BaseActionTest;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: mayan
 * Date: 13-6-25 Time: 下午5:33
 */
//@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class SessionServerTest extends BaseActionTest {
    private static ObjectMapper jsonMapper = JacksonJsonMapperUtil.getMapper();
    @Test
    public void testAuthSgId() throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        int clientId = 1120;
        String serverSecret = "4xoG%9>2Z67iL5]OdtBq$l#>DfW@TY";
        long ct = System.currentTimeMillis();

        String sgid="AVKcIZJsBHcpbaA-xstFVMo" ;
        String code = ManagerHelper.generatorCode(sgid, clientId, serverSecret, ct) ;
        params.put("user_ip","127.0.0.1");
        params.put("client_id", String.valueOf(clientId));
        params.put("code", code);
        params.put("ct", String.valueOf(ct));
        params.put("sgid",sgid);
        String result = sendPost("http://10.13.202.168:8090/verify_sid", params);
        System.out.println(result);
    }

    @Test
    public void testInsertSgId() throws Exception {
        Map<String, String> params = Maps.newHashMap();
        int clientId = 1120;
        String serverSecret = "4xoG%9>2Z67iL5]OdtBq$l#>DfW@TY";
        long ct = System.currentTimeMillis();
        String passportId="mayan@sogou.com";
        String sgid= SessionServerUtil.createSessionSid(passportId);
        String code = ManagerHelper.generatorCode(sgid, clientId, serverSecret, ct) ;

        params.put("client_id", String.valueOf(clientId));
        params.put("code", code);
        params.put("ct", String.valueOf(ct));
        params.put("sgid",sgid);
        params.put("user_info", jsonMapper.writeValueAsString(Maps.newHashMap().put("passport_id",passportId)));
        String result = HttpClientUtil.postRequest("http://session.account.sogou.com.z.sogou-op.org/set_session", params);

        Result result1= jsonMapper.readValue(result, Result.class);
        System.out.println(result1.getStatus());
    }

    @Test
    public void testQQ() throws Exception{
        Map<String, String> params = new HashMap<String, String>();

        params.put("grant_type","authorization_code");
        params.put("client_id","1120");
        params.put("client_secret","a873ac91cd703bc037e14c2ef47d2021") ;
        params.put("code","46559AC1CCA0A0F639766C92B670C82D");
        params.put("redirect_uri","http://account.sogou.com/connect/callback/qq&client_id=1120&type=wap&display=wml");

        String result = sendPost("https://graph.qq.com/oauth2.0/token", params);
        System.out.println(result);

    }
}

class Result{
    private String status;
    private String statusText;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }
}

