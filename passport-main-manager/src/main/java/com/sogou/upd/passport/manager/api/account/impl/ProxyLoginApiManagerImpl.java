package com.sogou.upd.passport.manager.api.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieUrlApiParams;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * 代理搜狐Passport的登录实现
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 上午10:36
 */
@Component("proxyLoginApiManager")
public class ProxyLoginApiManagerImpl extends BaseProxyManager implements LoginApiManager {

    private static Logger log = LoggerFactory.getLogger(ProxyLoginApiManagerImpl.class);

    @Override
    public Result webAuthUser(AuthUserApiParams authUserParameters) {
        String userId = authUserParameters.getUserid();
        if (AccountDomainEnum.INDIVID.equals(AccountDomainEnum.getAccountDomain(userId))) {
            userId = userId + CommonConstant.SOGOU_SUFFIX;
            authUserParameters.setUserid(userId);
        }
        if (PhoneUtil.verifyPhoneNumberFormat(userId)) {
            authUserParameters.setUsertype(1); // 手机号
        }
        authUserParameters.setPwdtype(1); // 密码为MD5
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.AUTH_USER, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(authUserParameters);
        return executeResult(requestModelXml);
    }

    @Override
    public Result appAuthToken(String token) {
        return null;
    }

    @Override
    public Result buildCreateCookieUrl(CreateCookieUrlApiParams createCookieUrlApiParams, boolean isRuEncode, boolean isHttps) {
        Result result = new APIResultSupport(false);
        try {
            String ru = createCookieUrlApiParams.getRu();
            String userId = createCookieUrlApiParams.getUserid();
            if (isRuEncode) {
                ru = URLEncoder.encode(ru, "UTF-8");
            }
            long ct = System.currentTimeMillis();
            String code = userId + SHPPUrlConstant.APP_ID + SHPPUrlConstant.APP_KEY + ct;
            code = Coder.encryptMD5GBK(code);
            String shUrl = SHPPUrlConstant.HTTP_SET_COOKIE;
            if (isHttps) {
                shUrl = SHPPUrlConstant.HTTPS_SET_COOKIE;
            }
            RequestModel requestModel = new RequestModel(shUrl);
            requestModel.addParam("userid", userId);
            requestModel.addParam("appid", SHPPUrlConstant.APP_ID);
            requestModel.addParam("ct", ct);
            requestModel.addParam("code", code);
            requestModel.addParam("ru", ru);
            requestModel.addParam("persistentcookie", createCookieUrlApiParams.getPersistentcookie());
            requestModel.addParam("domain", createCookieUrlApiParams.getDomain());
            result.setDefaultModel("requestModel", requestModel);

            StringBuilder urlBuilder = new StringBuilder(shUrl);
            urlBuilder.append("?").append("userid=").append(URLEncoder.encode(userId, "UTF-8"))
                    .append("&appid=").append(SHPPUrlConstant.APP_ID)
                    .append("&ct=").append(ct)
                    .append("&code=").append(code)
                    .append("&ru=").append(ru)
                    .append("&persistentcookie=").append(createCookieUrlApiParams.getPersistentcookie())
                    .append("&domain=" + createCookieUrlApiParams.getDomain());
            result.setDefaultModel("url", urlBuilder.toString());
            result.setSuccess(true);
        } catch (Exception e) {
            log.error("buildCreateCookieUrl error userid:" + createCookieUrlApiParams.getUserid() + " ru=" + createCookieUrlApiParams.getRu(), e);
            result.setCode(ErrorUtil.ERR_CODE_CREATE_COOKIE_FAILED);
        }
        return result;
    }

    @Override
    public Result getCookieInfo(CookieApiParams cookieApiParams) {
        Result result = new APIResultSupport(false);

        if (Strings.isNullOrEmpty(cookieApiParams.getPersistentcookie())) {
            cookieApiParams.setPersistentcookie(String.valueOf(1));
        }

        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.GET_COOKIE_VALUE_FROM_SOHU, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(cookieApiParams);
        requestModelXml.getParams().put("result_type", "json");       //sohu 传 xml参数，返回json
        Result getCookieInfoResult = executeResult(requestModelXml);
        if (getCookieInfoResult.isSuccess()) {
            Object obj = getCookieInfoResult.getModels().get("data");
            if (obj != null && obj instanceof List) {
                List<Map<String, String>> listMap = (List<Map<String, String>>) obj;
                if (CollectionUtils.isNotEmpty(listMap)) {
                    for (Map<String, String> map : listMap) {
                        String key = map.get("name");
                        String value = map.get("value");
                        if ("ppinf".equals(key)) {
                            result.getModels().put("ppinf", value);
                        }
                        if ("pprdig".equals(key)) {
                            result.getModels().put("pprdig", value);
                        }
                        if ("passport".equals(key)) {
                            result.getModels().put("passport", value);
                        }
                    }
                }
            }
            result.setSuccess(true);
            result.setMessage("获取cookie成功");
            result.setDefaultModel("userid", cookieApiParams.getUserid());
        } else {
            result = getCookieInfoResult;
        }
        return result;
    }

    @Override
    public Result getSGCookieInfoForAdapter(CookieApiParams cookieApiParams) {
        return null;
    }

}
