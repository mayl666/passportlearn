package com.sogou.upd.passport.manager.api.connect.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.ConnectManagerHelper;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import com.sogou.upd.passport.manager.form.connect.ConnectLoginParams;
import com.sogou.upd.passport.model.OAuthConsumer;
import com.sogou.upd.passport.model.OAuthConsumerFactory;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.common.types.ConnectRequest;
import com.sogou.upd.passport.oauth2.common.types.ConnectTypeEnum;
import com.sogou.upd.passport.oauth2.common.types.ResponseTypeEnum;
import com.sogou.upd.passport.oauth2.openresource.parameters.QQOAuth;
import com.sogou.upd.passport.oauth2.openresource.request.OAuthAuthzClientRequest;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import com.sogou.upd.passport.service.connect.ConnectTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-6-18
 * Time: 上午12:22
 * To change this template use File | Settings | File Templates.
 */
@Component("sgConnectApiManager")
public class SGConnectApiManagerImpl implements ConnectApiManager {

    private static Logger logger = LoggerFactory.getLogger(SGConnectApiManagerImpl.class);

    @Autowired
    private ConnectConfigService connectConfigService;
    @Autowired
    private ConnectTokenService connectTokenService;

    @Override
    public String buildConnectLoginURL(ConnectLoginParams connectLoginParams, String uuid, int provider, String ip, String httpOrHttps) throws OAuthProblemException {
        OAuthConsumer oAuthConsumer;
        OAuthAuthzClientRequest request;
        ConnectConfig connectConfig;
        try {
            int clientId = Integer.parseInt(connectLoginParams.getClient_id());
            oAuthConsumer = OAuthConsumerFactory.getOAuthConsumer(provider);
            // 获取connect配置
            connectConfig = connectConfigService.queryConnectConfig(clientId, provider);
            if (connectConfig == null) {
                return CommonConstant.DEFAULT_CONNECT_REDIRECT_URL;
            }

            String redirectURI = ConnectManagerHelper.constructRedirectURI(clientId, connectLoginParams.getRu(), connectLoginParams.getType(),
                    connectLoginParams.getTs(), oAuthConsumer.getCallbackUrl(httpOrHttps), ip, connectLoginParams.getFrom(), connectLoginParams.getDomain());
            String scope = connectConfig.getScope();
            String appKey = connectConfig.getAppKey();
            String connectType = connectLoginParams.getType();
            // 重新填充display，如果display为空，根据终端自动赋值；如果display不为空，则使用display
            String display = connectLoginParams.getDisplay();
            display = Strings.isNullOrEmpty(display) ? fillDisplay(connectType, connectLoginParams.getFrom(), provider) : display;

            String requestUrl;
            // 采用Authorization Code Flow流程
            //若provider=QQ && display=wml、xhtml调用WAP接口
            if (ConnectRequest.isQQWapRequest(connectLoginParams.getProvider(), display)) {
                requestUrl = oAuthConsumer.getWapUserAuthzUrl();
            } else {
                requestUrl = oAuthConsumer.getWebUserAuthzUrl();
            }
            OAuthAuthzClientRequest.AuthenticationRequestBuilder builder = OAuthAuthzClientRequest
                    .authorizationLocation(requestUrl).setAppKey(appKey)
                    .setRedirectURI(redirectURI)
                    .setResponseType(ResponseTypeEnum.CODE).setScope(scope)
                    .setDisplay(display, provider).setForceLogin(connectLoginParams.isForcelogin(), provider)
                    .setState(uuid);
            if (AccountTypeEnum.QQ.getValue() == provider) {
                builder.setShowAuthItems(QQOAuth.NO_AUTH_ITEMS);       // qq为搜狗产品定制化页面，隐藏授权信息
                if (!Strings.isNullOrEmpty(connectLoginParams.getViewPage())) {
                    builder.setViewPage(connectLoginParams.getViewPage());       // qq为搜狗产品定制化页面--输入法使用
                }
            }
            request = builder.buildQueryMessage(OAuthAuthzClientRequest.class);
        } catch (IOException e) {
            logger.error("read oauth consumer IOException!", e);
            throw new OAuthProblemException(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "read oauth consumer IOException!");
        } catch (ServiceException se) {
            logger.error("query connect config Exception!", se);
            throw new OAuthProblemException(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "query connect config Exception!");
        }

        return request.getLocationUri();
    }

    @Override
    public Result buildConnectAccount(int clientId, String providerStr, OAuthTokenVO oAuthTokenVO) {
        //To change body of implemented methods use File | Settings | File Templates.
        return null;
    }

    @Override
    public Result obtainConnectToken(BaseOpenApiParams baseOpenApiParams, int clientId, String clientKey) throws ServiceException {
        Result result = new APIResultSupport(false);
        try {
            int provider = AccountTypeEnum.getAccountType(baseOpenApiParams.getUserid()).getValue();
            ConnectConfig connectConfig = connectConfigService.queryConnectConfig(clientId, provider);
            ConnectToken connectToken = null;
            if (connectConfig != null) {
                connectToken = connectTokenService.queryConnectToken(baseOpenApiParams.getUserid(), provider, connectConfig.getAppKey());
                if (connectToken != null) {
                    result.setSuccess(true);
                }
            }
            result.setDefaultModel("connectToken", connectToken);
        } catch (Exception e) {
            logger.error("method[obtainConnectToken] obtain connect token error.{}", e);
            throw new ServiceException(e);
        }
        return result;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /*
     * 根据type和provider重新填充display
     */
    private String fillDisplay(String type, String from, int provider) {
        String display = "";
        if (ConnectTypeEnum.isMobileApp(type) || isMobileDisplay(type, from)) {
            switch (provider) {
                case 5:  // 人人
                    display = "touch";
                    break;
                case 6:  // 淘宝
                    display = "wap";
                    break;
                default:
                    display = "mobile";
                    break;
            }
        }
        return display;
    }

    private boolean isMobileDisplay(String type, String from) {
        return type.equals(ConnectTypeEnum.TOKEN.toString()) && "mob".equalsIgnoreCase(from)
                || type.equals(ConnectTypeEnum.MOBILE.toString());
    }

}
