package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.CommonHelper;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.math.RSAEncoder;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.DateUtil;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.common.utils.ToolUUIDUtil;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.CookieManager;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import com.sogou.upd.passport.manager.form.PPCookieParams;
import com.sogou.upd.passport.manager.form.SSOCookieParams;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.oauth2.common.types.ConnectDomainEnum;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-10-16
 * Time: 上午11:58
 * To change this template use File | Settings | File Templates.
 */
@Component
public class CookieManagerImpl implements CookieManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CookieManagerImpl.class);

    private String PPINF = "2|1381822290|1383031890|bG9naW5pZDowOnx1c2VyaWQ6MjU6c2dwcm9jZXNzdGVzdDAxQHNvZ291LmNvbXxzZXJ2aWNldXNlOjIwOjAwMTAwMDAwMDAwMDAwMDAwMDAwfGNydDoxMDoyMDEzLTEwLTE1fGVtdDoxOjB8YXBwaWQ6NDoxMTIwfHRydXN0OjE6MXxwYXJ0bmVyaWQ6MTowfHJlbGF0aW9uOjA6fHV1aWQ6MTY6NTNkNmUwNzFmYTk5NDdic3x1aWQ6MTY6NTNkNmUwNzFmYTk5NDdic3x1bmlxbmFtZTowOnw";
    private String PPRDIG = "kOdlRIxptgVY2ZRcjKYchIY9JBWkqGM_MjQy11OTC0fB9ACztDrs0lnjzAgHenjfCb8_Bgc6RAxO15TIaR5DeJTTQQUGiz-afCzDU9dYUUfge_WJLfiXjfR7iEGBDlBIQ5eSTV0oyMdLQHE-8UlVLYjbohSOzVVUPqRfoDvLE0w";

    private static final String LOGIN_INDEX_URL = "https://account.sogou.com";
    private static final int SG_COOKIE_MIN_LEN = 3;


    //搜狗域cookie 版本
    private static final int SG_COOKIE_VERSION = 5;

    // 非对称加密算法-私钥
    public static final
    String
            PRIVATE_KEY =
            "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMi+r3/yZsYSVdEHnDlEm6nw5Lp2" +
                    "Ki9tN/HBa/LnyInE3eIZ7x5PdjRqPNfv7oLX/JMUAT5uBzTDPjkBqGT9fxpjafdwQUCUtDbsZ6ZI" +
                    "Hb39LiWdr4c2bsKW3TbAp/0vx59OuiHaTjUVIZnNkFh72rThmIIZBsMM99ZOPoj8EQUTAgMBAAEC" +
                    "gYEAqUsOrGNbwuzRjH/TgwRWFqI98vYWK3r7NBl/lRFdsLniuXxPiQtQT3HMr/r69UN7EPpM9j5K" +
                    "O3fwcJjyT4Ds/266sO3WLk5fxIv704HttYO9/yTTKA1ZXjuebYxgg8HZMQwyb8uWO0/XT1kF02yU" +
                    "CZvTRMbAsrFahxusNex/2ZECQQD7J97jftybLcjbw/vLZgFEd3x2UzWrvS7XOGUPg0qAwCTCi9NX" +
                    "XMt7e1WCYdPyd6RoNnBox/44AINomfzlLARJAkEAzJ3mzQa88QZ3DVEH3zMyvXMXXHWQGjX7UCkQ" +
                    "Px6qqNrqbwoXB9T0Yp13Hi1tWih3JmFSESfzOrRfUHBVcsdGewJBAKOJ2LalupxI+cswGFrfNuAQ" +
                    "NbkOgZose72keRna0b54XvdW+Oyf/deP/aQCc3IkuacqG5P+9egdXXPVITlQqhECQCMZav/8ieim" +
                    "fUGRhtIozClnVriLih6U5/lGMf1B23B/rPtDNdQoGYvZCxfoHvv6OQYiZ5t9yOFnE3qO6nl36YUC" +
                    "QCUkidj2RX7aEGCy24mwYimbF0EKljzPYVbcoTWujGFOLaVIC6SNf95mFwfEO3D7xTs+UEdWVrUC" +
                    "0pOTjeSYPdw=";


    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private CommonManager commonManager;
    @Autowired
    private LoginApiManager proxyLoginApiManager;
    @Autowired
    private LoginApiManager sgLoginApiManager;


    @Override
    public AppConfig queryAppConfigByClientId(int clientId) {
        AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
        return appConfig;
    }

    @Override
    public Result setCookie(HttpServletResponse response, CookieApiParams cookieApiParams, int maxAge) {
        Result result = new APIResultSupport(false);
        Result getCookieValueResult = proxyLoginApiManager.getCookieInfo(cookieApiParams);
        if (getCookieValueResult.isSuccess()) {
            String ppinf = (String) getCookieValueResult.getModels().get("ppinf");
            String pprdig = (String) getCookieValueResult.getModels().get("pprdig");
            ServletUtil.setCookie(response, "ppinf", ppinf, maxAge, CommonConstant.SOGOU_ROOT_DOMAIN);
            ServletUtil.setCookie(response, "pprdig", pprdig, maxAge, CommonConstant.SOGOU_ROOT_DOMAIN);
            response.addHeader("Sohupp-Cookie", "ppinf,pprdig");
            //response 回去的时候设置一个p3p的header,用来定义IE的跨域问题,解决IE下iframe里无法种cookie的bug。
            response.setHeader("P3P", "CP=CAO PSA OUR");
            result.setSuccess(true);
        }
        return result;
    }

    @Override
    public Result setSGCookie(HttpServletResponse response, CookieApiParams cookieApiParams, int maxAge) {
        Result result = new APIResultSupport(false);

        Date current = new Date();
        long createTime = current.getTime() / 1000;
        long expireTime = DateUtils.addSeconds(current, (int) DateAndNumTimesConstant.TWO_WEEKS).getTime() / 1000;

        try {
            String infValue = buildCookieInfStr(cookieApiParams);
            StringBuilder sginfValue = new StringBuilder();
            sginfValue.append(SG_COOKIE_VERSION).append("|");
            sginfValue.append(createTime).append("|");
            sginfValue.append(expireTime).append("|");
            sginfValue.append(infValue);
            //生成ppinf
            String ppinf = sginfValue.toString();
            //生成pprdig
            RSAEncoder rsaEncoder = new RSAEncoder(PRIVATE_KEY);
            rsaEncoder.init();
            String pprdig = rsaEncoder.sgrdig(ppinf);

            ServletUtil.setCookie(response, "ppinf", ppinf, maxAge, CommonConstant.SOGOU_ROOT_DOMAIN);
            ServletUtil.setCookie(response, "pprdig", pprdig, maxAge, CommonConstant.SOGOU_ROOT_DOMAIN);
            response.addHeader("Sohupp-Cookie", "ppinf,pprdig");
            //response 回去的时候设置一个p3p的header,用来定义IE的跨域问题,解决IE下iframe里无法种cookie的bug。
            response.setHeader("P3P", "CP=CAO PSA OUR");
            result.setSuccess(true);
        } catch (Exception e) {
            LOGGER.error("setSGCookie error. userid:" + cookieApiParams.getUserid(), e);
        }
        return result;
    }


    @Override
    public Result setCookie(HttpServletResponse response, String passportId, int client_id, String ip, String ru, int maxAge) {
        CookieApiParams cookieApiParams = new CookieApiParams();
        cookieApiParams.setUserid(passportId);
        cookieApiParams.setClient_id(client_id);
        cookieApiParams.setRu(ru);
        cookieApiParams.setTrust(CookieApiParams.IS_ACTIVE);
        cookieApiParams.setPersistentcookie(String.valueOf(1));
        cookieApiParams.setIp(ip);
        Result result = setCookie(response, cookieApiParams, maxAge);
        return result;
    }

    @Override
    public Result setCookie(HttpServletResponse response, String passportId, int client_id, String ip, String ru, int maxAge, String uniqname, boolean setNewCookie) {
        CookieApiParams cookieApiParams = new CookieApiParams();
        cookieApiParams.setUserid(passportId);
        cookieApiParams.setClient_id(client_id);
        cookieApiParams.setRu(ru);
        cookieApiParams.setTrust(CookieApiParams.IS_ACTIVE);
        cookieApiParams.setPersistentcookie(String.valueOf(1));
        cookieApiParams.setIp(ip);
        cookieApiParams.setUniqname(uniqname);
        Result result;
        if (setNewCookie) {
            //种ver=5的新cookie
            result = setSGCookie(response, cookieApiParams, maxAge);
        } else {
            //仍然通过调用搜狗获取cookie信息接口
            result = setCookie(response, cookieApiParams, maxAge);
        }
        return result;

    }


