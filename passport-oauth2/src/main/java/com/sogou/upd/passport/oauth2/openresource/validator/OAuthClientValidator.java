package com.sogou.upd.passport.oauth2.openresource.validator;

import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;

public interface OAuthClientValidator {

    public void validateErrorResponse(OAuthClientResponse response) throws OAuthProblemException;

    public void validateRequiredParameters(OAuthClientResponse response) throws OAuthProblemException;

    public void validateNotAllowedParameters(OAuthClientResponse response) throws OAuthProblemException;

}
