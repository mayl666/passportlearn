package com.sogou.upd.passport.oauth2.openresource.validator;

import javax.servlet.http.HttpServletRequest;

import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.validators.AbstractValidator;
import com.sogou.upd.passport.oauth2.openresource.OpenOAuth;

public class OAuthSinaSSOTokenValidator extends AbstractValidator<HttpServletRequest> {

    public OAuthSinaSSOTokenValidator() {
        requiredParams.add(OpenOAuth.OAUTH_CLIENT_ID);
        requiredParams.add(OpenOAuth.OAUTH_CLIENT_SECRET);
        requiredParams.add(OpenOAuth.DEFAULT_OPEN_UID);
        requiredParams.add(OpenOAuth.OAUTH_ACCESS_TOKEN);
        requiredParams.add(OpenOAuth.OAUTH_EXPIRES_IN);
        requiredParams.add(OpenOAuth.OAUTH_REFRESH_TOKEN);
        requiredParams.add(OpenOAuth.OAUTH_INSTANCE_ID);

    }

}
