package com.sogou.upd.passport.common.utils;

import com.sogou.upd.passport.common.exception.ConnectException;
import com.sogou.upd.passport.common.math.Coder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-1-8
 * Time: 上午11:31
 * To change this template use File | Settings | File Templates.
 */
public class QQSigUtil {
    /**
     * URL编码 (符合FRC1738规范)
     *
     * @param input 待编码的字符串
     * @return 编码后的字符串
     * @throws ConnectException 不支持指定编码时抛出异常。
     */
    public static String encodeUrl(String input) throws ConnectException {
        try {
            return URLEncoder.encode(input, CONTENT_CHARSET).replace("+", "%20").replace("*", "%2A");
        } catch (UnsupportedEncodingException e) {
            throw new ConnectException(ErrorUtil.ERR_CODE_CONNECT_MAKE_SIGNATURE_ERROR, e);
        }
    }

    /* 生成签名
     *
     * @param method HTTP请求方法 "get" / "post"
     * @param url_path CGI名字, eg: /v3/user/get_info
     * @param params URL请求参数
     * @param secret 密钥
     * @return 签名值
     * @throws OpensnsException 不支持指定编码以及不支持指定的加密方法时抛出异常。
     */
    public static String makeSig(String method, String url_path, HashMap<String, Object> params, String secret) throws ConnectException {
        String sig = null;
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);

            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(CONTENT_CHARSET), mac.getAlgorithm());

            mac.init(secretKey);

            String mk = makeSource(method, url_path, params);

            byte[] hash = mac.doFinal(mk.getBytes(CONTENT_CHARSET));

            // base64
            sig = new String(Coder.encryptBase64(hash));
        } catch (NoSuchAlgorithmException e) {
            throw new ConnectException(ErrorUtil.ERR_CODE_CONNECT_MAKE_SIGNATURE_ERROR, e);
        } catch (UnsupportedEncodingException e) {
            throw new ConnectException(ErrorUtil.ERR_CODE_CONNECT_MAKE_SIGNATURE_ERROR, e);
        } catch (InvalidKeyException e) {
            throw new ConnectException(ErrorUtil.ERR_CODE_CONNECT_MAKE_SIGNATURE_ERROR, e);
        }
        return sig;
    }

    /* 生成签名所需源串
     *
     * @param method HTTP请求方法 "get" / "post"
     * @param url_path CGI名字, eg: /v3/user/get_info
     * @param params URL请求参数
     * @return 签名所需源串
     */
    public static String makeSource(String method, String url_path, HashMap<String, Object> params) throws ConnectException {
        Object[] keys = params.keySet().toArray();

        Arrays.sort(keys);

        StringBuilder buffer = new StringBuilder(128);

        buffer.append(method.toUpperCase()).append("&").append(encodeUrl(url_path)).append("&");

        StringBuilder buffer2 = new StringBuilder();

        for (int i = 0; i < keys.length; i++) {
            buffer2.append(keys[i]).append("=").append((String) params.get(keys[i]));

            if (i != keys.length - 1) {
                buffer2.append("&");
            }
        }

        buffer.append(encodeUrl(buffer2.toString()));

        return buffer.toString();
    }

    // 编码方式
    private static final String CONTENT_CHARSET = "UTF-8";

    // HMAC算法
    private static final String HMAC_ALGORITHM = "HmacSHA1";
}

