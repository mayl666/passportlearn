package com.sogou.upd.passport.manager.connect.impl;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.exception.ServiceException;
import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.connect.ConnectAuthManager;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.connect.ConnectRelation;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.oauth2.common.OAuthError;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthSinaSSOBindTokenRequest;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthSinaSSOTokenRequest;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.AccountTokenService;
import com.sogou.upd.passport.service.connect.ConnectRelationService;
import com.sogou.upd.passport.service.connect.ConnectTokenService;
import com.sogou.upd.passport.service.account.dataobject.PassportIDInfoDO;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-4-16
 * Time: 下午5:19
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ConnectAuthManagerImpl implements ConnectAuthManager {

    private static Logger logger = LoggerFactory.getLogger(ConnectAuthManager.class);

    @Autowired
    private ConnectConfigService connectConfigService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountTokenService accountTokenService;
    @Autowired
    private ConnectTokenService connectTokenService;
    @Autowired
    private ConnectRelationService connectRelationService;

    @Override
    public Result connectAuthBind(OAuthSinaSSOBindTokenRequest oauthRequest, int provider) throws SystemException {
        int clientId = oauthRequest.getClientId();
        String bindAccessToken = oauthRequest.getBindToken();
        String openid = oauthRequest.getOpenid();
        try {
            String appKey = connectConfigService.querySpecifyAppKey(clientId, provider);

            // 检查主账号access_token是否有效
            AccountToken bindAccountToken = accountTokenService.verifyAccessToken(bindAccessToken);
            if (bindAccessToken == null) {
                return Result.buildError(ErrorUtil.ERR_ACCESS_TOKEN);
            }

            String bindPassportId = bindAccountToken.getPassportId();
            Result inspectsPassportIdRule = isAbleBindDependBindPassportId(bindPassportId, provider, appKey);
            if (!inspectsPassportIdRule.isSuccess()) {
                return inspectsPassportIdRule;
            }

            Result inspectsOpenidRule = isAbleBindDependOpenid(openid, provider, appKey);
            if (!inspectsOpenidRule.isSuccess()) {
                return inspectsOpenidRule;
            }

            // 写入数据库
            ConnectToken newConnectToken = buildConnectToken(bindPassportId, provider, appKey, openid, oauthRequest.getAccessToken(),
                    oauthRequest.getExpiresIn(), oauthRequest.getRefreshToken());
            boolean isInitialConnectToken = connectTokenService.initialConnectToken(newConnectToken);
            if (!isInitialConnectToken) {
                return Result.buildError(ErrorUtil.BIND_CONNECT_ACCOUNT_FAIL);
            }
            ConnectRelation newConnectRelation = buildConnectRelation(openid, provider, bindPassportId, appKey);
            boolean isInitialConnectRelation = connectRelationService.initialConnectRelation(newConnectRelation);
            if (!isInitialConnectRelation) {
                return Result.buildError(ErrorUtil.BIND_CONNECT_ACCOUNT_FAIL);
            }
            return Result.buildSuccess("绑定成功", null, null);
        } catch (Exception e) {
            logger.error("SSO bind Account Fail:", e);
            return Result.buildError(ErrorUtil.ERR_CODE_COM_EXCEPTION, "unknown error");
        }
    }

    @Override
    public Result connectAuthLogin(OAuthSinaSSOTokenRequest oauthRequest, int provider, String ip) throws SystemException {

        int clientId = oauthRequest.getClientId();
        String openid = oauthRequest.getOpenid();
        String instanceId = oauthRequest.getInstanceId();

        AccountToken accountToken;
        try {
            // 获取第三方用户信息
            Map<String, ConnectRelation> connectRelations = connectRelationService.queryAppKeyMapping(openid, provider);
            String appKey = connectConfigService.querySpecifyAppKey(clientId, provider);
            String passportId = getPassportIdByAppointAppKey(connectRelations, appKey);

            if (passportId == null) { // 此账号未在当前应用登录过
                if (connectRelations.isEmpty()) { // 此账号未授权过任何应用
                    Account account = accountService.initialConnectAccount(openid, ip, provider);
                    if (account == null) {
                        return Result.buildError(OAuthError.Response.AUTHORIZE_FAIL, "login fail");
                    }
                    passportId = account.getPassportId();
                } else { // 此账号已存在，只是未在当前应用登录 TODO 注意QQ的不同appid返回的uid不同
                    passportId = connectRelations.get(0).getPassportId(); // 一个openid只可能对应一个passportId
                    Account account = accountService.verifyAccountVaild(passportId);
                    if (account == null) {
                        return Result.buildError(OAuthError.Response.INVALID_USER, "user account invalid");
                    }
                }
                // todo 并行插入
                accountToken = accountTokenService.initialAccountToken(passportId, clientId, instanceId);
                if (accountToken == null) {
                    return Result.buildError(OAuthError.Response.AUTHORIZE_FAIL, "login fail");
                }
                ConnectToken newConnectToken = buildConnectToken(passportId, provider, appKey, openid, oauthRequest.getAccessToken(),
                        oauthRequest.getExpiresIn(), oauthRequest.getRefreshToken());
                boolean isInitialConnectToken = connectTokenService.initialConnectToken(newConnectToken);
                if (!isInitialConnectToken) {
                    return Result.buildError(OAuthError.Response.AUTHORIZE_FAIL, "login fail");
                }
                ConnectRelation newConnectRelation = buildConnectRelation(openid, provider, passportId, appKey);
                boolean isInitialConnectRelation = connectRelationService.initialConnectRelation(newConnectRelation);
                if (!isInitialConnectRelation) {
                    return Result.buildError(OAuthError.Response.AUTHORIZE_FAIL, "login fail");
                }
            } else { // 此账号在当前应用第N次登录
                Account account = accountService.verifyAccountVaild(passportId);
                if (account == null) {
                    return Result.buildError(OAuthError.Response.INVALID_USER, "user account invalid");
                }
                // 更新当前应用的Account_token，出于安全考虑refresh_token和access_token重新生成
                accountToken = accountTokenService.updateAccountToken(passportId, clientId, instanceId);
                if (accountToken == null) {
                    return Result.buildError(OAuthError.Response.AUTHORIZE_FAIL, "login fail");
                }
                // 更新当前应用的Connect_token
                ConnectToken updateConnectToken = buildConnectToken(passportId, provider, appKey, openid, oauthRequest.getAccessToken(),
                        oauthRequest.getExpiresIn(), oauthRequest.getRefreshToken());
                boolean isUpdateAccountConnect = connectTokenService.updateConnectToken(updateConnectToken);
                if (!isUpdateAccountConnect) {
                    return Result.buildError(OAuthError.Response.AUTHORIZE_FAIL, "login fail");
                }
            }
            Map<String, Object> mapResult = Maps.newHashMap();
            mapResult.put("access_token", accountToken.getAccessToken());
            mapResult.put("expires_time", accountToken.getAccessValidTime());
            mapResult.put("refresh_token", accountToken.getRefreshToken());
            return Result.buildSuccess("登录成功！", "mapResult", mapResult);
        } catch (ServiceException e) {
            logger.error("SSO login Fail:", e);
            return Result.buildError(ErrorUtil.ERR_CODE_COM_EXCEPTION, "unknown error");
        }
    }

    /**
     * 校验是否可以绑定第三方账号
     * 绑定规则：
     * 在同一个appKey内，一种账号类型的账号只能绑定一个；例如：只能绑定qq_1，不能绑定qq_2；
     * 不能绑定和主账号同一账号类型的账号；例如：主账号qq_1，不能绑定qq_2，可以绑定sina_1，renren_1
     *
     * @return
     * @throws ServiceException
     */
    public Result isAbleBindDependBindPassportId(String bindPassportId, int provider, String appKey) throws ServiceException {

        PassportIDInfoDO passportIDInfoDO = PassportIDGenerator.parsePassportId(bindPassportId);
        String bindAccountTypeStr = passportIDInfoDO.getAccountTypeStr();
        int bindProvider = AccountTypeEnum.getProvider(bindAccountTypeStr);
        if (bindProvider == provider) {   // 不能绑定与主账号同一类型的账号
            return Result.buildError(ErrorUtil.CONNOT_BIND_SAME_TYPE_ACCOUNT);
        }

        String openid = connectTokenService.querySpecifyOpenId(bindPassportId, provider, appKey);
        if (openid == null) {
            return Result.buildError(ErrorUtil.NOTALLOWED_REPEAT_BIND_SAME_TYPE_ACCOUNT);
        }
        return new Result(true);
    }

    /**
     * 校验是否可以绑定第三方账号
     * 绑定规则：
     * 已注册过或被绑定的账号无法被其他账号再次绑定；
     *
     * @return
     */
    public Result isAbleBindDependOpenid(String openid, int provider, String appKey) throws ServiceException {
        ConnectRelation connectRelation = connectRelationService.querySpecifyConnectRelation(openid, provider, appKey);
        if (connectRelation != null) {
            return Result.buildError(ErrorUtil.ACCOUNT_ALREADY_REG_OR_BIND);
        }
        return new Result(true);
    }

    /**
     * 创建一个第三方账户对象
     */
    private ConnectToken buildConnectToken(String passportId, int provider, String appKey, String openid, String accessToken, long expiresIn, String refreshToken) {
        ConnectToken connect = new ConnectToken();
        connect.setPassportId(passportId);
        connect.setProvider(provider);
        connect.setAppKey(appKey);
        connect.setOpenid(openid);
        connect.setAccessToken(accessToken);
        connect.setExpiresIn(expiresIn);
        connect.setRefreshToken(refreshToken);
        connect.setCreateTime(new Date());
        return connect;
    }

    /**
     * 创建一个第三方关系关系（反查表）对象
     */
    private ConnectRelation buildConnectRelation(String openid, int provider, String passportId, String appKey) {
        ConnectRelation connectRelation = new ConnectRelation();
        connectRelation.setOpenid(openid);
        connectRelation.setProvider(provider);
        connectRelation.setPassportId(passportId);
        connectRelation.setAppKey(appKey);
        return connectRelation;
    }

    /**
     * 该账号是否在当前应用登录过
     * 返回passportId，如果没有登录过返回null
     *
     * @return
     */
    private String getPassportIdByAppointAppKey(Map<String, ConnectRelation> connectRelations, String appKey) {
        String passportId = null;
        if (!connectRelations.isEmpty()) {
            ConnectRelation connectRelation = connectRelations.get(appKey);
            if (connectRelation != null) {
                passportId = connectRelation.getPassportId();
            }
        }
        return passportId;
    }
}
