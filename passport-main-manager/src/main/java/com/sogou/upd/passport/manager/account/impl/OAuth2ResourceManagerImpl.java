package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.CommonHelper;
import com.sogou.upd.passport.common.parameter.OAuth2ResourceTypeEnum;
import com.sogou.upd.passport.common.result.OAuthResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.OAuth2ResourceManager;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieUrlApiParams;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.form.PCOAuth2ResourceParams;
import com.sogou.upd.passport.model.account.AccountBaseInfo;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.PCAccountTokenService;
import com.sogou.upd.passport.service.account.SHPlusTokenService;
import com.sogou.upd.passport.service.account.generator.TokenDecrypt;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private static final Logger shPlusTokenLog = LoggerFactory.getLogger("shPlusTokenLogger");

    public static final String RESOURCE = "resource";

    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private LoginApiManager proxyLoginApiManager;
    @Autowired
    private LoginApiManager sgLoginApiManager;
    @Autowired
    private UserInfoApiManager proxyUserInfoApiManager;
    @Autowired
    private UserInfoApiManager sgUserInfoApiManager;
    @Autowired
    private UserInfoApiManager shPlusUserInfoApiManager;
    @Autowired
    private SHPlusTokenService shPlusTokenService;
    @Autowired
    private PCAccountTokenService pcAccountTokenService;

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
                result = getCookieValue(accessToken, clientId, clientSecret, instanceId);
            } else if (OAuth2ResourceTypeEnum.isEqual(resourceType, OAuth2ResourceTypeEnum.GET_FULL_USERINFO)) {
                result = getFullUserInfo(accessToken, clientId, clientSecret, instanceId);
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
    public Result getCookieValue(String accessToken, int clientId, String clientSecret, String instanceId) {
        Result result = new OAuthResultSupport(false);
        Result cookieResult;
        Map resourceMap = Maps.newHashMap();
        try {
            String passportId = pcAccountTokenService.getPassportIdByToken(accessToken, clientSecret);
            if (!Strings.isNullOrEmpty(passportId)) {
                //校验accessToken
                if (!pcAccountTokenService.verifyAccessToken(passportId, clientId, instanceId, accessToken)) {
                    result.setCode(ErrorUtil.ERR_ACCESS_TOKEN);
                    return result;
                }

                CreateCookieUrlApiParams createCookieUrlApiParams = new CreateCookieUrlApiParams(passportId,
                        CommonConstant.DEFAULT_CONNECT_REDIRECT_URL, 1, "sogou.com");
                if (CommonHelper.isBuildNewCookie()) {
                    cookieResult = sgLoginApiManager.getCookieValue(createCookieUrlApiParams);
                } else {
                    cookieResult = proxyLoginApiManager.getCookieValue(createCookieUrlApiParams);
                }
                if (!cookieResult.isSuccess()) {
                    result.setCode(ErrorUtil.ERR_CODE_CREATE_COOKIE_FAILED);
                    return result;
                }
                String suffix = ";path=/;domain=.sogou.com;expires=Tuesday, 17-Sep-13 19:02:21 GMT";
                String ppinf = cookieResult.getModels().get("ppinf") + suffix;
                String pprdig = cookieResult.getModels().get("pprdig") + suffix;
                String[] cookieArray = new String[]{"ppinf=" + ppinf, "pprdig=" + pprdig};
                resourceMap.put("msg", "get cookie success");
                resourceMap.put("code", 0);
                resourceMap.put("scookie", cookieArray);
            } else {
                Map map = shPlusTokenService.getResourceByToken(instanceId, accessToken, OAuth2ResourceTypeEnum.GET_COOKIE);
                resourceMap = (Map) map.get(RESOURCE);
                shPlusTokenLog.info("[SHPlusToken] get shplus cookie by accesstoken,accessToken：" + accessToken);
            }
            result.setSuccess(true);
            result.setDefaultModel(RESOURCE, resourceMap);
        } catch (ServiceException e) {
            log.error("OAuth2 Resource get cookie value fail", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;

    }

    /**
     * 获取完整的个人信息
     *
     * @return
     */
    @Override
    public Result getFullUserInfo(String accessToken, int clientId, String clientSecret, String instanceId) {
        Result result = new OAuthResultSupport(false);
        Map resourceMap = Maps.newHashMap();
        try {
            String passportId = pcAccountTokenService.getPassportIdByToken(accessToken, clientSecret);
            if (!Strings.isNullOrEmpty(passportId)) {
                //校验accessToken
                if (!pcAccountTokenService.verifyAccessToken(passportId, clientId, instanceId, accessToken)) {
                    result.setCode(ErrorUtil.ERR_ACCESS_TOKEN);
                    return result;
                }

                Map data = Maps.newHashMap();
                data.put("nick", getUniqname(passportId));
                data.put("large_avatar", "");
                data.put("mid_avatar", "");
                data.put("tiny_avatar", "");
                data.put("sid", passportId);
                resourceMap.put("data", data);
                resourceMap.put("msg", "get full user info success");
                resourceMap.put("code", 0);
                result.setDefaultModel(RESOURCE, resourceMap);
            } else {
                Map map = shPlusTokenService.getResourceByToken(instanceId, accessToken, OAuth2ResourceTypeEnum.GET_FULL_USERINFO);
                resourceMap = (Map) map.get(RESOURCE);
                shPlusTokenLog.info("[SHPlusToken] get shplus userinfo by accesstoken,accessToken：" + accessToken);
            }
            result.setSuccess(true);
            result.setDefaultModel(RESOURCE, resourceMap);
        } catch (ServiceException e) {
            log.error("OAuth2 Resource get full userInfo fail", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    @Override
    public String getUniqname(String passportId) {
        GetUserInfoApiparams infoApiparams= new GetUserInfoApiparams();
        infoApiparams.setUserid(passportId);
        Result getUserInfoResult = shPlusUserInfoApiManager.getUserInfo(infoApiparams);
        String uniqname = null;
        if (getUserInfoResult.isSuccess()) {
            Object obj=getUserInfoResult.getModels().get("baseInfo");
            AccountBaseInfo accountBaseInfo=null;
            if(obj!=null){
                accountBaseInfo= (AccountBaseInfo) obj;
                uniqname = accountBaseInfo.getUniqname();
            }
        }

        if(Strings.isNullOrEmpty(uniqname)){
            return defaultUniqname(passportId);
        }
        return uniqname;
    }

    private String defaultUniqname(String passportId) {
        return passportId.substring(0, passportId.indexOf("@"));
    }

}
