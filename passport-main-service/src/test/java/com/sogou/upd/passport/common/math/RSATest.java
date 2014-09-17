package com.sogou.upd.passport.common.math;

import com.sogou.upd.passport.service.account.generator.TokenGenerator;
import junit.framework.Assert;
import org.junit.Test;

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

    /*
     * linux生成的私钥需要转换成pkcs8
     * openssl pkcs8 -topk8 -inform PEM -in key.txt -outform PEM -nocrypt
     */
    private String BROWER_PRIVATE_KEY="MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKIJPa0bHY4VCXQH\n" +
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
        String signStr = RSA.sign(str, TokenGenerator.PRIVATE_KEY);
        Assert.assertTrue(RSA.verify(str, TokenGenerator.PUBLIC_KEY, signStr));
    }

    /**
     * 私钥加密-公钥解密
     *
     * @throws Exception
     */
    @Test
    public void testEncryptByPrivateKey_DecryptByPublicKey() throws Exception {
        byte[] encbyte = RSA.encryptByPrivateKey(str.getBytes(), TokenGenerator.PRIVATE_KEY);
        String decStr = RSA.decryptByPublicKey(encbyte, TokenGenerator.PUBLIC_KEY);
        Assert.assertEquals(str, decStr);
    }

    /*
     * 测试浏览器客户端getcookie接口返回的加密字符串的RSA解密
     */
    @Test
    public void testDecryptBrowerCookie() throws Exception {
        String data = "aUtCBO1UpnqFQKYQyTgCKnB8HDr2LIcxkOjvi9M50i8kfcjef6OLP2Eno/YRxA9Mp+SbiDMQL5DTowQE/OUgqj0hHJFLncudMBDbf+Fr/q33nAlEaHSzwGWNoKgXngHIWnab2CdsVQ/2ApCn3u3u2HqbkIat7RUEOq0062lFsFYuhKFPIvPUFrDHY04bvUTVuZc6gsDW2GXDI7y7NOWTyGFB2u+izVGUXy25JexFxLgI7ViipK/+GN6KGCSTcPuaxaOwZvKoZr+CkZ8LDhvvf0pbdNV3wUSCCn4tX0EM/pfr1aGKli49tjJBPL92+4WEzdLINF9B7L1DNGcwS4Y5v4TwtZRx9j3p6rcQ9+Zl0TjwUsT1daYkNDVz87UmiTmlNFU7IxXAPpfbPKd7eZW9+dM+12V/MIzzCqK5GX4XrrvJvUljUJ/+5MP2aq7Aim77jPXDEwTphiwiBWzWhi2TMXfu+UHf9lI0bhAOFYeAwzurCMpICNyBUOMomOegnPwqgk4LO/31D3IJjWrXqYyfbJuX+030Hs3OgrrQGeNuD9KkMgi/glofV0eBLpLLSigHXL1DY9jVZm05whV4+LrLz3qhRsvvv+282bp2Q1u5jJ6sj5irdET3q9Y6FPGwYvOqUnlveh2FTWA70+e4rqG03/M5YHAckMAHX51LEOHuaL93bZDMgSAUkrXinZQLRVZR8HPY0fUKo05A/IigiWTK9uxp7qkCTC5Em0IYCy58W5zhANJ2nrYFUxnOMiquzPPy8uB/B722ChwjFs1eQm5cxfTSAJjypJHnf299TAd4ki0xndrMHm6L/crQXVWrZ1Lup7wq/hio4DMsuvAMtf0HPkDB78NlKchgAtrjZbcG1nXJwv/WqilfiYXsqWHDbRfo/u6VMT1bNJ259xECfqYigF1xbKCBXZsB5ihSC8KGddbWUGo0vsyhOHyoEQ93c9cNMqcSmkUcx54zkcsDXkMzR/RpEzTB2tihjOpxeqRHd4UJXLPTmWNIvn1ekJcGhghx";
        byte[] dataByte = Coder.decryptBASE64(data);
        String decryptData = RSA.decryptByPrivateKey(dataByte, BROWER_PRIVATE_KEY);
        System.out.println(decryptData);
    }


}
