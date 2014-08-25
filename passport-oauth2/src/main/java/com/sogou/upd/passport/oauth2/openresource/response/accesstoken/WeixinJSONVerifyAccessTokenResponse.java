package com.sogou.upd.passport.oauth2.openresource.response.accesstoken;

import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.parameters.WeiXinOAuthError;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import com.sogou.upd.passport.oauth2.openresource.vo.WeixinOAuthTokenVO;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-8-25
 * Time: 下午3:58
 * To change this template use File | Settings | File Templates.
 */
public class WeixinJSONVerifyAccessTokenResponse extends OAuthAccessTokenResponse {
    @Override
    public void setBody(String body) throws OAuthProblemException {
        this.body = body;
        try {
            this.parameters = JacksonJsonMapperUtil.getMapper().readValue(this.body, Map.class);
        } catch (Exception e) {
            throw OAuthProblemException.error(ErrorUtil.UNSUPPORTED_RESPONSE_TYPE,
                    "Invalid response! Response body resolve error,body is " + this.body);
        }
    }

    public OAuthTokenVO getOAuthTokenVO() {
        return new WeixinOAuthTokenVO((String) this.parameters.get(WeiXinOAuthError.ERROR_CODE), (String) this.parameters.get(WeiXinOAuthError.ERROR_MSG));
    }

    @Override
    public String getOpenid() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getNickName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
