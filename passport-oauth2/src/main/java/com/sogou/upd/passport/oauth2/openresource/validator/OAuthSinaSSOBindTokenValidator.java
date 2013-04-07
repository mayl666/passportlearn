package com.sogou.upd.passport.oauth2.openresource.validator;

import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.validators.AbstractValidator;
import com.sogou.upd.passport.oauth2.openresource.OpenOAuth;

import javax.servlet.http.HttpServletRequest;

public class OAuthSinaSSOBindTokenValidator extends OAuthSinaSSOTokenValidator {

    public OAuthSinaSSOBindTokenValidator() {
        super();
        requiredParams.add(OpenOAuth.BIND_ACCESS_TOKEN);
    }

}
