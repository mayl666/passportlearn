package com.sogou.upd.passport.manager.api.connect.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-2-11
 * Time: 上午11:57
 * To change this template use File | Settings | File Templates.
 */
public class SGConnectApiManagerImplTest extends BaseTest {

    @Autowired
    private ConnectApiManager sgConnectApiManager;

    @Test
    public void testBuildConnectAccount() throws Exception {
        String appKey = CommonConstant.APP_CONNECT_KEY;
        String providerStr = "qq";
        String accessToken = "";
        long expiresIn = 7776000;
        String refreshToken = "";
        String openId = "";
        OAuthTokenVO oAuthTokenVO = new OAuthTokenVO();
        Result result = sgConnectApiManager.buildConnectAccount(appKey, providerStr, oAuthTokenVO);
        if (result.isSuccess()) {

        }
    }

}
