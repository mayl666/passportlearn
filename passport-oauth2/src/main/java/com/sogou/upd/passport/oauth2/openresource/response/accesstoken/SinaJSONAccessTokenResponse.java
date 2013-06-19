package com.sogou.upd.passport.oauth2.openresource.response.accesstoken;

import com.sogou.upd.passport.common.HttpConstant;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.vo.SinaOAuthTokenVO;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.Map;

/**
 * 错误响应码为400
 *
 * @author shipengzhi(shipengzhi@sogou-inc.com)
 */
public class SinaJSONAccessTokenResponse extends OAuthAccessTokenResponse {

    private SinaOAuthTokenVO oAuthTokenDO;

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

    private SinaOAuthTokenVO getSinaOAuthTokenVO() throws Exception {
        return new ObjectMapper().readValue(this.body, SinaOAuthTokenVO.class);
    }

    @Override
    public String getOpenid() {
        try {
            SinaOAuthTokenVO sinaOAuthTokenVO = getSinaOAuthTokenVO();
            return sinaOAuthTokenVO.getOpenid();
        } catch (Exception e) {
            log.error("Connect OAuthToken Response parse error, connect:sina, body:"+body, e);
            return "";
        }
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
