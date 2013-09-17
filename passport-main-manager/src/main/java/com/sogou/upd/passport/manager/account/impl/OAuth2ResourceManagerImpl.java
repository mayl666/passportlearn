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
import com.sogou.upd.passport.service.account.SHPlusTokenService;
import com.sogou.upd.passport.service.account.generator.TokenDecrypt;
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

    public static final String RESOURCE = "resource";

    @Autowired
    private LoginApiManager proxyLoginApiManager;
    @Autowired
    private LoginApiManager sgLoginApiManager;
    @Autowired
    private UserInfoApiManager proxyUserInfoApiManager;
    @Autowired
    private UserInfoApiManager sgUserInfoApiManager;
    @Autowired
    private SHPlusTokenService shPlusTokenService;

    @Override
    public Result getCookieValue(String accessToken, String clientSecret, String instanceId) {
        Result result = new OAuthResultSupport(false);
        Result cookieResult;
        Map resourceMap = Maps.newHashMap();
        try {
            String passportId = TokenDecrypt.decryptPcToken(accessToken, clientSecret);
            if (!Strings.isNullOrEmpty(passportId)) {
                CreateCookieUrlApiParams createCookieUrlApiParams = new CreateCookieUrlApiParams(passportId,
                        CommonConstant.DEFAULT_CONNECT_REDIRECT_URL, 1);
                if (CommonHelper.isBuildNewCookie()) {
                    cookieResult = sgLoginApiManager.getCookieValue(createCookieUrlApiParams);
                } else {
                    cookieResult = proxyLoginApiManager.getCookieValue(createCookieUrlApiParams);
                }
                if (!cookieResult.isSuccess()) {
                    result.setCode(ErrorUtil.ERR_CODE_CREATE_COOKIE_FAILED);
                    return result;
                }
                String ppinf = (String) cookieResult.getModels().get("ppinf");
                String pprdig = (String) cookieResult.getModels().get("pprdig");
                String[] cookieArray = new String[]{"ppinf=" + ppinf, "ppridg=" + pprdig};
                Map resource = Maps.newHashMap();
                resource.put("msg", "get cookie success");
                resource.put("code", "0");
                resource.put("scookie", cookieArray);
                resourceMap.put(RESOURCE, resource);
            } else {
                Map map = shPlusTokenService.getResourceByToken(instanceId, accessToken, OAuth2ResourceTypeEnum.GET_COOKIE);
                resourceMap = (Map) map.get(RESOURCE);
                log.info("[SHPlusToken] get shplus cookie by accesstoken,accessToken：" + accessToken);
            }
            result.setSuccess(true);
            result.setModels(resourceMap);
        } catch (ServiceException e) {
            log.error("OAuth2 Resource get cookie value fail", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;

    }

    @Override
    public Result getFullUserInfo(String accessToken, String clientSecret, String instanceId) {
        Result result = new OAuthResultSupport(false);
        Map resourceMap = Maps.newHashMap();
        try {
            String passportId = TokenDecrypt.decryptPcToken(accessToken, clientSecret);
            if (!Strings.isNullOrEmpty(passportId)) {
                String fields = "sec_email,uniqname,avatarurl";
                String imagesize = "180,55";
                GetUserInfoApiparams getUserInfoApiparams = new GetUserInfoApiparams(passportId, fields);
                getUserInfoApiparams.setImagesize(imagesize);
                Result userInfoResult;
                if (CommonHelper.isInvokeProxyApi(passportId)) {
                    userInfoResult = proxyUserInfoApiManager.getUserInfo(getUserInfoApiparams);
                } else {
                    userInfoResult = sgUserInfoApiManager.getUserInfo(getUserInfoApiparams);
                }
                if (!userInfoResult.isSuccess()) {
                    result.setCode(ErrorUtil.ERR_CODE_GET_USER_INFO);
                    return result;
                }
                String largeAvatar = (String) ((Map) userInfoResult.getModels().get("avatarurl")).get("img_180");
                String midAvatar = (String) ((Map) userInfoResult.getModels().get("avatarurl")).get("img_55");
                String tinyAvatar = (String) ((Map) userInfoResult.getModels().get("avatarurl")).get("img_55");
                String email = (String) userInfoResult.getModels().get("sec_email");
                String uniqname = (String) userInfoResult.getModels().get("uniqname");
                Map resource = Maps.newHashMap();
                resource.put("msg", "get full user info success");
                resource.put("code", "0");
                Map data = Maps.newHashMap();
                data.put("sname", uniqname);
                data.put("nick", uniqname);
                data.put("email", Strings.isNullOrEmpty(email) ? "" : email);
                data.put("large_avatar", largeAvatar);
                data.put("mid_avatar", midAvatar);
                data.put("tiny_avatar", tinyAvatar);
                resource.put("data", data);
                resourceMap.put(RESOURCE, resource);
            } else {
                Map map = shPlusTokenService.getResourceByToken(instanceId, accessToken, OAuth2ResourceTypeEnum.GET_FULL_USERINFO);
                resourceMap = (Map) map.get(RESOURCE);

                log.info("[SHPlusToken] get shplus userinfo by accesstoken,accessToken：" + accessToken);
            }
            result.setSuccess(true);
            result.setModels(resourceMap);
        } catch (ServiceException e) {
            log.error("OAuth2 Resource get full userInfo fail", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

}
