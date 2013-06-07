package com.sogou.upd.passport.service.connect;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.response.accesstoken.OAuthAccessTokenResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-6-4
 * Time: 上午10:57
 * To change this template use File | Settings | File Templates.
 */
public class ConnectAuthorizeServiceTest extends BaseTest {

    private static final int clientId = 999;
    private static final int provider =  AccountTypeEnum.QQ.getValue();

    @Autowired
    private ConnectAuthorizeService connectAuthorizeService;

    @Test
    public void testObtainAccessTokenByCode() {
        String state = UUID.randomUUID().toString();
        String code = "3256211234615615151";
        try {
            OAuthAccessTokenResponse response = connectAuthorizeService.obtainAccessTokenByCode(clientId, provider, code, state);
            String body = response.getBody();
            System.out.println("body:" + body);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OAuthProblemException e) {
            e.printStackTrace();
        }

    }
}
