package com.sogou.upd.passport.oauth2.openresource.response.accesstoken;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.dataobject.RenrenOAuthTokenDO;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class RenrenJSONAccessTokenResponse extends AbstractAccessTokenResponse {

    private RenrenOAuthTokenDO oAuthTokenDO;

    @Override
    public void setBody(String body) throws OAuthProblemException {
        this.body = body;
        try {
            this.oAuthTokenDO = new ObjectMapper().readValue(this.body, RenrenOAuthTokenDO.class);
        } catch (IOException e) {
            throw OAuthProblemException.error(ErrorUtil.UNSUPPORTED_RESPONSE_TYPE,
                    "Invalid response! Response body is not " + OAuth.ContentType.JSON + " encoded");
        }
    }

    @Override
    public String getAccessToken() {
        return oAuthTokenDO.getAccess_token();
    }

    @Override
    public Long getExpiresIn() {
        String value = oAuthTokenDO.getExpires_in();
        return Strings.isNullOrEmpty(value) ? null : Long.valueOf(value);
    }

    @Override
    public String getRefreshToken() {
        return oAuthTokenDO.getRefresh_token();
    }

    @Override
    public String getScope() {
        return null;
    }

    @Override
    public String getOpenid() {
        return oAuthTokenDO.getUser().getId();
    }

}
