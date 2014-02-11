package com.sogou.upd.passport.manager.api.connect.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
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

    /**
     * 创建第三方账号写SG库
     *
     * @throws Exception
     */
    @Test
    public void testBuildConnectAccount() throws Exception {
        String appKey = CommonConstant.APP_CONNECT_KEY;
        String providerStr = "qq";
        long expiresIn = 7776000;
        String refreshToken = null;
        //用户的openId/openKey
        String openId = "CFF81AB013A94663D83FEC36AC117933";
        String accessToken = "AC1311EBBADD950C4A1113B4A7C19E31";
        OAuthTokenVO oAuthTokenVO = new OAuthTokenVO(accessToken, expiresIn, refreshToken);
        oAuthTokenVO.setOpenid(openId);
        Result result = sgConnectApiManager.buildConnectAccount(appKey, providerStr, oAuthTokenVO);
        System.out.println("------------------结果如下-------------------");
        System.out.println(result);

    }

    /**
     * 获取token，读SG库
     *
     * @throws Exception
     */
    @Test
    public void testObtainConnectToken() throws Exception {
        int clientId = 1120;
        String clientKey = "4xoG%9>2Z67iL5]OdtBq$l#>DfW@TY";
        //用户的openId/openKey
        String userId = "CFF81AB013A94663D83FEC36AC117933@qq.sohu.com";
        BaseOpenApiParams baseOpenApiParams = new BaseOpenApiParams();
        baseOpenApiParams.setUserid(userId);
        baseOpenApiParams.setOpenid(userId);
        Result result = sgConnectApiManager.obtainConnectToken(baseOpenApiParams, clientId, clientKey);
        System.out.println("------------------结果如下-------------------");
        System.out.println(result);

    }

}
