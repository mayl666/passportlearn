package com.sogou.upd.passport.oauth2.common.utils;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.exception.ProblemException;
import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.common.parameter.CommonParameters;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.StringUtil;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.OAuthError;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class OAuthUtils {

    /**
     * 格式化 into <code>application/x-www-form-urlencoded</code> String
     *
     * @param parameters 需编码的参数
     * @param encoding   编码格式
     * @return Translated string
     * @throws java.io.UnsupportedEncodingException
     *
     */
    public static String format(final Collection<? extends Map.Entry<String, Object>> parameters,
                                final String encoding) {
        final StringBuilder result = new StringBuilder();
        for (final Map.Entry<String, Object> parameter : parameters) {
            String value = parameter.getValue() == null ? null : String.valueOf(parameter
                    .getValue());
            if (!StringUtils.isEmpty(parameter.getKey()) && !StringUtils.isEmpty(value)) {
                final String encodedName = StringUtil.encode(parameter.getKey(), encoding);
                final String encodedValue = value != null ? StringUtil.encode(value, encoding) : "";
                if (result.length() > 0) {
                    result.append(CommonParameters.PARAMETER_SEPARATOR);
                }
                result.append(encodedName);
                result.append(CommonParameters.NAME_VALUE_SEPARATOR);
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
            charset = CommonParameters.DEFAULT_CONTENT_CHARSET;
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
        Set<String> scopes = new HashSet<String>();
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

    public static ProblemException handleBadContentTypeException(String expectedContentType) {
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
    public static ProblemException handleMissingParameters(Set<String> missingParams) {
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
    public static ProblemException handleNotAllowedParametersOAuthException(
            List<String> notAllowedParams) {
        StringBuffer sb = new StringBuffer("Not allowed parameters: ");
        if (!CollectionUtils.isEmpty(notAllowedParams)) {
            for (String notAllowed : notAllowedParams) {
                sb.append(notAllowed).append(" ");
            }
        }
        return handleOAuthProblemException(sb.toString().trim());
    }

    public static ProblemException handleOAuthProblemException(String message) {
        return ProblemException.error(OAuthError.Response.INVALID_REQUEST).description(message);
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

    public static <T> T instantiateClass(Class<T> clazz) throws SystemException {
        try {
            return (T) clazz.newInstance();
        } catch (Exception e) {
            throw new SystemException(e);
        }
    }

    public static Object instantiateClassWithParameters(Class clazz, Class[] paramsTypes,
                                                        Object[] paramValues) throws SystemException {
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
            throw new SystemException(e);
        } catch (InstantiationException e) {
            throw new SystemException(e);
        } catch (IllegalAccessException e) {
            throw new SystemException(e);
        } catch (InvocationTargetException e) {
            throw new SystemException(e);
        }
    }

}
