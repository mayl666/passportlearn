package com.sogou.upd.passport.web.account;

import com.sogou.upd.passport.common.exception.ProblemException;
import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountAuth;
import com.sogou.upd.passport.oauth2.authzserver.request.OAuthTokenRequest;
import com.sogou.upd.passport.oauth2.authzserver.response.OAuthASResponse;
import com.sogou.upd.passport.oauth2.common.OAuthError;
import com.sogou.upd.passport.oauth2.common.OAuthResponse;
import com.sogou.upd.passport.oauth2.common.types.GrantType;
import com.sogou.upd.passport.oauth2.common.utils.OAuthUtils;
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
        OAuthTokenRequest oauthRequest = null;
        OAuthResponse response = null;

        try {
            oauthRequest = new OAuthTokenRequest(request);

            int clientId = Integer.valueOf(oauthRequest.getClientId());
            String instanceId = oauthRequest.getInstanceId();

            // 检查client_id和client_secret是否有效
            if (!appConfigService.verifyClientVaild(clientId, oauthRequest.getClientSecret())) {
                response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.Response.INVALID_CLIENT)
                        .setErrorDescription("client_id or client_secret not found").buildJSONMessage();
                return response.getBody();
            }

            // 檢查不同的grant types是否正確
            // TODO 消除if-else
            AccountAuth renewAccountAuth = null;
            if (oauthRequest.getGrantType().equals(GrantType.PASSWORD.toString())) {
                Account account = accountService
                        .verifyUserVaild(oauthRequest.getUsername(), oauthRequest.getPassword());
                if (account == null) {
                    response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                            .setError(OAuthError.Response.INVALID_GRANT)
                            .setErrorDescription("invalid username or password").buildJSONMessage();
                    return response.getBody();
                } else {
                    renewAccountAuth = accountAuthService.updateAccountAuth(account.getId(), account.getPassportId(),
                            clientId, instanceId);
                }
            } else if (oauthRequest.getGrantType().equals(GrantType.REFRESH_TOKEN.toString())) {
                String refreshToken = oauthRequest.getRefreshToken();
                AccountAuth accountAuth = accountAuthService.verifyRefreshToken(refreshToken);
                if (accountAuth == null) {
                    response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                            .setError(OAuthError.Response.INVALID_GRANT).setErrorDescription("invalid refresh_token")
                            .buildJSONMessage();
                    return response.getBody();
                } else {
                    String passportId = TokenGenerator.parsePassportIdFromRefreshToken(refreshToken);
                    renewAccountAuth = accountAuthService.updateAccountAuth(accountAuth.getId(), passportId, clientId,
                            instanceId);
                }
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
        } catch (NumberFormatException ex) {
            logger.error("{} is not Number", oauthRequest.getClientId());
            response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                    .setError(OAuthError.Response.INVALID_REQUEST)
                    .setErrorDescription(oauthRequest.getClientId() + " is not Number").buildJSONMessage();
            return response.getBody();
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
