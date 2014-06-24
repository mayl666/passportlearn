package com.sogou.upd.passport.service.connect;

import com.google.common.base.Strings;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.OAuthConsumer;
import com.sogou.upd.passport.model.OAuthConsumerFactory;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.response.accesstoken.OAuthAccessTokenResponse;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import org.codehaus.jackson.map.ObjectMapper;
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
//@Ignore
public class ConnectAuthorizeServiceTest extends BaseTest {

    private static final int clientId = 1120;
    private static final int provider = AccountTypeEnum.QQ.getValue();
    private static final int provider_renren = AccountTypeEnum.RENREN.getValue();

    @Autowired
    private ConnectAuthService connectAuthorizeService;
    @Autowired
    private ConnectConfigService connectConfigService;
    @Autowired
    private RedisUtils dbRedisUtils;

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
        String refreshToken_qq = "33B7D25DA4F5FCD9F5DB7B4EE9136E67";
//        String refreshToken_renren = "209417|0.zNgdF8EEhp2MlUx9r48zLZuwZWEHIx7g.225106022.1383223561658";
        ConnectConfig connectConfig = connectConfigService.queryConnectConfig(clientId, provider);
        try {
            OAuthTokenVO oAuthTokenVO = connectAuthorizeService.refreshAccessToken(refreshToken_qq, connectConfig);
            System.out.println("--------------------结果如下-------------------");
            System.out.println("accessToken:" + oAuthTokenVO.getAccessToken() + "refreshToken:" + oAuthTokenVO.getRefreshToken());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OAuthProblemException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testObtainCachedConnectUserInfo() {
        String passportId = "4727D0820AEFF3CF8D480AEA69412423@qq.sohu.com";
        try {
            String cacheKey = "SP.PASSPORTID:CONNECTUSERINFO_4727D0820AEFF3CF8D480AEA69412423@qq.sohu.com";
            String str = "{\\\"gender\\\":1,\\\"province\\\":null,\\\"city\\\":null,\\\"original\\\":null,\\\"imageURL\\\":\\\"http://q1.qlogo.cn/g?b=qq&k=RWcbIMvza5WjcJg25Tjknw&s=100&t=1391957076\\\",\\\"nickname\\\":\\\"\\xe7\\x9c\\x9f\\xe6\\xad\\xa3\\xe7\\x9a\\x84\\xe6\\x9c\\x8b\\xe5\\x8f\\x8b\\xe6\\x98\\xaf\\xe8\\xb0\\x81.\\\",\\\"userDesc\\\":null,\\\"region\\\":null}";
             ObjectMapper jsonMapper = JacksonJsonMapperUtil.getMapper();
            if (!Strings.isNullOrEmpty(str)) {
                try {
                    ConnectUserInfoVO object = (ConnectUserInfoVO) jsonMapper.readValue(str,  ConnectUserInfoVO.class);
                    System.out.println("body:" + object);
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            ConnectUserInfoVO connectUserInfoVO = dbRedisUtils.getObject(cacheKey, ConnectUserInfoVO.class);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }
}
