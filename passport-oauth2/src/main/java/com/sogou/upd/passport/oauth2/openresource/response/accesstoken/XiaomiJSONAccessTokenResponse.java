package com.sogou.upd.passport.oauth2.openresource.response.accesstoken;

import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.parameters.XiaomiOAuth;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;

import java.util.Map;

/**
 * 小米登录与token相关的
 * Created with IntelliJ IDEA.

 * To change this template use File | Settings | File Templates.
 */
public class XiaomiJSONAccessTokenResponse extends OAuthAccessTokenResponse {

    @Override
    public String getOpenid() {
        return getParam(OAuth.OAUTH_OPENID);  //To change body of implemented methods use File | Settings | File Templates.
    }

    /* 小米刷新accessToken时，不返回昵称 */
    @Override
    public String getNickName() {
        return "";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void setBody(String body) throws OAuthProblemException {
        this.body = body;
        try {
            this.body=body.replace(XiaomiOAuth.XIAOMI_TOKEN_START,"");//小米开放平台多了特殊的响应值,去掉
            this.parameters = JacksonJsonMapperUtil.getMapper().readValue(this.body, Map.class);
        } catch (Exception e) {
            throw OAuthProblemException.error(ErrorUtil.UNSUPPORTED_RESPONSE_TYPE,
                    "Invalid response! Response body resolve error,body is " + this.body);
        }//To change body of implemented methods use File | Settings | File Templates.
    }

    public OAuthTokenVO getOAuthTokenVO() {
        OAuthTokenVO oAuthTokenVO = new OAuthTokenVO(getAccessToken(), getExpiresIn(), getRefreshToken(), getScope());
        oAuthTokenVO.setOpenid(String.valueOf(this.parameters.get(XiaomiOAuth.OPENID)));
        return oAuthTokenVO;
    }
}
