package com.sogou.upd.passport.manager.account;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.OAuthResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-9-17
 * Time: 上午12:21
 * To change this template use File | Settings | File Templates.
 */
public class OAuth2ResourceManagerTest extends BaseTest {

    public static final String CLIENT_SECRET = "c1756a351db27d817225e2a4fd7b3f7d";
    public static final String ACCESS_TOKEN_SHPLUS = "cd61a482ec2f328e63ec8408343f74cd83ed02548a635e20749a6fd27a67cbe4";
    public static final String INSTANCEID = "323906108";

    public static final String ACCESS_TOKEN_SG = "h34XqEVyxMaRphjblxAbLnoItQ1z_re3PfABehoSM3U1-GyQyQ0zAYxjVhEqWcjg";

    @Autowired
    private OAuth2ResourceManager oAuth2ResourceManager;

    @Test
    public void testGetCookieValue() {
        Result result = oAuth2ResourceManager.getCookieValue(ACCESS_TOKEN_SG, CLIENT_SECRET, INSTANCEID);
        System.out.println("get cookie value result" + result.toString());
    }

    @Test
    public void testGetFullUserInfo() {
        Result result = oAuth2ResourceManager.getFullUserInfo(ACCESS_TOKEN_SG, CLIENT_SECRET, INSTANCEID);
        System.out.println("get userinfo result" + result.toString());
    }

    @Test
    public void testResultJson() {
        Result result = new OAuthResultSupport(false);
        result.setCode(ErrorUtil.ERR_CODE_CREATE_COOKIE_FAILED);
        System.out.println("error result:" + result.toString());
        String ppinf = "dafdasfdsafasdfasdf";
        String pprdig = "daosuewxczyvzxgjoiwqen";
        result.setSuccess(true);
        Map resource = Maps.newHashMap();
        String[] cookieArray = {ppinf, pprdig};
        resource.put("msg", "get cookie success");
        resource.put("code", "0");
        resource.put("scookie", cookieArray);
        Map resourceMap = Maps.newHashMap();
        resourceMap.put("resource", resource);
        result.setModels(resourceMap);
        System.out.println("success result:" + result.toString());
    }
}
