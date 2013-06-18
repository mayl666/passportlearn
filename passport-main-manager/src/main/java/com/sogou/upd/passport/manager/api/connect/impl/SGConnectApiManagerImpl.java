package com.sogou.upd.passport.manager.api.connect.impl;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
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
            connectConfig = connectConfigService.querySpecifyConnectConfig(clientId, provider);
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
        String requestUrl;
        // web应用采用Authorization Code Flow流程，sina的web应用和客户端均采用此流程
        if (ConnectTypeEnum.WEB.toString().equals(connectType) || AccountTypeEnum.SINA.getValue() == provider) {
            requestUrl = oAuthConsumer.getWebUserAuthzUrl();
            request = OAuthAuthzClientRequest
                    .authorizationLocation(requestUrl).setAppKey(appKey)
                    .setRedirectURI(redirectURI)
                    .setResponseType(ResponseTypeEnum.CODE).setScope(scope)
                    .setDisplay(connectLoginParams.getDisplay(), provider).setForceLogin(connectLoginParams.isForcelogin(), provider)
                    .setState(uuid)
                    .buildQueryMessage(OAuthAuthzClientRequest.class);
        } else {  // 客户端应用采用Implicit Flow
            requestUrl = oAuthConsumer.getAppUserAuthzUrl();
            request = OAuthAuthzClientRequest
                    .authorizationLocation(requestUrl).setAppKey(appKey)
                    .setRedirectURI(redirectURI)
                    .setResponseType(ResponseTypeEnum.TOKEN).setScope(scope)
                    .setDisplay(connectLoginParams.getDisplay(), provider).setForceLogin(connectLoginParams.isForcelogin(), provider)
                    .setState(uuid)
                    .buildQueryMessage(OAuthAuthzClientRequest.class);
        }

        return request.getLocationUri();
    }

    private String constructRedirectURI(ConnectLoginParams oauthLoginParams, String pCallbackUrl, String ip) {
        try {
            String ru = oauthLoginParams.getRu();
            ru = URLEncoder.encode(ru, CommonConstant.DEFAULT_CONTENT_CHARSET);
            Map<String, Object> callbackParams = Maps.newHashMap();
            callbackParams.put("client_id", oauthLoginParams.getClient_id());
            callbackParams.put("ru", ru);
            callbackParams.put("ip", ip);
            StringBuffer query = new StringBuffer(OAuthUtils.format(callbackParams.entrySet(), CommonConstant.DEFAULT_CONTENT_CHARSET));
            String redirectURI = pCallbackUrl + query;
            redirectURI = URLEncoder.encode(redirectURI, CommonConstant.DEFAULT_CONTENT_CHARSET);
            return redirectURI;
        } catch (UnsupportedEncodingException e) {
            return CommonConstant.DEFAULT_CONNECT_REDIRECT_URL;
        }
    }
}
