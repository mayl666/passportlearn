package com.sogou.upd.passport.service.connect.validator.impl;

import com.sogou.upd.passport.common.exception.ProblemException;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.service.connect.message.OAuthResponse;
import com.sogou.upd.passport.service.connect.parameters.OAuth;
import org.apache.commons.lang3.StringUtils;

public class AuthzCodeValidator extends AbstractResponseValidator {

	public AuthzCodeValidator() {
		requiredParams.put(OAuth.OAUTH_CODE, new String[] {});

		notAllowedParams.add(OAuth.OAUTH_ACCESS_TOKEN);
		notAllowedParams.add(OAuth.OAUTH_EXPIRES_IN);
	}

	@Override
	public void validateErrorResponse(OAuthResponse response) throws ProblemException {
		String error = response.getParam(OAuth.OAUTH_ERROR);
		if (!StringUtils.isEmpty(error)) {
			String errorDesc = response.getParam(OAuth.OAUTH_ERROR_DESCRIPTION);
			throw ProblemException.error(ErrorUtil.CONNECT_USER_DENIED_LOGIN).description(errorDesc);
		}

	}
}
