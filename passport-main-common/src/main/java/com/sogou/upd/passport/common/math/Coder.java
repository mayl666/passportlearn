package com.sogou.upd.passport.common.math;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

public class Coder {
	public static final String KEY_SHA = "SHA";
	public static final String KEY_MD5 = "MD5";

	/** 
	 * MAC算法可选以下多种算法 
	 *  
	 * <pre> 
	 * HmacMD5  
	 * HmacSHA1  
	 * HmacSHA256  
	 * HmacSHA384  
	 * HmacSHA512 
	 * </pre> 
	 */
	public static final String KEY_MAC = "HmacSHA1";

	/** 
	 * BASE64解密 
	 *  
	 * @param key 
	 * @return 
	 * @throws Exception 
	 */
	public static byte[] decryptBASE64(String key) {
		return Base64.decodeBase64(key);
	}

	/** 
	 * BASE64加密 
	 *  
	 * @param key 
	 * @return 
	 * @throws Exception 
	 */
	public static String encryptBASE64(byte[] key) throws Exception {
		return Base64.encodeBase64URLSafeString(key);
	}

	/** 
	 * MD5加密 
	 *  
	 * @param data 
	 * @return 
	 * @throws Exception 
	 */
	public static byte[] encryptMD5(byte[] data) throws Exception {

		MessageDigest md5 = MessageDigest.getInstance(KEY_MD5);
		md5.update(data);

		return md5.digest();

	}

	/** 
	 * SHA加密 
	 *  
	 * @param data 
	 * @return 
	 * @throws Exception 
	 */
	public static byte[] encryptSHA(byte[] data) throws Exception {

		MessageDigest sha = MessageDigest.getInstance(KEY_SHA);
		sha.update(data);

		return sha.digest();

	}

	/** 
	 * 初始化HMAC密钥 
	 *  
	 * @return 
	 * @throws Exception 
	 */
	public static String initMacKey() throws Exception {
		KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_MAC);

		SecretKey secretKey = keyGenerator.generateKey();
		return encryptBASE64(secretKey.getEncoded());
	}

	/** 
	 * HMAC加密 
	 *  
	 * @param data 
	 * @param key 
	 * @return 
	 * @throws Exception 
	 */
	public static byte[] encryptHMAC(byte[] data, String key) throws Exception {

		SecretKey secretKey = new SecretKeySpec(decryptBASE64(key), KEY_MAC);
		Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		mac.init(secretKey);

		return mac.doFinal(data);

	}

    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte by : b) {
            sb.append(HEXCHAR[(by & 0xf0) >>> 4]);
            sb.append(HEXCHAR[by & 0x0f]);
        }
        return sb.toString();
    }

    public static byte[] toBytes(String s) {
        byte[] bytes;
        bytes = new byte[s.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    private static char[] HEXCHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
}
