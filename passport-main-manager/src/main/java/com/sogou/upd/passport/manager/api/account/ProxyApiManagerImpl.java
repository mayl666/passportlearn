package com.sogou.upd.passport.manager.api.account;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.model.httpclient.RequestModelXml;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.api.account.form.CheckUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieUrlApiParams;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * 搜狐遗漏的接口
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 下午1:57
 */
public class ProxyApiManagerImpl extends BaseProxyManager {

    private static String PP_COOKIE_URL = "http://account.sogou.com/act/setppcookie";

    @Autowired
    private CommonManager commonManager;

    /**
     * 搜狐接口，根据手机号获取主账号
     * @param baseMoblieApiParams
     * @return
     */
    public Result getPassportIdByMobile(BaseMoblieApiParams baseMoblieApiParams) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.MOBILE_GET_USERID, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(baseMoblieApiParams);
        return executeResult(requestModelXml, baseMoblieApiParams.getMobile());
    }

    /**
     * 搜狐接口，检查账号是否存在
     * @param checkUserApiParams
     * @return
     */
    public Result checkUser(CheckUserApiParams checkUserApiParams) {
        RequestModelXml requestModelXml = new RequestModelXml(SHPPUrlConstant.CHECK_USER, SHPPUrlConstant.DEFAULT_REQUEST_ROOTNODE);
        requestModelXml.addParams(checkUserApiParams);
        Result result = executeResult(requestModelXml);
        if (!result.isSuccess()) {
            result.setDefaultModel("userid", checkUserApiParams.getUserid());
        }
        return result;
    }

    /**
     * 获取cookie值，包括ppinf、pprdig、passport
     * 并且返回种搜狗域cookie的重定向url
     * 只有浏览器老版本PC端才会用到passport
     *
     * @param createCookieUrlApiParams
     * @return
     */
    public Result getCookieInfoWithRedirectUrl(CreateCookieUrlApiParams createCookieUrlApiParams) {
        //生成cookie
        String ru = createCookieUrlApiParams.getRu();
        CookieApiParams cookieApiParams = new CookieApiParams();
        cookieApiParams.setUserid(createCookieUrlApiParams.getUserid());
        cookieApiParams.setClient_id(CommonConstant.PC_CLIENTID);
        cookieApiParams.setRu(ru);
        cookieApiParams.setTrust(CookieApiParams.IS_ACTIVE);
        cookieApiParams.setPersistentcookie(String.valueOf(1));

        Result cookieInfoResult = getCookieInfo(cookieApiParams);
        Result result = new APIResultSupport(false);
        if (cookieInfoResult != null) {
            String ppinf = (String) cookieInfoResult.getModels().get("ppinf");
            String pprdig = (String) cookieInfoResult.getModels().get("pprdig");
            String passport = (String) cookieInfoResult.getModels().get("passport");

            result.setSuccess(true);
            result.setDefaultModel("ppinf", ppinf);
            result.setDefaultModel("pprdig", pprdig);
            result.setDefaultModel("passport", passport);

            long ct = System.currentTimeMillis();
            String code1 = "", code2 = "", code3 = "";
            if (!StringUtil.isBlank(ppinf)) {
                code1 = commonManager.getCode(ppinf, CommonConstant.PC_CLIENTID, ct);
            }
            if (!StringUtil.isBlank(ppinf)) {
                code2 = commonManager.getCode(pprdig, CommonConstant.PC_CLIENTID, ct);
            }
            if (!StringUtil.isBlank(ppinf)) {
                code3 = commonManager.getCode(passport, CommonConstant.PC_CLIENTID, ct);
            }

            StringBuilder locationUrlBuilder = new StringBuilder(PP_COOKIE_URL);  // 移动浏览器端使用https域名会有问题
            locationUrlBuilder.append("?").append("ppinf=").append(ppinf)
                    .append("&pprdig=").append(pprdig)
                    .append("&passport=").append(passport)
                    .append("&code1=").append(code1)
                    .append("&code2=").append(code2)
                    .append("&code=").append(code3)
                    .append("&s=").append(String.valueOf(ct))
                    .append("&lastdomain=").append(0);
            if (1 == createCookieUrlApiParams.getPersistentcookie()) {
                locationUrlBuilder.append("&livetime=1");
            }
            ru = buildRedirectUrl(ru, 0);
            try {
                // 1105不允许URLEncode，但壁纸需要URLEncode，所以传clientId区分
                if (!createCookieUrlApiParams.getClientId().equals(String.valueOf(CommonConstant.PINYIN_MAC_CLIENTID))) {
                    ru = URLEncoder.encode(ru, CommonConstant.DEFAULT_CHARSET);
                }
            } catch (UnsupportedEncodingException e) {
            }
            locationUrlBuilder.append("&ru=").append(ru);   // 输入法Mac要求Location里的ru不能decode
            result.setDefaultModel("redirectUrl", locationUrlBuilder.toString());
        } else {
            result.setCode(ErrorUtil.ERR_CODE_CREATE_COOKIE_FAILED);
        }
        return result;
    }

    private String buildRedirectUrl(String ru, int status) {
        if (Strings.isNullOrEmpty(ru)) {
            ru = CommonConstant.DEFAULT_INDEX_URL;
        }
        if (ru.contains("?")) {
            return ru + "&status=" + status;
        } else {
            return ru + "?status=" + status;
        }
    }

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

}
