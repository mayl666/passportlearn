package com.sogou.upd.passport.oauth2.common.utils;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.StringUtil;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OAuthUtils {

    private static Logger log = LoggerFactory.getLogger(OAuthUtils.class);

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
                    result.append(CommonConstant.PARAMETER_SEPARATOR);
                }
                result.append(encodedName);
                result.append(CommonConstant.NAME_VALUE_SEPARATOR);
                result.append(encodedValue);
            }
        }
        return result.toString();
    }

    public static String encodeOAuthHeader(Map<String, Object> entries) {
        StringBuffer sb = new StringBuffer();
        sb.append(OAuth.OAUTH_HEADER_NAME).append(" ");
        for (Map.Entry<String, Object> entry : entries.entrySet()) {
            String value = entry.getValue() == null ? null : String.valueOf(entry.getValue());
            if (!StringUtils.isEmpty(entry.getKey()) && !StringUtils.isEmpty(value)) {
                sb.append(entry.getKey());
                sb.append("=\"");
                sb.append(value);
                sb.append("\",");
            }
        }

        return sb.substring(0, sb.length() - 1);
    }

    public static String toString(final InputStream is, final String defaultCharset)
            throws IOException {
        if (is == null) {
            throw new IllegalArgumentException("InputStream may not be null");
        }

        String charset = defaultCharset;
        if (charset == null) {
            charset = CommonConstant.DEFAULT_CONTENT_CHARSET;
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
            return (T) clazz.newInstance();
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

}
