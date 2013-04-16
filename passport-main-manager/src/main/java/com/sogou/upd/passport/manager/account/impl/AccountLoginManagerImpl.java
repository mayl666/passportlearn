package com.sogou.upd.passport.manager.account.impl;

import com.sogou.upd.passport.common.exception.ProblemException;
import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.AccountLoginManager;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountAuth;
import com.sogou.upd.passport.oauth2.authzserver.request.OAuthTokenRequest;
import com.sogou.upd.passport.oauth2.authzserver.response.OAuthASResponse;
import com.sogou.upd.passport.oauth2.common.OAuthError;
import com.sogou.upd.passport.oauth2.common.OAuthResponse;
import com.sogou.upd.passport.oauth2.common.types.GrantType;
import com.sogou.upd.passport.service.account.AccountAuthService;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.generator.TokenGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

/**
 * User: mayan
 * Date: 13-4-15
 * Time: 下午4:34
 * To change this template use File | Settings | File Templates.
 */
@Component
public class AccountLoginManagerImpl implements AccountLoginManager {
    private static final Logger logger = LoggerFactory.getLogger(AccountLoginManagerImpl.class);

    @Inject
    private AccountService accountService;
    @Inject
    private AccountAuthService accountAuthService;
    @Override
    public String authorize(OAuthTokenRequest oauthRequest) throws SystemException {
        OAuthResponse response=null;
        try {
            // 檢查不同的grant types是否正確
            // TODO 消除if-else
            int clientId = oauthRequest.getClientId();
            String instanceId = oauthRequest.getInstanceId();

            AccountAuth renewAccountAuth;
            if (GrantType.PASSWORD.toString().equals(oauthRequest.getGrantType())) {
                Account account = accountService
                        .verifyUserPwdVaild(oauthRequest.getUsername(), oauthRequest.getPassword());
                if (account == null) {
                    response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                            .setError(OAuthError.Response.USERNAME_PWD_MISMATCH)
                            .setErrorDescription("username or password mismatch").buildJSONMessage();
                    return response.getBody();
                } else if (!account.isNormalAccount()) {
                    response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                            .setError(OAuthError.Response.INVALID_USER)
                            .setErrorDescription("user account invalid").buildJSONMessage();
                    return response.getBody();
                } else {
                    renewAccountAuth = accountAuthService.updateAccountAuth(account.getId(), account.getPassportId(),
                            clientId, instanceId);
                }
            } else if (GrantType.REFRESH_TOKEN.toString().equals(oauthRequest.getGrantType())) {
                String refreshToken = oauthRequest.getRefreshToken();
                AccountAuth accountAuth = accountAuthService.verifyRefreshToken(refreshToken, instanceId);
                if (accountAuth == null) {
                    response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                            .setError(OAuthError.Response.INVALID_REFRESH_TOKEN).setErrorDescription("refresh_token not exist or expired")
                            .buildJSONMessage();
                    return response.getBody();
                } else {
                    String passportId = TokenGenerator.parsePassportIdFromRefreshToken(refreshToken);
                    renewAccountAuth = accountAuthService.updateAccountAuth(accountAuth.getId(), passportId, clientId,
                            instanceId);
                }
            } else {
                response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.Response.UNSUPPORTED_GRANT_TYPE)
                        .setErrorDescription("unsupported_grant_type").buildJSONMessage();
                return response.getBody();
            }

            if (renewAccountAuth != null) { // 登录成功
                response = OAuthASResponse.tokenResponse(HttpServletResponse.SC_OK)
                        .setAccessToken(renewAccountAuth.getAccessToken())
                        .setExpiresTime(renewAccountAuth.getAccessValidTime())
                        .setRefreshToken(renewAccountAuth.getRefreshToken()).buildJSONMessage();
                return response.getBody();
            } else { // 登录失败，更新AccountAuth表发生异常
                response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.Response.AUTHORIZE_FAIL).setErrorDescription("login fail")
                        .buildJSONMessage();
                return response.getBody();
            }
        } catch (ProblemException e) {
            response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST).error(e).buildJSONMessage();
            return response.getBody();
        } catch (Exception e) {
            logger.error("OAuth Authorize Fail:", e);
            response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                    .setError(ErrorUtil.ERR_CODE_COM_EXCEPTION).setErrorDescription("unknown error").buildJSONMessage();
            return response.getBody();
        }
    }
}
