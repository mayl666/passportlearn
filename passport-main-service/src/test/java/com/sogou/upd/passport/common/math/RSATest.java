package com.sogou.upd.passport.common.math;

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

    /**
     * 公钥-私钥对生成
     * @throws Exception
     */
    @Test
    public void testInitKey() throws Exception {

        Map<String, Object> key = RSA.initKey();
        // 公钥
        RSAPublicKey publickKey = (RSAPublicKey) key.get("RSAPublicKey");
        System.out.println("公钥：" + Coder.encryptBASE64(publickKey.getEncoded()));

        // 私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) key.get("RSAPrivateKey");
        System.out.println("私钥：" + Coder.encryptBASE64(privateKey.getEncoded()));
    }

    /**
     * 签名-校验
     * @throws Exception
     */
    @Test
    public void testSig_Verify() throws Exception {
        String signStr = RSA.sign(str, RSA.PRIVATE_KEY);
        Assert.assertTrue(RSA.verify(str, RSA.PUBLIC_KEY, signStr));
    }

    /**
     * 私钥加密-公钥解密
     * @throws Exception
     */
    @Test
    public void testEncryptByPrivateKey_DecryptByPublicKey() throws Exception {
        byte[] encbyte = RSA.encryptByPrivateKey(str.getBytes(), RSA.PRIVATE_KEY);
        String decStr = RSA.decryptByPublicKey(encbyte, RSA.PUBLIC_KEY);
        Assert.assertEquals(str,decStr);
    }
}
