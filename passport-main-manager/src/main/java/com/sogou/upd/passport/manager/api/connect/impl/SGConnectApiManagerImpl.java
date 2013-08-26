package com.sogou.upd.passport.manager.api.connect.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.form.connect.ConnectLoginParams;
import com.sogou.upd.passport.model.OAuthConsumer;
import com.sogou.upd.passport.model.OAuthConsumerFactory;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.common.types.ConnectTypeEnum;
import com.sogou.upd.passport.oauth2.common.types.ResponseTypeEnum;
import com.sogou.upd.passport.oauth2.common.utils.OAuthUtils;
import com.sogou.upd.passport.oauth2.openresource.request.OAuthAuthzClientRequest;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

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

    @Override
    public String buildConnectLoginURL(ConnectLoginParams connectLoginParams, String uuid, int provider, String ip) throws OAuthProblemException {
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
        } catch (IOException e) {
            logger.error("read oauth consumer IOException!", e);
            throw new OAuthProblemException(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        } catch (ServiceException se) {
            logger.error("query connect config Exception!", se);
            throw new OAuthProblemException(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }

        String redirectURI = constructRedirectURI(connectLoginParams, oAuthConsumer.getCallbackUrl(), ip);
        String scope = connectConfig.getScope();
        String appKey = connectConfig.getAppKey();
        String connectType = connectLoginParams.getType();
        // 重新填充display，如果display为空，根据终端自动赋值；如果display不为空，则使用display
        String display = connectLoginParams.getDisplay();
        display = Strings.isNullOrEmpty(display) ? fillDisplay(connectType, provider) : display;

        String requestUrl;
        // 采用Authorization Code Flow流程
        requestUrl = oAuthConsumer.getWebUserAuthzUrl();
        request = OAuthAuthzClientRequest
                .authorizationLocation(requestUrl).setAppKey(appKey)
                .setRedirectURI(redirectURI)
                .setResponseType(ResponseTypeEnum.CODE).setScope(scope)
                .setDisplay(display, provider).setForceLogin(connectLoginParams.isForcelogin(), provider)
                .setState(uuid)
                .buildQueryMessage(OAuthAuthzClientRequest.class);

        return request.getLocationUri();
    }

    private String constructRedirectURI(ConnectLoginParams oauthLoginParams, String pCallbackUrl, String ip) {
        try {
            String ru = oauthLoginParams.getRu();
            ru = URLEncoder.encode(ru, CommonConstant.DEFAULT_CONTENT_CHARSET);
            Map<String, Object> callbackParams = Maps.newHashMap();
            callbackParams.put("client_id", oauthLoginParams.getClient_id());
            callbackParams.put("ru", ru);
            callbackParams.put("type", oauthLoginParams.getType());
            callbackParams.put("ip", ip);
            StringBuffer query = new StringBuffer(OAuthUtils.format(callbackParams.entrySet(), CommonConstant.DEFAULT_CONTENT_CHARSET));
            return pCallbackUrl + "?" + query;
        } catch (UnsupportedEncodingException e) {
            return CommonConstant.DEFAULT_CONNECT_REDIRECT_URL;
        }
    }

    /*
     * 根据type和provider重新填充display
     */
    private String fillDisplay(String type, int provider) {
        String display = "";
        if (ConnectTypeEnum.isMobileApp(type)) {
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

}
