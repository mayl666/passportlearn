package com.sogou.upd.passport.common.math;

import com.sogou.upd.passport.service.account.generator.TokenGenerator;
import junit.framework.Assert;
import org.junit.Test;

import java.net.URLDecoder;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-3-26
 * Time: 上午2:10
 * To change this template use File | Settings | File Templates.
 */
public class RSATest {

    private String str = "shipengzhi1986@126.com|1003|1363018968121|dafasdfasdfasdfasdfasdfasdfa";

    /**
     * 公钥-私钥对生成
     *
     * @throws Exception
     */
    @Test
    public void testInitKey() throws Exception {

        Map<String, Object> key = RSA.initKey();
        // 公钥
        RSAPublicKey publickKey = (RSAPublicKey) key.get("RSAPublicKey");
        System.out.println("公钥：" + Coder.encryptBase64URLSafeString(publickKey.getEncoded()));

        // 私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) key.get("RSAPrivateKey");
        System.out.println("私钥：" + Coder.encryptBase64URLSafeString(privateKey.getEncoded()));
    }

    /**
     * 签名-校验
     *
     * @throws Exception
     */
    @Test
    public void testSig_Verify() throws Exception {
        String signStr = RSA.sign(str, TokenGenerator.PINYIN_PRIVATE_KEY);
        Assert.assertTrue(RSA.verify(str, TokenGenerator.PUBLIC_KEY, signStr));
    }

    /**
     * 私钥加密-公钥解密
     *
     * @throws Exception
     */
    @Test
    public void testEncryptByPrivateKey_DecryptByPublicKey() throws Exception {
        byte[] encbyte = RSA.encryptByPrivateKey(str.getBytes(), TokenGenerator.PINYIN_PRIVATE_KEY);
        String decStr = RSA.decryptByPublicKey(encbyte, TokenGenerator.PUBLIC_KEY);
        Assert.assertEquals(str, decStr);
    }

    /*
     * 测试浏览器客户端gettoken接口、getcookie接口返回的加密字符串的RSA解密
     */
    @Test
    public void testDecryptBrowerToken_Cookie() throws Exception {
        String cookie = "aUtCBO1UpnqFQKYQyTgCKnB8HDr2LIcxkOjvi9M50i8kfcjef6OLP2Eno/YRxA9Mp+SbiDMQL5DTowQE/OUgqj0hHJFLncudMBDbf+Fr/q33nAlEaHSzwGWNoKgXngHIWnab2CdsVQ/2ApCn3u3u2HqbkIat7RUEOq0062lFsFYuhKFPIvPUFrDHY04bvUTVuZc6gsDW2GXDI7y7NOWTyGFB2u+izVGUXy25JexFxLgI7ViipK/+GN6KGCSTcPuaxaOwZvKoZr+CkZ8LDhvvf0pbdNV3wUSCCn4tX0EM/pfr1aGKli49tjJBPL92+4WEzdLINF9B7L1DNGcwS4Y5v4TwtZRx9j3p6rcQ9+Zl0TjwUsT1daYkNDVz87UmiTmlNFU7IxXAPpfbPKd7eZW9+dM+12V/MIzzCqK5GX4XrrvJvUljUJ/+5MP2aq7Aim77jPXDEwTphiwiBWzWhi2TMXfu+UHf9lI0bhAOFYeAwzurCMpICNyBUOMomOegnPwqgk4LO/31D3IJjWrXqYyfbJuX+030Hs3OgrrQGeNuD9KkMgi/glofV0eBLpLLSigHXL1DY9jVZm05whV4+LrLz3qhRsvvv+282bp2Q1u5jJ6sj5irdET3q9Y6FPGwYvOqUnlveh2FTWA70+e4rqG03/M5YHAckMAHX51LEOHuaL93bZDMgSAUkrXinZQLRVZR8HPY0fUKo05A/IigiWTK9uxp7qkCTC5Em0IYCy58W5zhANJ2nrYFUxnOMiquzPPy8uB/B722ChwjFs1eQm5cxfTSAJjypJHnf299TAd4ki0xndrMHm6L/crQXVWrZ1Lup7wq/hio4DMsuvAMtf0HPkDB78NlKchgAtrjZbcG1nXJwv/WqilfiYXsqWHDbRfo/u6VMT1bNJ259xECfqYigF1xbKCBXZsB5ihSC8KGddbWUGo0vsyhOHyoEQ93c9cNMqcSmkUcx54zkcsDXkMzR/RpEzTB2tihjOpxeqRHd4UJXLPTmWNIvn1ekJcGhghx";
        byte[] cookieByte = Coder.decryptBASE64(cookie);
        RSA.init(128);
        String cookieDecryptData = RSA.decryptDesktopByPrivateKey(cookieByte, TokenGenerator.BROWER_PRIVATE_KEY);
        System.out.println(cookieDecryptData);

        String token="kWNc4a+CflRkVjCrpCnfZXHM/aZ5aGBbyYjJydrhvUTJUE61RJQrt1a8Ah71uChUYvJTRglXWbsPvUEhwZAzNISU0eB8jNcg/0m3D4CwCt2dNEbJdNFMU6dDhsZe/CsRed+gz0OROpdEuQLvJXiH3+5K0q0issT1XTQKjvrawPgdTjQUqfIpnIJ4yLusbzfcO6G2d44+2doLVhvK/kc75XmU4zaGgJx+0ecocxROGdpk/JPh7dYVhCsmrjeAwz21knBINV9NYrFMNcg10wILt/ljXjxFMtrgQtD/125y6hmO9Hh6XFbaeMHLgSjhAavxfTgVVXjjfRVRxl+Gk4OBdg==";
        byte[] tokenByte = Coder.decryptBASE64(token);
        RSA.init(128);
        String tokenDecryptData = RSA.decryptDesktopByPrivateKey(tokenByte, TokenGenerator.BROWER_PRIVATE_KEY);
        System.out.println(tokenDecryptData);
    }

    /*
     * 测试输入法客户端gettoken返回的加密字符串的RSA解密
     */
    @Test
    public void testDecryptePinyinToken() throws Exception {
        String data = "JFV1XsoGdu3i817L7JQgMn%2FTb06MJB%2BLPfu%2F6an3RUatPm6dCSRsi4Ar9VERLrluDRFh90Mn4%2FO6YSmMWXTZP1hpqR2cgN0opJliX3xGucciFHNSz%2B2h0I0bmVVN3yj8At6ueV%2BSc0JgZKViu4Hl5jh%2FTL0hiE%2FBxW1U7Qeak9k2ByKefAz29W7nlkwhHCoC%2FmvShcmX0gcyBZxRfEMzu%2Buymn%2FTZA2zGEohMToeS8xIXIK%2BzNma1IhjUw%2BNOP%2F1";
        data = URLDecoder.decode(data,"UTF-8");
        byte[]  dataByte = Coder.decryptBASE64(data);
        String decryptData = RSA.decryptByPrivateKey(dataByte, TokenGenerator.PINYIN_PRIVATE_KEY);
        System.out.println(decryptData);
    }


}
