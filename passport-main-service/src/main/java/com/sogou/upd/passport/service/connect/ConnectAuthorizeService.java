package com.sogou.upd.passport.service.connect;

import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.response.accesstoken.OAuthAccessTokenResponse;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-5-28
 * Time: 上午12:14
 * To change this template use File | Settings | File Templates.
 */
public interface ConnectAuthorizeService {

    /**
     * 用code获取access_token
     *
     * @param clientId
     * @param provider qq|sina|renren|taobao
     * @param code   oauth授权第一步返回的code
     * @param state   oauth授权防CRSF攻击状态码
     * @throws IOException,OAuthProblemException
     *
     */
    public OAuthAccessTokenResponse obtainAccessTokenByCode(int clientId, int provider, String code, String state)
            throws IOException, OAuthProblemException;

    /**
     * 用access_token获取openid
     * @throws IOException
     */
//    public OAuthOpenIdResponse obtainOpenIdByAccessToken(String accessToken, AuthzConsumerDO consumer)
//            throws SystemException, ProblemException, IOException;

    /**
     * 用refresh_token刷新access_token
     * QQ微博和人人
     */
    public OAuthAccessTokenResponse refreshAccessToken(int appid, String connectName, String refreshToken)
            throws IOException, OAuthProblemException;
}
