package com.sogou.upd.passport.oauth2.common.utils;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.openresource.parameters.RenrenOAuth;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 人人 openAPI 工具类
 *
 * @author shipengzhi(shipengzhi@sogou-inc.com)
 */
public class RenrenSignatureUtils {

    /**
     * 构造签名Map，版本默认为1.0，返回格式json
     */
    public static Map<String, String> baseSignMap(String method, String accessToken) {
        return baseSignMap(method, RenrenOAuth.V1, RenrenOAuth.JSON, accessToken);
    }

    /**
     * 构造签名Map，版本默认为1.0
     */
    public static Map<String, String> baseSignMap(String method, String format, String accessToken) {
        return baseSignMap(method, RenrenOAuth.V1, format, accessToken);
    }

    /**
     * 构造基础签名map
     */
    public static Map<String, String> baseSignMap(String method, String version, String format, String accessToken) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(RenrenOAuth.METHOD, method);
        map.put(RenrenOAuth.VERSION, version);
        map.put(RenrenOAuth.FORMAT, format);
        map.put(OAuth.OAUTH_ACCESS_TOKEN, accessToken);

        return map;

    }

    /**
     * 签名算法
     *
     * @param paramMap
     * @param secret
     * @return
     */
    public static String getSignature(Map<String, String> paramMap, String secret) {
        List<String> paramList = new ArrayList<String>(paramMap.size());
        //1、参数格式化
        for (Map.Entry<String, String> param : paramMap.entrySet()) {
            paramList.add(param.getKey() + "=" + param.getValue());
        }
        //2、排序并拼接成一个字符串
        Collections.sort(paramList);
        StringBuffer buffer = new StringBuffer();
        for (String param : paramList) {
            buffer.append(param);
        }
        //3、追加script key
        buffer.append(secret);
        //4、将拼好的字符串转成MD5值
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            StringBuffer result = new StringBuffer();
            try {
                for (byte b : md.digest(buffer.toString().getBytes(CommonConstant.DEFAULT_CONTENT_CHARSET))) {
                    result.append(Integer.toHexString((b & 0xf0) >>> 4));
                    result.append(Integer.toHexString(b & 0x0f));
                }
            } catch (UnsupportedEncodingException e) {
                for (byte b : md.digest(buffer.toString().getBytes())) {
                    result.append(Integer.toHexString((b & 0xf0) >>> 4));
                    result.append(Integer.toHexString(b & 0x0f));
                }
            }
            return result.toString();
        } catch (java.security.NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
