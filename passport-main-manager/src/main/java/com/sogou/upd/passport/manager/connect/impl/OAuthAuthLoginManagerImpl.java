package com.sogou.upd.passport.manager.connect.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.CommonHelper;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.connect.OAuthAuthLoginManager;
import com.sogou.upd.passport.manager.form.connect.ConnectLoginParams;
import com.sogou.upd.passport.model.OAuthConsumer;
import com.sogou.upd.passport.model.OAuthConsumerFactory;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.model.connect.ConnectRelation;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.common.types.ConnectTypeEnum;
import com.sogou.upd.passport.oauth2.common.types.ResponseTypeEnum;
import com.sogou.upd.passport.oauth2.common.utils.OAuthUtils;
import com.sogou.upd.passport.oauth2.openresource.request.OAuthAuthzClientRequest;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthAuthzClientResponse;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthSinaSSOTokenRequest;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.AccountTokenService;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import com.sogou.upd.passport.service.connect.ConnectRelationService;
import com.sogou.upd.passport.service.connect.ConnectTokenService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-4-16
 * Time: 下午5:19
 * To change this template use File | Settings | File Templates.
 */
@Component
public class OAuthAuthLoginManagerImpl implements OAuthAuthLoginManager {

    private static Logger logger = LoggerFactory.getLogger(OAuthAuthLoginManagerImpl.class);

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
    public Result connectSSOLogin(OAuthSinaSSOTokenRequest oauthRequest, int provider, String ip) {
        Result result = new APIResultSupport(false);
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
                if (MapUtils.isEmpty(connectRelations)) { // 此账号未授权过任何应用
                    Account account = accountService.initialConnectAccount(openid, ip, provider);
                    if (account == null) {
                        result.setCode(ErrorUtil.AUTHORIZE_FAIL);
                        return result;
                    }
                    passportId = account.getPassportId();
                } else { // 此账号已存在，只是未在当前应用登录 TODO 注意QQ的不同appid返回的uid不同
                    passportId = obtainPassportId(connectRelations); // 一个openid只可能对应一个passportId
                    Account account = accountService.queryNormalAccount(passportId);
                    if (account == null) {
                        result.setCode(ErrorUtil.INVALID_ACCOUNT);
                        return result;
                    }
                }
                // 在切换appkey的时，应该是update，正常情况是Insert
                accountToken = accountTokenService.updateOrInsertAccountToken(passportId, clientId, instanceId);
                if (accountToken == null) {
                    result.setCode(ErrorUtil.AUTHORIZE_FAIL);
                    return result;
                }
                ConnectToken newConnectToken = ManagerHelper.buildConnectToken(passportId, provider, appKey, openid, oauthRequest.getAccessToken(),
                        oauthRequest.getExpiresIn(), oauthRequest.getRefreshToken());
                boolean isInitialConnectToken = connectTokenService.initialConnectToken(newConnectToken);
                if (!isInitialConnectToken) {
                    result.setCode(ErrorUtil.AUTHORIZE_FAIL);
                    return result;
                }
                ConnectRelation newConnectRelation = ManagerHelper.buildConnectRelation(openid, provider, passportId, appKey);
                boolean isInitialConnectRelation = connectRelationService.initialConnectRelation(newConnectRelation);
                if (!isInitialConnectRelation) {
                    result.setCode(ErrorUtil.AUTHORIZE_FAIL);
                    return result;
                }
            } else { // 此账号在当前应用第N次登录
                Account account = accountService.queryNormalAccount(passportId);
                if (account == null) {
                    result.setCode(ErrorUtil.INVALID_ACCOUNT);
                    return result;
                }
                // 更新当前应用的Account_token，出于安全考虑refresh_token和access_token重新生成
                accountToken = accountTokenService.updateOrInsertAccountToken(passportId, clientId, instanceId);
                if (accountToken == null) {
                    result.setCode(ErrorUtil.AUTHORIZE_FAIL);
                    return result;
                }
                // 更新当前应用的Connect_token
                ConnectToken updateConnectToken = ManagerHelper.buildConnectToken(passportId, provider, appKey, openid, oauthRequest.getAccessToken(),
                        oauthRequest.getExpiresIn(), oauthRequest.getRefreshToken());
                boolean isUpdateAccountConnect = connectTokenService.updateConnectToken(updateConnectToken);
                if (!isUpdateAccountConnect) {
                    result.setCode(ErrorUtil.AUTHORIZE_FAIL);
                    return result;
                }
            }
            Map<String, Object> mapResult = Maps.newHashMap();
            mapResult.put("access_token", accountToken.getAccessToken());
            mapResult.put("expires_time", accountToken.getAccessValidTime());
            mapResult.put("refresh_token", accountToken.getRefreshToken());

            result.setSuccess(true);
            result.setMessage("登录成功！");
            result.setModels(mapResult);
            return result;
        } catch (ServiceException e) {
            logger.error("SSO login Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    @Override
    public OAuthTokenVO buildConnectCallbackResponse(HttpServletRequest req, String connectType, int provider) throws OAuthProblemException {
        OAuthTokenVO oAuthTokenDO;
        OAuthAuthzClientResponse oar = buildOAuthAuthzClientResponse(req, connectType);
        // 验证state是否被篡改，防CRSF攻击
        String state = oar.getState();
        String stateCookie = ServletUtil.getCookie(req, state);
        if (Strings.isNullOrEmpty(stateCookie) || !stateCookie.equals(CommonHelper.constructStateCookieKey(provider))) {
            throw new OAuthProblemException(ErrorUtil.OAUTH_AUTHZ_STATE_INVALID);
        }

        if (ConnectTypeEnum.WEB.toString().equals(connectType)) {

        } else {
            oAuthTokenDO = new OAuthTokenVO(oar.getAccessToken(), oar.getExpiresIn(), oar.getRefreshToken());
        }

        return null;
    }

    private OAuthAuthzClientResponse buildOAuthAuthzClientResponse(HttpServletRequest req, String connectType) throws OAuthProblemException {
        OAuthAuthzClientResponse oar;
        if (ConnectTypeEnum.WEB.toString().equals(connectType)) { // 获取code，然后用code换取accessToken
            oar = OAuthAuthzClientResponse.oauthCodeAuthzResponse(req);
        } else {
            oar = OAuthAuthzClientResponse.oauthTokenAuthzResponse(req);
        }
        return oar;
    }

    /**
     * 该账号是否在当前应用登录过
     * 返回passportId，如果没有登录过返回null
     *
     * @return
     */
    private String getPassportIdByAppointAppKey(Map<String, ConnectRelation> connectRelations, String appKey) {
        String passportId = null;
        if (!MapUtils.isEmpty(connectRelations)) {
            ConnectRelation connectRelation = connectRelations.get(appKey);
            if (connectRelation != null) {
                passportId = connectRelation.getPassportId();
            }
        }
        return passportId;
    }



    private String obtainPassportId(Map<String, ConnectRelation> connectRelations) {
        String passportId = "";
        for (String key : connectRelations.keySet()) {
            return connectRelations.get(key).getPassportId();
        }
        return passportId;
    }
}
