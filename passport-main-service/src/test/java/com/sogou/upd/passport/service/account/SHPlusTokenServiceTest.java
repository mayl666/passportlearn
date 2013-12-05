package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.parameter.OAuth2ResourceTypeEnum;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-9-10
 * Time: 上午9:47
 * To change this template use File | Settings | File Templates.
 */
public class SHPlusTokenServiceTest extends BaseTest {

    public static final String ACCESS_TOKEN = "cd61a482ec2f328e63ec8408343f74cd83ed02548a635e20749a6fd27a67cbe4";
    public static final String INSTANCEID = "323906108";

    @Autowired
    private SHPlusTokenService shPlusTokenService;

    /**
     * http://open.account.sohu.com/oauth2/resource/?
     * client_id=30000004&client_secret=59be99d1f5e957ba5a20e8d9b4d76df6&scope=all&instance_id=323906108
     * &resource_type=cookie.get&access_token=cd61a482ec2f328e63ec8408343f74cd83ed02548a635e20749a6fd27a67cbe4
     */
//    @Test
//    public void testVerifyShPlusAccessToken() {
//
//        int clientId = 1044;
//        String instanceId = "323906108";
//        String accesstoken = "cd61a482ec2f328e63ec8408343f74cd83ed02548a635e20749a6fd27a67cbe4";
//
//        boolean successResult = shPlusTokenService.verifyShPlusAccessToken(clientId, instanceId, accesstoken);
//        Assert.assertTrue(successResult);
//    }

    /**
     * https://open.account.sohu.com/oauth2/token/?grant_type=heartbeat&client_id=30000004
     * &client_secret=59be99d1f5e957ba5a20e8d9b4d76df6&redirect_url=www.sohu.com&scope=all&username=shipengzhi1986@sogou.com
     * &refresh_token=7d91b7c1e68ab8ff51c252dd02611b4b5ffb542542144c521625123b339b54c6&instance_id=323906108&sid=1061601372
     * &h=E3C6A85CE88C48A1FC35EAC5DF7FE09B&r=0000&v=4.2.0.8850
     */
    @Test
    public void testVerifyShPlusRefreshToken() {
        int clientId = 1044;
        String instanceId = "323906108";
        String passportId = "shipengzhi1986@sogou.com";
        String refreshToken = "58cc24195472c1c25a378513b6ec7b94dbb290a9468b6d774bf0fc4706330e93";
        String accessToken = shPlusTokenService.queryATokenByRToken(passportId, instanceId, refreshToken, "");
        Assert.assertTrue(accessToken == null);
        refreshToken = "58cc24195472c1c25a378523b6ec7b94dbb290a9468b6d774bf0fc4706330e93";
        accessToken = shPlusTokenService.queryATokenByRToken(passportId, instanceId, refreshToken, "");
        Assert.assertTrue(accessToken != null);
    }

    /**
     * https://account.sogou.com/oauth2/resource/?client_id=30000004&scope=all&instance_id=1213178981&resource_type=cookie.get
     * &access_token=94875c31900ce878b995095ee911d9e950f59e81cf218cca25d42adcc3c6ade5&h=DF9BB5F023D9D0007F4EC6345416E8FE&r=2170&v=4.2.0.8850
     */
    @Test
    public void testGetCookieValue() {
        OAuth2ResourceTypeEnum resourceType = OAuth2ResourceTypeEnum.GET_COOKIE;

        Map resultMap = shPlusTokenService.getResourceByToken(INSTANCEID, ACCESS_TOKEN, resourceType);
        System.out.println("result map:" + resultMap);
    }
    /**
     * http://open.account.sohu.com/oauth2/resource/?scope=all&resource_type=full.get&client_id=30000004
     * &access_token=94875c31900ce878b995095ee911d9e950f59e81cf218cca25d42adcc3c6ade5&instance_id=1213178981
     */
    @Test
    public void testGetUserInfo() {
        OAuth2ResourceTypeEnum resourceType = OAuth2ResourceTypeEnum.GET_FULL_USERINFO;

        Map resultMap = shPlusTokenService.getResourceByToken(INSTANCEID, ACCESS_TOKEN, resourceType);
        System.out.println("result map:" + resultMap);
    }

}
