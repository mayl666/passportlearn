package com.sogou.upd.passport.service.account.generator;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.math.AES;
import com.sogou.upd.passport.service.account.dataobject.RefreshTokenCipherDO;
import com.sogou.upd.passport.service.account.dataobject.TokenCipherDO;
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

    // 公钥
    public static final String PUBLIC_KEY = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKg+nmc1UwpMGKHQP58jhJg/hLucm4oLBTBMyRBmCAKK\n" +
            "7rU/9UWJqy8li64i5bYtx7rE8+I4EdC00To5kz6D61UCAwEAAQ==";

    /**
     * access_token解密方法
     * 返回passportId
     * 解密完成后需要验证appKey是否正确，vaild_timestamp是否大于当前时刻
     * 如果失效了，返回null
     *
     * @param accessToken
     * @return
     * @throws Exception
     */
    public static TokenCipherDO decryptAccessToken(String accessToken) throws Exception {
        TokenCipherDO accessTokenCipherDO;
        try {
            byte[] tokenByte = Base64.decodeBase64(accessToken);
            String decryTokenStr = decryptByPublicKey(tokenByte, PUBLIC_KEY);
            accessTokenCipherDO = TokenCipherDO.parseEncryptString(decryTokenStr);
            return accessTokenCipherDO;
        } catch (Exception e) {
            logger.error("Access Token decryptURLSafeString fail, accessToken:{}", accessToken);
            throw e;
        }
    }

    /**
     * 根据refreshToken解密,并返回passportId
     */
    public static RefreshTokenCipherDO decryptRefreshToken(String refreshToken) throws Exception {
        try {
            String decryptStr = AES.decryptURLSafeString(refreshToken, TokenGenerator.SECRET_KEY);
            RefreshTokenCipherDO refreshTokenCipherDO = RefreshTokenCipherDO.parseEncryptString(decryptStr);
            return refreshTokenCipherDO;
        } catch (Exception e) {
            logger.error("Refresh Token decryptURLSafeString fail, refreshToken:{}", refreshToken);
            throw e;
        }
    }

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
            logger.error("Refresh Token decryptURLSafeString fail, refreshToken:{}", token);
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
