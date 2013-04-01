package com.sogou.upd.passport.service.account.generator;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;

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
public class AccessTokenDecrypt {

    // 公钥
    public static final String PUBLIC_KEY = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKg+nmc1UwpMGKHQP58jhJg/hLucm4oLBTBMyRBmCAKK\n" +
            "7rU/9UWJqy8li64i5bYtx7rE8+I4EdC00To5kz6D61UCAwEAAQ==";

    /**
     * 提供给T3使用的access_token解密方法
     * 返回passportId
     * 解密完成后需要验证appKey是否正确，vaild_timestamp是否大于当前时刻
     *
     * @param accessToken
     * @return
     * @throws Exception
     */
    public static String decryptAccessToken(String accessToken) throws Exception {
        byte[] tokenByte = Base64.decodeBase64(accessToken);
        String decryTokenStr = decryptByPublicKey(tokenByte, PUBLIC_KEY);
        String[] tokenArray = decryTokenStr.split("\\|");
        String passportId = tokenArray[0];
        long vaildTimestamp = Long.valueOf(tokenArray[2]);
        long currentTimestamp = System.currentTimeMillis();
        if(vaildTimestamp < currentTimestamp){
            return null;
        }else{
            return passportId;
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
