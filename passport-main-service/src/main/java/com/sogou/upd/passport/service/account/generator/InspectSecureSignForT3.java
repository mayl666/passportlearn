package com.sogou.upd.passport.service.account.generator;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sogou.upd.passport.service.account.dataobject.SecureSignDO;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 校验http访问令牌传输安全性，提供给应用使用
 * User: shipengzhi
 * Date: 13-5-7
 * Time: 上午11:14
 * To change this template use File | Settings | File Templates.
 */
public class InspectSecureSignForT3 {

    private static final String CLIENT_SECRET = "40db9c5a312a145e8ee8181f4de8957334c5800a"; //客户端密钥
    private static final String KEY_MAC = "HmacSHA1";
    private static final String AUTHORIZATION = "Authorization";
    private static final String AUTH_SCHEME = "MAC";
    private static final Pattern OAUTH_HEADER = Pattern.compile("\\s*(\\w*)\\s+(.*)");
    private static final Pattern NVP = Pattern.compile("(\\S*)\\s*\\=\\s*\"([^\"]*)\"");
    private static final String DEFAULT_CONTENT_CHARSET = "UTF-8";

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
        String uri = request.getRequestURI();
        String queryString = getRequests(request);
        if (!Strings.isNullOrEmpty(queryString)) {
            uri = uri + "?" + queryString;
        }

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
        baseBuilderString.append(secureSignatureDO.getTs());
        baseBuilderString.append(secureSignatureDO.getNonce());
        baseBuilderString.append(secureSignatureDO.getUri());
        baseBuilderString.append(secureSignatureDO.getServerName());
        String baseString = baseBuilderString.toString();
        try {
            String signature = Base64.encodeBase64String(encryptHMAC(baseString, secret));
            return signature;
        } catch (Exception e) {
            logger.error("Mac Signature generate fail", e);
            throw e;
        }
    }

    public static boolean verify(SecureSignDO secureSignatureDO, String secret, String signature) throws Exception {
        try {
            String actual = sign(secureSignatureDO, secret);
            return actual.equals(signature);
        } catch (Exception e) {
            logger.error("Mac Signature generate fail", e);
            throw e;
        }
    }

    private static String getRequests(HttpServletRequest request) {
        StringBuilder params = new StringBuilder();
        Map parameterMap = request.getParameterMap();
        Set<String> keys = parameterMap.keySet();
        TreeSet<String> sortKeys = Sets.newTreeSet(keys);
        for (String key : sortKeys) {
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

        SecretKey secretKey = new SecretKeySpec(key.getBytes(DEFAULT_CONTENT_CHARSET), KEY_MAC);
        Mac mac = Mac.getInstance(secretKey.getAlgorithm());
        mac.init(secretKey);

        return mac.doFinal(data.getBytes(DEFAULT_CONTENT_CHARSET));

    }

    public static Map<String, String> parseMacHeader(HttpServletRequest request) throws UnsupportedEncodingException {
        String header = request.getHeader(AUTHORIZATION);
        Map<String, String> headerValues = Maps.newHashMap();
        if (!Strings.isNullOrEmpty(header)) {
            Matcher m = OAUTH_HEADER.matcher(header);
            if (m.matches()) {
                if (AUTH_SCHEME.equalsIgnoreCase(m.group(1))) {
                    for (String nvp : m.group(2).split("\\s*,\\s*")) {
                        int index = nvp.indexOf("=");
                        String name = nvp.substring(0, index);
                        String value = nvp.substring(index + 1);
                        headerValues.put(name, value);
                    }
                }
            }
        }
        return headerValues;
    }

}
