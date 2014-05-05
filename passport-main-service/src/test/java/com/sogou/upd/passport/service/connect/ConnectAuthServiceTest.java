package com.sogou.upd.passport.service.connect;

import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.dao.connect.ConnectTokenDAO;
import com.sogou.upd.passport.model.OAuthConsumer;
import com.sogou.upd.passport.model.OAuthConsumerFactory;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-3-3
 * Time: 下午3:01
 * To change this template use File | Settings | File Templates.
 */
@Ignore
public class ConnectAuthServiceTest extends BaseTest {
    @Autowired
    private ConnectAuthService connectAuthService;
    @Autowired
    private ConnectConfigService connectConfigService;
    @Autowired
    private ConnectTokenDAO connectTokenDAO;

    private final static String passportId = "0115BAD1B6B4E49A27824FCC2398A1BA@qq.sohu.com";

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
        int provider = 3;
        int client_id = 1120;
        String passportId = "02BB90BF300D0E08F5CE9F2F6B1C6B06@qq.sohu.com";
        String openId = "05D74F458E9BA0E25C48585CB6AD0488";
        String accessToken = "79DF2A5475E6CD6F77E6C91067E94679";
        try {
            OAuthConsumer oAuthConsumer = OAuthConsumerFactory.getOAuthConsumer(provider);
            ConnectConfig connectConfig = connectConfigService.queryConnectConfig(client_id, provider);
            ConnectUserInfoVO connectUserInfoVO = connectAuthService.obtainConnectUserInfo(provider, connectConfig, openId, accessToken, oAuthConsumer);
            System.out.println(11);
//            ConnectToken connectToken = new ConnectToken();
//            connectToken.setPassportId(passportId);
//            connectToken.setOpenid(openId);
//            connectToken.setProvider(provider);
//            connectToken.setAccessToken(accessToken);
//            connectToken.setExpiresIn(777600);
//            connectToken.setRefreshToken("456");
//            connectToken.setConnectUniqname(connectUserInfoVO.getNickname());
//            connectToken.setAvatarSmall(connectUserInfoVO.getAvatarSmall());
//            connectToken.setGender("1");
//            connectToken.setAppKey("100294784");
//            connectToken.setUpdateTime(new Date());
//            int row = connectTokenDAO.insertOrUpdateAccountConnect(passportId, connectToken);
//            Assert.assertTrue(row == 1);
        } catch (OAuthProblemException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void testCode() {
        int provider = 3;
        int client_id = 1120;
        String passportId = "02BB90BF300D0E08F5CE9F2F6B1C6B06@qq.sohu.com";
        String openId = "02BB90BF300D0E08F5CE9F2F6B1C6B06";
        String accessToken = "B2553A801D4197A05A6B93E950DAC6C6";
        String str="accessToken=B2553A801D4197A05A6B93E950DAC6C6&client_id=1120&expires_in=777600&instance_id=1&isthird=0&openid=02BB90BF300D0E08F5CE9F2F6B1C6B06&yRWHIkB$2.9Esk>7mBNIFEcr:8\\[Cv";
       try {
           String code = Coder.encryptMD5(str);
           System.out.println("result code:"+code);
       } catch (Exception ex){

       }
    }


}
