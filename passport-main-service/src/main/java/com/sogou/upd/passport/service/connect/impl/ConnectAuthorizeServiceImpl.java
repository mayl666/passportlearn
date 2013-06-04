package com.sogou.upd.passport.service.connect.impl;

import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.model.OAuthConsumer;
import com.sogou.upd.passport.model.OAuthConsumerFactory;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.common.types.GrantTypeEnum;
import com.sogou.upd.passport.oauth2.openresource.http.OAuthHttpClient;
import com.sogou.upd.passport.oauth2.openresource.request.OAuthAuthzClientRequest;
import com.sogou.upd.passport.oauth2.openresource.request.OAuthClientRequest;
import com.sogou.upd.passport.oauth2.openresource.response.accesstoken.*;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import com.sogou.upd.passport.service.connect.ConnectAuthorizeService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ConnectAuthorizeServiceImpl implements ConnectAuthorizeService {

    @Autowired
    private ConnectConfigService connectConfigService;

    @Override
    public OAuthAccessTokenResponse obtainAccessTokenByCode(int clientId, int provider, String code, String state)
            throws IOException, OAuthProblemException {

        ConnectConfig connectConfig = connectConfigService.querySpecifyConnectConfig(clientId, provider);
        if (connectConfig == null) {
            return null;
        }
        String appKey = connectConfig.getAppKey();
        String appSecret = connectConfig.getAppSecret();

        OAuthConsumer oAuthConsumer = OAuthConsumerFactory.getOAuthConsumer(provider);
        String redirectUrl = oAuthConsumer.getCallbackUrl();

        OAuthAuthzClientRequest.TokenRequestBuilder builder = OAuthAuthzClientRequest.tokenLocation(oAuthConsumer.getAccessTokenUrl())
                .setAppKey(appKey).setAppSecret(appSecret).setRedirectURI(redirectUrl).setCode(code)
                .setGrantType(GrantTypeEnum.AUTHORIZATION_CODE).setState(state);

        OAuthAccessTokenResponse oauthResponse = null;
        OAuthClientRequest request = null;
        if (provider == AccountTypeEnum.SINA.getValue()) {
            //sina微博获取access_token接口，只允许POST方式
            request = builder.buildBodyMessage(OAuthClientRequest.class);
            oauthResponse = OAuthHttpClient.execute(request, OAuth.HttpMethod.POST, SinaJSONAccessTokenResponse.class);
        } else if (provider == AccountTypeEnum.QQ.getValue()) {
            request = builder.buildQueryMessage(OAuthClientRequest.class);
            oauthResponse = OAuthHttpClient.execute(request, OAuth.HttpMethod.GET, QQHTMLTextAccessTokenResponse.class);
        } else if (provider == AccountTypeEnum.RENREN.getValue()) {
            request = builder.buildQueryMessage(OAuthClientRequest.class);
            oauthResponse = OAuthHttpClient.execute(request, OAuth.HttpMethod.GET, RenrenJSONAccessTokenResponse.class);
        } else if (provider == AccountTypeEnum.TAOBAO.getValue()) {
            request = builder.buildBodyMessage(OAuthClientRequest.class);
            oauthResponse = OAuthHttpClient.execute(request, OAuth.HttpMethod.POST, TaobaoJSONAccessTokenResponse.class);
        } else {
            throw new OAuthProblemException(ErrorUtil.UNSUPPORT_THIRDPARTY);
        }
        return oauthResponse;
    }

    @Override
    public OAuthAccessTokenResponse refreshAccessToken(int appid, String connectName, String refreshToken) throws OAuthProblemException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
