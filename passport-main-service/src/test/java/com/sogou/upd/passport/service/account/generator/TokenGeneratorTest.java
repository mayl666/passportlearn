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
    //MLZaNnm0kJ4sDB4nCJDB+v23jmNODxRhpdVAkrhCajm6uZigFZCL7PeD2LzfIDLIZm5MKMilDaJQdISsLG7UB3AF51WTyFRn8iGgYe2KTAcaudMDbroULBWbaroEReIP7e4wdc/8Oy3gG2/z8cIn/amNFlbY64+baJkLO/i00Dg=
    //SnI7xWL7qXKNecIK7yYYdXAsAYcrxiaaxMAWo/hG5BmbOYVSvh+V79LgxWAWr4D8bbXTu2WpayW20wgas0SHeQ==


    @Test
    public void testGeneratorAccountAuth() {
        long userid = 100342;
        String passportID = "13621009174@sohu.com";
        int appkey = 1003;

        long start = System.currentTimeMillis();
        int expiresIn = 3600 * 24;
        String accessToken = null;
        String refreshToken = null;
        try {
            accessToken = TokenGenerator.generatorAccessToken(passportID, appkey, expiresIn);
            refreshToken = TokenGenerator.generatorRefreshToken(passportID, appkey);
            System.out.println("accessToken:" + accessToken);
            System.out.println("refreshToken:" + refreshToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

        AccountAuth accountAuth = new AccountAuth();
        accountAuth.setUserId(userid);
        accountAuth.setAppkey(appkey);
        accountAuth.setAccessToken(accessToken);
        accountAuth.setAccessValidTime(TokenGenerator.generatorVaildTime(expiresIn));
        accountAuth.setRefreshToken(refreshToken);
        long end = System.currentTimeMillis();
        System.out.println("use time:" + (end - start) + "ms");
    }
}
