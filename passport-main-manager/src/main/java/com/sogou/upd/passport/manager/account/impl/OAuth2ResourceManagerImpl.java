package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.CommonHelper;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.parameter.OAuth2ResourceTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.OAuthResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhotoUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.OAuth2ResourceManager;
import com.sogou.upd.passport.manager.account.PCAccountManager;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.form.PCOAuth2ResourceParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountBaseInfo;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.service.account.AccountBaseInfoService;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.PCAccountTokenService;
import com.sogou.upd.passport.service.account.SnamePassportMappingService;
import com.sogou.upd.passport.service.app.AppConfigService;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import com.sogou.upd.passport.service.connect.ConnectTokenService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * 采用OAuth2协议访问受保护数据
 * User: shipengzhi
 * Date: 13-9-16
 * Time: 下午3:40
 * To change this template use File | Settings | File Templates.
 */
@Component
public class OAuth2ResourceManagerImpl implements OAuth2ResourceManager {

    private Logger log = LoggerFactory.getLogger(OAuth2ResourceManagerImpl.class);
    public static final String RESOURCE = "resource";

    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private LoginApiManager proxyLoginApiManager;
    @Autowired
    private LoginApiManager sgLoginApiManager;
    @Autowired
    private PCAccountManager pcAccountManager;
    @Autowired
    private UserInfoApiManager shPlusUserInfoApiManager;
    @Autowired
    private PCAccountTokenService pcAccountTokenService;
    @Autowired
    private PhotoUtils photoUtils;
    @Autowired
    SnamePassportMappingService snamePassportMappingService;
    @Autowired
    private AccountBaseInfoService accountBaseInfoService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ConnectTokenService connectTokenService;
    @Autowired
    private ConnectConfigService connectConfigService;


