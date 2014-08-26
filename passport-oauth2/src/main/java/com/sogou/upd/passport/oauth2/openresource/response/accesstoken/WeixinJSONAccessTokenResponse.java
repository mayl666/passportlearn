package com.sogou.upd.passport.oauth2.openresource.response.accesstoken;

import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;

import java.util.Map;

/**
 * 微信登录与token相关的
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-8-26
 * Time: 上午10:46
 * To change this template use File | Settings | File Templates.
 */
public class WeixinJSONAccessTokenResponse extends OAuthAccessTokenResponse {

    @Override
    public String getOpenid() {
        return getParam(OAuth.OAUTH_OPENID);  //To change body of implemented methods use File | Settings | File Templates.
    }

    /* 微信刷新accessToken时，不返回昵称 */
    @Override
    public String getNickName() {
        return "";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void setBody(String body) throws OAuthProblemException {
        this.body = body;
        try {
            this.parameters = JacksonJsonMapperUtil.getMapper().readValue(this.body, Map.class);
        } catch (Exception e) {
            throw OAuthProblemException.error(ErrorUtil.UNSUPPORTED_RESPONSE_TYPE,
                    "Invalid response! Response body resolve error,body is " + this.body);
        }//To change body of implemented methods use File | Settings | File Templates.
    }

    public OAuthTokenVO getOAuthTokenVO() {
        OAuthTokenVO oAuthTokenVO = new OAuthTokenVO(getAccessToken(), getExpiresIn(), getRefreshToken(), getScope());
        oAuthTokenVO.setOpenid(String.valueOf(this.parameters.get(OAuth.OAUTH_OPENID)));
        return oAuthTokenVO;
    }
}
