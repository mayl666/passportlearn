package com.sogou.upd.passport.common.math;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;

/**
 * The AES 对称加密算法
 * User: shipengzhi
 * Date: 13-3-30
 * Time: 下午6:43
 * To change this template use File | Settings | File Templates.
 */
public class AES {

    private static final String KEY_ALGORITHM = "AES";

    /**
     * 加密
     *
     * @param secKey
     * @param data
     * @return
     * @throws Exception
     */
    public static String encrypt(String data, String secKey) throws Exception {
        Key key = generateKey(secKey);
        Cipher c = Cipher.getInstance(KEY_ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(data.getBytes());
        String encryptedValue = Coder.encryptBase64URLSafeString(encVal);
        return encryptedValue;
    }

    /**
     * 解密
     *
     * @param secKey
     * @param encryptedData
     * @return
     * @throws Exception
     */
    public static String decrypt(String encryptedData, String secKey) throws Exception {
        Key key = generateKey(secKey);
        Cipher c = Cipher.getInstance(KEY_ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = Base64.decodeBase64(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);

        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private static Key generateKey(String seckey) throws Exception {
        Key key = new SecretKeySpec(Coder.encryptMD5_Byte(seckey), KEY_ALGORITHM);
        return key;
    }

    /**
     * 加密
     *
     * @param content 需要加密的内容
     * @param secKey  加密密码
     * @return
     */
    public static byte[] encryptStr(String content, String secKey) throws Exception{
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(secKey.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return result; // 加密
    }
}
