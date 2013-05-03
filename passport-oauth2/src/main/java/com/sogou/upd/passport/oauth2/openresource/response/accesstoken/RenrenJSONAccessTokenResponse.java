package com.sogou.upd.passport.oauth2.openresource.response.accesstoken;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.dataobject.RenrenOAuthTokenDO;

public class RenrenJSONAccessTokenResponse extends AbstractAccessTokenResponse {

    private RenrenOAuthTokenDO oAuthTokenDO;

    @Override
    public void setBody(String body) throws OAuthProblemException {
        this.body = body;
        try {
            Gson gson = new Gson();
            this.oAuthTokenDO = gson.fromJson(this.body, new TypeToken<RenrenOAuthTokenDO>() {
            }.getType());
        } catch (JsonSyntaxException e) {
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
