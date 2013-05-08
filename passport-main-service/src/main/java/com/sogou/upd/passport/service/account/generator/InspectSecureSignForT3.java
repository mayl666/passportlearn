package com.sogou.upd.passport.service.account.generator;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.service.account.dataobject.SecureSignDO;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

/**
 * 校验http访问令牌传输安全性，提供给应用使用
 * User: shipengzhi
 * Date: 13-5-7
 * Time: 上午11:14
 * To change this template use File | Settings | File Templates.
 */
public class InspectSecureSignForT3 {

    private static final String CLIENT_SECRET = "40db9c5a312a145e8ee8181f4de8957334c5800a"; //客户端密钥

    public static final String KEY_MAC = "HmacSHA1";

    private static Logger logger = LoggerFactory.getLogger(InspectSecureSignForT3.class);

    /**
     * 校验client_mac合法性：
     * 1.ts有效期，在5分钟内有效；
     * 2.client_mac/mac签名正确；
     *
     * @param request
     * @return 校验通过则返回access_token，校验不通过返回null
     */
    public static boolean verifySecureSignature(HttpServletRequest request) throws Exception {
        String serverName = request.getServerName();
//        String method = request.getMethod();         // GET or POST
        String uri = request.getRequestURI();
//        if (method.equals("POST")) {
//            String queryString = getRequests(request);
//            if (!Strings.isNullOrEmpty(queryString)) {
//                uri = uri + "?" + queryString;
//            }
//        }

        Map<String, String> headerMap = parseMacHeader(request);
        if (MapUtils.isEmpty(headerMap)) {
            return false;
        }
        long timeStamp = Long.parseLong(headerMap.get("ts"));
        SecureSignDO secureSignatureDO = new SecureSignDO();
        secureSignatureDO.setTs(timeStamp);
        secureSignatureDO.setNonce(headerMap.get("nonce"));
        secureSignatureDO.setUri(uri);
        secureSignatureDO.setServerName(serverName);

        String headerSignature = headerMap.get("client_mac");
        if (Strings.isNullOrEmpty(headerSignature)) {
            return false;
        }
        boolean verify = verify(secureSignatureDO, CLIENT_SECRET, headerSignature);

        return verify;
    }

    /**
     * 生成签名
     *
     * @param secureSignatureDO
     * @param secret
     * @return
     * @throws Exception
     */
    public static String sign(SecureSignDO secureSignatureDO, String secret) throws Exception {
        StringBuilder baseBuilderString = new StringBuilder("");
        baseBuilderString.append(secureSignatureDO.getTs()).append("\n");
        baseBuilderString.append(secureSignatureDO.getNonce()).append("\n");
        baseBuilderString.append(secureSignatureDO.getUri()).append("\n");
        baseBuilderString.append(secureSignatureDO.getServerName()).append("\n");
        String baseString = baseBuilderString.toString();
        try {
            String signature = Base64.encodeBase64URLSafeString(encryptHMAC(baseString, secret));
            return signature;
        } catch (Exception e) {
            logger.error("Mac Signature generate fail", e);
            throw e;
        }
    }

    private static boolean verify(SecureSignDO secureSignatureDO, String secret, String signature) throws Exception {
        try {
            String actual = sign(secureSignatureDO, secret);
            return actual.equals(signature);
        } catch (Exception e) {
            logger.error("Mac Signature generate fail", e);
            throw e;
        }
    }

    private static Map<String, String> parseMacHeader(HttpServletRequest request) {
        String macHeader = request.getHeader("MAC");
        Map<String, String> headerMap = Maps.newHashMap();
        String[] headerArray = macHeader.split(",");
        for (String header : headerArray) {
            String[] result = header.split("=");
            headerMap.put(result[0], result[1]);
        }
        return headerMap;
    }

    private static String getRequests(HttpServletRequest request) {
        StringBuilder params = new StringBuilder();
        Map parameterMap = request.getParameterMap();
        Set<String> keys = parameterMap.keySet();
        for (String key : keys) {
            params.append(key).append("=").append(request.getParameter(key)).append("&");
        }
        String queryString = "";
        if (params.length() > 0) {
            queryString = params.deleteCharAt(params.length() - 1).toString();
        }
        return queryString;
    }

    /**
     * HMAC加密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    private static byte[] encryptHMAC(String data, String key) throws Exception {

        SecretKey secretKey = new SecretKeySpec(key.getBytes(), KEY_MAC);
        Mac mac = Mac.getInstance(secretKey.getAlgorithm());
        mac.init(secretKey);

        return mac.doFinal(data.getBytes());

    }

}
