package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.PasswordTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.OAuthResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.OAuth2AuthorizeManager;
import com.sogou.upd.passport.manager.account.PCAccountManager;
import com.sogou.upd.passport.manager.account.vo.OAuth2TokenVO;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.oauth2.authzserver.request.OAuthTokenASRequest;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.types.GrantTypeEnum;
import com.sogou.upd.passport.service.SHPlusConstant;
import com.sogou.upd.passport.service.account.*;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * OAuth2 授权Manager
 * User: shipengzhi
 * Date: 13-9-9
 */
@Component
public class OAuth2AuthorizeManagerImpl implements OAuth2AuthorizeManager {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthorizeManagerImpl.class);
    private static final Logger shPlusTokenLog = LoggerFactory.getLogger("shPlusTokenLogger");

    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountTokenService accountTokenService;
    @Autowired
    private MobilePassportMappingService mobilePassportMappingService;
    @Autowired
    private SHPlusTokenService shPlusTokenService;
    @Autowired
    private PCAccountTokenService pcAccountTokenService;
    @Autowired
    private PCAccountManager pcAccountManager;

    @Override
    public Result authorize(OAuthTokenASRequest oauthRequest) {
        Result result = new APIResultSupport(false);
        int clientId = oauthRequest.getClientId();
        String instanceId = oauthRequest.getInstanceId();

        try {
            // 檢查不同的grant types是否正確
            // TODO 消除if-else
            AccountToken renewAccountToken;
            if (GrantTypeEnum.PASSWORD.toString().equals(oauthRequest.getGrantType())) {
                String passportId = mobilePassportMappingService.queryPassportIdByUsername(oauthRequest.getUsername());
                if (Strings.isNullOrEmpty(passportId)) {
                    result.setCode(ErrorUtil.INVALID_ACCOUNT);
                    return result;
                }
                int pwdType = oauthRequest.getPwdType();
                boolean needMD5 = pwdType == PasswordTypeEnum.Plaintext.getValue() ? true : false;
                result = accountService
                        .verifyUserPwdVaild(passportId, oauthRequest.getPassword(), needMD5);
                if (!result.isSuccess()) {
                    return result;
                } else {
                    Account account = (Account) result.getDefaultModel();
                    result.setDefaultModel(null);
                    // 为了安全每次登录生成新的token
                    renewAccountToken = accountTokenService.updateOrInsertAccountToken(account.getPassportId(), clientId, instanceId);
                }
            } else if (GrantTypeEnum.REFRESH_TOKEN.toString().equals(oauthRequest.getGrantType())) {
                String refreshToken = oauthRequest.getRefreshToken();
                AccountToken accountToken = accountTokenService.verifyRefreshToken(refreshToken, clientId, instanceId);
                if (accountToken == null) {
                    result.setCode(ErrorUtil.INVALID_REFRESH_TOKEN);
                    return result;
                } else {
                    String passportId = accountToken.getPassportId();
                    renewAccountToken = accountTokenService.updateOrInsertAccountToken(passportId, clientId, instanceId);
                }
            } else {
                result.setCode(ErrorUtil.UNSUPPORTED_GRANT_TYPE);
                return result;
            }

            if (renewAccountToken != null) { // 登录成功
                Map<String, Object> mapResult = Maps.newHashMap();
                mapResult.put(OAuth.OAUTH_ACCESS_TOKEN, renewAccountToken.getAccessToken());
                mapResult.put(OAuth.OAUTH_EXPIRES_TIME, renewAccountToken.getAccessValidTime());
                mapResult.put(OAuth.OAUTH_REFRESH_TOKEN, renewAccountToken.getRefreshToken());
                result.setSuccess(true);
                result.setModels(mapResult);
                return result;
            } else { // 登录失败，更新AccountToken表发生异常
                result.setCode(ErrorUtil.AUTHORIZE_FAIL);
                return result;
            }
        } catch (ServiceException e) {
            logger.error("OAuth Authorize Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

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
                String sid = oauthRequest.getSid();
                boolean isRightPcRToken = isRightPcRToken(passportId, clientId, instanceId, refreshToken, sid);
                if (!isRightPcRToken) {
                    result.setCode(ErrorUtil.INVALID_REFRESH_TOKEN);
                    return result;
                }
                //如果是老的token或者SG_开头的token，则返回新的token
                if (refreshToken.startsWith(CommonConstant.SG_TOKEN_START) && !refreshToken.startsWith(CommonConstant.SG_TOKEN_OLD_START)) {
                    renewAccountToken = pcAccountTokenService.updateAccountToken(passportId, instanceId, appConfig);
                } else {
                    //非4.2版本的sogou token要重新生成token
                    renewAccountToken = pcAccountTokenService.initialAccountToken(passportId, instanceId, appConfig);
                }
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

    private boolean isRightPcRToken(String passportId, int clientId, String instanceId, String refreshToken, String sid) throws Exception {
        if (refreshToken.length() == SHPlusConstant.SHPl_TOKEN_LEN) {
            String accessToken = shPlusTokenService.queryATokenByRToken(passportId, instanceId, refreshToken, sid);
            if (accessToken != null) {
                // 记录log，等以后不再验证sohuplus的token了去掉这段逻辑
                shPlusTokenLog.info("[SHPlusToken] verify shplus refreshtoken，refreshtoken：" + refreshToken);
                return true;
            }
            return false;
        } else {
            boolean isRightPcRToken = pcAccountManager.verifyRefreshToken(passportId, clientId, instanceId, refreshToken);
            return isRightPcRToken;
        }
    }
}


