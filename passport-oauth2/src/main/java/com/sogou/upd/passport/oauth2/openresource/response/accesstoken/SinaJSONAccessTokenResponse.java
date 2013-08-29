package com.sogou.upd.passport.oauth2.openresource.response.accesstoken;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.HttpConstant;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.parameters.SinaOAuth;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.Map;

/**
 * 错误响应码为400
 *
 * @author shipengzhi(shipengzhi@sogou-inc.com)
 */
public class SinaJSONAccessTokenResponse extends OAuthAccessTokenResponse {

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
        String value = getParam(SinaOAuth.UID);
        return Strings.isNullOrEmpty(value) ? "" : value;
    }

    /**
     * Sina Authoz Code不返回nickName
     *
     * @return ""
     */
    @Override
    public String getNickName() {
        return "";
    }
}
