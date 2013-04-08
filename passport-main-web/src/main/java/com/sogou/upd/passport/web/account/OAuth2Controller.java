package com.sogou.upd.passport.web.account;

import com.sogou.upd.passport.common.exception.ProblemException;
import com.sogou.upd.passport.common.parameter.AccountStatusEnum;
import com.sogou.upd.passport.common.utils.ErrorUtil;
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
import com.sogou.upd.passport.service.app.AppConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-3-28
 * Time: 下午8:33
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/oauth2")
public class OAuth2Controller {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2Controller.class);

    @Inject
    private AppConfigService appConfigService;
    @Inject
    private AccountService accountService;
    @Inject
    private AccountAuthService accountAuthService;

    @RequestMapping(value = "/token", method = RequestMethod.POST)
    @ResponseBody
    public Object authorize(HttpServletRequest request) throws Exception {
        OAuthTokenRequest oauthRequest;
        OAuthResponse response;

        try {
            oauthRequest = new OAuthTokenRequest(request);

            int clientId = oauthRequest.getClientId();
            String instanceId = oauthRequest.getInstanceId();

            // 检查client_id和client_secret是否有效
            if (!appConfigService.verifyClientVaild(clientId, oauthRequest.getClientSecret())) {
                response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.Response.INVALID_CLIENT)
                        .setErrorDescription("client_id or client_secret mismatch").buildJSONMessage();
                return response.getBody();
            }

            // 檢查不同的grant types是否正確
            // TODO 消除if-else
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
