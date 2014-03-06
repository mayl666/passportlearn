package com.sogou.upd.passport.manager;

import com.google.common.collect.Lists;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.dao.connect.OpenTokenInfo;
import com.sogou.upd.passport.dao.connect.OpenTokenInfoDAO;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.model.OAuthConsumer;
import com.sogou.upd.passport.model.OAuthConsumerFactory;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import com.sogou.upd.passport.service.connect.ConnectAuthService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 14-2-24
 * Time: 下午8:32
 * To change this template use File | Settings | File Templates.
 */
public class TransferConnectTest extends BaseTest {

    @Autowired
    private OpenTokenInfoDAO openTokenInfoDAO;
    @Autowired
    private ConnectConfigService connectConfigService;
    @Autowired
    private ConnectAuthService connectAuthService;
    @Autowired
    private ConnectApiManager sgConnectApiManager;

    /**
     * 搜狐导出的open_token_info里refuserid不正确，写成了xxx@qq.sohu.com,需要订正
     */
    @Test
    public void modifyOpenTokenInfoErrorData() {
        try {
            List<String> dataList = Lists.newArrayList();
//            dataList.add("4BC5721FAA8C1913538A268E944F9EE9");
//            dataList.add("4ABAC654BC5997B717727BBA2A49C347");  // openid错误
            dataList.add("6C764A062D50C57B395B6DED7ECA6AE8");
            dataList.add("FCE6E963554E2F279B96D92FE40CEBEE");
            dataList.add("E1624B0B053479DB67B8FB021FE74744");
            for (String data : dataList) {
                int provider = 3;
                String appKey = "100294784";
                String openId = data;
                String providerStr = AccountTypeEnum.getProviderStr(provider);

                OpenTokenInfo openTokenInfo = openTokenInfoDAO.getOpenTokenInfo(openId, providerStr, appKey);
                String accessToken = openTokenInfo.getToken();
                OAuthTokenVO oAuthTokenVO = new OAuthTokenVO();
                oAuthTokenVO.setOpenid(openId);
                oAuthTokenVO.setAccessToken(accessToken);
                oAuthTokenVO.setExpiresIn(openTokenInfo.getExpireTime());
                oAuthTokenVO.setRefreshToken(openTokenInfo.getRefresh());

                ConnectConfig connectConfig = connectConfigService.queryConnectConfig(1120, provider);
                OAuthConsumer oAuthConsumer = OAuthConsumerFactory.getOAuthConsumer(provider);
                ConnectUserInfoVO connectUserInfoVO = connectAuthService.obtainConnectUserInfo(provider, connectConfig, openId, accessToken, oAuthConsumer);
                oAuthTokenVO.setConnectUserInfoVO(connectUserInfoVO);

                Result result = sgConnectApiManager.buildConnectAccount(appKey, provider, oAuthTokenVO);
                if (result.isSuccess()) {
                    System.out.println("build Connect Account fail!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OAuthProblemException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

}
