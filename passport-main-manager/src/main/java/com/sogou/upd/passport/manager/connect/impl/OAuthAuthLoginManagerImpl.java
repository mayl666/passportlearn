package com.sogou.upd.passport.manager.connect.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.DateUtil;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.AccountInfoManager;
import com.sogou.upd.passport.manager.account.PCAccountManager;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.ConnectManagerHelper;
import com.sogou.upd.passport.manager.api.connect.SessionServerManager;
import com.sogou.upd.passport.manager.connect.OAuthAuthLoginManager;
import com.sogou.upd.passport.manager.form.ObtainAccountInfoParams;
import com.sogou.upd.passport.model.OAuthConsumer;
import com.sogou.upd.passport.model.OAuthConsumerFactory;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.common.parameters.QueryParameterApplier;
import com.sogou.upd.passport.oauth2.common.types.ConnectTypeEnum;
import com.sogou.upd.passport.oauth2.openresource.parameters.BaiduOAuth;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthAuthzClientResponse;
import com.sogou.upd.passport.oauth2.openresource.response.accesstoken.OAuthAccessTokenResponse;
import com.sogou.upd.passport.oauth2.openresource.response.accesstoken.QQJSONAccessTokenResponse;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import com.sogou.upd.passport.service.account.TokenService;
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

    @Override
    public Result handleConnectCallback(HttpServletRequest req, String providerStr, String ru, String type, String httpOrHttps) {
        Result result = new APIResultSupport(false);
        try {
            int clientId = Integer.valueOf(req.getParameter(CommonConstant.CLIENT_ID));
            String ip = req.getParameter("ip");
            String instanceId = req.getParameter("ts");
            String from = req.getParameter("from"); //手机浏览器会传此参数，响应结果和PC端不一样
            String thirdInfo = req.getParameter("thirdInfo"); //用于SDK端请求，返回搜狗用户信息或者低三方用户信息；
            String domain = req.getParameter("domain"); //导航qq登陆，会传此参数
            int provider = AccountTypeEnum.getProvider(providerStr);

            //1.获取授权成功后返回的code值
            OAuthAuthzClientResponse oar = OAuthAuthzClientResponse.oauthCodeAuthzResponse(req);
            String code = oar.getCode();
            OAuthConsumer oAuthConsumer = OAuthConsumerFactory.getOAuthConsumer(provider);
            if (oAuthConsumer == null) {
                result.setCode(ErrorUtil.ERR_CODE_CONNECT_UNSUPPORT_THIRDPARTY);
                return result;
            }
            //根据code值获取access_token
            ConnectConfig connectConfig = connectConfigService.queryConnectConfig(clientId, provider);
            if (connectConfig == null) {
                result.setCode(ErrorUtil.ERR_CODE_CONNECT_UNSUPPORT_THIRDPARTY);
                return result;
            }
            String redirectUrl = ConnectManagerHelper.constructRedirectURI(clientId, ru, type, instanceId, oAuthConsumer.getCallbackUrl(httpOrHttps), ip, from, domain, thirdInfo);
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
                        result = buildErrorResult(type, ru, ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "create token fail");
                    }
                } else if (ConnectTypeEnum.MAPP.toString().equals(type)) {
                    if (!Strings.isNullOrEmpty(from) && "sso".equals(from)) {
                        String sgid = "", avatarSmall = "", avatarMiddle = "", avatarLarge = "", sex = "";
                        Result sessionResult = sessionServerManager.createSession(userId);
                        if (!sessionResult.isSuccess()) {
                            result = buildErrorResult(type, ru, ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "create session fail:" + userId);
                            return result;
                        }
                        sgid = (String) sessionResult.getModels().get(LoginConstant.COOKIE_SGID);
                        result.setSuccess(true);
                        result.getModels().put(LoginConstant.COOKIE_SGID, sgid);

                        if (!Strings.isNullOrEmpty(thirdInfo) && "0".equals(thirdInfo)) {
                            //获取搜狗用户信息
                            ObtainAccountInfoParams params = new ObtainAccountInfoParams();
                            params.setUsername(passportId);
                            params.setClient_id(String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID));
                            params.setFields("uniqname,sex");
                            result = accountInfoManager.getUserInfo(params);
                            if (result.isSuccess()) {
                                avatarLarge = (String) result.getModels().get("img_180");
                                avatarMiddle = (String) result.getModels().get("img_50");
                                avatarSmall = (String) result.getModels().get("img_30");
                                uniqname = (String) result.getModels().get("uniqname");
                                sex = (String) result.getModels().get("sex");
                            }
                        } else {
                            avatarLarge = connectUserInfoVO.getAvatarLarge();
                            avatarMiddle = connectUserInfoVO.getAvatarMiddle();
                            avatarSmall = connectUserInfoVO.getAvatarSmall();
                            sex = String.valueOf(connectUserInfoVO.getGender());
                        }

                        String url = buildSSOSuccessRu(ru, sgid, uniqname, sex, avatarLarge, avatarMiddle, avatarSmall, userId);
                        result.setDefaultModel(CommonConstant.RESPONSE_RU, url);
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
                        result.setDefaultModel(CommonConstant.RESPONSE_RU, "/oauth2pc/connectlogin");
                    } else {
                        result = buildErrorResult(type, ru, ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "create token fail");
                    }
                } else if (ConnectTypeEnum.WAP.toString().equals(type)) {
                    //写session 数据库
                    Result sessionResult = sessionServerManager.createSession(userId);
                    String sgid = null;
                    if (sessionResult.isSuccess()) {
                        sgid = (String) sessionResult.getModels().get(LoginConstant.COOKIE_SGID);
                        if (!Strings.isNullOrEmpty(sgid)) {
                            result.setSuccess(true);
                            result.getModels().put(LoginConstant.COOKIE_SGID, sgid);
                            ru = buildWapSuccessRu(ru, sgid);
                        }
                    } else {
                        result = buildErrorResult(type, ru, ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION, "create session fail:" + userId);
                    }
                    result.setDefaultModel(CommonConstant.RESPONSE_RU, ru);
                } else {
                    result.setSuccess(true);
                    result.setDefaultModel(CommonConstant.RESPONSE_RU, ru);
                    result.setDefaultModel("refnick", uniqname);

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
            logger.warn("handle oauth authroize code error!", ope);
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
            ru = URLDecoder.decode(ru, CommonConstant.DEFAULT_CHARSET);
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
            ru = CommonConstant.DEFAULT_CONNECT_REDIRECT_URL;
        }
        params.put("s_m_u", s_m_u);
        params.put("un", un);
        ru = QueryParameterApplier.applyOAuthParametersString(ru, params);
        return ru;
    }

    private String buildWapSuccessRu(String ru, String sgid) {
        Map params = Maps.newHashMap();
        try {
            ru = URLDecoder.decode(ru, CommonConstant.DEFAULT_CHARSET);
        } catch (Exception e) {
            logger.error("Url decode Exception! ru:" + ru);
            ru = CommonConstant.DEFAULT_WAP_URL;
        }
        //ru后缀一个sgid
        params.put(LoginConstant.COOKIE_SGID, sgid);
        ru = QueryParameterApplier.applyOAuthParametersString(ru, params);
        return ru;
    }

    private String buildSSOSuccessRu(String ru, String sgid, String uniqname, String sex, String avatarLarge, String avatarMiddle, String avatarSmall, String userId) {
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
        } else if (ConnectTypeEnum.TOKEN.toString().equals(type)) {
            ru = "/pcaccount/connecterr";
        } else if (ConnectTypeEnum.PC.toString().equals(type)) {
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

}
