package com.sogou.upd.passport.oauth2.openresource.validator;

import com.sogou.upd.passport.common.exception.ProblemException;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.OAuthResponse;
import com.sogou.upd.passport.oauth2.common.validators.AbstractValidator;
import com.sogou.upd.passport.oauth2.openresource.OpenOAuth;

import javax.servlet.http.HttpServletRequest;

public class OAuthSinaSSOTokenValidator extends AbstractValidator<HttpServletRequest> {

    public OAuthSinaSSOTokenValidator() {
        requiredParams.add(OpenOAuth.OAUTH_SINA_UID);
        requiredParams.add(OpenOAuth.OAUTH_ACCESS_TOKEN);
        requiredParams.add(OpenOAuth.OAUTH_EXPIRES_IN);
    }

}
