package com.sogou.upd.passport.service.account.generator;

import com.sogou.upd.passport.service.account.dataobject.TokenCipherDO;
import com.sogou.upd.passport.service.account.dataobject.RefreshTokenCipherDO;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-3-26
 * Time: 上午11:37
 * To change this template use File | Settings | File Templates.
 */
public class TokenGeneratorAndDecryptTest extends BaseGeneratorTest {

    private String accessToken;
    private String refreshToken;

    @Before
    public void init() {
        try {
            accessToken = TokenGenerator.generatorAccessToken(PASSPORT_ID_PHONE, CLIENT_ID, EXPIRES_IN, INSTANCE_ID);
            refreshToken = TokenGenerator.generatorRefreshToken(PASSPORT_ID_PHONE, CLIENT_ID, INSTANCE_ID);
            System.out.println("accessToken:" + accessToken);
            System.out.println("refreshToken:" + refreshToken);
            Assert.assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testParsePassportIdFromRefreshToken() {
        try {
            RefreshTokenCipherDO refreshTokenCipherDO = TokenDecrypt.decryptRefreshToken(this.refreshToken);
            String passportId = refreshTokenCipherDO.getPassportId();
            Assert.assertEquals(passportId, PASSPORT_ID_PHONE);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testParsePassportIdFromAccessToken() {
        try {
            TokenCipherDO accessTokenCipherDO = TokenDecrypt.decryptAccessToken(accessToken);
            String passportId = accessTokenCipherDO.getPassportId();
            Assert.assertEquals(passportId, PASSPORT_ID_PHONE);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }


    @Test
    public void testParseAccessToken() {
        String accessToken = "brFDqEVwJ9vRcDoKgdcr5bjYBfAGK9vs4DLAFiv3nND5LZXFPl3I7qARnCc3MF9Gr1ZOyElr3BdH1ileKDAJVWnA9VBDVUB4sEmVnGrq6IgzU1BxycyBU0qyckgueI6VL5UScz0al1d3mXkU6E9DOLKFi-LLhkwyqqS6KdKlO8Y";
        try {
            TokenCipherDO accessTokenCipherDO = TokenDecrypt.decryptAccessToken(accessToken);
            String passportId = accessTokenCipherDO.getPassportId();
            long vaildTime = accessTokenCipherDO.getVaildTime();
            if (vaildTime > System.currentTimeMillis()) {
                System.out.println("true");
            } else {
                System.out.println("false");
            }
            System.out.println("passportId:" + passportId + " vaildTime:" + accessTokenCipherDO.getVaildTime());
            Assert.assertEquals(passportId, "13800000000@sohu.com");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testGeneratorPcToken() {
        try {
            String pcToken = TokenGenerator.generatorPcToken(PASSPORT_ID_EMAIL, CLIENT_ID, EXPIRES_IN, "323906108", CLIENT_SECRET);
            System.out.println("pcToken:" + pcToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
