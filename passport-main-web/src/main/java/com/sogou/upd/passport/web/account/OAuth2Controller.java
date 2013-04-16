package com.sogou.upd.passport.web.account;

import com.sogou.upd.passport.common.exception.ProblemException;
import com.sogou.upd.passport.common.parameter.AccountStatusEnum;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.AccountLoginManager;
import com.sogou.upd.passport.manager.app.AppConfigManager;
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
    private AccountLoginManager accountLoginManager;
    @Inject
    private AppConfigManager appConfigManager;

    @Inject
    private AppConfigService appConfigService;

    @Inject
    private AccountAuthService accountAuthService;

    @RequestMapping(value = "/token", method = RequestMethod.POST)
    @ResponseBody
    public Object authorize(HttpServletRequest request) throws Exception {
        OAuthTokenRequest oauthRequest;
        OAuthResponse response;
        String result=null;
        try {
            oauthRequest = new OAuthTokenRequest(request);

            int clientId = oauthRequest.getClientId();

            // 检查client_id和client_secret是否有效

            if (!appConfigManager.verifyClientVaild(clientId, oauthRequest.getClientSecret())) {
                response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.Response.INVALID_CLIENT)
                        .setErrorDescription("client_id or client_secret mismatch").buildJSONMessage();
                return response.getBody();
            }
            //todo 返回Result
            result=accountLoginManager.authorize(oauthRequest);
        } catch (Exception e) {
            logger.error("OAuth Authorize Fail:", e);
            response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                    .setError(ErrorUtil.ERR_CODE_COM_EXCEPTION).setErrorDescription("unknown error").buildJSONMessage();
            return response.getBody();
        }
        return result;
    }

}
