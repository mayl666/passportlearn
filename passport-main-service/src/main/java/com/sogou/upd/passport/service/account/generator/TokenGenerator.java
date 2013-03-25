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

    @Inject
    private AppConfigService appConfigService;

    /**
     * 生成access_token
     * 构成格式 passportID|appkey|vaild_timestamp(过期时间点，单位毫秒)|6位随机数
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

        byte[] encbyte = RSA.encryptByPrivateKey(data.toString().getBytes(), RSA.PRIVATE_KEY);
        String encBase64Str = Coder.encryptBASE64(encbyte);

        return encBase64Str;
    }

    /**
     * 生成refresh_token
     * 构成格式 passportID|appkey|timestamp(当前时间戳，单位毫秒)
     * @param passportID
     * @param appkey
     * @return
     * @throws Exception
     */
    public String generatorRefreshToken(String passportID, int appkey) throws Exception {
        return null;
    }
}
