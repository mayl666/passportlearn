package com.sogou.upd.passport.manager.api.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.math.RSA;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
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

import java.io.UnsupportedEncodingException;
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
            "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJ_kLjVADIIzkB2aenKVdIGBa55BXFV6fgHhJQZhIanNGCDCxQn" +
                    "2gntud-IXKdSe5dFygevdYbnKTkoEP1clM4UCAwEAAQ";
    // 非对称加密算法-私钥
    public static final
    String
            PRIVATE_KEY =
            "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAn-QuNUAMgjOQHZp6cpV0gYFrnkFcVXp-AeE" +
                    "lBmEhqc0YIMLFCfaCe2534hcp1J7l0XKB691hucpOSgQ_VyUzhQIDAQABAkBabEnxlXo9e_mptD5" +
                    "BZOJRhyachiw4ryBp4bD1raLDCrN2t0gFWY8ZcmbIX0b_xPJHN9GVClHydp1HOzTt6B9BAiEA2ka" +
                    "SlgWHASvJ-koFphVhZ-UIQs0M9C9zPKFraFENpPECIQC7hnDQ1XhEZ1u3FzxWIcAepuTQwj6_xYf" +
                    "UEYqBqE5n1QIgfu9Lj7LnL-cnLkadwlfsrV6jzzUvs1Fk0n2M2L1KEgECIEAdVB8ijU8d446y5A8" +
                    "y1OPl_d-eOiQJHkqUgL2Z1MzNAiEAvdWYFlmaz80R3FGWKKQSeEfUmiwOFJxVqseZSvcl_hc";

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
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result getCookieValue(CreateCookieUrlApiParams createCookieUrlApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
            String rdigMD5Value = Coder.encryptMD5(sginf.toString());
            byte[] encByte = RSA.encryptByPrivateKey(rdigMD5Value.getBytes(CommonConstant.DEFAULT_CONTENT_CHARSET), PRIVATE_KEY);
            String sgrdig = Coder.encryptBase64(encByte);
            result.setSuccess(true);
            result.setDefaultModel("sginf", sginf);
            result.setDefaultModel("sgrdig", sgrdig);
        } catch (Exception e) {
            logger.error("cropen.weibo.cneat cookie value fail， userid：" + cookieApiParams.getUserid(), e);
            result.setCode(ErrorUtil.ERR_CODE_CREATE_COOKIE_FAILED);
        }
        return result;
    }

    /*
 * 构造sginf里的value
 * userid:18:houlinyan@sohu.com| crt:10:2011-12-23|clientid:4:9998|trust:1:1| uniqname:9:houlinyan|refnick:9:houlinyan|
 */
    private String buildCookieInfStr(CookieApiParams cookieApiParams) throws UnsupportedEncodingException {
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
        return Coder.encryptBase64(infValue.toString());
    }

}
