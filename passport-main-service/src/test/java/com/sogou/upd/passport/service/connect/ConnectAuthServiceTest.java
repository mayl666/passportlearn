package com.sogou.upd.passport.service.connect;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.model.OAuthConsumer;
import com.sogou.upd.passport.model.OAuthConsumerFactory;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-3-3
 * Time: 下午3:01
 * To change this template use File | Settings | File Templates.
 */
//@Ignore
public class ConnectAuthServiceTest extends BaseTest {
    @Autowired
    private ConnectAuthService connectAuthService;
    @Autowired
    private ConnectConfigService connectConfigService;
    @Autowired
    private ConnectTokenService connectTokenService;

//
//    @Test
//    public void testObtainConnectUserInfoFromSogou() {
//        int provider = AccountTypeEnum.getAccountType(passportId).getValue();
//        ConnectUserInfoVO connectUserInfoVO = connectAuthService.obtainConnectUserInfo(passportId, provider, CommonConstant.APP_CONNECT_KEY);
//        if (connectUserInfoVO != null) {
//            System.out.println("----------------------结果如下-----------------");
//            System.out.println(connectUserInfoVO.getNickname());
//            System.out.println(connectUserInfoVO.getAvatarSmall());
//            System.out.println(connectUserInfoVO.getAvatarMiddle());
//            System.out.println(connectUserInfoVO.getAvatarLarge());
//            System.out.println(connectUserInfoVO.getGender());
//        } else {
//            System.out.println("从搜狗获取用户信息失败");
//        }
//
//
//    }

    @Test
    public void testObtainConnectUserInfo() {
        int client_id = 1120;
        String passportId = "F82CF26224957BB2DB75DCC2D49A67EF@qq.sohu.com";
        int provider = AccountTypeEnum.getAccountType(passportId).getValue();
        try {
            String appKey = connectConfigService.querySpecifyAppKey(client_id, provider);
            ConnectToken connectToken = connectTokenService.queryConnectToken(passportId, provider, appKey);
            String openId = connectToken.getOpenid();
            String accessToken = connectToken.getAccessToken();
            OAuthConsumer oAuthConsumer = OAuthConsumerFactory.getOAuthConsumer(provider);
            ConnectConfig connectConfig = connectConfigService.queryDefaultConnectConfig(provider);
            ConnectUserInfoVO connectUserInfoVO = connectAuthService.obtainConnectUserInfo(provider, connectConfig, openId, accessToken, oAuthConsumer);
            System.out.println(connectUserInfoVO.toString());
        } catch (OAuthProblemException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
