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

    /* 非对称加密算法-私钥
     * 输入法生成加密串的私钥
     */
    public static final String
            PINYIN_PRIVATE_KEY =
            "MIIBUgIBADANBgkqhkiG9w0BAQEFAASCATwwggE4AgEAAkEAqD6eZzVTCkwYodA/nyOEmD+Eu5yb\n" +
                    "igsFMEzJEGYIAorutT/1RYmrLyWLriLlti3HusTz4jgR0LTROjmTPoPrVQIDAQABAkAaMgW/1BGl\n" +
                    "3MtJBn+ha4pNmjY0b+HX0HdyWcJEh15f5rkqhcrAxzaHo5vHnnW+mYrIIGdeqF8QTbB1lMKYuIxR\n" +
                    "AiEA2TBeNoI/EU+4I876iOHn5kopV0+OCtkLdxsu12nUNsMCIQDGTzgd7uVDGKzX4oNy4VY0FrZR\n" +
                    "bqTdtJtiRKTFU8EkBwIgBSByKOE8MeFq3FWHbnG+sp3vieMT3EexUJdwrJ8P5lcCIBYHVsR8dRsu\n" +
                    "8oRItTFdtqWyoC4LjGTUWy5fUa5Zz2qhAh9rQb9VP0rnQPP3Hm9z9SFccXUZaPiC9a8+r5g5WUen";

    /*
     * 浏览器生成加密串的私钥
     * linux生成的私钥需要转换成pkcs8
     * openssl pkcs8 -topk8 -inform PEM -in key.txt -outform PEM -nocrypt
     */
    public static final String
            BROWER_PRIVATE_KEY =
            "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKIJPa0bHY4VCXQH\n" +
                    "K6/L9rcWbe1Skr1ci7RHSDNc1fUHteiqhIMV359F1m2jPSG4XQObcS+wXxMdHj4C\n" +
                    "HtJ0zuPbNkmgzotlCbxPpK7bd+kn/19SrlguDgKNWPaGTR5Vx8mBZj/WNAXVK6LA\n" +
                    "9SOWZdiZrYjeu2kmWVwIs+zAO9i1AgMBAAECgYB72ChFqFXchIOnJNvlDzVQFlqK\n" +
                    "avQwuw0kCt9KMohtMSl93OZO8mbqawxK29sbbLfay/Gki17/UuAMcL5yCEkfcU8R\n" +
                    "2QvQZ2E54+QZK5intjoRyQn1Z78HBVXv7oZoGmV2xakS6Vps1K2dgUuZx41vI9J3\n" +
                    "yJXFu3WXV7saRdsP+QJBAODVja8RvM235JmmMbPT3BKGWAKxrMNOkT3G84dSmidm\n" +
                    "DkyVTUhE003vJkol2k/dKWJuqTHHVbaQYozrH4oHLfMCQQC4fz1enqf1Iqx1Jfgx\n" +
                    "njU4N5RgBqdhhpYOPTzL+zVMBtB7YG910JUueDhk0GUXw46sjPqf5HnkRh0O5DIK\n" +
                    "/gC3AkArN0EdloY48JDjK7u/+ggCE4qVMfuoKtDmE/i5WRpCWm6DL+uD6Z7ICyDL\n" +
                    "/cyhrzwGLIkfBVanWcdnmMYeLNUbAkBcq1yR6DMIx+/Dr9yoX4Tvxcr7KJxuOgGp\n" +
                    "CU0+T+GHXGzfa6LQlII6IxyAVsRQWWOSfAVuxn4LEMSLtEcGimqlAkBpwOO6UZDQ\n" +
                    "5WS9YNwyUum9lNB8O5e+3ESk8aqA/9X06LY3J+5S2j8aZcZYDBPT/SO343NzOobb\n" +
                    "Vs0uqTGIihZU";

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

    /**
     * 生成mapp端登录流程使用的token 构成格式 MD5(passportID|timestamp|4位随机数)
     *
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

}
