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
    public static String encryptURLSafeString(String data, String secKey) throws Exception {
        Key key = generateKey(secKey);
        Cipher c = Cipher.getInstance(KEY_ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(data.getBytes("utf-8"));
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
    public static String decryptURLSafeString(String encryptedData, String secKey) throws Exception {
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

}
