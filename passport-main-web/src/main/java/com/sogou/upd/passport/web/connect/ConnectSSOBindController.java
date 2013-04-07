package com.sogou.upd.passport.web.connect;

import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
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

        OAuthSinaSSOBindTokenRequest oauthRequest = new OAuthSinaSSOBindTokenRequest(req);
        OAuthResponse response = null;

        int accountType = AccountTypeEnum.SINA.getValue();
        int clientId = oauthRequest.getClientId();
        String instanceId = oauthRequest.getInstanceId();
        String connectUid = oauthRequest.getOpenid();

        if (!appConfigService.verifyClientVaild(clientId, oauthRequest.getClientSecret())) {
            response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                    .setError(OAuthError.Response.INVALID_CLIENT)
                    .setErrorDescription("client_id or client_secret not found").buildJSONMessage();
            return response.getBody();
        }

//        AccountAuth accountAuth;
//
//        // 获取第三方用户信息
//        try {
//            AccountConnectQuery query = buildAccountConnectQuery(connectUid, accountType);
//            List<AccountConnect> accountConnectList = accountConnectService.listAccountConnectByQuery(query);
//            AccountConnect oldAccountConnect = getAppointClientIdAccountConnect(accountConnectList, clientId);
//
//            long userId;
//            if (oldAccountConnect == null) { // 此账号未授权当前应用
//                if (CollectionUtils.isEmpty(accountConnectList)) { // 此账号未授权过任何应用
//                    Account newAccount = accountService.initialConnectAccount(connectUid, getIp(req), accountType);
//                    if (newAccount == null) {
//                        response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
//                                .setError(OAuthError.Response.AUTHORIZE_FAIL).setErrorDescription("login fail")
//                                .buildJSONMessage();
//                        return response.getBody();
//                    }
//                    userId = newAccount.getId();
//                } else { // 此账号已存在，只是未在当前应用登录 TODO 注意QQ的不同appid返回的uid不同
//                    userId = accountConnectList.get(0).getUserId();
//                }
//                // TODO 是否有必要并行初始化Account_Auth和Account_Connect？
//                String passportId = PassportIDGenerator.generator(connectUid, accountType);
//                accountAuth = accountAuthService.initialAccountAuth(userId, passportId, clientId, instanceId);
//                AccountConnect newAccountConnect = buildAccountConnect(userId, clientId, accountType,
//                        AccountConnect.STUTAS_LONGIN, connectUid, oauthRequest.getAccessToken(),
//                        oauthRequest.getExpiresIn(), oauthRequest.getRefreshToken());
//                boolean isInitalAccountConnect = accountConnectService.initialAccountConnect(newAccountConnect);
//                if (accountAuth == null || !isInitalAccountConnect) {
//                    response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
//                            .setError(OAuthError.Response.AUTHORIZE_FAIL).setErrorDescription("login fail")
//                            .buildJSONMessage();
//                    return response.getBody();
//                }
//
//            } else { // 此账号在当前应用第N次登录
//                userId = oldAccountConnect.getUserId();
//                // 更新当前应用的Account_Auth，处于安全考虑refresh_token和access_token重新生成
//                String passportId = PassportIDGenerator.generator(connectUid, accountType);
//                accountAuth = accountAuthService.updateAccountAuth(userId, passportId, clientId, instanceId);
//                // 更新当前应用的Account_Connect
//                AccountConnect accountConnect = buildAccountConnect(userId, clientId, accountType,
//                        AccountConnect.STUTAS_LONGIN, connectUid, oauthRequest.getAccessToken(),
//                        oauthRequest.getExpiresIn(), oauthRequest.getRefreshToken());
//                boolean isUpdateAccountConnect = accountConnectService.updateAccountConnect(accountConnect);
//                if (accountAuth == null || !isUpdateAccountConnect) {
//                    response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
//                            .setError(OAuthError.Response.AUTHORIZE_FAIL).setErrorDescription("login fail")
//                            .buildJSONMessage();
//                    return response.getBody();
//                }
//            }
//            // TODO 如何保证数据一致性，采用insertAndUpdate()？
//
//        } catch (Exception e) {
//            log.error("sso login fail", e);
//            response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
//                    .setError(OAuthError.Response.AUTHORIZE_FAIL).setErrorDescription("login fail").buildJSONMessage();
//            return response.getBody();
//        }
//        response = OAuthASResponse.tokenResponse(HttpServletResponse.SC_OK)
//                .setAccessToken(accountAuth.getAccessToken()).setExpiresTime(accountAuth.getAccessValidTime())
//                .setRefreshToken(accountAuth.getRefreshToken()).buildJSONMessage();
//        return response.getBody();
        return null;
    }


}
