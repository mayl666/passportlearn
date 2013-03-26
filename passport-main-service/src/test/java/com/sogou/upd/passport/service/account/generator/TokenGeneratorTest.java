package com.sogou.upd.passport.service.account.generator;

import com.sogou.upd.passport.model.account.AccountAuth;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-3-26
 * Time: 上午11:37
 * To change this template use File | Settings | File Templates.
 */
public class TokenGeneratorTest {
    @Test
    public void testGeneratorAccountAuth() throws Exception {
        long userid = 100342;
        String passportID = "098833A6A59C54E24DC58635D32141D6@qq.sohu.com";
        int appkey = 1003;

        long start = System.currentTimeMillis();
        TokenGenerator generator = new TokenGenerator();
        int expiresIn = 3600 * 24;
        String accessToken = null;
        String refreshToken = null;
        try {
            accessToken = generator.generatorAccessToken(passportID, appkey, expiresIn);
            refreshToken = generator.generatorRefreshToken(passportID, appkey);
        } catch (Exception e) {
            // TODO record error log
        }

        AccountAuth accountAuth = new AccountAuth();
        accountAuth.setUserId(userid);
        accountAuth.setAppkey(appkey);
        accountAuth.setAccessToken(accessToken);
        accountAuth.setAccessValidTime(generator.generatorVaildTime(expiresIn));
        accountAuth.setRefreshToken(refreshToken);
        long end = System.currentTimeMillis();
        System.out.println("use time:" + (end - start) + "ms");
    }
}
