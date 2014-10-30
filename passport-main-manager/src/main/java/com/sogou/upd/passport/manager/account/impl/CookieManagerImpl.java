package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.*;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.math.RSAEncoder;
import com.sogou.upd.passport.common.parameter.ConnectDomainEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.*;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.CookieManager;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import com.sogou.upd.passport.manager.form.PPCookieParams;
import com.sogou.upd.passport.manager.form.SSOCookieParams;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

    //    private static final Logger LOGGER = LoggerFactory.getLogger(CookieManagerImpl.class);
    private static final Logger LOGGER = LoggerFactory.getLogger("com.sogou.upd.passport.setCookieFileAppender");

    private static final int SG_COOKIE_MIN_LEN = 3;

    //搜狗域cookie 版本
    private static final int SG_COOKIE_VERSION = 5;

    //shard数
    private static final int SHARD_COUNT = 2;

    //目标值
    private static final int AIM_RESULT = 0;

    //生成cookie并且种cookie
    private static final int CREATE_COOKIE_AND_SET = 0;

    private static final String KEY_SPLITER = "|";

    private static final String VALUE_SPLITER = ":";

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    //制表符
    private static final String DATA_FORMAT_TAB = "\t";


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

    @Autowired
    private RedisUtils redisUtils;


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
    public Result setCookie(HttpServletResponse response, String passportId, int client_id, String ip, String ru, int maxAge, String uniqname) {
        CookieApiParams cookieApiParams = new CookieApiParams();
        cookieApiParams.setUserid(passportId);
        cookieApiParams.setClient_id(client_id);
        cookieApiParams.setRu(ru);
        cookieApiParams.setTrust(CookieApiParams.IS_ACTIVE);
        cookieApiParams.setPersistentcookie(String.valueOf(1));
        cookieApiParams.setIp(ip);
        cookieApiParams.setUniqname(uniqname);
        Result result = null;
        Boolean setNewCookie = Boolean.TRUE;
        if (client_id == 1100 || client_id == 1120) {
            if (setNewCookie) {
                //种ver=5的新cookie
                result = setSGCookie(response, cookieApiParams, maxAge);
            }
        } else {
            //仍然通过调用搜狗获取cookie信息接口
            result = setCookie(response, cookieApiParams, maxAge);
        }
        return result;

    }

    @Override
    public Result createCookie(HttpServletResponse response, CookieApiParams cookieApiParams) {
        Result result = new APIResultSupport(false);

        //首先根据产品线判断、部分用户种新cookie，剩余用户老cookie
        //首批应用市场（web端）、壁纸（桌面端）
        String ppinf = null;
        String pprdig = null;
        //默认为false
        boolean setNewCookie = false;
        try {
            String appModuleReplace = redisUtils.get(CacheConstant.CACHE_KEY_MODULE_APP_REPLACE);

            Map<String, String> appsMap = Maps.newConcurrentMap();
            if (!Strings.isNullOrEmpty(appModuleReplace)) {
                appsMap = Splitter.on(KEY_SPLITER).withKeyValueSeparator(VALUE_SPLITER).split(appModuleReplace);
            }
            //1110:应用市场 2002:壁纸 1100:搜狗游戏 1120:通行证
            if (appsMap.containsKey(String.valueOf(cookieApiParams.getClient_id()))) {
                //数据筛选 shard 基数
                int shard_count = Integer.parseInt(appsMap.get(String.valueOf(cookieApiParams.getClient_id())));
                setNewCookie = isSetNewCookie(cookieApiParams.getUserid(), shard_count, AIM_RESULT);
                //部分用户种新cookie、剩余用户种老cookie
                if (setNewCookie) {
                    //种新cookie
                    result = createSGCookie(cookieApiParams);
                    if (result.isSuccess()) {
                        ppinf = (String) result.getModels().get("ppinf");
                        pprdig = (String) result.getModels().get("pprdig");
                    }
                } else {
                    result = proxyLoginApiManager.getCookieInfo(cookieApiParams);
                    if (result.isSuccess()) {
                        ppinf = (String) result.getModels().get("ppinf");
                        pprdig = (String) result.getModels().get("pprdig");
                    } else {
                        result.setCode(ErrorUtil.ERR_CODE_CREATE_COOKIE_FAILED);
                        result.setMessage(ErrorUtil.ERR_CODE_MSG_MAP.get(ErrorUtil.ERR_CODE_CREATE_COOKIE_FAILED));
                        return result;
                    }
                }
            } else {
                result = proxyLoginApiManager.getCookieInfo(cookieApiParams);
                if (result.isSuccess()) {
                    ppinf = (String) result.getModels().get("ppinf");
                    pprdig = (String) result.getModels().get("pprdig");
                } else {
                    result.setCode(ErrorUtil.ERR_CODE_CREATE_COOKIE_FAILED);
                    result.setMessage(ErrorUtil.ERR_CODE_MSG_MAP.get(ErrorUtil.ERR_CODE_CREATE_COOKIE_FAILED));
                    return result;
                }
            }

            //web端生成cookie后、种下cookie 、桌面端不同
            if (cookieApiParams.getCreateAndSet() == CREATE_COOKIE_AND_SET) {
                ServletUtil.setCookie(response, "ppinf", ppinf, cookieApiParams.getMaxAge(), CommonConstant.SOGOU_ROOT_DOMAIN);
                ServletUtil.setCookie(response, "pprdig", pprdig, cookieApiParams.getMaxAge(), CommonConstant.SOGOU_ROOT_DOMAIN);
                response.addHeader("Sohupp-Cookie", "ppinf,pprdig");
                //response 回去的时候设置一个p3p的header,用来定义IE的跨域问题,解决IE下iframe里无法种cookie的bug。
                response.setHeader("P3P", "CP=CAO PSA OUR");
            }

            //构建桌面端获取cookie的跳转地址
            String redirecturl = buildRedirectUrl(result, cookieApiParams);
            result.setDefaultModel("redirectUrl", redirecturl);
            result.setSuccess(true);
        } catch (Exception e) {
            LOGGER.error("createCookie error. userid:{},client_id:{},setNewCookie:{}", new Object[]{cookieApiParams.getUserid(), cookieApiParams.getClient_id(), setNewCookie}, e);
        } finally {
            //记录用户种cookie的log
            LOGGER.info(buildSetCookieLog(cookieApiParams, setNewCookie, ppinf, result.getCode()));
        }
        return result;
    }

    /**
     * 构建记录module替换 用户种cookie的日志
     *
     * @param cookieApiParams
     * @param setNewCookie
     * @param ppinf
     * @return
     */
    private String buildSetCookieLog(CookieApiParams cookieApiParams, boolean setNewCookie, String ppinf, String resultCode) {
        StringBuilder setCookieLog = new StringBuilder();
        Date date = new Date();
        FastDateFormat fastDateFormat = FastDateFormat.getInstance(DATE_FORMAT);
        setCookieLog.append(fastDateFormat.format(date));
        setCookieLog.append(DATA_FORMAT_TAB).append(cookieApiParams.getIp());
        setCookieLog.append(DATA_FORMAT_TAB).append(cookieApiParams.getUserid());
        setCookieLog.append(DATA_FORMAT_TAB).append(cookieApiParams.getClient_id());
        setCookieLog.append(DATA_FORMAT_TAB).append(resultCode);
        setCookieLog.append(DATA_FORMAT_TAB).append(cookieApiParams.getRu());
        setCookieLog.append(DATA_FORMAT_TAB).append(setNewCookie == true ? 0 : 1);
        setCookieLog.append(DATA_FORMAT_TAB).append(ppinf);
        return setCookieLog.toString();
    }

    /**
     * 是否中新cookie
     *
     * @param userid
     * @param shardCount
     * @param aimCount
     * @return
     */
    private Boolean isSetNewCookie(String userid, int shardCount, int aimCount) {
        String useridHash = DigestUtils.md5Hex(userid);
        if (Strings.isNullOrEmpty(useridHash)) {
            return false;
        }
        int tempInt = Integer.parseInt(useridHash.substring(0, 2), 16);
        if (tempInt % shardCount == aimCount) {
            return true;
        }
        return false;
    }

    /**
     * 关于passport cookie:
     * 格式: ver|create_time|expire_time|info|hash|rsa
     * 其中, hash 随便填, 并不使用(但要有), 而 rsa 为 pprdig的值.
     * 存在passport cookie时, 也需要有ppinfo cookie, 值并不使用(但要有)
     * <p/>
     * <p/>
     * ver=5的 那passport “ver|create_time|expire_time|info” 这些就是按照sginf来生成，hash 可以是随机的字符串 ，
     * rsa是按照sgrdig方式来生成 ，同时 ppinfo 必须要传，可以是随机生成的字符串吧
     *
     * @param cookieApiParams
     * @return
     */
    public Result createSGCookie(CookieApiParams cookieApiParams) {
        Result result = new APIResultSupport(false);
        Date current = new Date();
        long createTime = current.getTime() / 1000;
        long expireTime = DateUtils.addSeconds(current, (int) DateAndNumTimesConstant.TWO_WEEKS).getTime() / 1000;

        try {
            String infValue = buildCookieInfStr(cookieApiParams);
            StringBuilder inf_prefix = new StringBuilder();
            inf_prefix.append(SG_COOKIE_VERSION).append("|");
            inf_prefix.append(createTime).append("|");
            inf_prefix.append(expireTime).append("|");
            inf_prefix.append(infValue);
            String ppinf = inf_prefix.toString();

            RSAEncoder rsaEncoder = new RSAEncoder(PRIVATE_KEY);
            rsaEncoder.init();
            String pprdig = rsaEncoder.sgrdig(ppinf);

            //生成passport cookie
            StringBuilder passportCookie = new StringBuilder();
            passportCookie.append(ppinf).append("|");
            passportCookie.append(ToolUUIDUtil.genreateUUidWithOutSplit().substring(0, 10)).append("|");
            passportCookie.append(pprdig);

            result.setSuccess(true);
            result.setDefaultModel("ppinf", ppinf);
            result.setDefaultModel("pprdig", pprdig);
            result.setDefaultModel("passport", passportCookie.toString());
            result.setDefaultModel("ppinfo", ToolUUIDUtil.genreateUUidWithOutSplit().substring(10, 20));
        } catch (Exception e) {
            LOGGER.error("createSGCookie error. userid:" + cookieApiParams.getUserid(), e);
        }
        return result;
    }

    /**
     * 构建桌面端获取cookie 用的跳转url
     *
     * @param result
     * @return
     */
    private String buildRedirectUrl(Result result, CookieApiParams cookieApiParams) {

        String ppinf = (String) result.getModels().get("ppinf");
        String pprdig = (String) result.getModels().get("pprdig");
        String passport = (String) result.getModels().get("passport");

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

        StringBuilder locationUrlBuilder = new StringBuilder(CommonConstant.PP_COOKIE_URL);  // 移动浏览器端使用https域名会有问题
        locationUrlBuilder.append("?").append("ppinf=").append(ppinf)
                .append("&pprdig=").append(pprdig)
                .append("&passport=").append(passport)
                .append("&code1=").append(code1)
                .append("&code2=").append(code2)
                .append("&code=").append(code3)
                .append("&s=").append(String.valueOf(ct))
                .append("&lastdomain=").append(0);
        if ("1".equals(cookieApiParams.getPersistentcookie())) {
            locationUrlBuilder.append("&livetime=1");
        }
        String ru = buildRedirectUrl(cookieApiParams.getRu(), 0);
        try {
            // 1105不允许URLEncode，但壁纸需要URLEncode，所以传clientId区分
            if (cookieApiParams.getClient_id() != CommonConstant.PINYIN_MAC_CLIENTID) {
                ru = URLEncoder.encode(ru, CommonConstant.DEFAULT_CHARSET);
            }
        } catch (UnsupportedEncodingException e) {
        }
        locationUrlBuilder.append("&ru=").append(ru);   // 输入法Mac要求Location里的ru不能decode
        return locationUrlBuilder.toString();
    }


    private String buildRedirectUrl(String ru, int status) {
        if (Strings.isNullOrEmpty(ru)) {
            ru = CommonConstant.DEFAULT_CONNECT_REDIRECT_URL;
        }
        if (ru.contains("?")) {
            return ru + "&status=" + status;
        } else {
            return ru + "?status=" + status;
        }
    }

    @Override
    public String buildCreateSSOCookieUrl(String domain, int client_id, String passportId, String uniqname, String refnick, String ru, String ip) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(ConnectDomainEnum.getSSOCookieUrl(domain)).append("?domain=").append(domain);
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
        result.setMessage("登录成功");
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
     * <p/>
     * 数据格式：clientid:4:2009|crt:10:1409652043|refnick:27:%E5%8D%90%E9%99%B6%E5%8D%8D|trust:1:1|userid:44:5EED6E92B98534E2B74020F85C875186@qq.sohu.com|uniqname:27:%E5%8D%90%E9%99%B6%E5%8D%8D|
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
        if (!Strings.isNullOrEmpty(cookieApiParams.getRefnick())) {
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
