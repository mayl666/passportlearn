package com.sogou.upd.passport.oauth2.openresource.response.accesstoken;

import com.sogou.upd.passport.common.HttpConstant;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.vo.RenrenOAuthTokenVO;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.Map;

public class RenrenJSONAccessTokenResponse extends OAuthAccessTokenResponse {

    private RenrenOAuthTokenVO oAuthTokenDO;

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

    private RenrenOAuthTokenVO getRenrenOAuthTokenVO() throws Exception {
        return new ObjectMapper().readValue(this.body, RenrenOAuthTokenVO.class);
    }

    @Override
    public String getOpenid() {
        try {
            RenrenOAuthTokenVO renrenOAuthTokenVO = getRenrenOAuthTokenVO();
            return renrenOAuthTokenVO.getUser().getId();
        } catch (Exception e) {
            log.error("Connect OAuthToken Response parse error, connect:renren, body:"+body, e);
            return "";
        }
    }

    @Override
    public String getNickName() {
        try {
            RenrenOAuthTokenVO renrenOAuthTokenVO = getRenrenOAuthTokenVO();
            return renrenOAuthTokenVO.getUser().getName();
        } catch (Exception e) {
            log.error("Connect OAuthToken Response parse error, connect:renren, body:"+body, e);
            return "";
        }
    }

}
