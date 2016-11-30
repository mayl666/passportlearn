package com.sogou.upd.passport.common.utils;

import com.sogou.upd.passport.common.math.Coder;

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
        return Coder.encryptMD5(baseStr);
    }

}