//    @Override
//    public boolean setSogouCookie(HttpServletResponse response, String passportId, int client_id, String ip, int maxAge, String ru) {
//        CookieApiParams cookieApiParams = new CookieApiParams();
//        cookieApiParams.setUserid(passportId);
//        cookieApiParams.setClient_id(client_id);
//        cookieApiParams.setRu(ru);
//        cookieApiParams.setTrust(CookieApiParams.IS_ACTIVE);
//        cookieApiParams.setPersistentcookie(String.valueOf(1));
//        cookieApiParams.setIp(ip);
//        Result getCookieValueResult = proxyLoginApiManager.getCookieInfo(cookieApiParams);
//        if (getCookieValueResult.isSuccess()) {
//            String ppinf = (String) getCookieValueResult.getModels().get("ppinf");
//            String pprdig = (String) getCookieValueResult.getModels().get("pprdig");
//            ServletUtil.setCookie(response, "ppinf", ppinf, maxAge, CommonConstant.SOGOU_ROOT_DOMAIN);
//            ServletUtil.setCookie(response, "pprdig", pprdig, maxAge, CommonConstant.SOGOU_ROOT_DOMAIN);
//            response.addHeader("Sohupp-Cookie", "ppinf,pprdig");
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public Result setCookie(HttpServletResponse response, String passportId, int client_id, String ip, int sogouMaxAge, String sogouRu, int sohuAutoLogin, String sohuRu) {
//        Result result = new APIResultSupport(false);
//        //种搜狗域cookie
//        boolean setSogouCookieRes = setSogouCookie(response, passportId, client_id, ip, sogouMaxAge, sogouRu);
//        if (!setSogouCookieRes) {
//            result.setSuccess(false);
//            result.setCode(ErrorUtil.ERR_CODE_CREATE_COOKIE_FAILED);
//            result.setMessage("生成cookie失败");
//            return result;
//        }
//
//        //todo 只有@sogou域 和 sohu矩阵域才种跨域cookie
//        result = createSohuCookieUrl(passportId, sohuRu, sohuAutoLogin);
//        return result;
//
//    }


    @Override
    public String buildCreateSSOCookieUrl(String domain, int client_id, String passportId, String uniqname, String refnick, String ru, String ip) {
        StringBuilder urlBuilder = new StringBuilder();
        String daohangDomain = ConnectDomainEnum.DAOHANG.toString();
        String haoDomain = ConnectDomainEnum.HAO.toString();
        String shurufaDomain = ConnectDomainEnum.SHURUFA.toString();
        String pinyinQQDomain = ConnectDomainEnum.PINYIN_CN.toString();
        String teemoDomain = ConnectDomainEnum.TEEMO.toString();

        if (domain.equals(daohangDomain)) {
            urlBuilder.append(CommonConstant.DAOHANG_CREATE_COOKIE_URL).append("?domain=").append(daohangDomain);
        } else if (domain.equals(haoDomain)) {
            urlBuilder.append(CommonConstant.HAO_CREATE_COOKIE_URL).append("?domain=").append(haoDomain);
        }
        ///////////这块需要修改成统一的， 先加上，以后改。  add by denghua/////////////
        else if (domain.equals(shurufaDomain)) {
            urlBuilder.append(CommonConstant.SHURUFA_CREATE_COOKIE_URL).append("?domain=").append(shurufaDomain);
        }
        ////////////////////// add by denghua end///////////////
        else if (domain.equals(pinyinQQDomain)) {
            urlBuilder.append(CommonConstant.PINYIN_CN_CREATE_COOKIE_URL).append("?domain=").append(pinyinQQDomain);
        } else if (domain.equals(teemoDomain)) {
            urlBuilder.append(CommonConstant.TEEMO_CN_CREATE_COOKIE_URL).append("?domain=").append(teemoDomain);
        } else {
            return null;
        }

        CookieApiParams cookieApiParams = new CookieApiParams(passportId, client_id, ru, ip, uniqname, refnick);
        Result getCookieValueResult = sgLoginApiManager.getCookieInfo(cookieApiParams);
        if (!getCookieValueResult.isSuccess()) {
            return null;
        }
        String sginf = (String) getCookieValueResult.getModels().get("sginf");
        String sgrdig = (String) getCookieValueResult.getModels().get("sgrdig");

        String cookieData[] = sginf.split("\\" + CommonConstant.SEPARATOR_1);
        String createtime = cookieData[1];
        long ct = new Long(createtime);
        String code1 = commonManager.getCode(sginf, CommonConstant.SGPP_DEFAULT_CLIENTID, ct);
        String code2 = commonManager.getCode(sgrdig, CommonConstant.SGPP_DEFAULT_CLIENTID, ct);
        urlBuilder.append("&sginf=").append(sginf)
                .append("&sgrdig=").append(sgrdig)
                .append("&code1=").append(code1)
                .append("&code2=").append(code2)
                .append("&ru=").append(Coder.encodeUTF8(ru));
        return urlBuilder.toString();
    }

    @Override
    public Result setSSOCookie(HttpServletResponse response, SSOCookieParams ssoCookieParams) {
        Result result = new APIResultSupport(false);
        //验证code
        String sginf = ssoCookieParams.getSginf();
        String sgrdig = ssoCookieParams.getSgrdig();
        String cookieData[] = sginf.split("\\" + CommonConstant.SEPARATOR_1);
        if (cookieData.length < SG_COOKIE_MIN_LEN) {
            result.setCode(ErrorUtil.ERR_CODE_ERROR_COOKIE);
            result.setMessage(ErrorUtil.getERR_CODE_MSG(ErrorUtil.ERR_CODE_ERROR_COOKIE));
            return result;
        }

        String createtime = cookieData[1];
        String expiretime = cookieData[2];
        long ct = new Long(createtime);
        long et = new Long(expiretime);
        boolean code1Res = commonManager.isCodeRight(sginf, CommonConstant.SGPP_DEFAULT_CLIENTID, ct, ssoCookieParams.getCode1());
        if (!code1Res) {
            result.setCode(ErrorUtil.INTERNAL_REQUEST_INVALID);
            result.setMessage(ErrorUtil.getERR_CODE_MSG(ErrorUtil.INTERNAL_REQUEST_INVALID));
            return result;
        }
        boolean code2Res = commonManager.isCodeRight(sgrdig, CommonConstant.SGPP_DEFAULT_CLIENTID, ct, ssoCookieParams.getCode2());
        if (!code2Res) {
            result.setCode(ErrorUtil.INTERNAL_REQUEST_INVALID);
            result.setMessage(ErrorUtil.getERR_CODE_MSG(ErrorUtil.INTERNAL_REQUEST_INVALID));
            return result;
        }
        boolean isCtValid = CommonHelper.isSecCtValid(ct);
        if (!isCtValid) {
            result.setCode(ErrorUtil.INTERNAL_REQUEST_INVALID);
            result.setMessage(ErrorUtil.getERR_CODE_MSG(ErrorUtil.INTERNAL_REQUEST_INVALID));
            return result;
        }

        int maxAge = getMaxAge(et);
        String domain = ssoCookieParams.getDomain();
        ServletUtil.setCookie(response, LoginConstant.COOKIE_SGINF, sginf, maxAge, domain);
        ServletUtil.setCookie(response, LoginConstant.COOKIE_SGRDIG, sgrdig, maxAge, domain);
        //response 回去的时候设置一个p3p的header,用来定义IE的跨域问题,解决IE的iframe里跨域无法种cookie的bug。
        response.setHeader("P3P", "CP=CAO PSA OUR");
        result.setSuccess(true);
        return result;
    }

    @Override
    public Result setPPCookie(HttpServletResponse response, PPCookieParams ppCookieParams) {
        Result result = new APIResultSupport(false);
        //验证code
        String ppinf = ppCookieParams.getPpinf();
        String pprdig = ppCookieParams.getPprdig();
        String passport = ppCookieParams.getPassport();
        long ct = 0;
        String s = ppCookieParams.getS().trim();
        if (s.contains(",")) {
            String sArr[] = s.split(",");
            String s1 = sArr[0];
            ct = new Long(Long.parseLong(s1));
        } else {
            ct = new Long(Long.parseLong(s));
        }
        boolean code1Res = commonManager.isCodeRight(ppinf, CommonConstant.PC_CLIENTID, ct, ppCookieParams.getCode1());
        if (!code1Res) {
            result.setCode(ErrorUtil.INTERNAL_REQUEST_INVALID);
            result.setMessage(ErrorUtil.getERR_CODE_MSG(ErrorUtil.INTERNAL_REQUEST_INVALID));
            return result;
        }
        boolean code2Res = commonManager.isCodeRight(pprdig, CommonConstant.PC_CLIENTID, ct, ppCookieParams.getCode2());
        if (!code2Res) {
            result.setCode(ErrorUtil.INTERNAL_REQUEST_INVALID);
            result.setMessage(ErrorUtil.getERR_CODE_MSG(ErrorUtil.INTERNAL_REQUEST_INVALID));
            return result;
        }
        boolean codeRes = commonManager.isCodeRight(passport, CommonConstant.PC_CLIENTID, ct, ppCookieParams.getCode());
        if (!codeRes) {
            result.setCode(ErrorUtil.INTERNAL_REQUEST_INVALID);
            result.setMessage(ErrorUtil.getERR_CODE_MSG(ErrorUtil.INTERNAL_REQUEST_INVALID));
            return result;
        }
        boolean isCtValid = CommonHelper.isMillCtValid(ct);
        if (!isCtValid) {
            result.setCode(ErrorUtil.INTERNAL_REQUEST_INVALID);
            result.setMessage(ErrorUtil.getERR_CODE_MSG(ErrorUtil.INTERNAL_REQUEST_INVALID));
            return result;
        }

        if (!"0".equals(ppCookieParams.getLivetime())) {
            int maxAge = (int) DateAndNumTimesConstant.TWO_WEEKS;
            long expire = DateUtil.generatorVaildTime(maxAge) / 1000;
//            ServletUtil.setCookie(response, LoginConstant.COOKIE_PPINF, ppinf, maxAge, CommonConstant.SOGOU_ROOT_DOMAIN);
            ServletUtil.setExpireCookie(response, LoginConstant.COOKIE_PPINF, ppinf, CommonConstant.SOGOU_ROOT_DOMAIN, expire);
            ServletUtil.setHttpOnlyCookie(response, LoginConstant.COOKIE_PPRDIG, pprdig, CommonConstant.SOGOU_ROOT_DOMAIN, expire);
            ServletUtil.setHttpOnlyCookie(response, LoginConstant.COOKIE_PASSPORT, passport, CommonConstant.SOGOU_ROOT_DOMAIN, expire);
        } else {
            int maxAge = -1;
            ServletUtil.setCookie(response, LoginConstant.COOKIE_PPINF, ppinf, maxAge, CommonConstant.SOGOU_ROOT_DOMAIN);
            ServletUtil.setHttpOnlyCookie(response, LoginConstant.COOKIE_PPRDIG, pprdig, CommonConstant.SOGOU_ROOT_DOMAIN);
            ServletUtil.setHttpOnlyCookie(response, LoginConstant.COOKIE_PASSPORT, passport, CommonConstant.SOGOU_ROOT_DOMAIN);
        }
        //response 回去的时候设置一个p3p的header,用来定义IE的跨域问题,解决IE的iframe里跨域无法种cookie的bug。
        response.setHeader("P3P", "CP=CAO PSA OUR");
        result.setSuccess(true);
        return result;
    }

    @Override
    public void clearCookie(HttpServletResponse response) {
        ServletUtil.clearCookie(response, LoginConstant.COOKIE_PPINF);
        ServletUtil.clearCookie(response, LoginConstant.COOKIE_PPRDIG);
        ServletUtil.clearCookie(response, LoginConstant.COOKIE_PASSPORT);
        ServletUtil.clearCookie(response, LoginConstant.COOKIE_PPINFO);
    }

    //获取cookie有效期
    private int getMaxAge(long et) {
        int maxAge = -1;
        if (et > 0) {
            long currentTime = System.currentTimeMillis() / 1000;
            maxAge = DateUtil.getIntervalSec(et, currentTime);
            if (maxAge == 0) {
                maxAge = -1;
            }
        }
        return maxAge;
    }

    /**
     * 生成ppinf中的参数value
     *
     * @param cookieApiParams
     * @return
     * @throws Exception
     */
    private String buildCookieInfStr(CookieApiParams cookieApiParams) throws Exception {
        StringBuilder infValue = new StringBuilder();
        Map<String, String> infValueMap = Maps.newHashMap();
        infValueMap.put("userid", cookieApiParams.getUserid());
        infValueMap.put("crt", String.valueOf(System.currentTimeMillis() / 1000));  // TODO 查表拿注册时间，但目前是临时方案且应用不用该字段，暂设定为当前时间
        infValueMap.put("clientid", String.valueOf(cookieApiParams.getClient_id()));
        infValueMap.put("trust", String.valueOf(cookieApiParams.getTrust()));

        if (!Strings.isNullOrEmpty(cookieApiParams.getUniqname())) {
            infValueMap.put("uniqname", Coder.encodeUTF8(cookieApiParams.getUniqname()));
        } else {
            infValueMap.put("uniqname", StringUtils.EMPTY);
        }
        if (!Strings.isNullOrEmpty(Coder.encodeUTF8(cookieApiParams.getRefnick()))) {
            infValueMap.put("refnick", Coder.encodeUTF8(cookieApiParams.getRefnick()));
        } else {
            infValueMap.put("refnick", StringUtils.EMPTY);
        }

        for (Map.Entry<String, String> entry : infValueMap.entrySet()) {
            infValue.append(entry.getKey()).append(":");
            infValue.append(entry.getValue().length()).append(":");
            infValue.append(entry.getValue()).append("|");
        }
        return Coder.encryptBase64URLSafeString(infValue.toString());
    }
}
