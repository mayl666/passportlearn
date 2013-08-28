package com.sogou.upd.passport.service.connect;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.response.accesstoken.OAuthAccessTokenResponse;
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
    private static final int provider = AccountTypeEnum.SINA.getValue();

    @Autowired
    private ConnectAuthService connectAuthorizeService;
    @Autowired
    private ConnectConfigService connectConfigService;

    @Test
    public void testObtainAccessTokenByCode() {
        String code = "3256211234615615151";
        ConnectConfig connectConfig = connectConfigService.queryConnectConfig(clientId, provider);
        try {
            OAuthAccessTokenResponse response = connectAuthorizeService.obtainAccessTokenByCode(provider, code, connectConfig);
            String body = response.getBody();
            System.out.println("body:" + body);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OAuthProblemException e) {
            e.printStackTrace();
        }

    }
}
