package com.sogou.upd.passport.manager.api.connect.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

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

    //String appKey, String providerStr, OAuthTokenVO oAuthTokenVO
    @Test
    public void testBuildConnectAccount() throws Exception {
        String appKey = CommonConstant.APP_CONNECT_KEY;
        String providerStr = "qq";
        OAuthTokenVO oAuthTokenVO = new OAuthTokenVO();
//        Result result = ConnectApiManager.buildConnectAccount(appKey, providerStr, oAuthTokenVO);
    }

}
