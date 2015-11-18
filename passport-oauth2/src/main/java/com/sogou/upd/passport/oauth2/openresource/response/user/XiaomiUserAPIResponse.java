package com.sogou.upd.passport.oauth2.openresource.response.user;

import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.JacksonJsonMapperUtil;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.parameters.XiaomiOAuth;
import com.sogou.upd.passport.oauth2.openresource.validator.impl.XiaomiAPIValidator;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;

import java.util.Map;

/**
 * Created by nahongxu on 2015/11/18.
 */
public class XiaomiUserAPIResponse extends UserAPIResponse {

    @Override
    public void init(String body, String contentType, int responseCode) throws OAuthProblemException {
        validator = new XiaomiAPIValidator();
        super.init(body, contentType, responseCode);
    }

    @Override
    protected void setBody(String body) throws OAuthProblemException {
        try {
            this.body = body;
            parameters = JacksonJsonMapperUtil.getMapper().readValue(this.body, Map.class);
        } catch (Exception e) {
            throw OAuthProblemException.error(ErrorUtil.UNSUPPORTED_RESPONSE_TYPE);
        }
    }

    @Override
    public ConnectUserInfoVO toUserInfo() {
        ConnectUserInfoVO user = new ConnectUserInfoVO();
        user.setNickname(getParam(XiaomiOAuth.NICK));
        user.setAvatarSmall(getParam(XiaomiOAuth.AVATAR_URL));    //
        user.setAvatarMiddle(getParam(XiaomiOAuth.AVATAR_URL));  //
        user.setAvatarLarge(getParam(XiaomiOAuth.AVATAR_URL));   //
        user.setUserid(getParam(XiaomiOAuth.USER_ID));
        user.setOriginal(parameters);
        return user;
    }
}
