package com.sogou.upd.passport.oauth2.openresource.response.accesstoken;

import com.sogou.upd.passport.common.HttpConstant;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.parameters.RenrenOAuth;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.Map;

public class RenrenJSONAccessTokenResponse extends OAuthAccessTokenResponse {

    @Override
    public void setBody(String body) throws OAuthProblemException {
        this.body = body;
        try {
            this.parameters = new ObjectMapper().readValue(this.body, Map.class);
        } catch (Exception e) {
            throw OAuthProblemException.error(ErrorUtil.UNSUPPORTED_RESPONSE_TYPE,
                    "Invalid response! Response body is not " + HttpConstant.ContentType.JSON + " encoded");
        }
    }

    @Override
    public String getOpenid() {
        Map user = (Map) parameters.get(RenrenOAuth.USER);
        Integer id = (Integer) user.get("id");
        return String.valueOf(id);
    }

    @Override
    public String getNickName() {
        Map user = (Map) parameters.get(RenrenOAuth.USER);
        String name = (String) user.get("name");
        return name;
    }

}
