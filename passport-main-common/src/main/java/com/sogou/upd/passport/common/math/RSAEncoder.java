package com.sogou.upd.passport.common.math;

import com.sogou.upd.passport.common.CommonConstant;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * Cookie的sgrdig签名密文
 * User: shipengzhi
 * Date: 14-1-9
 * Time: 下午3:31
 * To change this template use File | Settings | File Templates.
 */
public class RSAEncoder {

    final private byte[] _padding = new byte[]{
            48, 48, 48, 12, 6, 8, 42, -122, 72, -122, -9, 13, 2, 5, 5, 0, 4, 32
    };

    public static final String KEY_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM = "RSA/ECB/PKCS1Padding";

    private Cipher cipher;
    private String privateKey;

    public RSAEncoder(String privateKey) {
        this.privateKey = privateKey;
    }

    public void init() throws Exception {
        byte[] keyBytes = Coder.decryptBASE64(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateKey = factory.generatePrivate(keySpec);
        cipher = Cipher.getInstance(SIGNATURE_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
    }


    /**
     * 计算摘要，然后RSA私钥加密，最后转到BASE64格式
     *
     * @param sginf
     * @return
     * @throws Exception
     */
    public String sgrdig(String sginf) throws Exception {

        byte[] temp = encrypt(DigestUtils.md5Hex(sginf).getBytes(CommonConstant.DEFAULT_CHARSET));

        return Base64.encodeBase64URLSafeString(temp);
    }

    private byte[] encrypt(byte[] data) throws Exception {

        byte[] temp = new byte[50];

        System.arraycopy(_padding, 0, temp, 0, _padding.length);
        System.arraycopy(data, 0, temp, 18, data.length);

        cipher.update(temp);
        return cipher.doFinal();
    }

    public static void main(String[] args) throws Exception {

//            RSAEncoder encoder = new RSAEncoder("/opt/conf/passport/pkcs8_der.key");
        RSAEncoder encoder = new RSAEncoder("D:/zzz.key");
        encoder.init();
        String sginf = "2|1344871124|0|bG9naW5pZDowOnx1c2VyaWQ6MjQ6cGFzc3BvcnRtb25pdG9yQHNvaHUuY29tfHNlcnZpY2V1c2U6MjA6MTAwMDAwMDAwMDAwMDAwMDAwMDB8Y3J0OjEwOjIwMDctMDItMTN8ZW10OjE6MHxhcHBpZDo0Ojk5OTh8dHJ1c3Q6MToxfHBhcnRuZXJpZDoxOjB8cmVsYXRpb246MDp8dXVpZDoxNjoxNjgwOWFkZDhkNDQ0OGZzfHVpZDo5OnU4MDcxNTUxNHx1bmlxbmFtZTowOnw=";
        String sgrdig = encoder.sgrdig(sginf);
        System.out.println(sgrdig);

    }

}