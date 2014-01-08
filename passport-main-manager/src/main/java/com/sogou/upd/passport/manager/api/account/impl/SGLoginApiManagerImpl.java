package com.sogou.upd.passport.manager.api.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.math.RSA;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.AppAuthTokenApiParams;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieUrlApiParams;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.MappTokenService;
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
public class SGLoginApiManagerImpl implements LoginApiManager {
    private static Logger logger = LoggerFactory.getLogger(SGLoginApiManagerImpl.class);

    // 非对称加密算法-公钥
    public static final
    String
            PUBLIC_KEY =
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDkUuxaxxDrjs3xRnM8pHEvyl9w" +
                    "GOCXOF5iBF+GMu30rAmPVU5rrciyXitECpcCqWRIw67JfdW8D80RDSmMGktMaw3b" +
                    "vIJzobdBTpjZRBSE2HxWZaHtVR7pURmoYNz+NHi1UhS0sNo6Oe0v6FW96ShVGTtY" +
                    "X+Rk9o5FkqdfoXHp3QIDAQAB";
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
    private MappTokenService mappTokenService;

    @Override
    public Result webAuthUser(AuthUserApiParams authUserApiParams) {
        Result result = new APIResultSupport(false);
        try {
            result = accountService.verifyUserPwdVaild(authUserApiParams.getUserid(), authUserApiParams.getPassword(), false);
            return result;
        } catch (Exception e) {
            logger.error("accountLogin fail,userId:" + authUserApiParams.getUserid(), e);
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_LOGIN_FAILED);
            return result;
        }
    }

    @Override
    public Result appAuthToken(AppAuthTokenApiParams appAuthTokenApiParams) {
        Result result = new APIResultSupport(false);
        try {
            String passportId = mappTokenService.getPassprotIdByToken(appAuthTokenApiParams.getToken());
            if (!Strings.isNullOrEmpty(passportId)) {
                result.setSuccess(true);
                result.setMessage("操作成功");
                result.setDefaultModel("userid", passportId);
                return result;
            }
            result.setCode(ErrorUtil.ERR_SIGNATURE_OR_TOKEN);
            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            return result;
        }
    }

    @Override
    public Result buildCreateCookieUrl(CreateCookieUrlApiParams createCookieUrlApiParams, boolean isRuEncode, boolean isHttps) {
        return null;
    }

    @Override
    public Result getCookieValue(CreateCookieUrlApiParams createCookieUrlApiParams) {
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
            StringBuilder sginf = new StringBuilder();
            sginf.append(1).append("|");
            sginf.append(createTime).append("|");
            sginf.append(expireTime).append("|");
            sginf.append(infValue);
            //生成sgrdig
            String sgrdig = RSA.sign(sginf.toString(), PRIVATE_KEY);
            result.setSuccess(true);
            result.setDefaultModel("sginf", sginf);
            result.setDefaultModel("sgrdig", sgrdig);
        } catch (Exception e) {
            logger.error("creat cookie value fail， userid：" + cookieApiParams.getUserid(), e);
            result.setCode(ErrorUtil.ERR_CODE_CREATE_COOKIE_FAILED);
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
