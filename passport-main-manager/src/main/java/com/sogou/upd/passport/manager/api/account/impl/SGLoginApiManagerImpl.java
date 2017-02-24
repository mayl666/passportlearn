package com.sogou.upd.passport.manager.api.account.impl;

import com.google.common.collect.Maps;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.math.RSAEncoder;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.SohuPasswordType;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.common.utils.ToolUUIDUtil;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieUrlApiParams;
import com.sogou.upd.passport.manager.api.connect.SessionServerManager;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.MobilePassportMappingService;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-6-7
 * Time: 下午8:15
 * To change this template use File | Settings | File Templates.
 */
@Component("sgLoginApiManager")
public class SGLoginApiManagerImpl extends BaseProxyManager implements LoginApiManager {
    private static Logger logger = LoggerFactory.getLogger(SGLoginApiManagerImpl.class);

    // 非对称加密算法-公钥
    public static final
    String
            PUBLIC_KEY =
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDIvq9/8mbGElXRB5w5RJup8OS6diovbTfxwWvy" +
                    "58iJxN3iGe8eT3Y0ajzX7+6C1/yTFAE+bgc0wz45Aahk/X8aY2n3cEFAlLQ27GemSB29/S4lna+H" +
                    "Nm7Clt02wKf9L8efTroh2k41FSGZzZBYe9q04ZiCGQbDDPfWTj6I/BEFEwIDAQAB";
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
    private AccountService accountService;
    @Autowired
    private MobilePassportMappingService mobilePassportMappingService;
    @Autowired
    private SessionServerManager sessionServerManager;

