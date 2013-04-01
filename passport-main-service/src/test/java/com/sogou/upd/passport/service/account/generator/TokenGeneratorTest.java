package com.sogou.upd.passport.service.account.generator;

import com.sogou.upd.passport.model.account.AccountAuth;
import junit.framework.Assert;
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
        long userId = 100342;
        String passportID = "13621009174@sohu.com";
        int clientId = 1001;
        String instance_id = "dafadsfasdfa";

        long start = System.currentTimeMillis();
        int expiresIn = 3600 * 24;
        String accessToken = null;
        String refreshToken = null;
        try {
            accessToken = TokenGenerator.generatorAccessToken(passportID, clientId, expiresIn, instance_id);
            refreshToken = TokenGenerator.generatorRefreshToken(passportID, clientId, instance_id);
            System.out.println("accessToken:" + accessToken);
            System.out.println("refreshToken:" + refreshToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

        AccountAuth accountAuth = new AccountAuth();
        accountAuth.setUserId(userId);
        accountAuth.setClientId(clientId);
        accountAuth.setAccessToken(accessToken);
        accountAuth.setAccessValidTime(TokenGenerator.generatorVaildTime(expiresIn));
        accountAuth.setRefreshToken(refreshToken);
        long end = System.currentTimeMillis();
        System.out.println("use time:" + (end - start) + "ms");
    }

    @Test
    public void testParsePassportIdFromRefreshToken() throws Exception {
        String refreshToken = "a5YbARxr-rd0jV48xRdPm3PZG7sZJG6W120YC2nCuwsmSwf51mLnNnr1TSEKWA68eBp0u_S5VL4pFsdrhaQrSA";
        String passportId = TokenGenerator.parsePassportIdFromRefreshToken(refreshToken);
        System.out.println("passportId : " + passportId);
        Assert.assertEquals(passportId, "13621009174@sohu.com");
    }

    @Test
    public void testParsePassportIdFromAccessToken() throws Exception {
        String accessToken = "KrlquCryiVymgx9GlDbf3kXQmUzQY5C0TXNwoo48tBNcIQilybT1r+qdgFqillGHUFpefbUs+fkId2eVH8HjSIQs4NwnTP1kFTjLKxruXQ0TwaQOY03MB7X/V4mOKmMuo4RXn6A116jKO2AgLRl0KXXOmgOmvxKC90YFeZmT1tQ=";
        String passportId = AccessTokenDecrypt.decryptAccessToken(accessToken);
        if(passportId == null){
            System.out.println("accessToken is invalid");
        }
        Assert.assertEquals(passportId, "13621009174@sohu.com");
    }
}
