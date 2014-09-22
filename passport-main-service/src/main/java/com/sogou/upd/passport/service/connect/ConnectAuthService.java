package com.sogou.upd.passport.service.connect;

import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.OAuthConsumer;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.response.accesstoken.OAuthAccessTokenResponse;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;

import java.io.IOException;

/**
 * 第三方OAuth授权相关服务接口
 * User: shipengzhi
 * Date: 13-5-28
 * Time: 上午12:14
 * To change this template use File | Settings | File Templates.
 */
public interface ConnectAuthService {

    /**
     * 用code获取access_token
     *
     * @param provider      qq|sina|renren|taobao|baidu
     * @param code          oauth授权第一步返回的code
     * @param connectConfig
     * @throws IOException,OAuthProblemException
     *
     */
    public OAuthAccessTokenResponse obtainAccessTokenByCode(int provider, String code, ConnectConfig connectConfig, OAuthConsumer oAuthConsumer, String redirectUrl)
            throws IOException, OAuthProblemException;

    /**
     * 用refresh_token刷新access_token
     * QQ、人人、百度
     */
    public OAuthTokenVO refreshAccessToken(String refreshToken, ConnectConfig connectConfig) throws OAuthProblemException, IOException;

    /**
     * 获取第三方个人资料
     *
     * @param provider
     * @param connectConfig
     * @return
     * @throws IOException
     * @throws OAuthProblemException
     */
    public ConnectUserInfoVO obtainConnectUserInfo(int provider, ConnectConfig connectConfig, String openid, String accessToken,
                                                   OAuthConsumer oAuthConsumer) throws IOException, OAuthProblemException;


    /**
     * 更新第三方个人资料缓存
     *
     * @param passportId
     * @param connectUserInfoVO
     * @return
     * @throws ServiceException
     */
    public boolean initialOrUpdateConnectUserInfo(String passportId, ConnectUserInfoVO connectUserInfoVO) throws ServiceException;

    /**
     * 通过缓存获取个人资料
     *
     * @param passportId
     * @return
     * @throws ServiceException
     */
    public ConnectUserInfoVO obtainCachedConnectUserInfo(String passportId);

    /**
     * 调用第三方api获取用户个人资料
     *
     * @param provider
     * @param appKey
     * @param connectToken
     * @return
     * @throws IOException
     * @throws OAuthProblemException
     */
    public ConnectUserInfoVO getConnectUserInfo(int provider, String appKey, ConnectToken connectToken) throws IOException, OAuthProblemException;

    /**
     * 验证accessToken是否有效
     *
     * @param openid
     * @param accessToken
     * @return
     * @throws IOException
     * @throws OAuthProblemException
     */
    public OAuthTokenVO verifyAccessToken(String openid, String accessToken, ConnectConfig connectConfig) throws IOException, OAuthProblemException;
}
