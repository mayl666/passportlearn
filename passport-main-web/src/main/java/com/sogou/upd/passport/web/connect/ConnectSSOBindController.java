package com.sogou.upd.passport.web.connect;

import com.sogou.upd.passport.common.exception.ProblemException;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.parameter.CommonParameters;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountAuth;
import com.sogou.upd.passport.model.account.AccountConnect;
import com.sogou.upd.passport.model.account.query.AccountConnectQuery;
import com.sogou.upd.passport.oauth2.authzserver.response.OAuthASResponse;
import com.sogou.upd.passport.oauth2.common.OAuthError;
import com.sogou.upd.passport.oauth2.common.OAuthResponse;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthSinaSSOBindTokenRequest;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthSinaSSOTokenRequest;
import com.sogou.upd.passport.service.account.AccountAuthService;
import com.sogou.upd.passport.service.account.AccountConnectService;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import com.sogou.upd.passport.service.app.AppConfigService;
import com.sogou.upd.passport.web.BaseConnectController;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * SSO-SDK第三方登录授权回调接口
 * User: shipengzhi
 * Date: 13-3-24
 * Time: 下午12:07
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/v2/connect")
public class ConnectSSOBindController extends BaseConnectController {

    private static Logger log = LoggerFactory.getLogger(ConnectSSOBindController.class);

    @Inject
    private AppConfigService appConfigService;
    @Inject
    private AccountService accountService;
    @Inject
    private AccountAuthService accountAuthService;
    @Inject
    private AccountConnectService accountConnectService;

    @RequestMapping(value = "/ssobind/sina", method = RequestMethod.POST)
    @ResponseBody
    public Object handleSSOBind(HttpServletRequest req, HttpServletResponse res) throws Exception {

        OAuthResponse response = null;
        try {
            OAuthSinaSSOBindTokenRequest oauthRequest = new OAuthSinaSSOBindTokenRequest(req);


            int accountType = AccountTypeEnum.SINA.getValue();
            int clientId = oauthRequest.getClientId();
            String connectUid = oauthRequest.getOpenid();
            String bindAccessToken = oauthRequest.getBindToken();
            // 检查client_id和client_secret是否有效
            if (!appConfigService.verifyClientVaild(clientId, oauthRequest.getClientSecret())) {
                response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.Response.INVALID_CLIENT)
                        .setErrorDescription("client_id or client_secret not found").buildJSONMessage();
                return response.getBody();
            }

            // 检查主账号access_token是否有效
            AccountAuth bindAccountAuth = accountAuthService.verifyAccessToken(bindAccessToken);
            if (bindAccessToken == null) {
                response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.Response.INVALID_ACCESS_TOKEN).setErrorDescription("access_token not exist or expired")
                        .buildJSONMessage();
                return response.getBody();
            }

            // 检查主账号access_token是否可以绑定此第三方账号
            long userId = bindAccountAuth.getUserId();
            if (!accountAuthService.isAbleBind(userId, connectUid, accountType, clientId)) {
                response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.Response.UNABLE_BIND_ACCESS_TOKEN).setErrorDescription("access_token cannot bind")
                        .buildJSONMessage();
                return response.getBody();
            }

            // 写入数据库
            AccountConnect newAccountConnect = buildAccountConnect(userId, clientId, accountType,
                    AccountConnect.STUTAS_BIND, connectUid, oauthRequest.getAccessToken(),
                    oauthRequest.getExpiresIn(), oauthRequest.getRefreshToken());
            boolean isInitalAccountConnect = accountConnectService.initialAccountConnect(newAccountConnect);
            if (!isInitalAccountConnect) {
                response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.Response.BIND_FAIL).setErrorDescription("bind account fail")
                        .buildJSONMessage();
                return response.getBody();
            }

            response = OAuthASResponse.tokenResponse(HttpServletResponse.SC_OK)
                        .setParam(CommonParameters.RESPONSE_STATUS,"0")
                        .setParam(CommonParameters.RESPONSE_STATUS_TEXT, "OK")
                        .buildJSONMessage();
            return response.getBody();
        } catch (ProblemException e) {
            response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST).error(e).buildJSONMessage();
            return response.getBody();
        } catch (Exception e) {
            log.error("sso bind account fail", e);
            response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                    .setError(OAuthError.Response.AUTHORIZE_FAIL).setErrorDescription("login exception").buildJSONMessage();
            return response.getBody();
        }
    }


}
