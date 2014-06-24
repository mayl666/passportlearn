package com.sogou.upd.passport.common.math;

import com.sogou.upd.passport.service.account.generator.TokenGenerator;
import junit.framework.Assert;
import org.junit.Ignore;
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
@Ignore
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
}
