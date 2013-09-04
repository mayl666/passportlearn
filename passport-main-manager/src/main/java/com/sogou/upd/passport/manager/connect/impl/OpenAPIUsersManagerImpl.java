package com.sogou.upd.passport.manager.connect.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.HttpConstant;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.connect.OpenAPIUsersManager;
import com.sogou.upd.passport.model.OAuthConsumer;
import com.sogou.upd.passport.model.OAuthConsumerFactory;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.oauth2.openresource.http.OAuthHttpClient;
import com.sogou.upd.passport.oauth2.openresource.request.OAuthClientRequest;
import com.sogou.upd.passport.oauth2.openresource.request.user.QQUserAPIRequest;
import com.sogou.upd.passport.oauth2.openresource.request.user.RenrenUserAPIRequest;
import com.sogou.upd.passport.oauth2.openresource.request.user.SinaUserAPIRequest;
import com.sogou.upd.passport.oauth2.openresource.response.OAuthClientResponse;
import com.sogou.upd.passport.oauth2.openresource.response.user.QQUserAPIResponse;
import com.sogou.upd.passport.oauth2.openresource.response.user.RenrenUserAPIResponse;
import com.sogou.upd.passport.oauth2.openresource.response.user.SinaUserAPIResponse;
import com.sogou.upd.passport.service.account.AccountTokenService;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import com.sogou.upd.passport.service.connect.ConnectTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-22
 * Time: 上午12:59
 * To change this template use File | Settings | File Templates.
 */
@Component
public class OpenAPIUsersManagerImpl implements OpenAPIUsersManager {

    private static Logger log = LoggerFactory.getLogger(OpenAPIUsersManagerImpl.class);

    @Autowired
    private AccountTokenService accountTokenService;
    @Autowired
    private ConnectTokenService connectTokenService;
    @Autowired
    private ConnectConfigService connectConfigService;

    @Override
    public Result obtainOpenIdByPassportId(String passportId, int clientId, int provider) {
        Result result = new APIResultSupport(false);
        try {
            String appKey = connectConfigService.querySpecifyAppKey(clientId, provider);
            if (Strings.isNullOrEmpty(appKey)) {
                result.setCode(ErrorUtil.INVALID_CLIENTID);
                return result;
            }
            String openid = connectTokenService.querySpecifyOpenId(passportId, provider, appKey);
            if (Strings.isNullOrEmpty(openid)) {
                result.setCode(ErrorUtil.ERR_CODE_CONNECT_OBTAIN_OPENID_ERROR);
                return result;
            } else {
                result.setSuccess(true);
                result.setMessage("查询成功");
                result.setDefaultModel("openid", openid);
                return result;
            }
        } catch (ServiceException e) {
            log.error("get OpenId By PassportId fail, passportId:" + passportId + " clientId:" + clientId + " provider:" + provider, e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }

    }

    @Override
    public Result obtainUserInfo(String accessToken, int provider, ConnectConfig connectConfig) {
        Result result = new APIResultSupport(false);
        try {
            // 检查主账号access_token是否有效
            AccountToken accountToken = accountTokenService.verifyAccessToken(accessToken);
            if (accountToken == null) {
                result.setCode(ErrorUtil.ERR_ACCESS_TOKEN);
                return result;
            }
            String passportId = accountToken.getPassportId();

            String appKey = connectConfig.getAppKey();
            ConnectToken connectToken = connectTokenService.queryConnectToken(passportId, provider, appKey);

            String openid = connectToken.getOpenid();
            String connectAccessToken = connectToken.getAccessToken();

            OAuthConsumer oAuthConsumer = OAuthConsumerFactory.getOAuthConsumer(provider);
            String url = oAuthConsumer.getUserInfo();

            OAuthClientRequest request;
            OAuthClientResponse response;
            if (provider == AccountTypeEnum.QQ.getValue()) {
                request = QQUserAPIRequest.apiLocation(url, QQUserAPIRequest.QQUserAPIBuilder.class).setOauth_Consumer_Key(appKey)
                        .setOpenid(openid).setAccessToken(connectAccessToken).buildQueryMessage(QQUserAPIRequest.class);

                response = OAuthHttpClient.execute(request, QQUserAPIResponse.class);

            } else if (provider == AccountTypeEnum.SINA.getValue()) {
                request = SinaUserAPIRequest.apiLocation(url, SinaUserAPIRequest.SinaUserAPIBuilder.class).setUid(openid)
                        .setAccessToken(connectAccessToken).buildQueryMessage(SinaUserAPIRequest.class);

                response = OAuthHttpClient.execute(request, SinaUserAPIResponse.class);

            } else if (provider == AccountTypeEnum.RENREN.getValue()) {
                request = RenrenUserAPIRequest.apiLocation(url, RenrenUserAPIRequest.RenrenUserAPIBuilder.class).setUserId(openid).setAccessToken(connectAccessToken)
                        .buildBodyMessage(RenrenUserAPIRequest.class);

                response = OAuthHttpClient.execute(request, HttpConstant.HttpMethod.POST, RenrenUserAPIResponse.class);
            } else {
                result.setCode(ErrorUtil.UNSUPPORT_THIRDPARTY);
                return result;
            }
            log.info("[ConnectUserInfo] Provider:" + provider + " Openid:" + openid + " Response:"
                    + response.getBody());
            result.setSuccess(true);
            result.setMessage("获取第三方个人资料成功");
            result.setDefaultModel("userinfo", response.getBody());
            return result;
        } catch (Exception e) {
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

}
