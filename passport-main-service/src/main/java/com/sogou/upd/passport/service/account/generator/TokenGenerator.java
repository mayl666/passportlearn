package com.sogou.upd.passport.service.account.generator;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.math.AES;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.utils.DateUtil;
import org.apache.commons.lang3.RandomStringUtils;
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
            //特殊标识，是为了与sohu+ token长度区别开来
            token = CommonConstant.SG_TOKEN_START + AES.encryptURLSafeString(tokenContent, clientSecret);
        } catch (Exception e) {
            logger.error("Pc Token generator by AES fail, passportId:" + passportId);
            throw e;
        }
        return token;
    }

    public static void main(String args[]) throws Exception {
//        String passportId = "C8FB68EC3C5C62D21A8774B2870E79BC@qq.sohu.com";
        String passportId = "tinkame71wwwww01111@sogou.com";
        int expiresIn = 604800;
        String clientSecret = "c1756a351db27d817225e2a4fd7b3f7d";
        String encode = TokenGenerator.generatorPcToken(passportId, expiresIn, clientSecret);
        System.out.println("encode:" + encode);

        String decpde = TokenDecrypt.decryptPcToken(encode, clientSecret);
        System.out.println("decpde:" + decpde);

    }

    /**
     * 生成Wap端登录流程使用的token 构成格式 MD5(passportID|timestamp|4位随机数)
     */
    public static String generatorWapToken(String passportId)
            throws Exception {
        // 过期时间点
        long curTimestamp = System.currentTimeMillis();
        // 4位随机数
        String random = RandomStringUtils.randomAlphanumeric(4);
        String tokenContent = passportId + CommonConstant.SEPARATOR_1 + curTimestamp + CommonConstant.SEPARATOR_1 + random;
        String token;
        try {
            token = Coder.encryptMD5(tokenContent);
        } catch (Exception e) {
            logger.error("Pc Token generator by AES fail, passportId:" + passportId);
            throw e;
        }
        return token;
    }

    /**
     * 生成mapp端登录流程使用的token 构成格式 MD5(passportID|timestamp|4位随机数)
     * @param passportId
     * @return
     * @throws Exception
     */
    public static String generatorMappToken(String passportId)
            throws Exception {
        // 过期时间点
        long curTimestamp = System.currentTimeMillis();
        // 4位随机数
        String random = RandomStringUtils.randomAlphanumeric(4);
        String tokenContent = passportId + CommonConstant.SEPARATOR_1 + curTimestamp + CommonConstant.SEPARATOR_1 + random;
        String token;
        try {
            token = Coder.encryptMD5(tokenContent);
        } catch (Exception e) {
            logger.error("generatorMappToken fail, passportId:" + passportId);
            throw e;
        }
        return token;
    }

}
