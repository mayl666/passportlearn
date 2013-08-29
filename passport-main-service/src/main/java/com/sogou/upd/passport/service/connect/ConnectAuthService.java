package com.sogou.upd.passport.service.connect;

import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.response.accesstoken.OAuthAccessTokenResponse;
import com.sogou.upd.passport.oauth2.openresource.response.accesstoken.QQOpenIdResponse;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
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
    public OAuthAccessTokenResponse obtainAccessTokenByCode(int provider, String code, ConnectConfig connectConfig)
            throws IOException, OAuthProblemException;

    /**
     * QQ需要用access_token获取openid
     * @throws IOException
     */
    public QQOpenIdResponse obtainOpenIdByAccessToken(int provider, String accessToken)
            throws OAuthProblemException, IOException;

    /**
     * 用refresh_token刷新access_token
     * QQ微博和人人
     */
    public OAuthAccessTokenResponse refreshAccessToken(int appid, String connectName, String refreshToken)
            throws IOException, OAuthProblemException;
}
