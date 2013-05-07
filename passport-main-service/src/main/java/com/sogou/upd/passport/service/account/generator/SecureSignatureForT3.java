package com.sogou.upd.passport.service.account.generator;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.service.account.dataobject.SecureSignDO;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-5-7
 * Time: 下午3:24
 * To change this template use File | Settings | File Templates.
 */
public class SecureSignatureForT3 {

    public static final String KEY_MAC = "HmacSHA1";

    public static String sign(SecureSignDO secureSignatureDO, String secret) throws Exception {
        StringBuilder baseBuilderString = new StringBuilder("");
        baseBuilderString.append(secureSignatureDO.getTs()).append("\n");
        baseBuilderString.append(secureSignatureDO.getNonce()).append("\n");
        baseBuilderString.append(secureSignatureDO.getUri()).append("\n");
        baseBuilderString.append(secureSignatureDO.getServerName()).append("\n");
        String baseString = baseBuilderString.toString();
        try {
            String signature = encryptBASE64(encryptHMAC(baseString, secret));
            return signature;
        } catch (Exception e) {
            throw e;
        }
    }

    private static String encryptBASE64(byte[] key) throws Exception {
        return Base64.encodeBase64URLSafeString(key);
    }

    private static byte[] encryptHMAC(String data, String key) throws Exception {

        SecretKey secretKey = new SecretKeySpec(key.getBytes(), KEY_MAC);
        Mac mac = Mac.getInstance(secretKey.getAlgorithm());
        mac.init(secretKey);

        return mac.doFinal(data.getBytes());

    }
}
