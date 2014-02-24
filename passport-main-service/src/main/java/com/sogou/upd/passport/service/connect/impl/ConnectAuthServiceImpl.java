package com.sogou.upd.passport.service.connect.impl;

import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.HttpConstant;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.utils.DBRedisUtils;
import com.sogou.upd.passport.common.utils.DBShardRedisUtils;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.model.OAuthConsumer;
import com.sogou.upd.passport.model.OAuthConsumerFactory;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.common.types.GrantTypeEnum;
import com.sogou.upd.passport.oauth2.openresource.http.OAuthHttpClient;
import com.sogou.upd.passport.oauth2.openresource.request.OAuthAuthzClientRequest;
import com.sogou.upd.passport.oauth2.openresource.request.OAuthClientRequest;
import com.sogou.upd.passport.oauth2.openresource.request.user.BaiduUserAPIRequest;
import com.sogou.upd.passport.oauth2.openresource.request.user.QQUserAPIRequest;
import com.sogou.upd.passport.oauth2.openresource.request.user.RenrenUserAPIRequest;
import com.sogou.upd.passport.oauth2.openresource.request.user.SinaUserAPIRequest;
import com.sogou.upd.passport.oauth2.openresource.response.accesstoken.*;
import com.sogou.upd.passport.oauth2.openresource.response.user.*;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import com.sogou.upd.passport.service.connect.ConnectAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ConnectAuthServiceImpl implements ConnectAuthService {
    private Logger logger = LoggerFactory.getLogger(ConnectAuthServiceImpl.class);
    private static final String CACHE_PREFIX_PASSPORTID_CONNECTUSERINFO = CacheConstant.CACHE_PREFIX_PASSPORTID_CONNECTUSERINFO;

    @Autowired
    private DBShardRedisUtils dbShardRedisUtils;

    @Override
    public OAuthAccessTokenResponse obtainAccessTokenByCode(int provider, String code, ConnectConfig connectConfig, OAuthConsumer oAuthConsumer,
                                                            String redirectUrl) throws IOException, OAuthProblemException {

        String appKey = connectConfig.getAppKey();
        String appSecret = connectConfig.getAppSecret();

        OAuthAuthzClientRequest request = OAuthAuthzClientRequest.tokenLocation(oAuthConsumer.getAccessTokenUrl())
                .setAppKey(appKey).setAppSecret(appSecret).setRedirectURI(redirectUrl).setCode(code)
                .setGrantType(GrantTypeEnum.AUTHORIZATION_CODE).buildBodyMessage(OAuthAuthzClientRequest.class);
        OAuthAccessTokenResponse oauthResponse;
        if (provider == AccountTypeEnum.QQ.getValue()) {
            oauthResponse = OAuthHttpClient.execute(request, HttpConstant.HttpMethod.POST, QQJSONAccessTokenResponse.class);
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
    public OAuthTokenVO refreshAccessToken(String refreshToken, ConnectConfig connectConfig) throws OAuthProblemException, IOException {
        int provider = connectConfig.getProvider();
        OAuthConsumer oAuthConsumer = OAuthConsumerFactory.getOAuthConsumer(provider);
        if (oAuthConsumer == null) {
            return null;
        }
        String appKey = connectConfig.getAppKey();
        String appSecret = connectConfig.getAppSecret();

        OAuthAuthzClientRequest request = OAuthAuthzClientRequest.tokenLocation(oAuthConsumer.getRefreshAccessTokenUrl())
                .setGrantType(GrantTypeEnum.REFRESH_TOKEN).setAppKey(appKey).setAppSecret(appSecret)
                .setRefreshToken(refreshToken).buildBodyMessage(OAuthAuthzClientRequest.class);
        OAuthAccessTokenResponse response;
        if (provider == AccountTypeEnum.QQ.getValue()) {
            response = OAuthHttpClient.execute(request, HttpConstant.HttpMethod.POST, QQJSONAccessTokenResponse.class);
        } else if (provider == AccountTypeEnum.RENREN.getValue()) {
            //renren刷新access_token接口，只允许POST方式
            response = OAuthHttpClient.execute(request, HttpConstant.HttpMethod.POST, RenrenJSONAccessTokenResponse.class);
        } else if (provider == AccountTypeEnum.BAIDU.getValue()) {
            response = OAuthHttpClient.execute(request, HttpConstant.HttpMethod.POST, BaiduJSONAccessTokenResponse.class);
        } else {
            throw new OAuthProblemException(ErrorUtil.UNSUPPORT_THIRDPARTY);
        }
        OAuthTokenVO oAuthTokenVO = response.getOAuthTokenVO();
        return oAuthTokenVO;
    }

    @Override
    public ConnectUserInfoVO obtainConnectUserInfo(int provider, ConnectConfig connectConfig, String openid, String accessToken,
                                                   OAuthConsumer oAuthConsumer) throws IOException, OAuthProblemException {
        String url = oAuthConsumer.getUserInfo();
        String appKey = connectConfig.getAppKey();
        ConnectUserInfoVO userProfileFromConnect = null;

        OAuthClientRequest request;
        UserAPIResponse response;
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
        } else if (provider == AccountTypeEnum.BAIDU.getValue()) {
            request = BaiduUserAPIRequest.apiLocation(url, BaiduUserAPIRequest.BaiduUserAPIBuilder.class)
                    .setAccessToken(accessToken).buildQueryMessage(BaiduUserAPIRequest.class);
            response = OAuthHttpClient.execute(request, BaiduUserAPIResponse.class);
        } else {
            throw new OAuthProblemException(ErrorUtil.UNSUPPORT_THIRDPARTY);
        }
        if (response != null) {
            userProfileFromConnect = response.toUserInfo();
        }
        return userProfileFromConnect;
    }

    @Override
    public boolean initialOrUpdateConnectUserInfo(String passportId, ConnectUserInfoVO connectUserInfoVO) throws ServiceException {
        try {
            String cacheKey = buildConnectUserInfoCacheKey(passportId);
            dbShardRedisUtils.setWithinSeconds(cacheKey, connectUserInfoVO, DateAndNumTimesConstant.TIME_ONEDAY);
            return true;
        } catch (Exception e) {
            logger.error("[ConnectToken] service method initialOrUpdateConnectUserInfo error.{}", e);
            return false;
        }
    }

    @Override
    public ConnectUserInfoVO obtainCachedConnectUserInfo(String userid) {
        try {
            String cacheKey = buildConnectUserInfoCacheKey(userid);
            ConnectUserInfoVO connectUserInfoVO = dbShardRedisUtils.getObject(cacheKey, ConnectUserInfoVO.class);
            return connectUserInfoVO;
        } catch (Exception e) {
            logger.error("[ConnectToken] service method obtainCachedConnectUserInfo error.{}", e);
            return null;
        }
    }

    private String buildConnectUserInfoCacheKey(String passportId) {
        return CACHE_PREFIX_PASSPORTID_CONNECTUSERINFO + passportId;
    }
}
