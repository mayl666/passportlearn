package com.sogou.upd.passport.service.connect.impl;

import com.sogou.upd.passport.common.HttpConstant;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.model.OAuthConsumer;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.common.types.GrantTypeEnum;
import com.sogou.upd.passport.oauth2.openresource.http.OAuthHttpClient;
import com.sogou.upd.passport.oauth2.openresource.request.OAuthAuthzClientRequest;
import com.sogou.upd.passport.oauth2.openresource.request.OAuthClientRequest;
import com.sogou.upd.passport.oauth2.openresource.request.user.QQUserAPIRequest;
import com.sogou.upd.passport.oauth2.openresource.request.user.RenrenUserAPIRequest;
import com.sogou.upd.passport.oauth2.openresource.request.user.SinaUserAPIRequest;
import com.sogou.upd.passport.oauth2.openresource.response.accesstoken.*;
import com.sogou.upd.passport.oauth2.openresource.response.user.QQUserAPIResponse;
import com.sogou.upd.passport.oauth2.openresource.response.user.RenrenUserAPIResponse;
import com.sogou.upd.passport.oauth2.openresource.response.user.SinaUserAPIResponse;
import com.sogou.upd.passport.oauth2.openresource.response.user.UserAPIResponse;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import com.sogou.upd.passport.service.connect.ConnectAuthService;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-5-28
 * Time: 上午12:33
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ConnectAuthServiceImpl implements ConnectAuthService {

    @Override
    public OAuthAccessTokenResponse obtainAccessTokenByCode(int provider, String code, ConnectConfig connectConfig, OAuthConsumer oAuthConsumer, String redirectUrl)
            throws IOException, OAuthProblemException {

        String appKey = connectConfig.getAppKey();
        String appSecret = connectConfig.getAppSecret();

        OAuthAuthzClientRequest.TokenRequestBuilder builder = OAuthAuthzClientRequest.tokenLocation(oAuthConsumer.getAccessTokenUrl())
                .setAppKey(appKey).setAppSecret(appSecret).setRedirectURI(redirectUrl).setCode(code)
                .setGrantType(GrantTypeEnum.AUTHORIZATION_CODE);

        OAuthAccessTokenResponse oauthResponse;
        OAuthAuthzClientRequest request = builder.buildBodyMessage(OAuthAuthzClientRequest.class);
        if (provider == AccountTypeEnum.QQ.getValue()) {
            oauthResponse = OAuthHttpClient.execute(request, HttpConstant.HttpMethod.POST, QQHTMLTextAccessTokenResponse.class);
        } else if (provider == AccountTypeEnum.SINA.getValue()) {
            oauthResponse = OAuthHttpClient.execute(request, HttpConstant.HttpMethod.POST, SinaJSONAccessTokenResponse.class);
        } else if (provider == AccountTypeEnum.RENREN.getValue()) {
            oauthResponse = OAuthHttpClient.execute(request, HttpConstant.HttpMethod.POST, RenrenJSONAccessTokenResponse.class);
        } else if (provider == AccountTypeEnum.TAOBAO.getValue()) {
            oauthResponse = OAuthHttpClient.execute(request, HttpConstant.HttpMethod.POST, TaobaoJSONAccessTokenResponse.class);
        } else if (provider == AccountTypeEnum.BAIDU.getValue()) {
            oauthResponse = OAuthHttpClient.execute(request, HttpConstant.HttpMethod.POST, BaiduJSONAccessTokenResponse.class);
        } else {
            throw new OAuthProblemException(ErrorUtil.UNSUPPORT_THIRDPARTY);
        }
        return oauthResponse;
    }

    @Override
    public QQOpenIdResponse obtainOpenIdByAccessToken(int provider, String accessToken, OAuthConsumer oAuthConsumer) throws OAuthProblemException, IOException {
        OAuthAuthzClientRequest request = OAuthAuthzClientRequest.openIdLocation(oAuthConsumer.getOpenIdUrl())
                .setAccessToken(accessToken).buildQueryMessage(OAuthAuthzClientRequest.class);

        QQOpenIdResponse qqOpenIdResponse = OAuthHttpClient.execute(request,
                QQOpenIdResponse.class);
        return qqOpenIdResponse;
    }

    @Override
    public OAuthAccessTokenResponse refreshAccessToken(int appid, String connectName, String refreshToken) throws OAuthProblemException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ConnectUserInfoVO obtainConnectUserInfo(int provider, ConnectConfig connectConfig, String openid, String accessToken, OAuthConsumer oAuthConsumer) throws IOException, OAuthProblemException {
        String url = oAuthConsumer.getUserInfo();
        String appKey = connectConfig.getAppKey();
        ConnectUserInfoVO userProfileFromConnect = null;

        OAuthClientRequest request;
        UserAPIResponse response = null;
        if (provider == AccountTypeEnum.QQ.getValue()) {
            request = QQUserAPIRequest.apiLocation(url, QQUserAPIRequest.QQUserAPIBuilder.class)
                    .setOauth_Consumer_Key(appKey).setOpenid(openid).setAccessToken(accessToken)
                    .buildQueryMessage(QQUserAPIRequest.class);
            response = OAuthHttpClient.execute(request, QQUserAPIResponse.class);
        } else if (provider == AccountTypeEnum.SINA.getValue()) {
            request = SinaUserAPIRequest.apiLocation(url, SinaUserAPIRequest.SinaUserAPIBuilder.class).setUid(openid)
                    .setAccessToken(accessToken).buildQueryMessage(SinaUserAPIRequest.class);
            response = OAuthHttpClient.execute(request, SinaUserAPIResponse.class);
        } else if (provider == AccountTypeEnum.RENREN.getValue()) {
            request = RenrenUserAPIRequest.apiLocation(url, RenrenUserAPIRequest.RenrenUserAPIBuilder.class)
                    .setUserId(openid).setAccessToken(accessToken).buildQueryMessage(RenrenUserAPIRequest.class);
            response = OAuthHttpClient.execute(request, RenrenUserAPIResponse.class);
        }
        if (response != null) {
            userProfileFromConnect = response.toUserInfo();
        }
        return userProfileFromConnect;
    }

}
