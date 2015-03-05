package com.sogou.upd.passport.service.account.generator;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.math.AES;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-3-29
 * Time: 下午3:23
 * To change this template use File | Settings | File Templates.
 */
public class TokenDecrypt {

    private static Logger logger = LoggerFactory.getLogger(TokenDecrypt.class);

    /**
     * 根据refreshToken解密,并返回passportId
     * 解密失败则返回null
     */
    public static String decryptPcToken(String token, String clientSecret) throws Exception {
        try {
            String passportId = null;
            String tokenContent = token.substring(CommonConstant.SG_TOKEN_START.length(),token.length());
            String decryptStr = AES.decryptURLSafeString(tokenContent, clientSecret);
            if (!Strings.isNullOrEmpty(decryptStr)) {
                String[] strArray = decryptStr.split("\\"+CommonConstant.SEPARATOR_1);
                passportId = strArray[0];
            }
            return passportId;
        } catch (Exception e) {
            logger.warn("Refresh Token decryptURLSafeString Base62 fail, refreshToken:{}", token);
            return null;
        }
    }

    /**
     * 根据refreshToken解密,并返回passportId
     * 解密失败则返回null
     */
    public static String decryptOldPcToken(String token, String clientSecret) throws Exception {
        try {
            String passportId = null;
            String tokenContent = token.substring(CommonConstant.SG_TOKEN_OLD_START.length(),token.length());
            String decryptStr = AES.decryptURLSafeStringBase64(tokenContent, clientSecret);
            if (!Strings.isNullOrEmpty(decryptStr)) {
                String[] strArray = decryptStr.split("\\"+CommonConstant.SEPARATOR_1);
                passportId = strArray[0];
            }
            return passportId;
        } catch (Exception e) {
            logger.error("Refresh Token decryptURLSafeString Base64 fail, refreshToken:{}", token);
            return null;
        }
    }

    /**
     * 解密<br>
     * 用公钥解密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    private static String decryptByPublicKey(byte[] data, String key) throws Exception {
        // 对密钥解密
        byte[] keyBytes = Base64.decodeBase64(key);

        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key publicKey = keyFactory.generatePublic(x509KeySpec);

        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);

        byte[] decryptData = segmentCoder(data, cipher, 64);

        return new String(decryptData);
    }

    /**
     * 分段进行加密或解密
     *
     * @param data
     * @param cipher
     * @param maxBlock
     * @return
     * @throws Exception
     */
    private static byte[] segmentCoder(byte[] data, Cipher cipher, int maxBlock) throws Exception {
        byte[] encodedByteArray = new byte[]{};

        int inputLen = data.length;
        int offSet = 0;
        byte[] cache;
        int i = 0;
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > maxBlock) {
                cache = cipher.doFinal(data, offSet, maxBlock);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            encodedByteArray = ArrayUtils.addAll(encodedByteArray, cache);
            i++;
            offSet = i * maxBlock;
        }

        return encodedByteArray;
    }
}
