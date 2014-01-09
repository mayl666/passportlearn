package com.sogou.upd.passport.common.utils;

import com.sogou.upd.passport.common.CommonConstant;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * 编码解码工具类
 * User: mayan
 * Date: 14-1-9
 * Time: 下午2:30
 * To change this template use File | Settings | File Templates.
 */
public class EncodeUtils {
    private static final String ALGORITHM = "AES";
    public static void main(String[] args) throws Exception {
        String input="9fada5f7768b0aed1ca3de1941642bfdddb972a064ba9714ca4caf3442b5aa17";
        byte[] buf=Hex.decodeHex(input.toCharArray());

        String secretKey="afE0WZf345@werdm";

        String decryptedValue = new String(decrypt(buf,secretKey), CommonConstant.DEFAULT_CONTENT_CHARSET);

        System.out.println(decryptedValue);
    }
    public static byte[] encrypt(byte[] data, String key) throws Exception {
             Key k = toKey(key.getBytes("UTF-8"));
             byte[] raw = k.getEncoded();
             SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
             Cipher cipher = Cipher.getInstance(ALGORITHM);
             cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
             return cipher.doFinal(data);
         }
    public static byte[] decrypt(byte[] data, String key) throws Exception {
        Key k = toKey(key.getBytes("UTF-8"));
        byte[] raw = k.getEncoded();
       SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        return cipher.doFinal(data);
         }

    private static Key toKey(byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);
          return secretKey;
        }

}
