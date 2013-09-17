package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.CommonHelper;
import com.sogou.upd.passport.common.result.OAuthResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.AccountInfoManager;
import com.sogou.upd.passport.manager.account.OAuth2ResourceManager;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieUrlApiParams;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
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

    @Autowired
    private LoginApiManager proxyLoginApiManager;
    @Autowired
    private LoginApiManager sgLoginApiManager;
    @Autowired
    private UserInfoApiManager proxyUserInfoApiManager;
    @Autowired
    private UserInfoApiManager sgUserInfoApiManager;
    @Autowired
    private AccountInfoManager accountInfoManager;

    @Override
    public Result getCookieValue(String passportId) {
        Result result = new OAuthResultSupport(false);
        CreateCookieUrlApiParams createCookieUrlApiParams = new CreateCookieUrlApiParams(passportId,
                CommonConstant.DEFAULT_CONNECT_REDIRECT_URL, 1);
        Result cookieResult;
        try {
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
            result.setSuccess(true);
            Map resource = Maps.newHashMap();
            String[] cookieArray = {ppinf, pprdig};
            resource.put("msg", "get cookie success");
            resource.put("code", "0");
            resource.put("scookie", cookieArray);
            setResourceMap(resource, result);
        } catch (ServiceException e) {
            log.error("OAuth2 Resource get cookie value fail,passportId:" + passportId, e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;

    }

    @Override
    public Result getFullUserInfo(String passportId) {
        Result result = new OAuthResultSupport(false);
        String fields = "sec_email,uniqname";
        GetUserInfoApiparams getUserInfoApiparams = new GetUserInfoApiparams(passportId, fields);
        Result userInfoResult;
        try {
            if (CommonHelper.isInvokeProxyApi(passportId)) {
                userInfoResult = proxyUserInfoApiManager.getUserInfo(getUserInfoApiparams);
            } else {
                userInfoResult = sgUserInfoApiManager.getUserInfo(getUserInfoApiparams);
            }
            if (!userInfoResult.isSuccess()) {
                result.setCode(ErrorUtil.ERR_CODE_GET_USER_INFO);
                return result;
            }
            // TODO 支持一次返回多张图片
            Result photoResult = accountInfoManager.obtainPhoto(passportId, "180");
            String largeAvatar;
            String midAvatar;
            String tinyAvatar;
            if (!photoResult.isSuccess()) {          // TODO 换成搜狗的默认图片地址
                largeAvatar = CommonConstant.DEFAULT_AVATAR_URL + "175.png";
                midAvatar = CommonConstant.DEFAULT_AVATAR_URL + "95.png";
                tinyAvatar = CommonConstant.DEFAULT_AVATAR_URL + "55.png";
            } else {
                largeAvatar = (String) photoResult.getModels().get("180");
                midAvatar = (String) photoResult.getModels().get("95");
                tinyAvatar = (String) photoResult.getModels().get("55");
            }
            String email = (String) userInfoResult.getModels().get("sec_email");
            String uniqname = (String) userInfoResult.getModels().get("uniqname");
            result.setSuccess(true);
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
            setResourceMap(resource, result);
        } catch (ServiceException e) {
            log.error("OAuth2 Resource get full userInfo fail,passportId:" + passportId, e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    /*
     * 统一的返回结果
     *  resource: {...},
     *  result: "confirm"
     */
    private void setResourceMap(Map resource, Result result) {
        Map resourceMap = Maps.newHashMap();
        resourceMap.put("resource", resource);
        result.setModels(resourceMap);
    }
}
