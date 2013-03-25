package com.sogou.upd.passport.service.connect.validator;

import com.sogou.upd.passport.common.exception.ProblemException;
import com.sogou.upd.passport.service.connect.message.OAuthResponse;

public interface OAuthResponseValidator {
	
	public void validateErrorResponse(OAuthResponse response) throws ProblemException;

	public void validateRequiredParameters(OAuthResponse response) throws ProblemException;

	public void validateNotAllowedParameters(OAuthResponse response) throws ProblemException;

}
