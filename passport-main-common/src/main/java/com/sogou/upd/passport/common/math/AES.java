package com.sogou.upd.passport.common.math;

import com.sogou.upd.passport.common.CommonConstant;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * The AES 对称加密算法
 * User: shipengzhi
 * Date: 13-3-30
 * Time: 下午6:43
 * To change this template use File | Settings | File Templates.
 */
public class AES {

    private static final String KEY_ALGORITHM = "AES";
    private static final String AES_PADDING_MODE = "AES/ECB/PKCS5Padding";

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
        byte[] encVal = c.doFinal(data.getBytes(CommonConstant.DEFAULT_CHARSET));
//        String encryptedValue = Coder.encryptBase64URLSafeString(encVal);
        String encryptedValue = Base62.encodeBase62(encVal).toString();
        return encryptedValue;
    }

    //用于SSO加密，指定padding模式AES/ECB/PKCS5Padding
    public static String encryptSSO(String data, String secKey) throws Exception {
        Key key = generateKey(secKey);
        Cipher c = Cipher.getInstance(AES_PADDING_MODE);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(data.getBytes(CommonConstant.DEFAULT_CHARSET));
//        String encryptedValue = Coder.encryptBase64URLSafeString(encVal);
        String encryptedValue = Base62.encodeBase62(encVal).toString();
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
        byte[] decordedValue = Base62.decodeBase62(encryptedData.toCharArray());//Base64.decodeBase64(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);

        String decryptedValue = new String(decValue,CommonConstant.DEFAULT_CHARSET);
        return decryptedValue;
    }


    public static String decryptSSO(String encryptedData, String secKey) throws Exception {
        Key key = generateKey(secKey);
        Cipher c = Cipher.getInstance(AES_PADDING_MODE);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = Base62.decodeBase62(encryptedData.toCharArray());//Base64.decodeBase64(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);

        String decryptedValue = new String(decValue,CommonConstant.DEFAULT_CHARSET);
        return decryptedValue;
    }

    public static byte[] decrypt(byte[] data, String key) throws Exception {
        Key k = new SecretKeySpec(key.getBytes(CommonConstant.DEFAULT_CHARSET), KEY_ALGORITHM);
        byte[] raw = k.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        return cipher.doFinal(data);
    }

    public static String decryptURLSafeStringBase64(String encryptedData, String secKey) throws Exception {
        Key key = generateKey(secKey);
        Cipher c = Cipher.getInstance(KEY_ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = Base64.decodeBase64(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);

        String decryptedValue = new String(decValue,CommonConstant.DEFAULT_CHARSET);
        return decryptedValue;
    }

    private static Key generateKey(String seckey) throws Exception {
        Key key = new SecretKeySpec(Coder.encryptMD5_Byte(seckey), KEY_ALGORITHM);
        return key;
    }

}
