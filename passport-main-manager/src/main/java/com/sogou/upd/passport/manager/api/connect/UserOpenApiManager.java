package com.sogou.upd.passport.manager.api.connect;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.api.connect.form.user.UserOpenApiParams;
import com.sogou.upd.passport.model.OAuthConsumer;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;

import java.io.IOException;

/**
 * 用户类第三方开放平台接口代理
 * User: shipengzhi
 * Date: 13-7-10
 * Time: 上午11:25
 * To change this template use File | Settings | File Templates.
 */
public interface UserOpenApiManager {

    public Result getUserInfo(UserOpenApiParams userOpenApiParams);

    /**
     * 获取第三方个人资料，先从搜狗获取，如果没有获取到，再从第三方获取，获取成功后，更新到搜狗库中
     *
     * @param provider
     * @param openid
     * @param accessToken
     * @param original
     * @return
     * @throws com.sogou.upd.passport.exception.ServiceException
     *
     * @throws java.io.IOException
     * @throws com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException
     *
     */
    public Result handleObtainConnectUserInfo(int provider, String openid, String accessToken, int original) throws ServiceException, IOException, OAuthProblemException;

}
