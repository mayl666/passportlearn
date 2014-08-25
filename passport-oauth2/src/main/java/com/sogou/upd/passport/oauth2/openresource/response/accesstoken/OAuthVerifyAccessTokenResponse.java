package com.sogou.upd.passport.oauth2.openresource.response.accesstoken;

import com.google.common.base.Strings;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;
import com.sogou.upd.passport.oauth2.openresource.validator.impl.TokenValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-8-25
 * Time: 下午7:17
 * To change this template use File | Settings | File Templates.
 */
public abstract class OAuthVerifyAccessTokenResponse extends OAuthClientResponse {

    protected static final Logger log = LoggerFactory.getLogger(OAuthAccessTokenResponse.class);

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

    @Override
    public void init(String body, String contentType, int responseCode) throws OAuthProblemException {
        validator = new TokenValidator();
        super.init(body, contentType, responseCode);
    }

}
