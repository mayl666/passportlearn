package com.sogou.upd.passport.oauth2.authzserver.validator;


import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.validators.AbstractValidator;

import javax.servlet.http.HttpServletRequest;

/**
 * GrantType=refresh_token的授权请求验证器
 * 用refresh_token刷新access_token
 */
public class RefreshTokenValidator extends AbstractValidator<HttpServletRequest> {

    public RefreshTokenValidator() {
        requiredParams.add(OAuth.OAUTH_GRANT_TYPE);
        requiredParams.add(OAuth.OAUTH_CLIENT_ID);
        requiredParams.add(OAuth.OAUTH_REFRESH_TOKEN);
        requiredParams.add(OAuth.OAUTH_CLIENT_SECRET);
        requiredParams.add(OAuth.OAUTH_INSTANCE_ID);
    }

}
