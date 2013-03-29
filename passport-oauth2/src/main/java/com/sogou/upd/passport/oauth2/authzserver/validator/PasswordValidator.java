package com.sogou.upd.passport.oauth2.authzserver.validator;

import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.validators.AbstractValidator;

import javax.servlet.http.HttpServletRequest;

/**
 * GrantType=passport的授权请求验证器
 * OAuth2.0协议中Resource Owner Password Credentials Grant
 */
public class PasswordValidator extends AbstractValidator<HttpServletRequest> {

    public PasswordValidator() {

        requiredParams.add(OAuth.OAUTH_GRANT_TYPE);
        requiredParams.add(OAuth.OAUTH_CLIENT_ID);
        requiredParams.add(OAuth.OAUTH_USERNAME);
        requiredParams.add(OAuth.OAUTH_PASSWORD);
        requiredParams.add(OAuth.OAUTH_CLIENT_SECRET);
        requiredParams.add(OAuth.OAUTH_INSTANCE_ID);
    }

}
