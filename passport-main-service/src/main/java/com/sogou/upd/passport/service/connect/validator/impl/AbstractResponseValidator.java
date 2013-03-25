package com.sogou.upd.passport.service.connect.validator.impl;

import com.sogou.upd.passport.common.exception.ProblemException;
import com.sogou.upd.passport.service.connect.ConnectHelper;
import com.sogou.upd.passport.service.connect.message.OAuthResponse;
import com.sogou.upd.passport.service.connect.validator.OAuthResponseValidator;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public abstract class AbstractResponseValidator implements OAuthResponseValidator{
	
	public Map<String, String[]> requiredParams = new HashMap<String, String[]>();
	public List<String> notAllowedParams = new ArrayList<String>();
	
	public void validate(OAuthResponse response) throws ProblemException {
		validateErrorResponse(response);
		validateParameters(response);
	}

	public void validateParameters(OAuthResponse response) throws ProblemException {
		validateRequiredParameters(response);
		validateNotAllowedParameters(response);
	}

	/**
	 * 响应返回错误码的处理逻辑
	 */
	public abstract void validateErrorResponse(OAuthResponse response) throws ProblemException;

	/**
	 * 验证必须存在的参数
	 */
    @Override
	public void validateRequiredParameters(OAuthResponse response) throws ProblemException {
		Set<String> missingParameters = new HashSet<String>();

		for (Map.Entry<String, String[]> requiredParam : requiredParams.entrySet()) {
			String paramName = requiredParam.getKey();
			String val = response.getParam(paramName);
			if (StringUtils.isEmpty(val)) {
				missingParameters.add(paramName);
			} else {
				String[] dependentParams = requiredParam.getValue();
				if (!ConnectHelper.hasEmptyValues(dependentParams)) {
					for (String dependentParam : dependentParams) {
						val = response.getParam(dependentParam);
						if (StringUtils.isEmpty(val)) {
							missingParameters.add(dependentParam);
						}
					}
				}
			}
		}

		if (!missingParameters.isEmpty()) { throw ConnectHelper.handleMissingParameters(missingParameters); }
	}

	/**
	 * 验证不允许存在的参数
	 */
    @Override
	public void validateNotAllowedParameters(OAuthResponse response) throws ProblemException {
		List<String> notAllowedParameters = new ArrayList<String>();
		for (String requiredParam : notAllowedParams) {
			String val = response.getParam(requiredParam);
			if (!StringUtils.isEmpty(val)) {
				notAllowedParameters.add(requiredParam);
			}
		}
		if (!notAllowedParameters.isEmpty()) { throw ConnectHelper
				.handleNotAllowedParametersOAuthException(notAllowedParameters); }
	}

}
