package com.sogou.upd.passport.manager.api.connect.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

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
    @Autowired
    private ConnectApiManager proxyConnectApiManager;

    /**
     * 创建第三方账号写SG库
     *
     * @throws Exception
     */
    @Test
    public void testBuildConnectAccount() throws Exception {
        String appKey = CommonConstant.APP_CONNECT_KEY;
        int provider = AccountTypeEnum.QQ.getValue();
        long expiresIn = 7776000;
        String refreshToken = null;
        //用户的openId/openKey
        String openId = "CFF81AB013A94663D83FEC36AC117933";
        String accessToken = "AC1311EBBADD950C4A1113B4A7C19E31";
        OAuthTokenVO oAuthTokenVO = new OAuthTokenVO(accessToken, expiresIn, refreshToken);
        oAuthTokenVO.setOpenid(openId);
        Result result = sgConnectApiManager.buildConnectAccount(appKey, provider, oAuthTokenVO);
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

    @Test
    public void testGetSHToken() {
        String userId = "CFF81AB013A94663D83FEC36AC117933@qq.sohu.com";
        BaseOpenApiParams baseOpenApiParams = new BaseOpenApiParams();
        baseOpenApiParams.setUserid(userId);
        baseOpenApiParams.setOpenid(userId);
        int clientId = 1120;
        String clientKey = "4xoG%9>2Z67iL5]OdtBq$l#>DfW@TY";
        Result result = proxyConnectApiManager.obtainConnectToken(baseOpenApiParams, clientId, clientKey);
        System.out.println("----------------------结果如下----------------------");
        System.out.println(result);


    }

    @Test
    public void testRefreshToken() {
        String userId = "CFF81AB013A94663D83FEC36AC117933@qq.sohu.com";
        ConnectToken connectToken = new ConnectToken();
        connectToken.setPassportId(userId);
        connectToken.setProvider(AccountTypeEnum.QQ.getValue());
        connectToken.setAppKey("100294784");
        connectToken.setOpenid("CFF81AB013A94663D83FEC36AC117933");
        connectToken.setAccessToken("0CD2495BB6C35EDE1D5D97D02E2809B1");
        connectToken.setRefreshToken("33B7D25DA4F5FCD9F5DB7B4EE9136E67");
        connectToken.setExpiresIn(7776000l);
        connectToken.setUpdateTime(new Date());

        ConnectConfig connectConfig = new ConnectConfig();
        connectConfig.setAppKey("100294784");
        connectConfig.setAppSecret("a873ac91cd703bc037e14c2ef47d2021");
        connectConfig.setProvider(AccountTypeEnum.QQ.getValue());
        Result result = null;
        //刷新token是私有方法，如果测试，去掉注释，改为public即可
//        try {
//            result = sgConnectApiManager.verifyRefreshAccessToken(connectToken, connectConfig);
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (OAuthProblemException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
        System.out.println("----------------------结果如下----------------------");
        System.out.println(result);


    }

}
