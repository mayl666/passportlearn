package com.sogou.upd.passport.service.account.generator;

import com.sogou.upd.passport.common.math.AES;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.math.RSA;
import com.sogou.upd.passport.common.parameter.CommonParameters;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.Date;

/**
 * Token生成器，生成access_token和refresh_token
 * User: shipengzhi
 * Date: 13-3-25
 * Time: 下午10:22
 * To change this template use File | Settings | File Templates.
 */
public class TokenGenerator {

    // 非对称加密算法-公钥
    public static final String PUBLIC_KEY = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKg+nmc1UwpMGKHQP58jhJg/hLucm4oLBTBMyRBmCAKK\n" +
            "7rU/9UWJqy8li64i5bYtx7rE8+I4EdC00To5kz6D61UCAwEAAQ==";
    // 非对称加密算法-私钥
    public static final String PRIVATE_KEY = "MIIBUgIBADANBgkqhkiG9w0BAQEFAASCATwwggE4AgEAAkEAqD6eZzVTCkwYodA/nyOEmD+Eu5yb\n" +
            "igsFMEzJEGYIAorutT/1RYmrLyWLriLlti3HusTz4jgR0LTROjmTPoPrVQIDAQABAkAaMgW/1BGl\n" +
            "3MtJBn+ha4pNmjY0b+HX0HdyWcJEh15f5rkqhcrAxzaHo5vHnnW+mYrIIGdeqF8QTbB1lMKYuIxR\n" +
            "AiEA2TBeNoI/EU+4I876iOHn5kopV0+OCtkLdxsu12nUNsMCIQDGTzgd7uVDGKzX4oNy4VY0FrZR\n" +
            "bqTdtJtiRKTFU8EkBwIgBSByKOE8MeFq3FWHbnG+sp3vieMT3EexUJdwrJ8P5lcCIBYHVsR8dRsu\n" +
            "8oRItTFdtqWyoC4LjGTUWy5fUa5Zz2qhAh9rQb9VP0rnQPP3Hm9z9SFccXUZaPiC9a8+r5g5WUen";

    // 对称加密算法-密钥
    public static final String SECRET_KEY = "187e4310dd2dd9966dddcc10d1732241c96ba131";

    /**
     * 生成access_token
     * 构成格式 passportID|clientId|instanceId|vaild_timestamp(过期时间点，单位毫秒)|4位随机数
     * TODO
     *
     * @param passportID
     * @param clientId
     * @return
     */
    public static String generatorAccessToken(String passportID, int clientId, int expiresIn, String instanceId) throws Exception {

        // 过期时间点
        long vaildTime = generatorVaildTime(expiresIn);

        // 4位随机数
        String random = RandomStringUtils.randomAlphanumeric(4);

        StringBuilder data = new StringBuilder();
        data.append(passportID).append(CommonParameters.SEPARATOR_1);
        data.append(clientId).append(CommonParameters.SEPARATOR_1);
        data.append(instanceId).append(CommonParameters.SEPARATOR_1);
        data.append(vaildTime).append(CommonParameters.SEPARATOR_1);
        data.append(random);

        byte[] encByte = RSA.encryptByPrivateKey(data.toString().getBytes(), PRIVATE_KEY);
        String encBase64Str = Coder.encryptBASE64(encByte);

        return encBase64Str;
    }

    /**
     * 生成refresh_token
     * 构成格式 passportID|clientId|instanceId|timestamp(当前时间戳，单位毫秒)
     *
     * @param passportID
     * @param clientId
     * @return
     * @throws Exception
     */
    public static String generatorRefreshToken(String passportID, int clientId, String instanceId) throws Exception {
        long timestamp = System.currentTimeMillis();

        StringBuilder data = new StringBuilder();
        data.append(passportID).append(CommonParameters.SEPARATOR_1);
        data.append(clientId).append(CommonParameters.SEPARATOR_1);
        data.append(instanceId).append(CommonParameters.SEPARATOR_1);
        data.append(timestamp);

        String encrypt = AES.encrypt(data.toString(), SECRET_KEY);
        return encrypt;
    }

    /**
     * 根据refreshToken解密,并返回passportId
     *
     * @param refreshToken
     * @return
     * @throws Exception
     */
    public static String parsePassportIdFromRefreshToken(String refreshToken) throws Exception {
        String decrypt = AES.decrypt(refreshToken, SECRET_KEY);
        String[] array = decrypt.split(CommonParameters.SEPARATOR_1);
        return array[0];
    }

    /**
     * 生成过期时间点
     *
     * @param expiresIn
     * @return
     */
    public static long generatorVaildTime(int expiresIn) {
        DateTime dateTime = new DateTime();
        long vaildTime = dateTime.plusSeconds(expiresIn).getMillis();
        return vaildTime;
    }

}
