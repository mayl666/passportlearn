package com.sogou.upd.passport.manager.api.connect.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-12-19
 * Time: 下午3:31
 * To change this template use File | Settings | File Templates.
 */
public class ConnectApiManagerTest extends BaseTest {

    @Autowired
    private ConnectApiManager proxyConnectApiManager;

    @Test
    public void testSGGetUserInfo() throws Exception {
        BaseOpenApiParams baseOpenApiParams = new BaseOpenApiParams();
        baseOpenApiParams.setOpenid("E4AB85CD9373A582582F05342BB36D2F@qq.sohu.com");
        baseOpenApiParams.setUserid("E4AB85CD9373A582582F05342BB36D2F@qq.sohu.com");
        Result openResult = proxyConnectApiManager.obtainConnectTokenInfo(baseOpenApiParams, SHPPUrlConstant.APP_ID, SHPPUrlConstant.APP_KEY);
        if (openResult.isSuccess()) {
            //获取用户的openId/openKey
            Map<String, String> accessTokenMap = (Map<String, String>) openResult.getModels().get("result");
            String openId = accessTokenMap.get("open_id").toString();
            String accessToken = accessTokenMap.get("access_token").toString();
            System.out.println("openId:" + openId + ", accessToken:" + accessToken);
        } else {
            Assert.assertTrue(false);
        }
    }
}
