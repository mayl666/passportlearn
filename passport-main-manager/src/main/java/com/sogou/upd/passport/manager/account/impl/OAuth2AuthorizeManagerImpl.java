package com.sogou.upd.passport.manager.account.impl;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.result.OAuthResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.OAuth2AuthorizeManager;
import com.sogou.upd.passport.manager.account.PCAccountManager;
import com.sogou.upd.passport.manager.account.vo.OAuth2TokenVO;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.oauth2.authzserver.request.OAuthTokenASRequest;
import com.sogou.upd.passport.oauth2.common.types.GrantTypeEnum;
import com.sogou.upd.passport.service.account.PCAccountTokenService;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * OAuth2 授权Manager
 * User: shipengzhi
 * Date: 13-9-9
 */
@Component
public class OAuth2AuthorizeManagerImpl implements OAuth2AuthorizeManager {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthorizeManagerImpl.class);

    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private PCAccountTokenService pcAccountTokenService;
    @Autowired
    private PCAccountManager pcAccountManager;

    @Override
    public Result oauth2Authorize(OAuthTokenASRequest oauthRequest) {
        Result result = new OAuthResultSupport(false);
        int clientId = oauthRequest.getClientId();
        String instanceId = oauthRequest.getInstanceId();

        try {
            clientId = clientId == 30000004 ? CommonConstant.PC_CLIENTID : clientId;  //兼容浏览器PC端sohu+接口
            AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
            if (appConfig == null) {
                result.setCode(ErrorUtil.INVALID_CLIENT);
                return result;
            }

            String passportId = oauthRequest.getUsername();
            String grantType = oauthRequest.getGrantType();
            AccountToken renewAccountToken;
            if (GrantTypeEnum.HEART_BEAT.toString().equals(grantType)) {
                String refreshToken = oauthRequest.getRefreshToken();
                boolean isRightPcRToken = pcAccountManager.verifyRefreshToken(passportId, clientId, instanceId, refreshToken);
                if (!isRightPcRToken) {
                    result.setCode(ErrorUtil.INVALID_REFRESH_TOKEN);
                    return result;
                }
                renewAccountToken = pcAccountTokenService.updateAccountToken(passportId, instanceId, appConfig);
            } else {
                result.setCode(ErrorUtil.UNSUPPORTED_GRANT_TYPE);
                return result;
            }

            if (renewAccountToken != null) { // 登录成功
                OAuth2TokenVO oAuth2TokenVO = new OAuth2TokenVO();
                oAuth2TokenVO.setAccess_token(renewAccountToken.getAccessToken());
                oAuth2TokenVO.setExpires_time(renewAccountToken.getAccessValidTime());
                oAuth2TokenVO.setRefresh_token(renewAccountToken.getRefreshToken());
                oAuth2TokenVO.setSid(passportId);
                result.setSuccess(true);
                result.setDefaultModel(oAuth2TokenVO);
                return result;
            } else { // 登录失败，更新AccountToken表发生异常
                result.setCode(ErrorUtil.AUTHORIZE_FAIL);
                return result;
            }
        } catch (Exception e) {
            logger.error("OAuth Authorize Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

}


