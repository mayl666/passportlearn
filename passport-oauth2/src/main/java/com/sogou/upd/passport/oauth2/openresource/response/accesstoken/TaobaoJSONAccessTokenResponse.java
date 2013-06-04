package com.sogou.upd.passport.oauth2.openresource.response.accesstoken;

import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.dataobject.TaobaoOAuthTokenDO;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-5-28
 * Time: 上午11:37
 * To change this template use File | Settings | File Templates.
 */
public class TaobaoJSONAccessTokenResponse extends AbstractAccessTokenResponse {

    private TaobaoOAuthTokenDO oAuthTokenDO;

    @Override
    public void setBody(String body) throws OAuthProblemException {
        this.body = body;
        try {
            oAuthTokenDO = new ObjectMapper().readValue(this.body, TaobaoOAuthTokenDO.class);
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
        return oAuthTokenDO.getExpires_in();
    }

    @Override
    public String getRefreshToken() {
        return null;
    }

    @Override
    public Long getRefreshTokenExpiresIn() {
        return oAuthTokenDO.getRe_expires_in();
    }

    @Override
    public String getScope() {
        return null;
    }

    @Override
    public String getOpenid() {
        return oAuthTokenDO.getTaobao_user_id();
    }

    @Override
    public String getNickName() {
        return oAuthTokenDO.getTaobao_user_nick();
    }


}
