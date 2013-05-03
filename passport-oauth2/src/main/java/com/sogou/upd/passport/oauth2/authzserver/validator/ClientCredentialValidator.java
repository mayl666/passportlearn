package com.sogou.upd.passport.oauth2.authzserver.validator;

import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.validators.AbstractValidator;

import javax.servlet.http.HttpServletRequest;

/**
 * GrantTypeEnum=client_credentials的授权请求验证器
 * OAuth2.0协议中的Client Credentials Grant
 */
public class ClientCredentialValidator extends AbstractValidator<HttpServletRequest> {
    public ClientCredentialValidator() {
        requiredParams.add(OAuth.OAUTH_GRANT_TYPE);
    }
}
