package com.sogou.upd.passport.manager.connect.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.CommonHelper;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.OAuth2ResourceManager;
import com.sogou.upd.passport.manager.account.PCAccountManager;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.ConnectManagerHelper;
import com.sogou.upd.passport.manager.api.connect.SessionServerManager;
import com.sogou.upd.passport.manager.connect.OAuthAuthLoginManager;
import com.sogou.upd.passport.model.OAuthConsumer;
import com.sogou.upd.passport.model.OAuthConsumerFactory;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountBaseInfo;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.model.connect.ConnectRelation;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.common.parameters.QueryParameterApplier;
import com.sogou.upd.passport.oauth2.common.types.ConnectRequest;
import com.sogou.upd.passport.oauth2.common.types.ConnectTypeEnum;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthAuthzClientResponse;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthSinaSSOTokenRequest;
import com.sogou.upd.passport.oauth2.openresource.response.accesstoken.OAuthAccessTokenResponse;
import com.sogou.upd.passport.oauth2.openresource.response.accesstoken.QQJSONAccessTokenResponse;
import com.sogou.upd.passport.oauth2.openresource.response.accesstoken.QQOpenIdResponse;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import com.sogou.upd.passport.oauth2.openresource.response.accesstoken.QQJSONAccessTokenResponse;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import com.sogou.upd.passport.service.account.AccountBaseInfoService;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.AccountTokenService;
import com.sogou.upd.passport.service.account.WapTokenService;
import com.sogou.upd.passport.service.app.AppConfigService;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import com.sogou.upd.passport.service.connect.ConnectAuthService;
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
import java.net.URL;
import java.net.URLDecoder;
import java.util.Map;

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
    @Autowired
    private ConnectAuthService connectAuthService;
    @Autowired
    private AccountBaseInfoService accountBaseInfoService;
    @Autowired
    private ConnectApiManager proxyConnectApiManager;
    @Autowired
    private PCAccountManager pcAccountManager;
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private SessionServerManager sessionServerManager;
    @Autowired
    private OAuth2ResourceManager oAuth2ResourceManager;



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
            mapResult.put(OAuth.OAUTH_ACCESS_TOKEN, accountToken.getAccessToken());
            mapResult.put(OAuth.OAUTH_EXPIRES_TIME, accountToken.getAccessValidTime());
            mapResult.put(OAuth.OAUTH_REFRESH_TOKEN, accountToken.getRefreshToken());

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
    public Result handleConnectCallback(HttpServletRequest req, String providerStr, String ru, String type) {
        Result result = new APIResultSupport(false);
        try {
            int clientId = Integer.valueOf(req.getParameter(CommonConstant.CLIENT_ID));
            String ip = req.getParameter("ip");
            String instanceId = req.getParameter("ts");
            String from = req.getParameter("from"); //手机浏览器会传此参数，响应结果和PC端不一样
            int provider = AccountTypeEnum.getProvider(providerStr);
            //1.获取授权成功后返回的code值
            OAuthAuthzClientResponse oar = OAuthAuthzClientResponse.oauthCodeAuthzResponse(req);
            String code = oar.getCode();
            OAuthConsumer oAuthConsumer = OAuthConsumerFactory.getOAuthConsumer(provider);
            if (oAuthConsumer == null) {
                result.setCode(ErrorUtil.UNSUPPORT_THIRDPARTY);
                return result;
            }
            //根据code值获取access_token
            ConnectConfig connectConfig = connectConfigService.queryConnectConfig(clientId, provider);
            if (connectConfig == null) {
                result.setCode(ErrorUtil.UNSUPPORT_THIRDPARTY);
                return result;
            }
            String redirectUrl = ConnectManagerHelper.constructRedirectURI(clientId, ru, type, instanceId, oAuthConsumer.getCallbackUrl(), ip, from);
            OAuthAccessTokenResponse oauthResponse = connectAuthService.obtainAccessTokenByCode(provider, code, connectConfig,
                    oAuthConsumer, redirectUrl);
            OAuthTokenVO oAuthTokenVO = oauthResponse.getOAuthTokenVO();
            oAuthTokenVO.setIp(ip);

            String openId = oAuthTokenVO.getOpenid();
            // 获取第三方个人资料
            ConnectUserInfoVO connectUserInfoVO;
            if (provider == AccountTypeEnum.QQ.getValue()) {    // QQ根据code获取access_token时，已经取到了个人资料
                connectUserInfoVO = ((QQJSONAccessTokenResponse) oauthResponse).getUserInfo();
            } else {
                connectUserInfoVO = connectAuthService.obtainConnectUserInfo(provider, connectConfig, openId, oAuthTokenVO.getAccessToken(), oAuthConsumer);
            }
            String uniqname = openId;
            if (connectUserInfoVO != null) {
                uniqname = connectUserInfoVO.getNickname();
                oAuthTokenVO.setNickName(uniqname);
            }

            // 创建第三方账号
            Result connectAccountResult = proxyConnectApiManager.buildConnectAccount(providerStr, oAuthTokenVO);

            if (connectAccountResult.isSuccess()) {
                String passportId = (String) connectAccountResult.getModels().get("userid");
                result.setDefaultModel("userid", passportId);
                String userId = (String) connectAccountResult.getModels().get("userid");
                if (type.equals(ConnectTypeEnum.TOKEN.toString())) {
                    Result tokenResult = pcAccountManager.createConnectToken(clientId, userId, instanceId);
                    AccountToken accountToken = (AccountToken) tokenResult.getDefaultModel();
                    if (tokenResult.isSuccess()) {
                        String value = "0|" + accountToken.getAccessToken() + "|" + accountToken.getRefreshToken() + "|" +
                                accountToken.getPassportId() + "|";
                        String responseVm = "/pcaccount/connectlogin";
                        if (!Strings.isNullOrEmpty(from) && "mob".equals(from)) {
                            value = "semob://semob/pairtoken?" + value;
                            responseVm = "/pcaccount/connectmobilelogin";
                        }
                        result.setSuccess(true);
                        result.setDefaultModel("uniqname", uniqname);
                        result.setDefaultModel("result", value);
                        result.setDefaultModel(CommonConstant.RESPONSE_RU, responseVm);
                    } else {
                        result = buildErrorResult(type, ru, ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "create token fail");
                    }
                } else if (type.equals(ConnectTypeEnum.MAPP.toString())) {
                    String token = (String) connectAccountResult.getModels().get("token");
                    String url = buildMAppSuccessRu(ru, userId, token, uniqname);
                    result.setSuccess(true);
                    result.setDefaultModel(CommonConstant.RESPONSE_RU, url);
                } else if (type.equals(ConnectTypeEnum.PC.toString())) {
                    Result tokenResult = pcAccountManager.createConnectToken(clientId, userId, instanceId);
                    AccountToken accountToken = (AccountToken) tokenResult.getDefaultModel();
                    if (tokenResult.isSuccess()) {
                        result.setSuccess(true);

                        AccountBaseInfo accountBaseInfo = null;
                        if (connectUserInfoVO != null) {
                            passportId = AccountTypeEnum.generateThirdPassportId(openId, providerStr);
                            accountBaseInfo = accountBaseInfoService.initConnectAccountBaseInfo(passportId, connectUserInfoVO);// TODO 后续更新其他个人资料，并移至buildConnectAccount()里
                        }
                        if (accountBaseInfo == null || StringUtil.isEmpty(accountBaseInfo.getUniqname())) {
                            uniqname = oAuth2ResourceManager.defaultUniqname(userId);
                        }
                        uniqname = StringUtil.filterSpecialChar(uniqname);  // 昵称需处理,浏览器的js解析不了昵称就会白屏
                        ManagerHelper.setModelForOAuthResult(result, uniqname, accountToken, providerStr);
                        result.setDefaultModel(CommonConstant.RESPONSE_RU, "/oauth2pc/connectlogin");
                    } else {
                        result = buildErrorResult(type, ru, ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "create token fail");
                    }
                } else if (type.equals(ConnectTypeEnum.WAP.toString())) {

                    //写session 数据库
                    Result sessionResult = sessionServerManager.createSession(userId);
                    String sgid=null;
                    if(sessionResult.isSuccess()){
                         sgid= (String) sessionResult.getModels().get("sgid");
                         if (!Strings.isNullOrEmpty(sgid)) {
                            result.setSuccess(true);
                            result.getModels().put("sgid", sgid);
                            ru= buildWapSuccessRu(ru, sgid);
                         }
                    }else {
                        result=buildErrorResult(type, ru, ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "create session fail:"+userId);
                    }
                    result.setDefaultModel(CommonConstant.RESPONSE_RU, ru);
                } else {
                    result.setSuccess(true);
                    result.setDefaultModel(CommonConstant.RESPONSE_RU, ru);
                }
            } else {
                result = buildErrorResult(type, ru, connectAccountResult.getCode(), ErrorUtil.ERR_CODE_MSG_MAP.get(connectAccountResult.getCode()));
            }
        } catch (IOException e) {
            logger.error("read oauth consumer IOException!", e);
            result = buildErrorResult(type, ru, ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "read oauth consumer IOException");
        } catch (ServiceException se) {
            logger.error("query connect config Exception!", se);
            result = buildErrorResult(type, ru, ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "query connect config Exception");
        } catch (OAuthProblemException ope) {
            logger.error("handle oauth authroize code error!", ope);
            result = buildErrorResult(type, ru, ope.getError(), ope.getDescription());
        } catch (Exception exp) {
//            logger.error("handle oauth authroize code system error!", exp);
            result = buildErrorResult(type, ru, ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "system error!");
        }
        return result;
    }

    private String buildMAppSuccessRu(String ru, String userid, String token, String uniqname) {
        Map params = Maps.newHashMap();
        try {
            ru = URLDecoder.decode(ru, CommonConstant.DEFAULT_CONTENT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            logger.error("Url decode Exception! ru:" + ru);
            ru = CommonConstant.DEFAULT_CONNECT_REDIRECT_URL;
        }
        params.put("userid", userid);
        params.put("token", token);
        params.put("uniqname", uniqname);
        ru = QueryParameterApplier.applyOAuthParametersString(ru, params);
        return ru;
    }

    private String buildWapSuccessRu(String ru, String sgid) {
        Map params = Maps.newHashMap();
        try {
            ru = URLDecoder.decode(ru, CommonConstant.DEFAULT_CONTENT_CHARSET);
        } catch (Exception e) {
            logger.error("Url decode Exception! ru:" + ru);
            ru = CommonConstant.DEFAULT_WAP_URL;
        }
        //ru后缀一个sgid
        params.put("sgid", sgid);
        ru = QueryParameterApplier.applyOAuthParametersString(ru, params);
        return ru;
    }

    /*
     * 返回错误情况下的重定向url
     */
    private String buildErrorRu(String type, String ru, String errorCode, String errorText) {
        if (Strings.isNullOrEmpty(ru)) {
            ru = CommonConstant.DEFAULT_CONNECT_REDIRECT_URL;
        }
        if (!Strings.isNullOrEmpty(errorCode) && (ConnectTypeEnum.isMobileApp(type) || ConnectTypeEnum.isMobileWap(type))) {
            Map params = Maps.newHashMap();
            params.put(CommonConstant.RESPONSE_STATUS, errorCode);
            if (Strings.isNullOrEmpty(errorText)) {
                errorText = ErrorUtil.ERR_CODE_MSG_MAP.get(errorCode);
            }
            params.put(CommonConstant.RESPONSE_STATUS_TEXT, errorText);
            ru = QueryParameterApplier.applyOAuthParametersString(ru, params);
        } else if (type.equals(ConnectTypeEnum.TOKEN.toString())) {
            ru = "/pcaccount/connecterr";
        } else if (type.equals(ConnectTypeEnum.PC.toString())) {
            ru = "/oauth2pc/pclogin";
        }
        return ru;
    }

    private Result buildErrorResult(String type, String ru, String errorCode, String errorText) {
        Result result = new APIResultSupport(false);
        result.setCode(errorCode);
        result.setMessage(errorText);
        result.setDefaultModel(CommonConstant.RESPONSE_RU, buildErrorRu(type, ru, errorCode, errorText));
        // type=token返回的错误信息
        if (type.equals(ConnectTypeEnum.TOKEN.toString())) {
            String error = errorCode + "|" + errorText;
            result.setDefaultModel(CommonConstant.RESPONSE_ERROR, error);
        }
        return result;
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
