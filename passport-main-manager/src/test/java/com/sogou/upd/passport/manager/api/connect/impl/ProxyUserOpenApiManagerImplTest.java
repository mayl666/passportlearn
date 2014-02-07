package com.sogou.upd.passport.manager.api.connect.impl;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.UserOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import com.sogou.upd.passport.manager.api.connect.form.user.UserOpenApiParams;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.oauth2.openresource.http.OAuthHttpClient;
import com.sogou.upd.passport.oauth2.openresource.request.OAuthClientRequest;
import com.sogou.upd.passport.oauth2.openresource.request.user.QQUserAPIRequest;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;
import com.sogou.upd.passport.oauth2.openresource.response.user.QQUserAPIResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-7-10
 * Time: 下午4:37
 * To change this template use File | Settings | File Templates.
 */
public class ProxyUserOpenApiManagerImplTest extends BaseTest {

    @Autowired
    private UserOpenApiManager sgUserOpenApiManager;


    @Autowired
    private ConnectApiManager proxyConnectApiManager;
    @Autowired
    private ConfigureManager configureManager;


    /**
     * 通过搜狐代理接口调用第三方OpenAPI
     *
     * @throws Exception
     */
    @Test
    public void testGetUserInfo() throws Exception {
        UserOpenApiParams params = new UserOpenApiParams();
        params.setUserid("4BC5721FAA8C1913538A268E944F9EE9@qq.sohu.com");
        params.setOpenid("4BC5721FAA8C1913538A268E944F9EE9@qq.sohu.com");
        params.setClient_id(1120);
        Result result = sgUserOpenApiManager.getUserInfo(params);
        System.out.println("result data:" + result);
    }

    /**
     * 直接调用第三方OpenAPI
     *
     * @throws Exception
     */
    @Test
    public void testSGGetUserInfo() throws Exception {
        BaseOpenApiParams baseOpenApiParams = new BaseOpenApiParams();
        baseOpenApiParams.setOpenid("E4AB85CD9373A582582F05342BB36D2F@qq.sohu.com");
        baseOpenApiParams.setUserid("E4AB85CD9373A582582F05342BB36D2F@qq.sohu.com");
        Result openResult = proxyConnectApiManager.obtainConnectToken(baseOpenApiParams, SHPPUrlConstant.APP_ID, SHPPUrlConstant.APP_KEY);
        if (openResult.isSuccess()) {
            //获取用户的openId/openKey
            Map<String, String> accessTokenMap = (Map<String, String>) openResult.getModels().get("result");
            String openId = accessTokenMap.get("open_id").toString();
            String accessToken = accessTokenMap.get("access_token").toString();
            int provider = 3;
            ConnectConfig connectConfig = configureManager.obtainConnectConfig(SHPPUrlConstant.APP_ID, provider);
            if (connectConfig != null) {
                String url = "https://graph.qq.com/user/get_user_info";
                OAuthClientRequest request = QQUserAPIRequest.apiLocation(url, QQUserAPIRequest.QQUserAPIBuilder.class).setOauth_Consumer_Key(connectConfig.getAppKey())
                        .setOpenid(openId).setAccessToken(accessToken).buildQueryMessage(QQUserAPIRequest.class);
                System.out.println("request url:" + request.getLocationUri());
                OAuthClientResponse response = OAuthHttpClient.execute(request, QQUserAPIResponse.class);
                System.out.println("response body:" + response.getBody());
            }
        }
    }

}
