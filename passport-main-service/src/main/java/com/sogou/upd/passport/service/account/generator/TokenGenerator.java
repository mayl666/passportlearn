package com.sogou.upd.passport.service.account.generator;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.math.AES;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.math.RSA;
import com.sogou.upd.passport.common.utils.DateUtil;
import com.sogou.upd.passport.service.account.dataobject.RefreshTokenCipherDO;
import com.sogou.upd.passport.service.account.dataobject.TokenCipherDO;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Token生成器，生成access_token和refresh_token User: shipengzhi Date: 13-3-25 Time: 下午10:22 To change this template use File |
 * Settings | File Templates.
 */
public class TokenGenerator {

    private static Logger logger = LoggerFactory.getLogger(TokenGenerator.class);

    // 非对称加密算法-公钥
    public static final
    String
            PUBLIC_KEY =
            "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKg+nmc1UwpMGKHQP58jhJg/hLucm4oLBTBMyRBmCAKK\n" +
                    "7rU/9UWJqy8li64i5bYtx7rE8+I4EdC00To5kz6D61UCAwEAAQ==";
    // 非对称加密算法-私钥
    public static final
    String
            PRIVATE_KEY =
            "MIIBUgIBADANBgkqhkiG9w0BAQEFAASCATwwggE4AgEAAkEAqD6eZzVTCkwYodA/nyOEmD+Eu5yb\n" +
                    "igsFMEzJEGYIAorutT/1RYmrLyWLriLlti3HusTz4jgR0LTROjmTPoPrVQIDAQABAkAaMgW/1BGl\n" +
                    "3MtJBn+ha4pNmjY0b+HX0HdyWcJEh15f5rkqhcrAxzaHo5vHnnW+mYrIIGdeqF8QTbB1lMKYuIxR\n" +
                    "AiEA2TBeNoI/EU+4I876iOHn5kopV0+OCtkLdxsu12nUNsMCIQDGTzgd7uVDGKzX4oNy4VY0FrZR\n" +
                    "bqTdtJtiRKTFU8EkBwIgBSByKOE8MeFq3FWHbnG+sp3vieMT3EexUJdwrJ8P5lcCIBYHVsR8dRsu\n" +
                    "8oRItTFdtqWyoC4LjGTUWy5fUa5Zz2qhAh9rQb9VP0rnQPP3Hm9z9SFccXUZaPiC9a8+r5g5WUen";

    // 对称加密算法-密钥
    public static final String SECRET_KEY = "187e4310dd2dd9966dddcc10d1732241c96ba131";

    /**
     * 生成access_token 构成格式 passportID|clientId|vaild_timestamp(过期时间点，单位毫秒)|4位随机数|instanceId
     * 采用非对称加密算法RSA
     */
    public static String generatorAccessToken(String passportId, int clientId, int expiresIn, String instanceId)
            throws Exception {

        // 过期时间点
        long vaildTime = DateUtil.generatorVaildTime(expiresIn);

        // 4位随机数
        String random = RandomStringUtils.randomAlphanumeric(4);

        TokenCipherDO accessTokenCipherData = new TokenCipherDO(passportId, clientId, vaildTime, random, instanceId);
        String accessTokenContent = accessTokenCipherData.structureEncryptString();

        String encBase64Str;
        try {
            byte[] encByte = RSA.encryptByPrivateKey(accessTokenContent.getBytes(), PRIVATE_KEY);
            encBase64Str = Coder.encryptBase64URLSafeString(encByte);
        } catch (Exception e) {
            logger.error(
                    "Account Token generator fail, passportId:" + passportId + " clientId:" + clientId + " instanceId:"
                            + instanceId);
            throw e;
        }
        return encBase64Str;
    }

    /**
     * 生成refresh_token 构成格式 passportID|clientId|timestamp(当前时间戳，单位毫秒)|instanceId
     */
    public static String generatorRefreshToken(String passportId, int clientId, String instanceId) throws Exception {
        long timestamp = System.currentTimeMillis();
        String encrypt;
        try {
            RefreshTokenCipherDO refreshTokenCipherData = new RefreshTokenCipherDO(passportId, clientId, timestamp, instanceId);
            String refreshTokenContent = refreshTokenCipherData.structureEncryptString();
            encrypt = AES.encryptURLSafeString(refreshTokenContent, SECRET_KEY);
        } catch (Exception e) {
            logger.error(
                    "Refresh Token generator fail, passportId:" + passportId + " clientId:" + clientId + " instanceId:"
                            + instanceId);
            throw e;
        }
        return encrypt;
    }

    /**
     * 生成Pc端登录流程使用的token 构成格式 passportID|vaild_timestamp(过期时间点，单位毫秒)
     * 采用AES算法
     */
    public static String generatorPcToken(String passportId, int expiresIn, String clientSecret)
            throws Exception {
        // 过期时间点
        long vaildTime = DateUtil.generatorVaildTime(expiresIn);
        String tokenContent = passportId + CommonConstant.SEPARATOR_1 + vaildTime;
        String token;
        try {
            //加上4为随机数，是为了与sohu+ token长度区别开来
            token = RandomStringUtils.randomAlphanumeric(4) + AES.encryptURLSafeString(tokenContent, clientSecret);
        } catch (Exception e) {
            logger.error("Pc Token generator by AES fail, passportId:" + passportId);
            throw e;
        }
        return token;
    }
    //sohu生成token算法
    public static String generateSoHuPcToken(String passportId, int expiresIn, String clientSecret)
            throws Exception {
        RandomStr rs = new RandomStr();
        String refreshToken = rs.getRandomStr(CommonConstant.SOHU_PCTOKEN_LEN);
        return refreshToken;
    }


}
