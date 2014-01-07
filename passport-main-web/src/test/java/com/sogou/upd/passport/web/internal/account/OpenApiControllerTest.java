package com.sogou.upd.passport.web.internal.account;

import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.web.BaseActionTest;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-11-29
 * Time: 下午4:19
 * To change this template use File | Settings | File Templates.
 */
public class OpenApiControllerTest extends BaseActionTest {

    @Test
    public void testConnectOpenApi() throws IOException {
        Map<String, String> params = new HashMap<String, String>();
//        String userid = "1548840104@sohu.com";
//        String openid = "1548840104@sohu.com";
        String userid = "1677D17252BC8AC2985ED6E989D2894E@qq.sohu.com";
        String openid = "1677D17252BC8AC2985ED6E989D2894E@qq.sohu.com";
        int clientId = 1120;
        String serverSecret = "4xoG%9>2Z67iL5]OdtBq$l#>DfW@TY";
        long ct = System.currentTimeMillis();
        String code = ManagerHelper.generatorCodeGBK(userid.toString(), clientId, serverSecret, ct);
        System.out.println("code:" + code);
        params.put("client_id", String.valueOf(clientId));
        params.put("userid", userid);
        params.put("openid", openid);
        params.put("code", code);
        params.put("ct", String.valueOf(ct));
        params.put("openApiName", "/v3/user/sogou_flag");
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("format", "json");
        maps.put("opt", "set");
        maps.put("pf", "qzone");
        maps.put("value", "0");
        maps.put("userip", "10.128.134.167");
        String mapString = JacksonJsonMapperUtil.getMapper().writeValueAsString(maps);
        params.put("params", mapString);
        String result = sendPost("http://localhost/internal/connect/qq/light", params);
        System.out.println(result);
    }
}