    @Override
    public Result resource(PCOAuth2ResourceParams params) {
        Result result = new OAuthResultSupport(false);
        int clientId = params.getClient_id();
        String instanceId = params.getInstance_id();
        String accessToken = params.getAccess_token();
        try {
            clientId = clientId == 30000004 ? CommonConstant.PC_CLIENTID : clientId;  //兼容浏览器PC端sohu+接口
            AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
            if (appConfig == null) {
                result.setCode(ErrorUtil.INVALID_CLIENT);
                return result;
            }
            String clientSecret = appConfig.getClientSecret();
            String resourceType = params.getResource_type();
            if (OAuth2ResourceTypeEnum.isEqual(resourceType, OAuth2ResourceTypeEnum.GET_COOKIE)) {
                result = getCookieValue(accessToken, clientId, clientSecret, instanceId, params.getUsername());
            } else if (OAuth2ResourceTypeEnum.isEqual(resourceType, OAuth2ResourceTypeEnum.GET_FULL_USERINFO)) {
                result = getFullUserInfo(accessToken, clientId, clientSecret, instanceId, params.getUsername());
            } else {
                result.setCode(ErrorUtil.INVALID_RESOURCE_TYPE);
                return result;
            }
        } catch (Exception e) {
            log.error("Obtain OAuth2 Resource Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    /**
     * 获取cookie值
     *
     * @return
     */
    @Override
    public Result getCookieValue(String accessToken, int clientId, String clientSecret, String instanceId, String username) {
        Result result = new OAuthResultSupport(false);
        Result cookieResult;
        Map resourceMap = Maps.newHashMap();
        String passportId = null;
        try {
            passportId = getPassportIdByToken(accessToken, clientId, clientSecret, instanceId, username);
            if (Strings.isNullOrEmpty(passportId)) {
                result.setCode(ErrorUtil.ERR_ACCESS_TOKEN);
                return result;
            }

            if (CommonHelper.isBuildNewCookie()) {
                //生成cookie
                CookieApiParams cookieApiParams = new CookieApiParams();
                cookieApiParams.setUserid(passportId);
                cookieApiParams.setClient_id(clientId);
                cookieApiParams.setRu(CommonConstant.DEFAULT_CONNECT_REDIRECT_URL);
                cookieApiParams.setTrust(CookieApiParams.IS_ACTIVE);
                cookieApiParams.setPersistentcookie(String.valueOf(1));
                cookieResult = sgLoginApiManager.getCookieInfo(cookieApiParams);
            } else {
                //生成cookie
                CookieApiParams cookieApiParams = new CookieApiParams();
                cookieApiParams.setUserid(passportId);
                cookieApiParams.setClient_id(clientId);
                cookieApiParams.setRu(CommonConstant.DEFAULT_CONNECT_REDIRECT_URL);
                cookieApiParams.setTrust(CookieApiParams.IS_ACTIVE);
                cookieApiParams.setPersistentcookie(String.valueOf(1));
                cookieResult = proxyLoginApiManager.getCookieInfo(cookieApiParams);

            }
            if (!cookieResult.isSuccess()) {
                result.setCode(ErrorUtil.ERR_CODE_CREATE_COOKIE_FAILED);
                return result;
            }
            Date expires = DateUtils.addDays(new Date(), 7);
//            String suffix = ";path=/;domain=.sogou.com;expires=Tuesday, 15-Sep-15 19:02:21 GMT";   // TODO 这里不能写死有效期，要改
            String suffix = ";path=/;domain=.sogou.com;expires=" + expires;
            String ppinf = cookieResult.getModels().get("ppinf") + suffix;
            String pprdig = cookieResult.getModels().get("pprdig") + suffix;
            String[] cookieArray = new String[]{"ppinf=" + ppinf, "pprdig=" + pprdig};
            resourceMap.put("msg", "get cookie success");
            resourceMap.put("code", 0);
            resourceMap.put("scookie", cookieArray);

            result.setSuccess(true);
            result.setDefaultModel(RESOURCE, resourceMap);
        } catch (ServiceException e) {
            log.error("OAuth2 Resource get cookie value fail", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;

    }

    @Override
    public Result queryPassportIdByAccessToken(String token, int clientId, String instanceId, String username) {
        Result finalResult = new APIResultSupport(false);
        try {
            AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
            if (appConfig == null) {
                finalResult.setCode(ErrorUtil.INVALID_CLIENTID);
                return finalResult;
            }
            String passportId = getPassportIdByToken(token, clientId, appConfig.getClientSecret(), instanceId, username);
            if (Strings.isNullOrEmpty(passportId)) {
                finalResult.setCode(ErrorUtil.ERR_ACCESS_TOKEN);
                return finalResult;
            }
            finalResult.setSuccess(true);
            finalResult.setDefaultModel(passportId);
            return finalResult;
        } catch (Exception e) {
            log.error("createToken fail", e);
            finalResult.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return finalResult;
        }
    }

    @Override
    public Result getPassportIdByToken(String accessToken, int clientId) {
        Result finalResult = new APIResultSupport(false);
        try {
            AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
            if (appConfig == null) {
                finalResult.setCode(ErrorUtil.INVALID_CLIENTID);
                return finalResult;
            }
            String passportId = pcAccountTokenService.getPassportIdByToken(accessToken, appConfig.getClientSecret());
            ;
            if (Strings.isNullOrEmpty(passportId)) {
                finalResult.setCode(ErrorUtil.ERR_ACCESS_TOKEN);
                return finalResult;
            }
            finalResult.setSuccess(true);
            finalResult.setDefaultModel(passportId);
            return finalResult;
        } catch (Exception e) {
            log.error("createToken fail", e);
            finalResult.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return finalResult;
        }
    }

    private String getPassportIdByToken(String accessToken, int clientId, String clientSecret, String instanceId, String username) {
        String passportId = null;
        if (accessToken.startsWith(CommonConstant.SG_TOKEN_OLD_START)) {
            passportId = pcAccountTokenService.getPassportIdByOldToken(accessToken, clientSecret);
            return getPassportIdByUsername(passportId, accessToken, clientId, clientSecret, instanceId, username);
        } else if (accessToken.startsWith(CommonConstant.SG_TOKEN_START)) {
            passportId = pcAccountTokenService.getPassportIdByToken(accessToken, clientSecret);
            return getPassportIdByUsername(passportId, accessToken, clientId, clientSecret, instanceId, username);
        } else {
            return null;
        }
    }

    private String getPassportIdByUsername(String passportId, String accessToken, int clientId, String clientSecret, String instanceId, String username) {
        if (StringUtils.isBlank(passportId) && AccountDomainEnum.isPassportId(username)) {
            passportId = username;
        }
        if (!StringUtils.isBlank(passportId)) {
            //校验accessToken
            if (!pcAccountTokenService.verifyAccessToken(passportId, clientId, instanceId, accessToken)) {
                return null;
            }
        }
        return passportId;
    }

    /**
     * 获取完整的个人信息
     *
     * @return
     */
    @Override
    public Result getFullUserInfo(String accessToken, int clientId, String clientSecret, String instanceId, String username) {
        Result result = new OAuthResultSupport(false);
        Map resourceMap = Maps.newHashMap();
        try {
            String passportId = getPassportIdByToken(accessToken, clientId, clientSecret, instanceId, username);
            if (Strings.isNullOrEmpty(passportId)) {
                result.setCode(ErrorUtil.ERR_ACCESS_TOKEN);
                return result;
            }

            Result getUserInfoResult = getUserInfo(passportId, clientId);
            String uniqname = "", large_avatar = "", mid_avatar = "", tiny_avatar = "";
            if (getUserInfoResult.isSuccess()) {
                uniqname = (String) getUserInfoResult.getModels().get("uniqname");
                large_avatar = (String) getUserInfoResult.getModels().get("img_180");
                mid_avatar = (String) getUserInfoResult.getModels().get("img_50");
                tiny_avatar = (String) getUserInfoResult.getModels().get("img_30");
            }
            Map data = Maps.newHashMap();
            data.put("nick", uniqname);
            data.put("large_avatar", large_avatar);
            data.put("mid_avatar", mid_avatar);
            data.put("tiny_avatar", tiny_avatar);
            data.put("sid", passportId);
            resourceMap.put("data", data);
            resourceMap.put("msg", "get full user info success");
            resourceMap.put("code", 0);
            result.setSuccess(true);
            result.setDefaultModel(RESOURCE, resourceMap);
        } catch (ServiceException e) {
            log.error("OAuth2 Resource get full userInfo fail", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    private ConnectToken getConnectToken(String userId, int clientId) {
        //从connect_token中获取
        int provider = AccountTypeEnum.getAccountType(userId).getValue();
        ConnectConfig connectConfig = connectConfigService.queryConnectConfig(clientId, provider);
        ConnectToken connectToken = null;
        if (connectConfig != null) {
            connectToken = connectTokenService.queryConnectToken(userId, provider, connectConfig.getAppKey());
        }
        return connectToken;
    }

    @Override
    public String getUniqname(String passportId, int clientId) {
        String uniqname = null;
        try {
            //第三方账户先从account里获取
            AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(passportId);
            if (domain == AccountDomainEnum.THIRD) {
                Account account = accountService.queryAccountByPassportId(passportId);
                if (account != null && !Strings.isNullOrEmpty(account.getUniqname())) {
                    uniqname = account.getUniqname();
                } else {
                    ConnectToken connectToken = getConnectToken(passportId, clientId);
                    if (connectToken != null) {
                        uniqname = connectToken.getConnectUniqname();
                    }
                }
            } else {
                AccountBaseInfo accountBaseInfo = getBaseInfo(passportId);
                if (accountBaseInfo != null) {
                    uniqname = accountBaseInfo.getUniqname();
                }
                uniqname = getAndUpdateUniqname(passportId, accountBaseInfo, uniqname);
            }

        } catch (Exception e) {
            log.error("getUniqname error! passportId:" + passportId, e);
        }
        return Strings.isNullOrEmpty(uniqname) ? passportId : uniqname;
    }

    @Override
    public String getEncodedUniqname(String passportId, int clientId) {
        String uniqname = getUniqname(passportId, clientId);
        if (!StringUtils.isBlank(uniqname)) {
            uniqname = Coder.encode(uniqname, "UTF-8");
        }
        return uniqname;
    }


    @Override
    public String defaultUniqname(String passportId) {
        if (AccountDomainEnum.THIRD == AccountDomainEnum.getAccountDomain(passportId)) {
            return "搜狗用户";
        }
        return passportId.substring(0, passportId.indexOf("@"));
    }

    /**
     * 非第三方账号全量数据迁移完成后
     * 用户昵称、头像信息 读写 account_base_info 切换到 account_0~32
     * 用户其他信息 读写调用搜狐Api 切换到 读写 account_info_0~32
     *
     * TODO 改为调用 AccountInfoManager getUserInfo 方法
     *
     * @param passportId
     * @param clientId
     * @return
     */
    @Override
    public Result getUserInfo(String passportId, int clientId) {
        Result result = new APIResultSupport(false);

        String avatarurl;
        String uniqname = defaultUniqname(passportId), large_avatar = "", mid_avatar = "", tiny_avatar = "";
        AccountBaseInfo accountBaseInfo;
        try {
            //第三方账户先从account里获取
            AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(passportId);
            if (domain == AccountDomainEnum.THIRD) {
                Account account = accountService.queryAccountByPassportId(passportId);
                ConnectToken connectToken = null;
                if (account != null) {
                    uniqname = account.getUniqname();
                    avatarurl = account.getAvatar();
                    if (Strings.isNullOrEmpty(uniqname) || Strings.isNullOrEmpty(avatarurl)) {
                        connectToken = getConnectToken(passportId, clientId);
                        if (connectToken != null) {
                            if (Strings.isNullOrEmpty(uniqname)) {
                                uniqname = connectToken.getConnectUniqname();
                            }
                            if (Strings.isNullOrEmpty(avatarurl)) {
                                large_avatar = connectToken.getAvatarLarge();
                                mid_avatar = connectToken.getAvatarMiddle();
                                tiny_avatar = connectToken.getAvatarSmall();
                            }
                        }
                    } else {
                        //获取不同尺寸头像
                        Result getPhotoResult = photoUtils.obtainPhoto(avatarurl, "30,50,180");
                        large_avatar = (String) getPhotoResult.getModels().get("img_180");
                        mid_avatar = (String) getPhotoResult.getModels().get("img_50");
                        tiny_avatar = (String) getPhotoResult.getModels().get("img_30");
                    }
                    result.setDefaultModel("userid", account.getPassportId());
                }
            } else {
                accountBaseInfo = getBaseInfo(passportId);
                if (accountBaseInfo != null) {
                    uniqname = accountBaseInfo.getUniqname();
                    Result getPhotoResult = photoUtils.obtainPhoto(accountBaseInfo.getAvatar(), "30,50,180");
                    large_avatar = (String) getPhotoResult.getModels().get("img_180");
                    mid_avatar = (String) getPhotoResult.getModels().get("img_50");
                    tiny_avatar = (String) getPhotoResult.getModels().get("img_30");
                    uniqname = getAndUpdateUniqname(passportId, accountBaseInfo, uniqname);
                }
            }
            result.setSuccess(true);
            result.setDefaultModel("uniqname", uniqname);
            result.setDefaultModel("img_30", tiny_avatar);
            result.setDefaultModel("img_50", mid_avatar);
            result.setDefaultModel("img_180", large_avatar);
        } catch (Exception e) {
            log.error("getUserInfo error! passportId:" + passportId, e);
        }

        return result;
    }

    /**
     * 从浏览器论坛取昵称
     *
     * @param passportId
     * @param accountBaseInfo
     * @param uniqname
     * @return
     */
    private String getAndUpdateUniqname(String passportId, AccountBaseInfo accountBaseInfo, String uniqname) {
        if (!isValidUniqname(passportId, uniqname)) {
            //从论坛获取昵称
            uniqname = pcAccountManager.getBrowserBbsUniqname(passportId);
            if (isValidUniqname(passportId, uniqname)) {
                if (accountBaseInfo != null) {
                    accountBaseInfoService.updateUniqname(accountBaseInfo, uniqname);
                } else {
                    accountBaseInfoService.insertAccountBaseInfo(passportId, uniqname, "");
                }
            }
        }
        if (!isValidUniqname(passportId, uniqname)) {
            uniqname = defaultUniqname(passportId);
        }
        return uniqname;
    }

    private boolean isValidUniqname(String passportId, String uniqname) {
        if (Strings.isNullOrEmpty(uniqname) || uniqname.equals(passportId.substring(0, passportId.indexOf("@")))) {
            return false;
        }
        return true;
    }

    private AccountBaseInfo getBaseInfo(String passportId) {
        GetUserInfoApiparams infoApiparams = new GetUserInfoApiparams();
        infoApiparams.setUserid(passportId);
        Result getUserInfoResult = shPlusUserInfoApiManager.getUserInfo(infoApiparams);
        if (getUserInfoResult.isSuccess()) {
            Object obj = getUserInfoResult.getModels().get("baseInfo");
            AccountBaseInfo accountBaseInfo = null;
            if (obj != null) {
                accountBaseInfo = (AccountBaseInfo) obj;
            }
            return accountBaseInfo;
        }
        return null;
    }

}
