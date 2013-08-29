package com.sogou.upd.passport.oauth2.openresource.response.user;

import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-8-29
 * Time: 下午8:58
 * To change this template use File | Settings | File Templates.
 */
public abstract class UserAPIResponse extends OAuthClientResponse {

    @Override
    protected abstract void setBody(String body) throws OAuthProblemException;

    public abstract ConnectUserInfoVO toUserInfo();

}
