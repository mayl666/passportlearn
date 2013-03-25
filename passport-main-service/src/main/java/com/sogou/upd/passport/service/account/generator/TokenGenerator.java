package com.sogou.upd.passport.service.account.generator;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.math.RSA;
import com.sogou.upd.passport.common.parameter.CommonParameters;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.apache.commons.lang3.RandomStringUtils;

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

    // HMAC_SHA1密钥
    public static final String HMAC_SHA_KEY = "q2SyvfJ8dTwjK3t0x1pnL78Mrq9FkN5tF00p2wEgQg0HmCFx4GXGONOf5FQykc45Evt8odc9OXjGLNX9KnPNWw==";

    // 公钥
    public static final String PUBLIC_KEY = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKg+nmc1UwpMGKHQP58jhJg/hLucm4oLBTBMyRBmCAKK\n" +
            "7rU/9UWJqy8li64i5bYtx7rE8+I4EdC00To5kz6D61UCAwEAAQ==";
    // 私钥
    public static final String PRIVATE_KEY = "MIIBUgIBADANBgkqhkiG9w0BAQEFAASCATwwggE4AgEAAkEAqD6eZzVTCkwYodA/nyOEmD+Eu5yb\n" +
            "igsFMEzJEGYIAorutT/1RYmrLyWLriLlti3HusTz4jgR0LTROjmTPoPrVQIDAQABAkAaMgW/1BGl\n" +
            "3MtJBn+ha4pNmjY0b+HX0HdyWcJEh15f5rkqhcrAxzaHo5vHnnW+mYrIIGdeqF8QTbB1lMKYuIxR\n" +
            "AiEA2TBeNoI/EU+4I876iOHn5kopV0+OCtkLdxsu12nUNsMCIQDGTzgd7uVDGKzX4oNy4VY0FrZR\n" +
            "bqTdtJtiRKTFU8EkBwIgBSByKOE8MeFq3FWHbnG+sp3vieMT3EexUJdwrJ8P5lcCIBYHVsR8dRsu\n" +
            "8oRItTFdtqWyoC4LjGTUWy5fUa5Zz2qhAh9rQb9VP0rnQPP3Hm9z9SFccXUZaPiC9a8+r5g5WUen";

    @Inject
    private AppConfigService appConfigService;

    /**
     * 生成access_token
     * 构成格式 passportID|appkey|vaild_timestamp(过期时间点，单位毫秒)|6位随机数
     *
     * @param passportID
     * @param appkey
     * @return
     */
    public String generatorAccessToken(String passportID, int appkey) throws Exception {

        // 过期时间点
        int expiresIn = appConfigService.getAccessTokenExpiresIn(appkey);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.SECOND, expiresIn);
        long vaild_time = c.getTimeInMillis();

        // 6位随机数
        String random = RandomStringUtils.randomAlphanumeric(6);

        StringBuffer data = new StringBuffer();
        data.append(passportID).append(CommonParameters.SEPARATOR_1);
        data.append(appkey).append(CommonParameters.SEPARATOR_1);
        data.append(vaild_time).append(CommonParameters.SEPARATOR_1);
        data.append(random);

        byte[] encbyte = RSA.encryptByPrivateKey(data.toString().getBytes(), PRIVATE_KEY);
        String encBase64Str = Coder.encryptBASE64(encbyte);

        return encBase64Str;
    }

    /**
     * 生成refresh_token
     * 构成格式 passportID|appkey|timestamp(当前时间戳，单位毫秒)
     *
     * @param passportID
     * @param appkey
     * @return
     * @throws Exception
     */
    public String generatorRefreshToken(String passportID, int appkey) throws Exception {
        long timestamp = System.currentTimeMillis();
        StringBuffer data = new StringBuffer();
        data.append(passportID).append(appkey).append(timestamp);

        byte[] encryByte = Coder.encryptHMAC(data.toString().getBytes(), TokenGenerator.HMAC_SHA_KEY);

        return Coder.toHexString(encryByte);
    }
}
