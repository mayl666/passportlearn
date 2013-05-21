package com.sogou.upd.passport.oauth2.openresource.response.accesstoken;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.dataobject.SinaOAuthTokenDO;

/**
 * 错误响应码为400
 *
 * @author shipengzhi(shipengzhi@sogou-inc.com)
 */
public class SinaJSONAccessTokenResponse extends AbstractAccessTokenResponse {

    private SinaOAuthTokenDO oAuthTokenDO;

    @Override
    public void setBody(String body) throws OAuthProblemException {
        this.body = body;
        try {
            Gson gson = new Gson();
            this.oAuthTokenDO = gson.fromJson(this.body, new TypeToken<SinaOAuthTokenDO>() {
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
        return oAuthTokenDO.getExpires_in();
    }

    @Override
    public String getRefreshToken() {
        return null;
    }

    @Override
    public String getScope() {
        return null;
    }

    @Override
    public String getOpenid() {
        return oAuthTokenDO.getOpenid();
    }
}
