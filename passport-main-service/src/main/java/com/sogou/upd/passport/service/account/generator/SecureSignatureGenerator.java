package com.sogou.upd.passport.service.account.generator;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.service.account.dataobject.SecureSignDO;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi                                                   x
 * Date: 13-5-6
 * Time: 上午10:46
 * To change this template use File | Settings | File Templates.
 */
public class SecureSignatureGenerator {

    private static Logger logger = LoggerFactory.getLogger(SecureSignatureGenerator.class);
    private static final String CLIENT_SECRET = "40db9c5a312a145e8ee8181f4de8957334c5800a"; //客户端密钥

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

    public static String sign(SecureSignDO secureSignatureDO, String secret) throws Exception {
        StringBuilder baseBuilderString = new StringBuilder("");
        baseBuilderString.append(secureSignatureDO.getTs());
        baseBuilderString.append(secureSignatureDO.getNonce());
        baseBuilderString.append(secureSignatureDO.getUri());
        baseBuilderString.append(secureSignatureDO.getServerName());
        String baseString = baseBuilderString.toString();
        try {
            String signature = Coder.encryptBase64URLSafeString(Coder.encryptHMAC(baseString, secret.getBytes(CommonConstant.DEFAULT_CHARSET)));
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

    private static Map<String, String> parseMacHeader(HttpServletRequest request) {
        String macHeader = request.getHeader("MAC");
        Map<String, String> headerMap = Maps.newHashMap();
        if (!Strings.isNullOrEmpty(macHeader)) {
            String[] headerArray = macHeader.split(",");
            for (String header : headerArray) {
                String[] result = header.split("=");
                if (result.length != 2) {
                    continue;
                }
                headerMap.put(result[0], result[1]);
            }
        }
        return headerMap;
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

}
