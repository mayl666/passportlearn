package com.sogou.upd.passport.common.utils;

import com.sogou.upd.passport.common.math.Coder;

import javax.sound.midi.Soundbank;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;
import java.util.TreeMap;

/**
 * 签名算法工具类
 * User: mayan
 * Date: 14-3-13
 * Time: 下午2:51
 */
public class SignatureUtils {
    public static String generateSignature(TreeMap params, String secret) throws Exception {
        StringBuilder paramsBase = new StringBuilder("");
        Set<String> sortKeys = params.keySet();
        for (String key : sortKeys) {
            paramsBase.append(key).append("=").append(params.get(key)).append("&");
        }
        String enParams=paramsBase.toString();
        String baseStr = enParams + secret;
        System.out.println("code will encode string:+"+enParams);
        return Coder.encryptMD5(baseStr);
    }

    /**
     * 将参数URLEncode为UTF-8
     *
     * @param params
     * @return
     * @throws java.io.UnsupportedEncodingException
     */
    public static String urlEncodeUTF8(String params) throws UnsupportedEncodingException {
        String en = URLEncoder.encode(params, "UTF8");
        en = en.replace("+", "%20");
        en = en.replace("*", "%2A");
        return en;
    }
}