    @Override
    public Result webAuthUser(AuthUserApiParams authUserApiParams) {
        Result result = new APIResultSupport(false);
        try {
            String userId = authUserApiParams.getUserid();
            if (PhoneUtil.verifyPhoneNumberFormat(userId)) {
                userId = mobilePassportMappingService.queryPassportIdByMobile(userId);
                if (StringUtils.isBlank(userId)) {//账号不存在
                    result.setCode(ErrorUtil.INVALID_ACCOUNT);
                    return result;
                }
            }
            if (AccountDomainEnum.INDIVID.equals(AccountDomainEnum.getAccountDomain(userId))) {
                userId = userId + CommonConstant.SOGOU_SUFFIX;
            }
            Result verifyUserResult = accountService.verifyUserPwdVaild(userId, authUserApiParams.getPassword(), false, SohuPasswordType.MD5);
            if (verifyUserResult.isSuccess()) {
                // if (authUserApiParams.getNeedsgid() == 1) {
                // web 登录都生成 sgid
                Result sessionResult = sessionServerManager.createSession(userId, null, authUserApiParams.isWap());
                if (sessionResult.isSuccess()) {
                    String sgid = (String) sessionResult.getModels().get(LoginConstant.COOKIE_SGID);
                    result.setDefaultModel("sgid", sgid);
                } else {
                    result.setCode(sessionResult.getCode());
                    return result;
                }
                // }
                result.setSuccess(true);
                result.setMessage("登录成功");
                result.setDefaultModel("userid", userId);
                result.setDefaultModel("uniqName", verifyUserResult.getModels().get("uniqName"));
                result.setDefaultModel("refnick", verifyUserResult.getModels().get("uniqName"));
            } else {
                result = verifyUserResult;
            }
            return result;
        } catch (Exception e) {
            logger.error("accountLogin fail,userId:" + authUserApiParams.getUserid(), e);
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_LOGIN_FAILED);
            return result;
        }
    }

    @Override
    public Result appAuthToken(String token) {
        return null;
    }

    @Override
    public Result buildCreateCookieUrl(CreateCookieUrlApiParams createCookieUrlApiParams, boolean isRuEncode, boolean isHttps) {
        return null;
    }

    @Override
    public Result getCookieInfo(CookieApiParams cookieApiParams) {
        Result result = new APIResultSupport(false);
        Date current = new Date();
        long createTime = current.getTime() / 1000;
        long expireTime = DateUtils.addSeconds(current, (int) DateAndNumTimesConstant.TWO_WEEKS).getTime() / 1000;
        try {
            //生成sginf
            String infValue = buildCookieInfStr(cookieApiParams);
            StringBuilder sginfValue = new StringBuilder();
            sginfValue.append(1).append("|");
            sginfValue.append(createTime).append("|");
            sginfValue.append(expireTime).append("|");
            sginfValue.append(infValue);
            String sginf = sginfValue.toString();
            //生成sgrdig
            RSAEncoder rsaEncoder = new RSAEncoder(PRIVATE_KEY);
            rsaEncoder.init();
            String sgrdig = rsaEncoder.sgrdig(sginf);
            result.setSuccess(true);
            result.setDefaultModel("sginf", sginf);
            result.setDefaultModel("sgrdig", sgrdig);
        } catch (Exception e) {
            logger.error("creat cookie value fail， userid：" + cookieApiParams.getUserid(), e);
            result.setCode(ErrorUtil.ERR_CODE_CREATE_COOKIE_FAILED);
        }
        return result;
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
    public Result getSGCookieInfoForAdapter(CookieApiParams cookieApiParams) {
        Result result = new APIResultSupport(false);
        Date current = new Date();
        long createTime = current.getTime() / 1000;
        long expireTime = DateUtils.addSeconds(current, (int) DateAndNumTimesConstant.TWO_WEEKS).getTime() / 1000;

        try {
            String infValue = buildCookieInfStr(cookieApiParams);
            StringBuilder inf_prefix = new StringBuilder();
            inf_prefix.append(5).append("|");
            inf_prefix.append(createTime).append("|");
            inf_prefix.append(expireTime).append("|");
            inf_prefix.append(infValue);
            String sginf = inf_prefix.toString();

            RSAEncoder rsaEncoder = new RSAEncoder(PRIVATE_KEY);
            rsaEncoder.init();
            String sgrdig = rsaEncoder.sgrdig(sginf);

            //生成passport cookie
            StringBuilder passportCookie = new StringBuilder();
            passportCookie.append(sginf).append("|");
            passportCookie.append(ToolUUIDUtil.genreateUUidWithOutSplit().substring(0, 10)).append("|");
            passportCookie.append(sgrdig);

            result.setSuccess(true);
            result.setDefaultModel("ppinf", sginf);
            result.setDefaultModel("pprdig", sgrdig);
            result.setDefaultModel("passport", passportCookie.toString());
            result.setDefaultModel("ppinfo", ToolUUIDUtil.genreateUUidWithOutSplit().substring(10, 20)); //再次找兰顺确定下？ module是否需要
        } catch (Exception e) {
            logger.error("getSGCookieInfoForAdapter error. userid:" + cookieApiParams.getUserid(), e);
        }
        return result;
    }


    /*
     * 构造sginf里的value
     * userid:18:houlinyan@sohu.com| crt:10:2011-12-23|clientid:4:9998|trust:1:1| uniqname:9:houlinyan|refnick:9:houlinyan|
     */
    private String buildCookieInfStr(CookieApiParams cookieApiParams) throws Exception {
        StringBuilder infValue = new StringBuilder();
        Map<String, String> infValueMap = Maps.newHashMap();
        infValueMap.put("userid", cookieApiParams.getUserid());
        infValueMap.put("crt", String.valueOf(System.currentTimeMillis() / 1000));  // TODO 查表拿注册时间，但目前是临时方案且应用不用该字段，暂设定为当前时间
        infValueMap.put("clientid", String.valueOf(cookieApiParams.getClient_id()));
        infValueMap.put("trust", String.valueOf(cookieApiParams.getTrust()));
        infValueMap.put("uniqname", Coder.encodeUTF8(cookieApiParams.getUniqname()));
        infValueMap.put("refnick", Coder.encodeUTF8(cookieApiParams.getRefnick()));
        for (Map.Entry<String, String> entry : infValueMap.entrySet()) {
            infValue.append(entry.getKey()).append(":");
            infValue.append(entry.getValue().length()).append(":");
            infValue.append(entry.getValue()).append("|");
        }
        return Coder.encryptBase64URLSafeString(infValue.toString());
    }

}
