package com.sogou.upd.passport.service.connect;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.model.OAuthConsumer;
import com.sogou.upd.passport.model.OAuthConsumerFactory;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.response.accesstoken.OAuthAccessTokenResponse;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-6-4
 * Time: 上午10:57
 * To change this template use File | Settings | File Templates.
 */
public class ConnectAuthorizeServiceTest extends BaseTest {

    private static final int clientId = 1120;
    private static final int provider = AccountTypeEnum.QQ.getValue();
    private static final int provider_sina = AccountTypeEnum.SINA.getValue();

    @Autowired
    private ConnectAuthService connectAuthorizeService;
    @Autowired
    private ConnectConfigService connectConfigService;

    @Test
    public void testObtainAccessTokenByCode() {
        String code = "627A7E46B28835F183C656909A6AF5B4";

        ConnectConfig connectConfig = connectConfigService.queryConnectConfig(clientId, provider);
        try {
            OAuthConsumer oAuthConsumer = OAuthConsumerFactory.getOAuthConsumer(provider);
            String ru = "https://account.sogou.com";
            String accessToken = oAuthConsumer.getAccessTokenUrl();
            OAuthAccessTokenResponse response = connectAuthorizeService.obtainAccessTokenByCode(provider, code, connectConfig, oAuthConsumer, ru);
            String body = response.getBody();
            System.out.println("body:" + body);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OAuthProblemException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testRefreshAccessToken() {
        String refreshToken = "33B7D25DA4F5FCD9F5DB7B4EE9136E67";
        ConnectConfig connectConfig = connectConfigService.queryConnectConfig(clientId, provider);
        try {
            OAuthTokenVO oAuthTokenVO = connectAuthorizeService.refreshAccessToken(refreshToken, connectConfig);
            System.out.println("--------------------结果如下-------------------");
            System.out.println(oAuthTokenVO);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OAuthProblemException e) {
            e.printStackTrace();
        }

    }
}
