package com.sogou.upd.passport.oauth2.authzserver.request;

import com.google.common.base.Strings;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.authzserver.validator.AuthorizationCodeValidator;
import com.sogou.upd.passport.oauth2.authzserver.validator.ClientCredentialValidator;
import com.sogou.upd.passport.oauth2.authzserver.validator.PasswordValidator;
import com.sogou.upd.passport.oauth2.authzserver.validator.RefreshTokenValidator;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.types.GrantTypeEnum;
import com.sogou.upd.passport.oauth2.common.utils.OAuthUtils;
import com.sogou.upd.passport.oauth2.common.validators.OAuthValidator;

import javax.servlet.http.HttpServletRequest;


/**
 * OAuth2.0 授权请求，用于获取access_token和refresh_token
 */
public class OAuthTokenASRequest extends OAuthASRequest {


    public OAuthTokenASRequest(HttpServletRequest request) throws OAuthProblemException {
        super(request);
    }

    @Override
    protected OAuthValidator<HttpServletRequest> initValidator() throws OAuthProblemException {
        validators.put(GrantTypeEnum.PASSWORD.toString(), PasswordValidator.class);
        validators.put(GrantTypeEnum.CLIENT_CREDENTIALS.toString(), ClientCredentialValidator.class);
        validators.put(GrantTypeEnum.AUTHORIZATION_CODE.toString(), AuthorizationCodeValidator.class);
        validators.put(GrantTypeEnum.REFRESH_TOKEN.toString(), RefreshTokenValidator.class);
        validators.put(GrantTypeEnum.HEART_BEAT.toString(), RefreshTokenValidator.class);
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

    public int getPwdType() {
        String value = getParam(OAuth.OAUTH_PASSWORD_TYPE);
        return value == null ? 0 : Integer.valueOf(value);
    }

}
