package com.sogou.upd.passport.manager.api.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.CommonHelper;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXmlGBK;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.HttpMethodEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.HttpClientUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
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
        userId = AccountDomainEnum.getInternalCase(userId);
        authUserParameters.setUserid(userId);

        if (AccountDomainEnum.INDIVID.equals(AccountDomainEnum.getAccountDomain(userId))) {
            userId = userId + "@sogou.com";
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
    public Result appAuthToken(AppAuthTokenApiParams appAuthTokenApiParams) {
        appAuthTokenApiParams.setType(2); // 手机端第三方登录后返回的token
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.MOBILE_AUTH_TOKEN, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(appAuthTokenApiParams);
        return executeResult(requestModelXml, appAuthTokenApiParams.getToken());
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
            requestModel.addParam("userid",userId);
            requestModel.addParam("appid", SHPPUrlConstant.APP_ID);
            requestModel.addParam("ct", ct);
            requestModel.addParam("code", code);
            requestModel.addParam("ru", ru);
            requestModel.addParam("persistentcookie", createCookieUrlApiParams.getPersistentcookie());
            requestModel.addParam("domain", createCookieUrlApiParams.getDomain());
            result.setDefaultModel("requestModel", requestModel);

            StringBuilder urlBuilder = new StringBuilder(shUrl);
            urlBuilder.append("?").append("userid=").append(userId)
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
    public Result getCookieInfo(CookieApiParams cookieApiParams){
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.GET_COOKIE_VALUE_FROM_SOHU, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(cookieApiParams);
        requestModelXml.getParams().put("result_type","json");       //sohu 传 xml参数，返回json
        Result result = executeResult(requestModelXml);
        if (result.isSuccess()) {
            Object obj= result.getModels().get("data");
            if(obj!=null && obj instanceof List) {
                List<Map<String, String>> listMap = (List<Map<String, String>>) obj;
                if(CollectionUtils.isNotEmpty(listMap)){
                    for (Map<String,String>map:listMap){
                         String key=(String)map.get("name");
                         String value=(String)map.get("value");
                         if("ppinf".equals(key)){
                             result.getModels().put("ppinf",value);
                         }
                         if("pprdig".equals(key)){
                             result.getModels().put("pprdig",value);
                         }
                    }
                }
            }
            result.setMessage("获取cookie成功");
            result.setDefaultModel("userid", cookieApiParams.getUserid());
        }
        return result;
    }

    @Override
    public Result getCookieValue(CreateCookieUrlApiParams createCookieUrlApiParams) {
        Result cookieUrlResult = buildCreateCookieUrl(createCookieUrlApiParams, false, false);

        String url = (String) cookieUrlResult.getModels().get("url");
//        RequestModel requestModel = (RequestModel) cookieUrlResult.getModels().get("requestModel");
//        Header[] headers = SGHttpClient.executeHeaders(requestModel);
//        start = System.currentTimeMillis();
        Header[] headers = HttpClientUtil.getResponseHeadersWget(url);
//        CommonHelper.recordTimestamp(start,"getCookieValue-getCookieValue");

        Result result = new APIResultSupport(false);
        if (headers != null) {
            String locationKey = "Location";
            String locationUrl = "";
            for (Header header : headers) {
                if (locationKey.equals(header.getName())) {
                    locationUrl = header.getValue();
                }
            }
            if (!Strings.isNullOrEmpty(locationUrl)) {
                Map paramMap = StringUtil.extractParameterMap(locationUrl);
                String status = (String) paramMap.get("status");
                if (Strings.isNullOrEmpty(status)) {
                    result.setSuccess(true);
                    result.setDefaultModel("ppinf", paramMap.get("ppinf"));
                    result.setDefaultModel("pprdig", paramMap.get("pprdig"));
                    result.setDefaultModel("passport", paramMap.get("passport"));
                    locationUrl = modifyClientRu(locationUrl);  // 输入法Mac要求Location里的ru不能decode
                }
            }
            result.setDefaultModel("redirectUrl", locationUrl);
        } else {
            result.setCode(ErrorUtil.ERR_CODE_CREATE_COOKIE_FAILED);
        }
        return result;
    }

    /**
     * 输入法Mac，passport.sogou.com/sso/setcookie？ru=xxx不需要urlencode
     * 手机浏览器跳转的passport.sogou.com/sso/setcookie必须为http
     *
     * @param locationUrl
     * @return
     */
    private String modifyClientRu(String locationUrl) {
        Map paramMap = StringUtil.extractParameterMap(locationUrl);
        String ru = (String) paramMap.get("ru");
        if (!Strings.isNullOrEmpty(ru)) {
            try {
                String decodeRu = URLDecoder.decode(ru, CommonConstant.DEFAULT_CONTENT_CHARSET);
                locationUrl = locationUrl.replaceAll(ru, decodeRu);
            } catch (UnsupportedEncodingException e) {
                log.error("sohu sso setcookie ru encode fail,url:" + ru);
            }
        }
        return locationUrl;
    }

}
