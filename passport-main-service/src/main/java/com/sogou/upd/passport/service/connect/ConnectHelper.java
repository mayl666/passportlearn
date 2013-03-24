package com.sogou.upd.passport.service.connect;

import com.sogou.upd.passport.common.exception.ProblemException;
import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.common.parameter.CommonParameters;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.StringUtil;
import com.sogou.upd.passport.service.connect.parameters.OAuth;
import net.sf.cglib.reflect.FastClass;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConnectHelper {

	public static final String PARAMETER_SEPARATOR = "&";
	public static final String NAME_VALUE_SEPARATOR = "=";

	private static final String DEFAULT_CONTENT_CHARSET = CommonParameters.UTF8;

	/**
	 * 格式化 into <code>application/x-www-form-urlencoded</code> String
	 * @param parameters 需编码的参数
	 * @param encoding  编码格式
	 * @return Translated string
	 * @throws java.io.UnsupportedEncodingException
	 */
	public static String format(final Collection<? extends Map.Entry<String, Object>> parameters,
			final String encoding) throws UnsupportedEncodingException {
		final StringBuilder result = new StringBuilder();
		for (final Map.Entry<String, Object> parameter : parameters) {
			String value = parameter.getValue() == null ? null : String.valueOf(parameter
					.getValue());
			if (!StringUtils.isEmpty(parameter.getKey()) && !StringUtils.isEmpty(value)) {
				final String encodedName = StringUtil.encode(parameter.getKey(), encoding);
				final String encodedValue = value != null ? StringUtil.encode(value, encoding) : "";
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

	/*====================  Exception Handle Method========================*/
	/**
	 * 创建一个包含缺失oauth参数的OAuthProblemException
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
		return ProblemException.error(ErrorUtil.INVALID_REQUEST).description(message);
	}

	/*=========================== Utils ==============================*/
	public static boolean hasEmptyValues(String[] array) {
		if (array == null || array.length == 0) { return true; }
		for (String s : array) {
			if (StringUtils.isEmpty(s)) { return true; }
		}
		return false;
	}

	// TODO 需做测试，采用java Reflect
	public static Object instantiateClassWithParameters(Class clazz, Class[] paramsTypes,
			Object[] paramValues) throws SystemException {
		try {
			if (paramsTypes != null && paramValues != null) {
				if (!(paramsTypes.length == paramValues.length)) { throw new IllegalArgumentException(
						"Number of types and values must be equal"); }

				if (paramsTypes.length == 0 && paramValues.length == 0) { return clazz
						.newInstance(); }
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

	// TODO 需做测试，采用Cglib
	public static Object cglibInstantiateClassWithParameters(Class clazz, Class[] paramsTypes,
			Object[] paramValues) throws SystemException {
		try {
			if (paramsTypes != null && paramValues != null) {
				if (!(paramsTypes.length == paramValues.length)) { throw new IllegalArgumentException(
						"Number of types and values must be equal"); }

				if (paramsTypes.length == 0 && paramValues.length == 0) { return FastClass.create(clazz); }
				FastClass cglibClass = FastClass.create(clazz);
				return cglibClass.newInstance(paramsTypes, paramValues);
			}
			return FastClass.create(clazz);
		} catch (InvocationTargetException e) {
			throw new SystemException(e);
		}
	}

}
