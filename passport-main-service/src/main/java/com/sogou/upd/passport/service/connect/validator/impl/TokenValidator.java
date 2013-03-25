package com.sogou.upd.passport.service.connect.validator.impl;

import org.apache.commons.lang3.StringUtils;

import com.sogou.upd.passport.common.exception.ProblemException;
import com.sogou.upd.passport.service.connect.message.OAuthResponse;
import com.sogou.upd.passport.service.connect.parameters.OAuth;

public class TokenValidator extends AbstractResponseValidator {

	public TokenValidator() {

		requiredParams.put(OAuth.OAUTH_ACCESS_TOKEN, new String[] {});

		notAllowedParams.add(OAuth.OAUTH_CODE);
	}
	
	@Override
	public void validateErrorResponse(OAuthResponse response) throws ProblemException {
		String error = response.getParam(OAuth.OAUTH_ERROR);
		if (!StringUtils.isEmpty(error)) {
			String errorDesc = response.getParam(OAuth.OAUTH_ERROR_DESCRIPTION);
			throw ProblemException.error(error).description(errorDesc);
		}
	}
}
