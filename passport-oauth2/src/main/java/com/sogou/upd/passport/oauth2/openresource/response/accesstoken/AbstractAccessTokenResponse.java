package com.sogou.upd.passport.oauth2.openresource.response.accesstoken;

import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.dataobject.OAuthTokenDO;

public abstract class AbstractAccessTokenResponse extends OAuthAccessTokenResponse {

    // refresh_token不为空时，默认有效期为3个月，单位秒
    protected static final Long RToken_ExpiresIn = 60 * 24 * 3600L * 1000;

    public AbstractAccessTokenResponse() {
        super();
    }

    @Override
    public Long getRefreshTokenExpiresIn() {
        return RToken_ExpiresIn;
    }

    @Override
    public OAuthTokenDO getOAuthToken() {
        return new OAuthTokenDO(getAccessToken(), getExpiresIn(), getRefreshToken(),getRefreshToken(), getScope());
    }

    @Override
    public abstract void setBody(String body) throws OAuthProblemException;

    @Override
    protected void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    protected void setResponseCode(int code) {
        this.responseCode = code;
    }

}
