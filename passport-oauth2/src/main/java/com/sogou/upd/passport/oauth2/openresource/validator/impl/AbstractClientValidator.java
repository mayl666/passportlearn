package com.sogou.upd.passport.oauth2.openresource.validator.impl;

import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.common.utils.OAuthUtils;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;
import com.sogou.upd.passport.oauth2.openresource.validator.OAuthClientValidator;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public abstract class AbstractClientValidator implements OAuthClientValidator {

    public Map<String, String[]> requiredParams = new HashMap<String, String[]>();
    public List<String> notAllowedParams = new ArrayList<String>();

    public void validate(OAuthClientResponse response) throws OAuthProblemException {
        validateErrorResponse(response);
        validateParameters(response);
    }

    public void validateParameters(OAuthClientResponse response) throws OAuthProblemException {
        validateRequiredParameters(response);
        validateNotAllowedParameters(response);
    }

    /**
     * 响应返回错误码的处理逻辑
     */
    public abstract void validateErrorResponse(OAuthClientResponse response) throws OAuthProblemException;

    /**
     * 验证必须存在的参数
     */
    public void validateRequiredParameters(OAuthClientResponse response) throws OAuthProblemException {
        Set<String> missingParameters = new HashSet<String>();

        for (Map.Entry<String, String[]> requiredParam : requiredParams.entrySet()) {
            String paramName = requiredParam.getKey();
            String val = response.getParam(paramName);
            if (StringUtils.isEmpty(val)) {
                missingParameters.add(paramName);
            } else {
                String[] dependentParams = requiredParam.getValue();
                if (!OAuthUtils.hasEmptyValues(dependentParams)) {
                    for (String dependentParam : dependentParams) {
                        val = response.getParam(dependentParam);
                        if (StringUtils.isEmpty(val)) {
                            missingParameters.add(dependentParam);
                        }
                    }
                }
            }
        }

        if (!missingParameters.isEmpty()) {
            throw OAuthUtils.handleMissingParameters(missingParameters);
        }
    }

    /**
     * 验证不允许存在的参数
     */
    public void validateNotAllowedParameters(OAuthClientResponse response) throws OAuthProblemException {
        List<String> notAllowedParameters = new ArrayList<String>();
        for (String requiredParam : notAllowedParams) {
            String val = response.getParam(requiredParam);
            if (!StringUtils.isEmpty(val)) {
                notAllowedParameters.add(requiredParam);
            }
        }
        if (!notAllowedParameters.isEmpty()) {
            throw OAuthUtils
                    .handleNotAllowedParametersOAuthException(notAllowedParameters);
        }
    }

}
