package com.sogou.upd.passport.oauth2.openresource.response.accesstoken;

import com.google.common.base.Strings;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;
import com.sogou.upd.passport.oauth2.openresource.validator.impl.TokenValidator;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class OAuthAccessTokenResponse extends OAuthClientResponse {

    protected static final Logger log = LoggerFactory.getLogger(OAuthAccessTokenResponse.class);

    // refresh_token不为空时，默认有效期为3个月，单位秒
    protected static final Long RToken_ExpiresIn = 60 * 24 * 3600L * 1000;

    public abstract String getOpenid();

    public abstract String getNickName();

    public String getAccessToken() {
        return getParam(OAuth.OAUTH_ACCESS_TOKEN);
    }

    public Long getExpiresIn() {
        String value = getParam(OAuth.OAUTH_EXPIRES_IN);
        return Strings.isNullOrEmpty(value) ? 0 : Long.valueOf(value);
    }

    public String getRefreshToken() {
        String value = getParam(OAuth.OAUTH_REFRESH_TOKEN);
        return Strings.isNullOrEmpty(value) ? "" : value;
    }

    public Long getRefreshTokenExpiresIn() {
        String value = getParam(OAuth.OAUTH_RTOKEN_EXPIRES_IN);
        if (!Strings.isNullOrEmpty(value)) {
            return Long.valueOf(value);
        } else if (!Strings.isNullOrEmpty(getRefreshToken())) {
            return RToken_ExpiresIn;
        } else {
            return 0l;
        }
    }

    public String getScope() {
        String value = getParam(OAuth.OAUTH_SCOPE);
        return Strings.isNullOrEmpty(value) ? "" : value;
    }

    public OAuthTokenVO getOAuthTokenVO() {
        return new OAuthTokenVO(getAccessToken(), getExpiresIn(), getRefreshToken(), getRefreshTokenExpiresIn(), getScope(), getOpenid(), getNickName());
    }

    @Override
    public void init(String body, String contentType, int responseCode) throws OAuthProblemException {
        validator = new TokenValidator();
        super.init(body, contentType, responseCode);
    }
}
