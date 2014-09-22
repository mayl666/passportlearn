package com.sogou.upd.passport.web.connect;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.utils.SignatureUtils;
import com.sogou.upd.passport.web.BaseActionTest;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * User: mayan
 * Date: 13-6-25 Time: 下午5:33
 */
//@ContextConfiguration(locations = "classpath:spring-config-test.xml")
@Ignore
public class ConnectSSOControllerTest extends BaseActionTest {

    @Test
    public void testGetUserinfo() throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        String openid = "3AAEA8586881A6DE841C0440E9D658F3";
        String clientid = "1100";
        String token = "6B42336294805EA55F38B3D094C40AFF";
        long expires_in = 7776000;
        int isthird = 1;
//        String sgid="AVMZenycErU2BZhAHYSdGJA";
        String instance_id = "B8C447F9-8D77-42E9-83A5-F962DFDC9128";
        String secret = "yRWHIkB$2.9Esk>7mBNIFEcr:8\\[Cv";

        TreeMap map = new TreeMap();
        map.put("openid", openid);
        map.put("access_token", token);
        map.put("expires_in", Long.toString(expires_in));
        map.put("client_id", clientid);
        map.put("isthird", Integer.toString(isthird));
        map.put("instance_id", instance_id);

        String code = SignatureUtils.generateSignature(map, secret);
        params.put("code", code);
        params.put("openid", openid);
        params.put("access_token", token);
        params.put("expires_in", Long.toString(expires_in));
        params.put("client_id", clientid);
        params.put("isthird", Integer.toString(isthird));
        params.put("instance_id", instance_id);
//
        params.put("code", code);
        String result = sendPost("http://10.16.139.157:8090/connect/sso/afterauth/qq", params);
        System.out.println(result);
    }

    // 个别应用需要获取到特定的第三方返回结果
    private static Map<Integer, Map<String, String[]>> SPECIAL_PARAMS_MAPPING = Maps.newHashMap();

    static {
        Map gameMap = Maps.newHashMap();
        gameMap.put(AccountTypeEnum.getProviderStr(AccountTypeEnum.QQ.getValue()), new String[]{"pf", "pfkey", "pay_token"});
        SPECIAL_PARAMS_MAPPING.put(2021, gameMap);
    }

    private String buildSpecialResultParams(int clientId, String providerStr) {
        Map<String, String[]> map = SPECIAL_PARAMS_MAPPING.get(clientId);
        if (map != null && !map.isEmpty()) {
            String[] paramArray = map.get(providerStr);
            if (paramArray != null) {
                StringBuilder sb = new StringBuilder();
                for (String param : paramArray) {
                     sb.append(param).append(",");
                }
                return sb.toString();
            }
        }
        return null;
    }

    @Test
    public void testBuildSpecialParams() {
        int clientId = 2021;
        String providerStr = "qq";
        Assert.assertEquals("pf,pfkey,pay_token,", buildSpecialResultParams(clientId, providerStr));
        clientId = 2021;
        providerStr = "sina";
        Assert.assertEquals(null, buildSpecialResultParams(clientId, providerStr));
        clientId = 1120;
        providerStr = "qq";
        Assert.assertEquals(null, buildSpecialResultParams(clientId, providerStr));
        clientId = 1120;
        providerStr = "sina";
        Assert.assertEquals(null, buildSpecialResultParams(clientId, providerStr));
    }
}

