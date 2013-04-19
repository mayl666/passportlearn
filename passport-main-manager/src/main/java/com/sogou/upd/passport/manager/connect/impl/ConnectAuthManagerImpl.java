package com.sogou.upd.passport.manager.connect.impl;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.exception.ProblemException;
import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.common.parameter.CommonParameters;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.connect.ConnectAuthManager;
import com.sogou.upd.passport.manager.connect.params.ConnectParams;
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
import com.sogou.upd.passport.service.app.AppConfigService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-4-16
 * Time: 下午5:19
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ConnectAuthManagerImpl implements ConnectAuthManager {
    @Inject
    private AppConfigService appConfigService;
    @Inject
    private AccountService accountService;
    @Inject
    private AccountAuthService accountAuthService;
    @Inject
    private AccountConnectService accountConnectService;

    @Override
    public Result connectAuthBind(OAuthSinaSSOBindTokenRequest oauthRequest, ConnectParams connectParams) throws SystemException {
        int clientId = connectParams.getClientId();
        String bindAccessToken = connectParams.getBindAccessToken();
        String connectUid = connectParams.getConnectUid();
        int accountType = connectParams.getAccountType();
        OAuthResponse response;
        try {
//            OAuthSinaSSOBindTokenRequest oauthRequest = new OAuthSinaSSOBindTokenRequest(req);
//
//
//            int accountType = AccountTypeEnum.SINA.getValue();
//            int clientId = oauthRequest.getClientId();
//            String connectUid = oauthRequest.getOpenid();
//            String bindAccessToken = oauthRequest.getBindToken();
            // 检查client_id和client_secret是否有效
            if (!appConfigService.verifyClientVaild(clientId, oauthRequest.getClientSecret())) {
                response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.Response.INVALID_CLIENT)
                        .setErrorDescription("client_id or client_secret not found").buildJSONMessage();
                //TODO Result格式的返回值 原始返回值：return response.getBody()
                return null;
            }

            // 检查主账号access_token是否有效
            AccountAuth bindAccountAuth = accountAuthService.verifyAccessToken(bindAccessToken);
            if (bindAccessToken == null) {
                response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.Response.INVALID_ACCESS_TOKEN).setErrorDescription("access_token not exist or expired")
                        .buildJSONMessage();
                //TODO Result格式的返回值 原始返回值：return response.getBody()
                return null;
            }

            // 检查主账号access_token是否可以绑定此第三方账号
            long userId = bindAccountAuth.getUserId();
            if (!accountAuthService.isAbleBind(userId, connectUid, accountType, clientId)) {
                response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.Response.UNABLE_BIND_ACCESS_TOKEN).setErrorDescription("access_token cannot bind")
                        .buildJSONMessage();
                //TODO Result格式的返回值 原始返回值：return response.getBody()
                return null;
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
                //TODO Result格式的返回值 原始返回值：return response.getBody()
                return null;
            }

            response = OAuthASResponse.tokenResponse(HttpServletResponse.SC_OK)
                    .setParam(CommonParameters.RESPONSE_STATUS, "0")
                    .setParam(CommonParameters.RESPONSE_STATUS_TEXT, "OK")
                    .buildJSONMessage();
            //TODO Result格式的返回值 原始返回值：return response.getBody()
            return null;
