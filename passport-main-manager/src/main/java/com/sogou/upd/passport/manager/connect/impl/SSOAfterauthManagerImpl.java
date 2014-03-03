package com.sogou.upd.passport.manager.connect.impl;

import com.sogou.upd.passport.common.HttpConstant;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.AccountInfoManager;
import com.sogou.upd.passport.manager.account.OAuth2ResourceManager;
import com.sogou.upd.passport.manager.account.PCAccountManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.ConnectManagerHelper;
import com.sogou.upd.passport.manager.api.connect.SessionServerManager;
import com.sogou.upd.passport.manager.api.connect.UserOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.form.user.UserOpenApiParams;
import com.sogou.upd.passport.manager.connect.SSOAfterauthManager;
import com.sogou.upd.passport.manager.form.ObtainAccountInfoParams;
import com.sogou.upd.passport.model.OAuthConsumer;
import com.sogou.upd.passport.model.OAuthConsumerFactory;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.http.OAuthHttpClient;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthAuthzClientResponse;
import com.sogou.upd.passport.oauth2.openresource.response.accesstoken.OAuthAccessTokenResponse;
import com.sogou.upd.passport.oauth2.openresource.response.accesstoken.QQJSONAccessTokenResponse;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import com.sogou.upd.passport.service.account.AccountBaseInfoService;
import com.sogou.upd.passport.service.account.MappTokenService;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import com.sogou.upd.passport.service.connect.ConnectAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * User: mayan
 * Date: 14-3-3
 * Time: 下午5:21
 * To change this template use File | Settings | File Templates.
 */
@Component
public class SSOAfterauthManagerImpl implements SSOAfterauthManager{
    private static Logger logger = LoggerFactory.getLogger(SSOAfterauthManagerImpl.class);

    @Autowired
    private ConnectConfigService connectConfigService;
    @Autowired
    private AccountInfoManager accountInfoManager;
    @Autowired
    private UserOpenApiManager sgUserOpenApiManager;
    @Autowired
    private ConnectApiManager sgConnectApiManager;

    @Override
    public Result handleSSOAfterauth(HttpServletRequest req,String providerStr) {
        Result result = new APIResultSupport(false);

        try {
            String openId = req.getParameter("openid");
            String accessToken = req.getParameter("access_token");
            long expires_in = Long.parseLong(req.getParameter("expires_in"));
            int client_id = Integer.parseInt(req.getParameter("client_id"));
            int isthird = Integer.parseInt(req.getParameter("isthird"));
            String instance_id = req.getParameter("instance_id");

            int provider = AccountTypeEnum.getProvider(providerStr);

            OAuthConsumer oAuthConsumer = OAuthConsumerFactory.getOAuthConsumer(provider);
            if (oAuthConsumer == null) {
                result.setCode(ErrorUtil.UNSUPPORT_THIRDPARTY);
                return result;
            }

            //根据code值获取access_token
            ConnectConfig connectConfig = connectConfigService.queryConnectConfig(client_id, provider);
            if (connectConfig == null) {
                result.setCode(ErrorUtil.UNSUPPORT_THIRDPARTY);
                return result;
            }
            String passportId=openId+"@qq.sohu.com";
            //isthird=0或1；0表示去搜狗通行证个人信息，1表示获取第三方个人信息
            switch (isthird){
                case 0:  //获取搜狗个人信息
                    if (provider == AccountTypeEnum.QQ.getValue()) {
                        ObtainAccountInfoParams params=new ObtainAccountInfoParams();
                        params.setUsername(passportId);
                        params.setClient_id(String.valueOf(client_id));
                        params.setFields("uniqname");
                        result = accountInfoManager.getUserInfo(params);
                    }
                    break;
                case 1:  //获取第三方个人信息
                    if (provider == AccountTypeEnum.QQ.getValue()) {
                        UserOpenApiParams userOpenApiParams=new UserOpenApiParams();
                        userOpenApiParams.setClient_id(client_id);
                        userOpenApiParams.setUserid(passportId);
                        userOpenApiParams.setAccessToken(accessToken);
                        result=sgUserOpenApiManager.getUserInfo(userOpenApiParams);
                    }
                    break;
            }

//            ConnectUserInfoVO connectUserInfoVO = connectAuthService.obtainConnectUserInfo(provider, connectConfig, openId, accessToken, oAuthConsumer);
//            if (connectUserInfoVO == null) {
//                result.setCode(ErrorUtil.ERR_CODE_CONNECT_GET_USERINFO_ERROR);
//                return result;
//            }
            OAuthTokenVO oAuthTokenVO=new OAuthTokenVO();
//            oAuthTokenVO
            // 创建第三方账号
            Result connectAccountResult = sgConnectApiManager.buildConnectAccount(connectConfig.getAppKey(), provider, oAuthTokenVO);


        } catch (IOException e) {
            logger.error("read oauth consumer IOException!", e);
//            result = buildErrorResult(type, ru, ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "read oauth consumer IOException");
        } catch (ServiceException se) {
            logger.error("query connect config Exception!", se);
//            result = buildErrorResult(type, ru, ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "query connect config Exception");
       } //catch (OAuthProblemException ope) {
//            logger.error("handle oauth authroize code error!", ope);
////            result = buildErrorResult(type, ru, ope.getError(), ope.getDescription());
//        } catch (Exception exp) {
////            logger.error("handle oauth authroize code system error!", exp);
////            result = buildErrorResult(type, ru, ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "system error!");
//        }

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
