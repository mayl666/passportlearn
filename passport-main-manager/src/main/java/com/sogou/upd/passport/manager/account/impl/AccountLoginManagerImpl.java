package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.AccountLoginManager;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.oauth2.authzserver.request.OAuthTokenASRequest;
import com.sogou.upd.passport.oauth2.common.types.GrantTypeEnum;
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
    public Result authorize(OAuthTokenASRequest oauthRequest) {
        int clientId = oauthRequest.getClientId();
        String instanceId = oauthRequest.getInstanceId();

        try {
            // 檢查不同的grant types是否正確
            // TODO 消除if-else
            AccountToken renewAccountToken;
            if (GrantTypeEnum.PASSWORD.toString().equals(oauthRequest.getGrantType())) {
                String passportId = mobilePassportMappingService.queryPassportIdByUsername(oauthRequest.getUsername());
                if (Strings.isNullOrEmpty(passportId)) {
                    return Result.buildError(ErrorUtil.INVALID_ACCOUNT);
                }
                Account account = accountService
                        .verifyUserPwdVaild(passportId, oauthRequest.getPassword());
                if (account == null) {
                    return Result.buildError(ErrorUtil.USERNAME_PWD_MISMATCH);
                } else if (!account.isNormalAccount()) {
                    return Result.buildError(ErrorUtil.INVALID_ACCOUNT);
                } else {
                    // 为了安全每次登录生成新的token
                    renewAccountToken = accountTokenService.updateAccountToken(account.getPassportId(), clientId, instanceId);
                }
            } else if (GrantTypeEnum.REFRESH_TOKEN.toString().equals(oauthRequest.getGrantType())) {
                String refreshToken = oauthRequest.getRefreshToken();
                AccountToken accountToken = accountTokenService.verifyRefreshToken(refreshToken, instanceId);
                if (accountToken == null) {
                    return Result.buildError(ErrorUtil.INVALID_REFRESH_TOKEN);
                } else {
                    String passportId = accountToken.getPassportId();
                    renewAccountToken = accountTokenService.updateAccountToken(passportId, clientId, instanceId);
                }
            } else {
                return Result.buildError(ErrorUtil.UNSUPPORTED_GRANT_TYPE);
            }

            if (renewAccountToken != null) { // 登录成功
                Map<String, Object> mapResult = Maps.newHashMap();
                mapResult.put("access_token", renewAccountToken.getAccessToken());
                mapResult.put("expires_time", renewAccountToken.getAccessValidTime());
                mapResult.put("refresh_token", renewAccountToken.getRefreshToken());
                return Result.buildSuccess("success", "mapResult", mapResult);
            } else { // 登录失败，更新AccountToken表发生异常
                return Result.buildError(ErrorUtil.AUTHORIZE_FAIL);
            }
        } catch (ServiceException e) {
            logger.error("OAuth Authorize Fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }

}
