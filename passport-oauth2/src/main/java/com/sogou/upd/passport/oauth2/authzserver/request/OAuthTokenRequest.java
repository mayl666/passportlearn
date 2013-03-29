package com.sogou.upd.passport.oauth2.authzserver.request;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.exception.ProblemException;
import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.oauth2.authzserver.validator.AuthorizationCodeValidator;
import com.sogou.upd.passport.oauth2.authzserver.validator.ClientCredentialValidator;
import com.sogou.upd.passport.oauth2.authzserver.validator.PasswordValidator;
import com.sogou.upd.passport.oauth2.authzserver.validator.RefreshTokenValidator;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.types.GrantType;
import com.sogou.upd.passport.oauth2.common.utils.OAuthUtils;
import com.sogou.upd.passport.oauth2.common.validators.OAuthValidator;

import javax.servlet.http.HttpServletRequest;


/**
 * OAuth2.0 授权请求，用于获取access_token和refresh_token
 */
public class OAuthTokenRequest extends OAuthRequest {


    public OAuthTokenRequest(HttpServletRequest request) throws SystemException, ProblemException {
        super(request);
    }

    @Override
    protected OAuthValidator<HttpServletRequest> initValidator() throws ProblemException, SystemException {
        validators.put(GrantType.PASSWORD.toString(), PasswordValidator.class);
        validators.put(GrantType.CLIENT_CREDENTIALS.toString(), ClientCredentialValidator.class);
        validators.put(GrantType.AUTHORIZATION_CODE.toString(), AuthorizationCodeValidator.class);
        validators.put(GrantType.REFRESH_TOKEN.toString(), RefreshTokenValidator.class);
        String requestTypeValue = getParam(OAuth.OAUTH_GRANT_TYPE);
        if (Strings.isNullOrEmpty(requestTypeValue)) {
            throw OAuthUtils.handleOAuthProblemException("Missing grant_type parameter value");
        }
        Class<? extends OAuthValidator<HttpServletRequest>> clazz = validators.get(requestTypeValue);
        if (clazz == null) {
            throw OAuthUtils.handleOAuthProblemException("Invalid grant_type parameter value");
        }
        return OAuthUtils.instantiateClass(clazz);
    }

    public String getPassword() {
        return getParam(OAuth.OAUTH_PASSWORD);
    }

    public String getUsername() {
        return getParam(OAuth.OAUTH_USERNAME);
    }

    public String getRefreshToken() {
        return getParam(OAuth.OAUTH_REFRESH_TOKEN);
    }
    
    public String getCode() {
        return getParam(OAuth.OAUTH_CODE);
    }

    public String getGrantType() {
        return getParam(OAuth.OAUTH_GRANT_TYPE);
    }

}
