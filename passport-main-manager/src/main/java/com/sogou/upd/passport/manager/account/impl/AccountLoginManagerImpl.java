package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.exception.ServiceException;
import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.AccountLoginManager;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.oauth2.authzserver.request.OAuthTokenRequest;
import com.sogou.upd.passport.oauth2.common.OAuthError;
import com.sogou.upd.passport.oauth2.common.types.GrantType;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.AccountTokenService;
import com.sogou.upd.passport.service.account.MobilePassportMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * User: mayan Date: 13-4-15 Time: 下午4:34 To change this template use File | Settings | File Templates.
 */
@Component
public class AccountLoginManagerImpl implements AccountLoginManager {

    private static final Logger logger = LoggerFactory.getLogger(AccountLoginManagerImpl.class);

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountTokenService accountTokenService;
    @Autowired
    private MobilePassportMappingService mobilePassportMappingService;

    @Override
    public Result authorize(OAuthTokenRequest oauthRequest) throws SystemException {
        int clientId = oauthRequest.getClientId();
        String instanceId = oauthRequest.getInstanceId();

        try {
            // 檢查不同的grant types是否正確
            // TODO 消除if-else
            AccountToken renewAccountToken;
            if (GrantType.PASSWORD.toString().equals(oauthRequest.getGrantType())) {
                String passportId = mobilePassportMappingService.queryPassportIdByUsername(oauthRequest.getUsername());
                if (Strings.isNullOrEmpty(passportId)) {
                    return Result.buildError(ErrorUtil.ERR_CODE_COM_NOUSER);
                }
                Account account = accountService
                        .verifyUserPwdVaild(passportId, oauthRequest.getPassword());
                if (account == null) {
                    return Result.buildError(OAuthError.Response.USERNAME_PWD_MISMATCH, "username or password mismatch");
                } else if (!account.isNormalAccount()) {
                    return Result.buildError(OAuthError.Response.INVALID_USER, "user account invalid");
                } else {
                    // 为了安全每次登录生成新的token
                    renewAccountToken = accountTokenService.updateAccountToken(account.getPassportId(), clientId, instanceId);
                }
            } else if (GrantType.REFRESH_TOKEN.toString().equals(oauthRequest.getGrantType())) {
                String refreshToken = oauthRequest.getRefreshToken();
                AccountToken accountToken = accountTokenService.verifyRefreshToken(refreshToken, instanceId);
                if (accountToken == null) {
                    return Result.buildError(OAuthError.Response.INVALID_REFRESH_TOKEN, "refresh_token not exist or expired");
                } else {
                    String passportId = accountToken.getPassportId();
                    renewAccountToken = accountTokenService.updateAccountToken(passportId, clientId, instanceId);
                }
            } else {
                return Result.buildError(OAuthError.Response.UNSUPPORTED_GRANT_TYPE, "unsupported_grant_type");
            }

            if (renewAccountToken != null) { // 登录成功
                Map<String, Object> mapResult = Maps.newHashMap();
                mapResult.put("access_token", renewAccountToken.getAccessToken());
                mapResult.put("expires_time", renewAccountToken.getAccessValidTime());
                mapResult.put("refresh_token", renewAccountToken.getRefreshToken());
                return Result.buildSuccess("登录成功！", "mapResult", mapResult);
            } else { // 登录失败，更新AccountToken表发生异常
                return Result.buildError(OAuthError.Response.AUTHORIZE_FAIL, "login fail");
            }
        } catch (ServiceException e) {
            logger.error("OAuth Authorize Fail:", e);
            return Result.buildError(ErrorUtil.ERR_CODE_COM_EXCEPTION, "unknown error");
        }
    }

}
