package com.sogou.upd.passport.oauth2.openresource.response.accesstoken;

import com.sogou.upd.passport.oauth2.openresource.dataobject.OAuthTokenDO;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;
import com.sogou.upd.passport.oauth2.openresource.validator.impl.TokenValidator;

public abstract class OAuthAccessTokenResponse extends OAuthClientResponse {

    public abstract String getAccessToken();

    public abstract Long getExpiresIn();

    public abstract String getRefreshToken();

    public abstract Long getRefreshTokenExpiresIn();

    public abstract String getScope();

    public abstract String getOpenid();

    public abstract String getNickName();

    public abstract OAuthTokenDO getOAuthToken();

    public String getBody() {
        return body;
    }

    @Override
    public void init(String body, String contentType, int responseCode) throws OAuthProblemException {
        validator = new TokenValidator();
        super.init(body, contentType, responseCode);
    }
}
