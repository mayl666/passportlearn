package com.sogou.upd.passport.oauth2.common.utils;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sogou.upd.passport.common.HttpConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OAuthUtils {

    private static Logger log = LoggerFactory.getLogger(OAuthUtils.class);

    private static final String PARAMETER_SEPARATOR = "&";
    private static final String NAME_VALUE_SEPARATOR = "=";

    public static final String AUTH_SCHEME = OAuth.OAUTH_HEADER_NAME;

    private static final Pattern OAUTH_HEADER = Pattern.compile("\\s*(\\w*)\\s+(.*)");
    private static final Pattern NVP = Pattern.compile("(\\S*)\\s*\\=\\s*\"([^\"]*)\"");

    public static final String MULTIPART = "multipart/";

    private static final String DEFAULT_CONTENT_CHARSET = "UTF-8";

    /**
     * 格式化 into <code>application/x-www-form-urlencoded</code> String
     *
     * @param parameters 需编码的参数
     * @param charset    编码格式
     * @return Translated string
     * @throws java.io.UnsupportedEncodingException
     *
     */
    public static String format(final Collection<? extends Map.Entry<String, Object>> parameters,
                                final String charset) {
        final StringBuilder result = new StringBuilder();
        for (final Map.Entry<String, Object> parameter : parameters) {
            String value = parameter.getValue() == null ? null : String.valueOf(parameter
                    .getValue());
            if (!StringUtils.isEmpty(parameter.getKey()) && !StringUtils.isEmpty(value)) {
                final String encodedName = Coder.encode(parameter.getKey(), charset);
                final String encodedValue = value != null ? Coder.encode(value, charset) : "";
                if (result.length() > 0) {
                    result.append(PARAMETER_SEPARATOR);
                }
                result.append(encodedName);
                result.append(NAME_VALUE_SEPARATOR);
                result.append(encodedValue);
            }
        }
        return result.toString();
    }

    public static String toString(final InputStream is, final String defaultCharset)
            throws IOException {
        if (is == null) {
            throw new IllegalArgumentException("InputStream may not be null");
        }

        String charset = defaultCharset;
        if (charset == null) {
            charset = DEFAULT_CONTENT_CHARSET;
        }
        Reader reader = new InputStreamReader(is, charset);
        StringBuilder sb = new StringBuilder();
        int l;
        try {
            char[] tmp = new char[4096];
            while ((l = reader.read(tmp)) != -1) {
                sb.append(tmp, 0, l);
            }
        } finally {
            reader.close();
        }
        return sb.toString();
    }

    public static Set<String> decodeScopes(String s) {
        Set<String> scopes = Sets.newHashSet();
        if (!Strings.isNullOrEmpty(s)) {
            String[] scopeArray = s.split(",");
            for (String scope : scopeArray) {
                scopes.add(scope);
            }

        }
        return scopes;

    }

    public static String encodeScopes(Set<String> s) {
        StringBuffer scopes = new StringBuffer();
        for (String scope : s) {
            scopes.append(scope).append(",");
        }
        return scopes.toString().trim();

    }

    public static boolean hasContentType(String requestContentType, String requiredContentType) {
        if (Strings.isNullOrEmpty(requiredContentType) || Strings.isNullOrEmpty(requestContentType)) {
            return false;
        }
        String[] contentTypes = requestContentType.split(";");
        for (String type : contentTypes) {
            if (requiredContentType.equals(type)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isMultipart(HttpServletRequest request) {

        if (!"post".equals(request.getMethod().toLowerCase())) {
            return false;
        }
        String contentType = request.getContentType();
        if (contentType == null) {
            return false;
        }
        if (contentType.toLowerCase().startsWith(MULTIPART)) {
            return true;
        }
        return false;
    }

    /**
     * QQ OAuth根据refreshToken刷新accessToken时，
     * 返回的格式为"xx=xx&xx=xx&xx=xx"字符串时
     * 的解析方法
     *
     * @param body
     * @return
     * @throws OAuthProblemException
     */
    public static Map<String, Object> parseQQIrregularStringObject(String body) throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        try {
            String[] params = StringUtils.split(body, "\\&");
            if (params.length > 0) {
                for (String str : params) {
                    parameters.put(StringUtils.split(str, "\\=")[0], (Object) StringUtils.split(str, "\\=")[1]);
                }
            }
        } catch (Exception e) {
            throw new Exception("[parseQQIrregularStringObject]Parse string to map error,", e);
        }
        return parameters;

    }

    /**
     * QQ OAuth授权根据code换取access_token时，
     * 返回的格式为(callback：{json})
     * 特殊的解析json方法
     *
     * @param body
     * @return Map
     * @throws OAuthProblemException
     */
    public static Map<String, Object> parseQQIrregularJSONObject(String body) throws OAuthProblemException {

        Map<String, Object> parameters;
        int fromIndex1 = body.indexOf("{") - 1;
        int fromIndex2 = body.lastIndexOf("}") + 1;
        if (isJsonBodyBlank(fromIndex1, fromIndex2)) {
            String json = body.substring(fromIndex1 + 1, fromIndex2);
            try {
                parameters = JacksonJsonMapperUtil.getMapper().readValue(json, Map.class);
            } catch (Exception e) {
                throw OAuthProblemException.error(ErrorUtil.UNSUPPORTED_RESPONSE_TYPE,
                        "Invalid response! Response body is not " + HttpConstant.ContentType.JSON + " encoded");
            }
        } else {
            throw OAuthProblemException.error(ErrorUtil.UNSUPPORTED_RESPONSE_TYPE, "Invalid response! Response body is not "
                    + HttpConstant.ContentType.HTML_TEXT + " encoded");
        }

        return parameters;
    }

    private static boolean isJsonBodyBlank(int index, int lastIndex) {
        return index != -2 && lastIndex != 0;
    }

	/*====================  Exception Handle Method========================*/

    public static OAuthProblemException handleBadContentTypeException(String expectedContentType) {
        StringBuilder errorMsg = new StringBuilder("Bad request content type. Expecting: ").append(
                expectedContentType);
        return handleOAuthProblemException(errorMsg.toString());
    }

    /**
     * 创建一个包含缺失oauth参数的ProblemException
     *
     * @param missingParams 缺失的参数
     * @return OAuthProblemException 这个类中包含缺失oauth参数的信息
     */
    public static OAuthProblemException handleMissingParameters(Set<String> missingParams) {
        StringBuffer sb = new StringBuffer("Missing parameters: ");
        if (!CollectionUtils.isEmpty(missingParams)) {
            for (String miss : missingParams) {
                sb.append(miss).append(" ");
            }
        }
        return handleOAuthProblemException(sb.toString().trim());
    }

    /**
     * 创建一个不允许传入的oauth参数的ProblemException
     *
     * @param notAllowedParams
     * @return
     */
    public static OAuthProblemException handleNotAllowedParametersOAuthException(
            List<String> notAllowedParams) {
        StringBuffer sb = new StringBuffer("Not allowed parameters: ");
        if (!CollectionUtils.isEmpty(notAllowedParams)) {
            for (String notAllowed : notAllowedParams) {
                sb.append(notAllowed).append(" ");
            }
        }
        return handleOAuthProblemException(sb.toString().trim());
    }

    public static OAuthProblemException handleOAuthProblemException(String message) {
        return OAuthProblemException.error(ErrorUtil.ERR_CODE_COM_REQURIE).description(message);
    }

    /*=========================== Utils ==============================*/
    public static boolean hasEmptyValues(String[] array) {
        if (array == null || array.length == 0) {
            return true;
        }
        for (String s : array) {
            if (StringUtils.isEmpty(s)) {
                return true;
            }
        }
        return false;
    }

    public static <T> T instantiateClass(Class<T> clazz) throws OAuthProblemException {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            log.error("Instantiate Class Exception! Class:" + clazz.getName(), e);
            throw new OAuthProblemException(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }

    public static Object instantiateClassWithParameters(Class clazz, Class[] paramsTypes,
                                                        Object[] paramValues) throws OAuthProblemException {
        try {
            if (paramsTypes != null && paramValues != null) {
                if (!(paramsTypes.length == paramValues.length)) {
                    throw new IllegalArgumentException(
                            "Number of types and values must be equal");
                }

                if (paramsTypes.length == 0 && paramValues.length == 0) {
                    return clazz
                            .newInstance();
                }
                Constructor clazzConstructor = clazz.getConstructor(paramsTypes);
                return clazzConstructor.newInstance(paramValues);
            }
            return clazz.newInstance();
        } catch (NoSuchMethodException e) {
            log.error("Instantiate Class With Parameters NoSuchMethodException! Class:" + clazz.getName(), e);
            throw new OAuthProblemException(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        } catch (InstantiationException e) {
            log.error("Instantiate Class With Parameters InstantiationException! Class:" + clazz.getName(), e);
            throw new OAuthProblemException(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        } catch (IllegalAccessException e) {
            log.error("Instantiate Class With Parameters IllegalAccessException! Class:" + clazz.getName(), e);
            throw new OAuthProblemException(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        } catch (InvocationTargetException e) {
            log.error("Instantiate Class With Parameters InvocationTargetException! Class:" + clazz.getName(), e);
            throw new OAuthProblemException(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }


    /*====================  Authorize Header Handler  ========================*/
    public static String getAuthHeaderField(String authHeader) {

        if (authHeader != null) {
            Matcher m = OAUTH_HEADER.matcher(authHeader);
            if (m.matches()) {
                if (AUTH_SCHEME.equalsIgnoreCase(m.group(1))) {
                    return m.group(2);
                }
            }
        }
        return null;
    }

    public static Map<String, String> decodeOAuthHeader(String header) {
        Map<String, String> headerValues = Maps.newHashMap();
        if (header != null) {
            Matcher m = OAUTH_HEADER.matcher(header);
            if (m.matches()) {
                if (AUTH_SCHEME.equalsIgnoreCase(m.group(1))) {
                    for (String nvp : m.group(2).split("\\s*,\\s*")) {
                        m = NVP.matcher(nvp);
                        if (m.matches()) {
                            String name = decodePercent(m.group(1));
                            String value = decodePercent(m.group(2));
                            headerValues.put(name, value);
                        }
                    }
                }
            }
        }
        return headerValues;
    }

    /**
     * Construct a WWW-Authenticate header
     */
    public static String encodeOAuthHeader(Map<String, Object> entries) {
        StringBuffer sb = new StringBuffer();
        sb.append(OAuth.OAUTH_HEADER_NAME).append(" ");
        for (Map.Entry<String, Object> entry : entries.entrySet()) {
            String value = entry.getValue() == null ? null : String.valueOf(entry.getValue());
            if (!Strings.isNullOrEmpty(entry.getKey()) && !Strings.isNullOrEmpty(value)) {
                sb.append(entry.getKey());
                sb.append("=\"");
                sb.append(value);
                sb.append("\",");
            }
        }

        return sb.substring(0, sb.length() - 1);
    }

    /**
     * Construct an Authorization Bearer header
     */
    public static String encodeAuthorizationBearerHeader(Map<String, Object> entries) {
        StringBuffer sb = new StringBuffer();
        sb.append(OAuth.OAUTH_HEADER_NAME).append(" ");
        for (Map.Entry<String, Object> entry : entries.entrySet()) {
            String value = entry.getValue() == null ? null : String.valueOf(entry.getValue());
            if (!Strings.isNullOrEmpty(entry.getKey()) && !Strings.isNullOrEmpty(value)) {
                sb.append(value);
            }
        }

        return sb.toString();
    }

    public static String decodePercent(String s) {
        try {
            return URLDecoder.decode(s, DEFAULT_CONTENT_CHARSET);
        } catch (java.io.UnsupportedEncodingException wow) {
            throw new RuntimeException(wow.getMessage(), wow);
        }
    }


}
