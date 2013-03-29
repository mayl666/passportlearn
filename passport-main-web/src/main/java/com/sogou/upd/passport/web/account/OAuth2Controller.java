package com.sogou.upd.passport.web.account;

import com.sogou.upd.passport.common.exception.ProblemException;
import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.oauth2.authzserver.request.OAuthTokenRequest;
import com.sogou.upd.passport.oauth2.authzserver.response.OAuthASResponse;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.OAuthError;
import com.sogou.upd.passport.oauth2.common.OAuthResponse;
import com.sogou.upd.passport.oauth2.common.types.GrantType;
import com.sogou.upd.passport.service.account.AccountAuthService;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
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
    public Object authorize(HttpServletRequest request) throws SystemException {
        OAuthTokenRequest oauthRequest = null;
        OAuthResponse response = null;

        try {
            oauthRequest = new OAuthTokenRequest(request);

            // 检查client_id和client_secret是否有效
            if (!appConfigService.verifyClientVaild(oauthRequest.getClientId(), oauthRequest.getClientSecret())) {
                response =
                        OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                                .setError(OAuthError.Response.INVALID_CLIENT).setErrorDescription("client_id or client_secret not found")
                                .buildJSONMessage();
            }

            //do checking for different grant types
            if (oauthRequest.getGrantType()
                    .equals(GrantType.PASSWORD.toString())) {
                if (!accountService.verifyUserVaild(oauthRequest.getUsername(), oauthRequest.getPassword())) {
                    response = OAuthASResponse
                            .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                            .setError(OAuthError.Response.INVALID_GRANT)
                            .setErrorDescription("invalid username or password")
                            .buildJSONMessage();
                }
            } else if (oauthRequest.getGrantType()
                    .equals(GrantType.REFRESH_TOKEN.toString())) {
                if (!accountAuthService.verifyRefreshToken(oauthRequest.getRefreshToken())) {
                    response = OAuthASResponse
                            .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                            .setError(OAuthError.Response.INVALID_GRANT)
                            .setErrorDescription("invalid refresh_token")
                            .buildJSONMessage();
                }
            }

            if (response.getResponseStatus() != HttpServletResponse.SC_BAD_REQUEST) {
                // TODO 数据库操作
                response = OAuthASResponse
                        .tokenResponse(HttpServletResponse.SC_OK)
                        .setAccessToken("")
                        .setExpiresIn("3600")
                        .setRefreshToken("")
                        .buildJSONMessage();
            }
        } catch (ProblemException e) {
            OAuthResponse res = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST).error(e)
                    .buildJSONMessage();
        } finally {
            return response.getBody();
        }
    }

}
