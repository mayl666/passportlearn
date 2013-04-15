package com.sogou.upd.passport.common.utils;

import com.google.common.collect.Maps;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.Set;
import java.util.TreeMap;

public class AESencrp {

	private static final String ALGO = "AES";
	private static final long TEN_MINUTE = (1000 * 60) * 100;

	public static final String CLIENT_SIGNATURE = "client_signature";
	public static final String SERVER_SIGNATURE = "signature";

	/**
	 * 加密
	 * data构成格式appid|openid|timestamp|version
	 * @param seckey
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String seckey, String data) throws Exception {
		Key key = generateKey(seckey);
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.ENCRYPT_MODE, key);
		byte[] encVal = c.doFinal(data.getBytes());
		String encryptedValue = new String(Base64.encodeBase64URLSafe(encVal));
		return encryptedValue;
	}

	/**
	 * 解密
	 * data构成格式appid|openid|timestamp|version
	 * @param seckey
	 * @param encryptedData
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String seckey, String encryptedData) throws Exception {
		Key key = generateKey(seckey);
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.DECRYPT_MODE, key);
		byte[] decordedValue = Base64.decodeBase64(encryptedData);
		byte[] decValue = c.doFinal(decordedValue);

		String decryptedValue = new String(decValue);
		return decryptedValue;
	}

	/**
	 * 验证access_token是否正确
	 * 没有对版本验证，不支持修改密码验证
	 * @param appid
	 * @param seckey
	 * @param accessToken
	 * @return
	 * @throws Exception
	 */
	public static boolean isAccessTokenValid(String appid, String seckey, String accessToken)
			throws Exception {

		if (StringUtils.isEmpty(appid) || StringUtils.isEmpty(seckey)
				|| StringUtils.isEmpty(accessToken)) return false;

		try {
			String token = decrypt(seckey, accessToken);
			String[] parts = token.split("\\|");
			if (parts != null && parts.length == 4) {
				long vt = Long.parseLong(parts[2]);
				long ct = new Date().getTime();
				if (vt > ct && appid.equals(parts[0])) return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public static boolean checkSignature(String appid, String seckey, String signature) {

		if (StringUtils.isEmpty(appid) || StringUtils.isEmpty(seckey)
				|| StringUtils.isEmpty(signature)) return false;

		try {
			String token = decrypt(seckey, signature);
			String[] parts = token.split("\\|");
			if (parts != null && parts.length == 2) {
				long vt = Long.parseLong(parts[1]);
				long ct = new Date().getTime();
				if (appid.equals(parts[0]) && (ct - TEN_MINUTE) < vt) return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	private static Key generateKey(String seckey) throws Exception {

		Key key = new SecretKeySpec(DigestUtils.md5(seckey.getBytes()), ALGO);
		return key;
	}


	public static String generateSignature(TreeMap<String, String> params, String secret) {

		StringBuilder paramsBase = new StringBuilder("");
		Set<String> sortKeys = params.keySet();
		for (String key : sortKeys) {
			if (!(CLIENT_SIGNATURE.equals(key) || SERVER_SIGNATURE.equals(key))) {
				paramsBase.append(key).append("=").append(params.get(key));
			}
		}
		String enParams;
		try {

			enParams = StringUtil.urlEncodeUTF8(paramsBase.toString());
			enParams = enParams.replace("+", "%20");
			enParams = enParams.replace("*", "%2A");
		} catch (Exception e) {
			return null;
		}
		String baseStr = enParams + "&" + secret;
		String baseMd5 = DigestUtils.md5Hex(baseStr.getBytes());
		return baseMd5;

	}

	public static void main(String[] args) throws Exception {

//		Date d = new Date();
//		Calendar c = Calendar.getInstance();
//		c.setTime(d);
//		c.add(Calendar.MINUTE, 10);
//
//		String token = "1003|89|" + c.getTimeInMillis() + "|1";
//
		String seckey = "6b6c15f66cafaafbd3eca597f4df1fb1";
//		String clientKey = "f978fdbeef00abe2d464a9acf3d58046";
//		String sigBase = "1003|" + c.getTimeInMillis();
//
//
//		//		System.out.println(new String(DigestUtils.md5(seckey.getBytes())));
//
//		String etoken = encrypt(seckey, sigBase);
//		System.out.println(etoken);
//
//		String dtoken = decrypt(seckey, etoken);
//		System.out.println(dtoken);
//
//		String etokenc = encrypt(clientKey, sigBase);
//		System.out.println(etokenc);
//
//		String dtokenc = decrypt(clientKey, etokenc);
//		System.out.println(dtokenc);

		//		String dtokenc = decrypt(seckey, "kmdIVWY3ol_NkfHpoakRtwCv9nbCtb2M_kW1ivRo3VJyZ22PLxhbuVnvNfT3BEc4fIDuT6lE5drDF5Dus33YEA");
		//		System.out.println(dtokenc);



		//		System.out.println(isAccessTokenValid("1001", seckey, etoken));


		//		System.out.println(new String(new Base64().decode("aGVsbG93b3JsZA")));

		//access_token=oQ_ezYkK0gGgAibIf7-xX4R_-kdUdtEkHf5XM0DQW0DLG_27eF5bQmm8CbCbEXBmhUNtMs0fuN_SIW1QVj2HMw&appid=1003&provider=qq
		TreeMap<String, String> params = Maps.newTreeMap();

				params.put("appid", "1003");
				params.put("openid", "111");
				params.put("query", "sina_2,renren_2,pengtou_3");
				params.put("inapps", "1003");
				params.put("fields", "origin_name,name,head_url");
				params.put("sort", "1");

//		params.put("access_token",
//				"oQ_ezYkK0gGgAibIf7-xX4R_-kdUdtEkHf5XM0DQW0DLG_27eF5bQmm8CbCbEXBmhUNtMs0fuN_SIW1QVj2HMw");
//		//		params.put("provider", "qq");
//		params.put("appid", "1003");

		String sig = generateSignature(params, seckey);

		System.out.println("sig:" + sig);
		System.out.println("map:" + params.toString());
	}

}