//        }
//        catch (ProblemException e) {
//            response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST).error(e).buildJSONMessage();
//            //TODO Result格式的返回值 原始返回值：return response.getBody()
//            return null;
        } catch (Exception e) {
            response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                    .setError(OAuthError.Response.AUTHORIZE_FAIL).setErrorDescription("login exception").buildJSONMessage();
            //TODO Result格式的返回值 原始返回值：return response.getBody()
            return null;
        }
    }

    @Override
    public Result connectAuthLogin(OAuthSinaSSOTokenRequest oauthRequest, ConnectParams connectParams) throws SystemException {
        int clientId = connectParams.getClientId();
        String connectUid = connectParams.getConnectUid();
        String ip = connectParams.getIp();
        int accountType = connectParams.getAccountType();
        String instanceId = connectParams.getInstanceId();

        OAuthResponse response;

        try {
//            OAuthSinaSSOTokenRequest oauthRequest = new OAuthSinaSSOTokenRequest(req);
//
//            int accountType = AccountTypeEnum.SINA.getValue();
//            int clientId = oauthRequest.getClientId();
//            String instanceId = oauthRequest.getInstanceId();
//            String connectUid = oauthRequest.getOpenid();
            // 检查client_id和client_secret是否有效
            if (!appConfigService.verifyClientVaild(clientId, oauthRequest.getClientSecret())) {
                response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.Response.INVALID_CLIENT)
                        .setErrorDescription("client_id or client_secret mismatch").buildJSONMessage();
                //TODO Result格式的返回值 原始返回值：return response.getBody()
                return null;
            }

            AccountAuth accountAuth;

            // 获取第三方用户信息
            AccountConnectQuery query = new AccountConnectQuery(connectUid, accountType);
            List<AccountConnect> accountConnectList = accountConnectService.listAccountConnectByQuery(query);
            AccountConnect oldAccountConnect = getAppointClientIdAccountConnect(accountConnectList, clientId);

            long userId;
            Account account;
            if (oldAccountConnect == null) { // 此账号未在当前应用登录过
                if (CollectionUtils.isEmpty(accountConnectList)) { // 此账号未授权过任何应用
                    account = accountService.initialConnectAccount(connectUid, ip, accountType);
                    if (account == null) {
                        response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                                .setError(OAuthError.Response.AUTHORIZE_FAIL).setErrorDescription("login fail")
                                .buildJSONMessage();
                        //TODO Result格式的返回值 原始返回值：return response.getBody()
                        return null;
                    }
                    userId = account.getId();
                } else { // 此账号已存在，只是未在当前应用登录 TODO 注意QQ的不同appid返回的uid不同
                    userId = accountConnectList.get(0).getUserId();
                    account = accountService.verifyAccountVaild(userId);
                    if (account == null) {
                        response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                                .setError(OAuthError.Response.INVALID_USER)
                                .setErrorDescription("user account invalid").buildJSONMessage();
                        //TODO Result格式的返回值 原始返回值：return response.getBody()
                        return null;
                    }
                }
                // TODO 是否有必要并行初始化Account_Auth和Account_Connect？
                accountAuth = accountAuthService.initialAccountAuth(userId, account.getPassportId(), clientId, instanceId);
                AccountConnect newAccountConnect = buildAccountConnect(userId, clientId, accountType,
                        AccountConnect.STUTAS_LONGIN, connectUid, oauthRequest.getAccessToken(),
                        oauthRequest.getExpiresIn(), oauthRequest.getRefreshToken());
                boolean isInitalAccountConnect = accountConnectService.initialAccountConnect(newAccountConnect);
                if (accountAuth == null || !isInitalAccountConnect) {
                    response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                            .setError(OAuthError.Response.AUTHORIZE_FAIL).setErrorDescription("login fail")
                            .buildJSONMessage();
                    //TODO Result格式的返回值 原始返回值：return response.getBody()
                    return null;
                }

            } else { // 此账号在当前应用第N次登录
                userId = oldAccountConnect.getUserId();
                account = accountService.verifyAccountVaild(userId);
                if (account == null) {
                    response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                            .setError(OAuthError.Response.INVALID_USER)
                            .setErrorDescription("user account invalid").buildJSONMessage();
                    //TODO Result格式的返回值 原始返回值：return response.getBody()
                    return null;
                }

                // 更新当前应用的Account_Auth，处于安全考虑refresh_token和access_token重新生成
                accountAuth = accountAuthService.updateAccountAuth(userId, account.getPassportId(), clientId, instanceId);
                // 更新当前应用的Account_Connect
                AccountConnect accountConnect = buildAccountConnect(userId, clientId, accountType,
                        oldAccountConnect.getAccountRelation(), connectUid, oauthRequest.getAccessToken(),
                        oauthRequest.getExpiresIn(), oauthRequest.getRefreshToken());
                boolean isUpdateAccountConnect = accountConnectService.updateAccountConnect(accountConnect);
                if (accountAuth == null || !isUpdateAccountConnect) {
                    response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                            .setError(OAuthError.Response.AUTHORIZE_FAIL).setErrorDescription("login fail")
                            .buildJSONMessage();
                    //TODO Result格式的返回值 原始返回值：return response.getBody()
                    return null;
                }
            }
            // TODO 如何保证数据一致性，采用insertAndUpdate()？
            response = OAuthASResponse.tokenResponse(HttpServletResponse.SC_OK)
                    .setAccessToken(accountAuth.getAccessToken()).setExpiresTime(accountAuth.getAccessValidTime())
                    .setRefreshToken(accountAuth.getRefreshToken()).buildJSONMessage();
            //TODO Result格式的返回值 原始返回值：return response.getBody()
            return null;
        } catch (ProblemException e) {
            response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST).error(e).buildJSONMessage();
            //TODO Result格式的返回值 原始返回值：return response.getBody()
            return null;
        } catch (Exception e) {
            response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                    .setError(OAuthError.Response.AUTHORIZE_FAIL).setErrorDescription("login exception").buildJSONMessage();
            //TODO Result格式的返回值 原始返回值：return response.getBody()
            return null;
        }
    }

    @Override
    public Result getOpenIdByPassportId(String passportId,int clientId,int accountType) {

        String uid = null;
        if (!Strings.isNullOrEmpty(passportId)) {
            long userId = accountService.getUserIdByPassportId(passportId);
            if (userId != 0) {
                uid = accountConnectService.getOpenIdByQuery(
                    new AccountConnectQuery(userId, accountType, clientId));
            }
        }
        return Strings.isNullOrEmpty(uid) ? Result.buildError(ErrorUtil.ERR_CODE_CONNECT_OBTAIN_OPENID_ERROR) : Result.buildSuccess("查询成功", "openid", uid);
    }

    /**
     * 创建一个第三方账户对象
     *
     * @param userId
     * @param clientId
     * @param accountType
     * @param accountRelation
     * @param connectUid
     * @param accessToken
     * @param expiresIn
     * @param refreshToken
     * @return
     */
    private AccountConnect buildAccountConnect(long userId, int clientId, int accountType, int accountRelation,
                                               String connectUid, String accessToken, long expiresIn, String refreshToken) {
        AccountConnect connect = new AccountConnect();
        connect.setUserId(userId);
        connect.setClientId(clientId);
        connect.setAccountType(accountType);
        connect.setAccountRelation(accountRelation);
        connect.setConnectUid(connectUid);
        connect.setConnectAccessToken(accessToken);
        connect.setConnectExpiresIn(expiresIn);
        connect.setConnectRefreshToken(refreshToken);
        connect.setCreateTime(new Date());
        return connect;
    }

    /**
     * 该账号是否在当前应用登录过
     *
     * @param accountConnectList
     * @param clientId
     * @return
     */
    private AccountConnect getAppointClientIdAccountConnect(List<AccountConnect> accountConnectList, int clientId) {
        AccountConnect accountConnect = null;
        for (AccountConnect connect : accountConnectList) {
            if (clientId == connect.getClientId()) {
                accountConnect = connect;
                break;
            }
        }
        return accountConnect;
    }
}
