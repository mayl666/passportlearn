package com.sogou.upd.passport.manager.connect.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.CommonHelper;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.parameter.SSOScanAccountType;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.DateUtil;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.common.utils.SignatureUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.AccountInfoManager;
import com.sogou.upd.passport.manager.account.PCAccountManager;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.ConnectManagerHelper;
import com.sogou.upd.passport.manager.api.connect.SessionServerManager;
import com.sogou.upd.passport.manager.connect.OAuthAuthLoginManager;
import com.sogou.upd.passport.manager.connect.QQOpenAPIManager;
import com.sogou.upd.passport.manager.form.ObtainAccountInfoParams;
import com.sogou.upd.passport.manager.form.connect.AfterAuthParams;
import com.sogou.upd.passport.manager.form.connect.ConnectLoginParams;
import com.sogou.upd.passport.manager.form.connect.ConnectLoginRedirectParams;
import com.sogou.upd.passport.model.OAuthConsumer;
import com.sogou.upd.passport.model.OAuthConsumerFactory;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.common.parameters.QueryParameterApplier;
import com.sogou.upd.passport.oauth2.common.types.ConnectRequest;
import com.sogou.upd.passport.oauth2.common.types.ConnectTypeEnum;
import com.sogou.upd.passport.oauth2.common.types.ResponseTypeEnum;
import com.sogou.upd.passport.oauth2.openresource.parameters.BaiduOAuth;
import com.sogou.upd.passport.oauth2.openresource.parameters.QQOAuth;
import com.sogou.upd.passport.oauth2.openresource.request.OAuthAuthzClientRequest;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthAuthzClientResponse;
import com.sogou.upd.passport.oauth2.openresource.response.accesstoken.OAuthAccessTokenResponse;
import com.sogou.upd.passport.oauth2.openresource.response.accesstoken.QQJSONAccessTokenResponse;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import com.sogou.upd.passport.service.account.TokenService;
import com.sogou.upd.passport.service.app.AppConfigService;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import com.sogou.upd.passport.service.connect.ConnectAuthService;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
    private ConnectAuthService connectAuthService;
    @Autowired
    private PCAccountManager pcAccountManager;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private SessionServerManager sessionServerManager;
    @Autowired
    private AccountInfoManager accountInfoManager;
    @Autowired
    private ConnectApiManager sgConnectApiManager;
    @Autowired
    private QQOpenAPIManager qqOpenAPIManager;
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public String buildConnectLoginURL(ConnectLoginParams connectLoginParams, int provider, String ip, String httpOrHttps, String userAgent) throws OAuthProblemException {
        OAuthConsumer oAuthConsumer;
        OAuthAuthzClientRequest request;
        ConnectConfig connectConfig;
        try {
            oAuthConsumer = OAuthConsumerFactory.getOAuthConsumer(provider);
            if (oAuthConsumer.getCallbackUrl(httpOrHttps) == null) {
                logger.error("callbackUrl is null,callbackurl=" + oAuthConsumer.getCallbackUrl() + ",accesstokenurl=" + oAuthConsumer.getAccessTokenUrl() + ",refreshtokenurl=" + oAuthConsumer.getRefreshAccessTokenUrl() + ",userinfourl=" + oAuthConsumer.getUserInfo());
            }
            // 获取connect配置
            String thirdAppId = connectLoginParams.getThird_appid();
            connectConfig = connectConfigService.queryConnectConfigByAppId(thirdAppId, provider);
            if (connectConfig == null) {
                return CommonConstant.DEFAULT_INDEX_URL;
            }

            String pCallbackUrl = oAuthConsumer.getCallbackUrl(httpOrHttps);
            ConnectLoginRedirectParams redirectParams = new ConnectLoginRedirectParams(connectLoginParams, ip, userAgent);
            String state = UUID.randomUUID().toString();
            String redirectURL;
            if (AccountTypeEnum.WEIXIN.getValue() == provider) { //微信不能把redirectURL里的参数带到回跳接口，需要通过state存储并获取
                redirectURL = pCallbackUrl;
                redisUtils.set(state, redirectParams, 30, TimeUnit.MINUTES);
            } else {
                redirectURL = ConnectManagerHelper.constructRedirectURL(pCallbackUrl, redirectParams);
            }
            String scope = connectConfig.getScope();
            String appKey = connectConfig.getAppKey();
            String connectType = connectLoginParams.getType();

            // 重新填充display，如果display为空，根据终端自动赋值；如果display不为空，则使用display
            String display = connectLoginParams.getDisplay();
            display = Strings.isNullOrEmpty(display) ? fillDisplay(connectType, connectLoginParams.getFrom(), provider) : display;
            //若provider=QQ display=wml、xhtml已废弃，qq不支持wap接口
            if (ConnectRequest.isQQWapRequest(connectLoginParams.getProvider(), display)) {
                display = "mobile";
            }

            OAuthAuthzClientRequest.AuthenticationRequestBuilder builder = OAuthAuthzClientRequest
                    .authorizationLocation(oAuthConsumer.getWebUserAuthzUrl()).setAppKey(appKey, provider)
                    .setRedirectURI(redirectURL)
                    .setResponseType(ResponseTypeEnum.CODE).setScope(scope)
                    .setDisplay(display, provider).setForceLogin(connectLoginParams.isForcelogin(), provider)
                    .setState(state);

            if (AccountTypeEnum.QQ.getValue() == provider) {
                builder.setShowAuthItems(QQOAuth.NO_AUTH_ITEMS);       // qq为搜狗产品定制化页面，隐藏授权信息
                if (!Strings.isNullOrEmpty(connectLoginParams.getViewPage())) {
                    builder.setViewPage(connectLoginParams.getViewPage());       // qq为搜狗产品定制化页面--输入法使用
                }
            }
            request = builder.buildQueryMessage(OAuthAuthzClientRequest.class);
        } catch (IOException e) {
            logger.error("read oauth consumer IOException!", e);
            throw new OAuthProblemException(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "read oauth consumer IOException!");
        } catch (ServiceException se) {
            logger.error("query connect config Exception!", se);
            throw new OAuthProblemException(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "query connect config Exception!");
        }
        return request.getLocationUri();
    }

    @Override
    public Result handleConnectCallback(ConnectLoginRedirectParams redirectParams, HttpServletRequest req, String providerStr, String httpOrHttps) {
        Result result = new APIResultSupport(false);
        String v = redirectParams.getV(); //浏览器根据v判断展示新旧UI样式
        String ru = redirectParams.getRu();
        String type = redirectParams.getType();
        String ip = redirectParams.getIp();
        String instanceId = redirectParams.getTs();
        String from = redirectParams.getFrom(); //手机浏览器会传此参数，响应结果和PC端不一样
        String thirdInfo = redirectParams.getThirdInfo(); //用于SDK端请求，返回搜狗用户信息或者低三方用户信息；
        int provider = AccountTypeEnum.getProvider(providerStr);
        String thirdAppId = redirectParams.getThird_appid(); //不为空时代表应用使用独立appid；
        try {
            int clientId = Integer.valueOf(redirectParams.getClient_id());
            //1.获取授权成功后返回的code值
            OAuthAuthzClientResponse oar = OAuthAuthzClientResponse.oauthCodeAuthzResponse(req);
            String code = oar.getCode();
            OAuthConsumer oAuthConsumer = OAuthConsumerFactory.getOAuthConsumer(provider);
            if (oAuthConsumer == null) {
                result.setCode(ErrorUtil.ERR_CODE_CONNECT_UNSUPPORT_THIRDPARTY);
                return result;
            }
            //根据code值获取access_token
            ConnectConfig connectConfig = connectConfigService.queryConnectConfigByAppId(thirdAppId, provider);
            if (connectConfig == null) {
                result.setCode(ErrorUtil.ERR_CODE_CONNECT_UNSUPPORT_THIRDPARTY);
                return result;
            }
            String redirectUrl;
            String pCallbackUrl = oAuthConsumer.getCallbackUrl(httpOrHttps);
            if (AccountTypeEnum.WEIXIN.getValue() == provider) { //微信不能把redirectURL里的参数带到回跳接口
                redirectUrl = pCallbackUrl;
            } else {
                redirectUrl = ConnectManagerHelper.constructRedirectURL(pCallbackUrl, redirectParams);
            }
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
                if (provider == AccountTypeEnum.BAIDU.getValue()) {     // 百度 oauth2.0授权的openid需要从用户信息接口获取
                    setBaiduOpenid(connectUserInfoVO, oAuthTokenVO);
                } else if (provider == AccountTypeEnum.WEIXIN.getValue()) {  //微信的用户唯一标示unionid需要从用户信息接口获取
                    oAuthTokenVO.setUnionId(connectUserInfoVO.getUnionid());
                }
            }
            String uniqname = openId;
            if (connectUserInfoVO != null) {
                uniqname = connectUserInfoVO.getNickname();
                oAuthTokenVO.setNickName(uniqname);
                oAuthTokenVO.setConnectUserInfoVO(connectUserInfoVO);
            }
            Result connectAccountResult = sgConnectApiManager.buildConnectAccount(connectConfig.getAppKey(), provider, oAuthTokenVO);

            if (connectAccountResult.isSuccess()) {
                ConnectToken connectToken = (ConnectToken) connectAccountResult.getModels().get("connectToken");
                String passportId = connectToken.getPassportId();
                result.setDefaultModel("userid", passportId);
                String userId = passportId;
                if (provider != AccountTypeEnum.QQ.getValue()) {
                    //更新第三方个人资料缓存
                    connectAuthService.initialOrUpdateConnectUserInfo(userId, connectUserInfoVO);
                }

                if (ConnectTypeEnum.TOKEN.toString().equals(type)) {
                    Result tokenResult = pcAccountManager.createAccountToken(userId, instanceId, clientId);
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
                        result = buildErrorResult(type, ru, ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "create token fail", v);
                    }
                } else if (ConnectTypeEnum.MAPP.toString().equals(type)) {
                    if (!Strings.isNullOrEmpty(from) && "sso".equals(from)) {
                        // SDK1.08及之前的版本使用type=mapp&from=sso，1.09及之后版本使用type=wap
                        // TODO 调用量少时去除这块兼容
                        result = buildWapResult(result, connectUserInfoVO, userId, passportId, type, ru, thirdInfo, uniqname, v);
                    } else {
                        String token = tokenService.saveWapToken(userId);
                        String url = buildMAppSuccessRu(ru, userId, token, uniqname);
                        result.setSuccess(true);
                        result.setDefaultModel(CommonConstant.RESPONSE_RU, url);
                    }
                } else if (ConnectTypeEnum.MOBILE.toString().equals(type)) {
                    String s_m_u = getSMU(userId);
                    String url = buildMOBILESuccessRu(ru, userId, s_m_u, uniqname);
                    result.setSuccess(true);
                    result.setDefaultModel(CommonConstant.RESPONSE_RU, url);
                } else if (ConnectTypeEnum.PC.toString().equals(type)) {
                    Result tokenResult = pcAccountManager.createAccountToken(userId, instanceId, clientId);
                    AccountToken accountToken = (AccountToken) tokenResult.getDefaultModel();
                    if (tokenResult.isSuccess()) {
                        uniqname = (String) connectAccountResult.getModels().get("uniqName");
                        uniqname = StringUtil.filterEmoji(uniqname);  // 昵称需处理,浏览器的js解析不了昵称就会白屏
                        ManagerHelper.setModelForOAuthResult(result, uniqname, accountToken, providerStr);
                        result.setSuccess(true);
                        if (CommonHelper.isNewVersionSE(v)) {
                            result.setDefaultModel(CommonConstant.RESPONSE_RU, "/oauth2pc_new/connectlogin");
                        } else {
                            result.setDefaultModel(CommonConstant.RESPONSE_RU, "/oauth2pc/connectlogin");
                        }
                    } else {
                        result = buildErrorResult(type, ru, ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "create token fail", v);
                    }
                } else if (ConnectTypeEnum.WAP.toString().equals(type)) {
                    //写session 数据库
                    result = buildWapResult(result, connectUserInfoVO, userId, passportId, type, ru, thirdInfo, uniqname, v);
                } else {
                    result.setSuccess(true);
                    result.setDefaultModel(CommonConstant.RESPONSE_RU, ru);
                    result.setDefaultModel("refnick", uniqname);
                }
            } else {
                result = buildErrorResult(type, ru, connectAccountResult.getCode(), ErrorUtil.ERR_CODE_MSG_MAP.get(connectAccountResult.getCode()), v);
            }
        } catch (IOException e) {
            logger.error("read oauth consumer IOException!", e);
            result = buildErrorResult(type, ru, ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "read oauth consumer IOException", v);
        } catch (ServiceException se) {
            logger.error("query connect config Exception!", se);
            result = buildErrorResult(type, ru, ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "query connect config Exception", v);
        } catch (OAuthProblemException ope) {
            logger.warn("handle oauth authroize code error!", ope);
            result = buildErrorResult(type, ru, ope.getError(), ope.getDescription(), v);
        } catch (Exception exp) {
            result = buildErrorResult(type, ru, ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "system error!", v);
        }
        return result;
    }

    private Result buildWapResult(Result result, ConnectUserInfoVO connectUserInfoVO, String userId, String passportId, String type, String ru, String thirdInfo, String uniqname, String v) {
        Result sessionResult = sessionServerManager.createSession(userId);
        if (!sessionResult.isSuccess()) {
            result = buildErrorResult(type, ru, ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "create session fail:" + userId, v);
            return result;
        }
        String sgid = (String) sessionResult.getModels().get(LoginConstant.COOKIE_SGID);
        if (!Strings.isNullOrEmpty(sgid)) {
            result.setSuccess(true);
            result.getModels().put(LoginConstant.COOKIE_SGID, sgid);
            ru = buildWapSuccessRu(ru, sgid, userId);
        } else {
            result = buildErrorResult(type, ru, ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "create session fail:" + userId, v);
            return result;
        }
        String avatarSmall = "", avatarMiddle = "", avatarLarge = "", sex = "";
        if (!Strings.isNullOrEmpty(thirdInfo)) {
            if ("0".equals(thirdInfo)) {
                //获取搜狗用户信息
                ObtainAccountInfoParams params = new ObtainAccountInfoParams(String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID), passportId, "uniqname,avatarurl,sex");
                params.setImagesize("30,50,180");
                result = accountInfoManager.getUserInfo(params);
                if (result.isSuccess()) {
                    avatarLarge = (String) result.getModels().get("large_avatar");
                    avatarMiddle = (String) result.getModels().get("mid_avatar");
                    avatarSmall = (String) result.getModels().get("tiny_avatar");
                    uniqname = (String) result.getModels().get("uniqname");
                    sex = (String) result.getModels().get("sex");
                }
            } else {
                avatarLarge = connectUserInfoVO.getAvatarLarge();
                avatarMiddle = connectUserInfoVO.getAvatarMiddle();
                avatarSmall = connectUserInfoVO.getAvatarSmall();
                sex = String.valueOf(connectUserInfoVO.getGender());
            }
            ru = buildWapUserInfoSuccessRu(ru, sgid, uniqname, sex, avatarLarge, avatarMiddle, avatarSmall, userId);
        }
        result.setDefaultModel(CommonConstant.RESPONSE_RU, ru);
        return result;
    }

    @Override
    public Result handleSSOAfterauth(HttpServletRequest req, AfterAuthParams authParams, String providerStr, String ip) {
        Result result = new APIResultSupport(false);

        try {
            String openId = authParams.getOpenid();
            String accessToken = authParams.getAccess_token();
            String refreshToken = authParams.getRefresh_token();
            long expiresIn = authParams.getExpires_in();
            int clientId = authParams.getClient_id();
            int isthird = authParams.getIsthird();
            String instance_id = req.getParameter("instance_id");
            String appidtypeString = req.getParameter("appid_type");
            Integer appidType = appidtypeString == null ? null : Integer.valueOf(appidtypeString);
            int provider = AccountTypeEnum.getProvider(providerStr);
            String tcode = authParams.getTcode();
            String thirdAppId = req.getParameter(CommonConstant.THIRD_APPID); //不为空时代表应用使用独立appid；
            String type = authParams.getType();
            if (AccountTypeEnum.isConnect(provider)) {
                ConnectConfig connectConfig = queryConnectConfig(thirdAppId, appidType, clientId, provider);
                if (connectConfig == null) {
                    result.setCode(ErrorUtil.ERR_CODE_CONNECT_UNSUPPORT_THIRDPARTY);
                    return result;
                }
                OAuthConsumer oAuthConsumer = OAuthConsumerFactory.getOAuthConsumer(provider);
                if (oAuthConsumer == null && AccountTypeEnum.HUAWEI.getValue() != provider) {   //华为账号不用取oAuthConsumer
                    result.setCode(ErrorUtil.ERR_CODE_CONNECT_UNSUPPORT_THIRDPARTY);
                    return result;
                }
                if (!Strings.isNullOrEmpty(tcode)) {
                    OAuthAccessTokenResponse oauthResponse = connectAuthService.obtainAccessTokenByCode(provider, tcode, connectConfig,
                            oAuthConsumer, "");
                    OAuthTokenVO oAuthTokenVO = oauthResponse.getOAuthTokenVO();
                    openId = oAuthTokenVO.getOpenid();
                    accessToken = oAuthTokenVO.getAccessToken();
                    expiresIn = oAuthTokenVO.getExpiresIn();
                    refreshToken = oAuthTokenVO.getRefreshToken();
                } else {
                    //验证code是否有效
                    result = checkCodeIsCorrect(authParams, req);
                    if (!result.isSuccess()) {
                        return result;
                    }
                }
                // 获取第三方个人资料
                OAuthTokenVO oAuthTokenVO = new OAuthTokenVO();
                ConnectUserInfoVO connectUserInfoVO = new ConnectUserInfoVO();
                if (AccountTypeEnum.HUAWEI.getValue() == provider) {  //华为账号只有昵称，且由SDK传入
                    String uniqname = authParams.getUniqname();
                    connectUserInfoVO.setNickname(uniqname);
                } else {
                    if (qqManagerCooperate(type, provider)) {    // QQ管家和输入法合作，传入openkey(也就是accesstoken）来登录，使用开平API，不能使用互联API，openkey有效期为2小时
                       connectUserInfoVO =qqOpenAPIManager.getQQUserInfo(openId, accessToken, connectConfig);
                    } else {
                        connectUserInfoVO = connectAuthService.obtainConnectUserInfo(provider, connectConfig, openId, accessToken, oAuthConsumer);
                    }
                    if (connectUserInfoVO == null) {
                        result.setCode(ErrorUtil.ERR_CODE_CONNECT_GET_USERINFO_ERROR);
                        return result;
                    }

                }
                String uniqname = connectUserInfoVO.getNickname();
                oAuthTokenVO.setAccessToken(accessToken);
                oAuthTokenVO.setRefreshToken(refreshToken);
                oAuthTokenVO.setOpenid(openId);
                oAuthTokenVO.setExpiresIn(expiresIn);
                oAuthTokenVO.setNickName(uniqname);
                oAuthTokenVO.setIp(ip);
                oAuthTokenVO.setConnectUserInfoVO(connectUserInfoVO);
                oAuthTokenVO.setUnionId(connectUserInfoVO.getUnionid());
                Result connectAccountResult = sgConnectApiManager.buildConnectAccount(connectConfig.getAppKey(), provider, oAuthTokenVO);
                ConnectToken connectToken;
                if (!connectAccountResult.isSuccess()) {
                    return connectAccountResult;
                } else {
                    connectToken = (ConnectToken) connectAccountResult.getModels().get("connectToken");
                    if (connectToken == null) {
                        return connectAccountResult;
                    }
                }
                String passportId = connectToken.getPassportId();
                if (Strings.isNullOrEmpty(passportId)) {
                    result.setCode(ErrorUtil.ERR_CODE_SSO_After_Auth_FAILED);
                    return result;
                }
                //如果没有从搜狗方(数据库或缓存)获取到第三方的个人信息，则从第三方VO中获取个人头像信息,默认值为false,不从VO中拿
                boolean isConnectUserInfo = false;
                //isthird=0或1；0表示去搜狗通行证个人信息，1表示获取第三方个人信息
                if (isthird == 0) {
                    ObtainAccountInfoParams params = new ObtainAccountInfoParams(String.valueOf(clientId), passportId, "uniqname,avatarurl,sex");
                    params.setImagesize("30,50,180");
                    result = accountInfoManager.getUserInfo(params);
                    if (result.isSuccess()) {
                        String img180 = (String) result.getModels().get("large_avatar");
                        String img50 = (String) result.getModels().get("mid_avatar");
                        String img30 = (String) result.getModels().get("tiny_avatar");
                        uniqname = (String) result.getModels().get("uniqname");
                        String gender = (String) result.getModels().get("sex");

                        result.getModels().put("large_avatar", Strings.isNullOrEmpty(img180) ? "" : img180);
                        result.getModels().put("mid_avatar", Strings.isNullOrEmpty(img50) ? "" : img50);
                        result.getModels().put("tiny_avatar", Strings.isNullOrEmpty(img30) ? "" : img30);
                        result.getModels().put("uniqname", Strings.isNullOrEmpty(uniqname) ? "" : uniqname);
                        result.getModels().put("gender", Strings.isNullOrEmpty(gender) ? 0 : Integer.parseInt(gender));
                    } else {
                        isConnectUserInfo = true;
                    }
                } else {
                    isConnectUserInfo = true;
                }
                if (isConnectUserInfo) {
                    if (connectUserInfoVO != null) {
                        result.getModels().put("large_avatar", connectUserInfoVO.getAvatarLarge());
                        result.getModels().put("mid_avatar", connectUserInfoVO.getAvatarMiddle());
                        result.getModels().put("tiny_avatar", connectUserInfoVO.getAvatarSmall());
                        result.getModels().put("uniqname", connectUserInfoVO.getNickname());
                        result.getModels().put("gender", connectUserInfoVO.getGender());
                    }
                }
                //写session 数据库
                if (ConnectTypeEnum.WAP.toString().equals(type)) {
                    Result sessionResult = sessionServerManager.createSession(passportId);
                    if (sessionResult.isSuccess()) {
                        String sgid = (String) sessionResult.getModels().get(LoginConstant.COOKIE_SGID);
                        if (!Strings.isNullOrEmpty(sgid)) {
                            result.getModels().put(LoginConstant.COOKIE_SGID, sgid);
                            result.setSuccess(true);
                            result.setMessage("success");
                            removeParam(result);
                        } else {
                            result.setCode(ErrorUtil.ERR_CODE_CREATE_SGID_FAILED);
                        }
                    }
                } else if (qqManagerCooperate(type, provider)) {
                    Result tokenResult = pcAccountManager.createAccountToken(passportId, instance_id, clientId);
                    AccountToken accountToken = (AccountToken) tokenResult.getDefaultModel();
                    if (tokenResult.isSuccess()) {
                        result.setDefaultModel("token", accountToken.getAccessToken());
                        result.setDefaultModel("refreshToken", accountToken.getRefreshToken());
                        result.setSuccess(true);
                        result.setMessage("success");
                        removeParam(result);
                    } else {
                        result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
                        result.setMessage("create token fail");
                    }
                }
                result.getModels().put("userid", passportId);
            } else {
                result.setCode(ErrorUtil.ERR_CODE_SSO_After_Auth_FAILED);
            }
        } catch (IOException e) {
            logger.error("read oauth consumer IOException!", e);
            result = buildErrorResult(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "read oauth consumer IOException");
        } catch (ServiceException se) {
            logger.error("query connect config Exception!", se);
            result = buildErrorResult(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "query connect config Exception");
        } catch (OAuthProblemException ope) {
            logger.error("handle oauth authroize code error!", ope);
            result = buildErrorResult(ope.getError(), ope.getDescription());
        } catch (Exception exp) {
            logger.error("handle oauth authroize code system error!", exp);
            result = buildErrorResult(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "handle oauth authroize code system error!");
        }
        return result;
    }

    /**
     * 最早版本没有thirdAppId和appIdType参数，使用passport的appId
     * 然后版本只有appIdType参数，当appIdType=1时，找到clientId对应的appId
     * 安卓 V1.1和IOS V2.0之后只有thirdAppId参数，可以使用应用独立appId
     *
     * @param thirdAppId
     * @param appidType
     * @param clientId
     * @param provider
     * @return
     */
    private ConnectConfig queryConnectConfig(String thirdAppId, Integer appidType, int clientId, int provider) {
        ConnectConfig connectConfig;
        if (!Strings.isNullOrEmpty(thirdAppId)) {
            return connectConfigService.queryConnectConfigByAppId(thirdAppId, provider);
        }
        if (appidType == null) {
            connectConfig = connectConfigService.queryDefaultConnectConfig(provider);
        } else {
            if (appidType == 1) {
                connectConfig = connectConfigService.queryConnectConfigByClientId(clientId, provider);
            } else {
                connectConfig = connectConfigService.queryDefaultConnectConfig(provider);
            }
        }
        return connectConfig;
    }

    private void removeParam(Result result) {
        result.getModels().remove("img_30");
        result.getModels().remove("img_50");
        result.getModels().remove("img_180");
        result.getModels().remove("avatarurl");
    }

    private Result buildErrorResult(String errorCode, String errorText) {
        Result result = new APIResultSupport(false);
        result.setCode(errorCode);
        result.setMessage(errorText);
        return result;
    }

    private String buildMAppSuccessRu(String ru, String userid, String token, String uniqname) {
        Map params = Maps.newHashMap();
        try {
            ru = URLDecoder.decode(ru, CommonConstant.DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            logger.error("Url decode Exception! ru:" + ru);
            ru = CommonConstant.DEFAULT_INDEX_URL;
        }
        params.put("userid", userid);
        params.put("token", token);
        params.put("uniqname", uniqname);
        ru = QueryParameterApplier.applyOAuthParametersString(ru, params);
        return ru;
    }

    private String getSMU(String userId) {
        int days = gethDays();
        String digestStr = getDigest(userId, days);
        return userId + "|" + days + "|" + digestStr;
    }

    /**
     * @return 相对于2011-1-1的天数
     */
    private int gethDays() {
        Date strartDate = DateUtil.parse("2011-01-01", DateUtil.DATE_FMT_3);
        Date endDate = new Date();
        int dateNum = DateUtil.getDayNum(strartDate, endDate);
        return dateNum;
    }

    private String getDigest(String userId, int days) {
        int[] WAP_SIG_OFFSET = {60, 126, 15, 85, 19, 81, 48, 71, 50, 22};
        String sha = DigestUtils.sha512Hex(userId + "|" + days + "|" + "sohu.wap.secretkey#@!%^@");
        char[] chars = new char[WAP_SIG_OFFSET.length];

        for (int i = 0; i < WAP_SIG_OFFSET.length; i++) {
            chars[i] = sha.charAt(WAP_SIG_OFFSET[i]);
        }
        String digestNo = new String(chars);
        return digestNo;
    }

    private String buildMOBILESuccessRu(String ru, String userid, String s_m_u, String un) {
        Map params = Maps.newHashMap();
        try {
            ru = URLDecoder.decode(ru, CommonConstant.DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            logger.error("Url decode Exception! ru:" + ru);
            ru = CommonConstant.DEFAULT_INDEX_URL;
        }
        params.put("s_m_u", s_m_u);
        params.put("un", un);
        ru = QueryParameterApplier.applyOAuthParametersString(ru, params);
        return ru;
    }

    private String buildWapSuccessRu(String ru, String sgid, String userid) {
        Map params = Maps.newHashMap();
        String acountType = SSOScanAccountType.getSSOScanAccountType(userid);
        String acountTypeEncode = null;
        try {
            ru = URLDecoder.decode(ru, CommonConstant.DEFAULT_CHARSET);
            acountTypeEncode = URLEncoder.encode(acountType, CommonConstant.DEFAULT_CHARSET);
        } catch (Exception e) {
            logger.error("Url decode Exception! ru:" + ru);
            ru = CommonConstant.DEFAULT_WAP_URL;
        }
        //ru后缀一个sgid
        params.put(LoginConstant.COOKIE_SGID, sgid);
        if (null != acountTypeEncode) {
            params.put(LoginConstant.SSO_ACCOUNT_TYPE, acountTypeEncode);
        }
        ru = QueryParameterApplier.applyOAuthParametersString(ru, params);
        return ru;
    }

    private String buildWapUserInfoSuccessRu(String ru, String sgid, String uniqname, String sex, String avatarLarge, String avatarMiddle, String avatarSmall, String userId) {
        Map params = Maps.newHashMap();
        try {
            ru = URLDecoder.decode(ru, CommonConstant.DEFAULT_CHARSET);
        } catch (Exception e) {
            logger.error("Url decode Exception! ru:" + ru);
            ru = CommonConstant.DEFAULT_WAP_URL;
        }
        //ru后缀一个sgid
        params.put(LoginConstant.COOKIE_SGID, sgid);
        params.put("uniqname", uniqname);
        params.put("gender", sex);
        params.put("avatarLarge", avatarLarge);
        params.put("avatarMiddle", avatarMiddle);
        params.put("avatarSmall", avatarSmall);
        params.put("userid", userId);
        ru = QueryParameterApplier.applyOAuthParametersString(ru, params);
        return ru;
    }

    /*
     * 返回错误情况下的重定向url
     */
    private String buildErrorRu(String type, String ru, String errorCode, String errorText, String v) {
        if (Strings.isNullOrEmpty(ru)) {
            ru = CommonConstant.DEFAULT_INDEX_URL;
        }
        if (!Strings.isNullOrEmpty(errorCode) && (ConnectTypeEnum.isMobileApp(type) || ConnectTypeEnum.isMobileWap(type))) {
            Map params = Maps.newHashMap();
            params.put(CommonConstant.RESPONSE_STATUS, errorCode);
            if (Strings.isNullOrEmpty(errorText)) {
                errorText = ErrorUtil.ERR_CODE_MSG_MAP.get(errorCode);
            }
            params.put(CommonConstant.RESPONSE_STATUS_TEXT, errorText);
            ru = QueryParameterApplier.applyOAuthParametersString(ru, params);
        } else if (ConnectTypeEnum.TOKEN.toString().equals(type)) {
            ru = "/pcaccount/connecterr";
        } else if (ConnectTypeEnum.PC.toString().equals(type)) {
            if (CommonHelper.isNewVersionSE(v)) {
                ru = "/oauth2pc_new/pclogin";
            } else {
                ru = "/oauth2pc/pclogin";
            }
        }
        return ru;
    }

    private Result buildErrorResult(String type, String ru, String errorCode, String errorText, String v) {
        Result result = new APIResultSupport(false);
        result.setCode(errorCode);
        result.setMessage(errorText);
        result.setDefaultModel(CommonConstant.RESPONSE_RU, buildErrorRu(type, ru, errorCode, errorText, v));
        // type=token返回的错误信息
        if (ConnectTypeEnum.TOKEN.toString().equals(type)) {
            String error = errorCode + "|" + errorText;
            result.setDefaultModel(CommonConstant.RESPONSE_ERROR, error);
        }
        return result;
    }

    private void setBaiduOpenid(ConnectUserInfoVO connectUserInfoVO, OAuthTokenVO oAuthTokenVO) {
        String baiduOpenid = (String) connectUserInfoVO.getOriginal().get(BaiduOAuth.OPENID);
        if (!Strings.isNullOrEmpty(baiduOpenid)) {
            oAuthTokenVO.setOpenid(baiduOpenid);
        }
    }

    //openid+ client_id +access_token+expires_in+isthird +instance_id+ client _secret
    private Result checkCodeIsCorrect(AfterAuthParams params, HttpServletRequest req) {
        Result result = new APIResultSupport(false);
        AppConfig appConfig = appConfigService.queryAppConfigByClientId(params.getClient_id());
        if (appConfig != null) {
            String secret = appConfig.getClientSecret();

            TreeMap map = new TreeMap();
            map.put("openid", params.getOpenid());
            map.put("access_token", params.getAccess_token());
            map.put("expires_in", Long.toString(params.getExpires_in()));
            map.put("client_id", Integer.toString(params.getClient_id()));
            //处理默认值方式
            Object isthird = req.getParameterMap().get("isthird");
            if (isthird != null) {
                map.put("isthird", Integer.toString(params.getIsthird()));
            }
            Object refresh_token = req.getParameterMap().get("refresh_token");
            if (refresh_token != null && !refresh_token.equals("")) {
                map.put("refresh_token", params.getRefresh_token());
            }
            map.put("instance_id", params.getInstance_id());
            String appidType = req.getParameter("appid_type");
            if (!Strings.isNullOrEmpty(appidType)) {
                map.put("appid_type", appidType);
            }
            //计算默认的code
            String code = "";
            try {
                code = SignatureUtils.generateSignature(map, secret);
            } catch (Exception e) {
                logger.error("calculate default code error", e);
            }

            if (code.equalsIgnoreCase(params.getCode())) {
                result.setSuccess(true);
                result.setMessage("接口code签名正确！");
            } else {
                result.setCode(ErrorUtil.INTERNAL_REQUEST_INVALID);
            }
        } else {
            result.setCode(ErrorUtil.INVALID_CLIENTID);
        }
        return result;
    }

    /*
     * 根据type和provider重新填充display
     */
    private String fillDisplay(String type, String from, int provider) {
        String display = "";
        if (ConnectTypeEnum.isMobileApp(type) || isMobileDisplay(type, from) || ConnectTypeEnum.isMobileWap(type)) {
            switch (provider) {
                case 5:  // 人人
                    display = "touch";
                    break;
                case 6:  // 淘宝
                    display = "wap";
                    break;
                default:
                    display = "mobile";
                    break;
            }
        }
        return display;
    }

    private boolean isMobileDisplay(String type, String from) {
        return ConnectTypeEnum.TOKEN.toString().equals(type) && "mob".equalsIgnoreCase(from)
                || ConnectTypeEnum.MOBILE.toString().equals(type);
    }

    private boolean qqManagerCooperate(String type, int provider) {
        return ConnectTypeEnum.TOKEN.toString().equals(type) && AccountTypeEnum.QQ.getValue() == provider;
    }

}
