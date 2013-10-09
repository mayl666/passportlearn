package com.sogou.upd.passport.oauth2.openresource.response.accesstoken;

import com.sogou.upd.passport.common.HttpConstant;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.vo.BaiduOAuthTokenVO;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-5-28
 * Time: 上午11:37
 * To change this template use File | Settings | File Templates.
 */
public class BaiduJSONAccessTokenResponse extends OAuthAccessTokenResponse {

    private BaiduOAuthTokenVO oAuthTokenDO;

    @Override
    public void setBody(String body) throws OAuthProblemException {
        this.body = body;
        try {
            this.parameters = JacksonJsonMapperUtil.getMapper().readValue(this.body, Map.class);
        } catch (Exception e) {
            throw OAuthProblemException.error(ErrorUtil.UNSUPPORTED_RESPONSE_TYPE,
                    "Invalid response! Response body is not " + HttpConstant.ContentType.JSON + " encoded");
        }
    }

    private BaiduOAuthTokenVO getBaiduOAuthTokenVO() throws Exception {
        return JacksonJsonMapperUtil.getMapper().readValue(this.body, BaiduOAuthTokenVO.class);
    }

    @Override
    public String getOpenid() {
        return "";
    }

    @Override
    public String getNickName() {
        return "";
    }


}
