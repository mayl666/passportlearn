package com.sogou.upd.passport.common.math;

import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * The RSA 非对称加密算法.
 *
 * @author shipengzhi
 */
public class RSA extends Coder {

    public static final String KEY_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    //RSA最大加密明文大小
    private static int MAX_ENCRYPT_BLOCK = 53;
//    private static final int MAX_ENCRYPT_BLOCK = 117;

    //RSA最大解密密文大小
    private static int MAX_DECRYPT_BLOCK = 64;
//    private static final int MAX_DECRYPT_BLOCK = 128;

    //默认为64，但例如浏览器用C的RSA加密需要128
    public static void init(int maxDecryptBlock){
         MAX_DECRYPT_BLOCK = maxDecryptBlock;
    }

    /**
     * 加密<br>
     * 用公钥加密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String key) throws Exception {
        // 对公钥解密
        byte[] keyBytes = decryptBASE64(key);

        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);

        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return segmentCoder(data, cipher, MAX_ENCRYPT_BLOCK);
    }

    /**
     * 加密<br>
     * 用私钥加密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String key) throws Exception {
        // 对密钥解密
        byte[] keyBytes = decryptBASE64(key);

        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        return segmentCoder(data, cipher, MAX_ENCRYPT_BLOCK);
    }

    /**
     * 解密<br>
     * 用私钥解密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String decryptByPrivateKey(byte[] data, String key) throws Exception {
        // 对密钥解密
        byte[] keyBytes = decryptBASE64(key);

        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] decryptData = segmentCoder(data, cipher, MAX_DECRYPT_BLOCK);

        return new String(decryptData);
    }

    /**
     * 桌面端产品解密<br>
     * 用私钥解密
     * 由于桌面端产品使用C进行RSA加密，密文的顺序和java加密相反，所以单独一个方法
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String decryptDesktopByPrivateKey(byte[] data, String key) throws Exception {
        // 对密钥解密
        byte[] keyBytes = decryptBASE64(key);

        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] decryptData = segmentOppositeCoder(data, cipher, MAX_DECRYPT_BLOCK);

        return new String(decryptData);
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
    public static String decryptByPublicKey(byte[] data, String key) throws Exception {
        // 对密钥解密
        byte[] keyBytes = decryptBASE64(key);

        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);

        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);

        byte[] decryptData = segmentCoder(data, cipher, MAX_DECRYPT_BLOCK);

        return new String(decryptData);
    }

    /**
     * 用私钥对信息生成数字签名
     */
    public static String sign(String data, String privateKey) throws Exception {
        // 解密由base64编码的私钥
        byte[] keyBytes = decryptBASE64(privateKey);

        // 构造PKCS8EncodedKeySpec对象
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);

        // KEY_ALGORITHM 指定的加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

        // 取私钥匙对象
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

        // 用私钥对信息生成数字签名
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(priKey);
        signature.update(data.getBytes());

        return encryptBase64URLSafeString(signature.sign());
    }

    /**
     * 校验数字签名
     */
    public static boolean verify(String data, String publicKey, String sign) throws Exception {

        // 解密由base64编码的公钥
        byte[] keyBytes = decryptBASE64(publicKey);

        // 构造X509EncodedKeySpec对象
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

        // KEY_ALGORITHM 指定的加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

        // 取公钥匙对象
        PublicKey pubKey = keyFactory.generatePublic(keySpec);

        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(pubKey);
        signature.update(data.getBytes());

        // 验证签名是否正常
        return signature.verify(decryptBASE64(sign));
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
    public static byte[] segmentCoder(byte[] data, Cipher cipher, int maxBlock) throws Exception {
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

    /**
     * 分段进行加密或解密
     * 由于桌面端产品使用C进行RSA加密，密文的顺序和java加密相反，所以用相反顺序分段
     *
     * @param data
     * @param cipher
     * @param maxBlock
     * @return
     * @throws Exception
     */
    public static byte[] segmentOppositeCoder(byte[] data, Cipher cipher, int maxBlock) throws Exception {
        byte[] encodedByteArray = new byte[]{};

        int inputLen = data.length;
        int offSet = 0;
        byte[] cache;
        int i = 0;
        Stack<byte[]> stack = new Stack();
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > maxBlock) {
                cache = cipher.doFinal(data, offSet, maxBlock);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
//            encodedByteArray = ArrayUtils.addAll(encodedByteArray, cache);
            stack.push(cache);
            i++;
            offSet = i * maxBlock;
        }
        while (!stack.isEmpty()) {
            byte[] value = stack.pop();
            encodedByteArray = ArrayUtils.addAll(encodedByteArray, value);
        }
        return encodedByteArray;
    }

    /**
     * 初始化密钥
     *
     * @return
     * @throws Exception
     */
    public static Map<String, Object> initKey() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(512);

        KeyPair keyPair = keyPairGen.generateKeyPair();

        // 公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        // 私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        Map<String, Object> keyMap = new HashMap<String, Object>(2);

        keyMap.put("RSAPublicKey", publicKey);
        keyMap.put("RSAPrivateKey", privateKey);
        return keyMap;
    }

}
