package com.sogou.upd.passport.service.account.generator;

import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.model.account.AccountToken;

import com.sogou.upd.passport.service.account.dataobject.AccessTokenCipherDO;
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
            AccessTokenCipherDO accessTokenCipherDO = TokenDecrypt.decryptAccessToken(accessToken);
            String passportId = accessTokenCipherDO.getPassportId();
            Assert.assertEquals(passportId, PASSPORT_ID_PHONE);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }


}
