package com.sogou.upd.passport.manager;

import com.sogou.upd.passport.model.account.AccountInfo;
import com.sogou.upd.passport.oauth2.openresource.response.accesstoken.OAuthAccessTokenResponse;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import com.sogou.upd.passport.service.account.AccountInfoService;
import com.sogou.upd.passport.service.connect.ConnectAuthService;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * Created with IntelliJ IDEA.
 * User: mayan
 * Date: 13-12-11
 * Time: 上午11:46
 * To change this template use File | Settings | File Templates.
 */
@Ignore
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class QQConnectTest extends AbstractJUnit4SpringContextTests {
    @Autowired
    private ConnectAuthService connectAuthService;

   /**
     * 测试查询是否成功
     */
//    @Test
//    public void testQueryAccountInfoByPassportId() {
//        OAuthAccessTokenResponse oauthResponse = connectAuthService.obtainAccessTokenByCode(provider, code, connectConfig,
//                oAuthConsumer, redirectUrl);
//        OAuthTokenVO oAuthTokenVO = oauthResponse.getOAuthTokenVO();
//    }


}
