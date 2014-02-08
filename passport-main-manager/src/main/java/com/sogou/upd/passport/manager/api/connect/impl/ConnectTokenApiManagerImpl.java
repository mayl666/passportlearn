package com.sogou.upd.passport.manager.api.connect.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import com.sogou.upd.passport.manager.form.connect.ConnectLoginParams;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.response.accesstoken.OAuthAccessTokenResponse;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import com.sogou.upd.passport.service.connect.ConnectAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-2-7
 * Time: 上午11:15
 * To change this template use File | Settings | File Templates.
 */
@Component("connectTokenApiManager")
public class ConnectTokenApiManagerImpl implements ConnectApiManager {

    private static final Logger logger = LoggerFactory.getLogger(ConnectTokenApiManagerImpl.class);

    @Autowired
    private ConnectApiManager proxyConnectApiManager;
    @Autowired
    private ConnectApiManager sgConnectApiManager;
    @Autowired
    private ConnectAuthService connectAuthService;

    @Override
    public String buildConnectLoginURL(ConnectLoginParams connectLoginParams, String uuid, int provider, String ip, String httpOrHttps) throws OAuthProblemException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result buildConnectAccount(String providerStr, OAuthTokenVO oAuthTokenVO) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result obtainConnectToken(BaseOpenApiParams baseOpenApiParams, int clientId, String clientKey) {
        Result result = new APIResultSupport(false);
        try {
            Result tokenResult;
            //先查SG方有无此用户token信息,其中实现是先缓存再搜狗数据库
            tokenResult = sgConnectApiManager.obtainConnectToken(baseOpenApiParams, clientId, clientKey);
            ConnectToken connectToken = (ConnectToken) tokenResult.getModels().get("connectToken");
            //如果SG方无此用户token信息,查SH方有无此用户token信息
            if (connectToken == null) {
                tokenResult = proxyConnectApiManager.obtainConnectToken(baseOpenApiParams, clientId, clientKey);
                //如果sohu有此用户token信息
                if (tokenResult.isSuccess()) {
                    Map<String, String> accessTokenMap = (Map<String, String>) tokenResult.getModels().get("result");
                    String openId = accessTokenMap.get("open_id").toString();
                    String accessToken = accessTokenMap.get("access_token").toString();
                    long expiresIn = Long.parseLong(accessTokenMap.get("expires_in").toString());
                    ConnectToken tokenInfo = new ConnectToken();
                    tokenInfo.setOpenid(openId);
                    tokenInfo.setAccessToken(accessToken);
                    tokenInfo.setExpiresIn(expiresIn);
                    result.setDefaultModel("connectToken", tokenInfo);
                    result.setSuccess(true);
                    return result;
                }
            } else {
                //如果SG方有此用户token信息，则判断access_token是否过期
                long expiresIn = connectToken.getExpiresIn();
                //如果token过期,根据refreshToken刷新
                if (!isValidToken(expiresIn)) {
                    String refreshToken = connectToken.getRefreshToken();
                    //如果refreshToken不为空，则刷新
                    if (!Strings.isNullOrEmpty(refreshToken)) {
                        OAuthAccessTokenResponse oAuthAccessTokenResponse = connectAuthService.refreshAccessToken(0, null, refreshToken);
                    }
                }
                result.setDefaultModel("connectToken", connectToken);
                result.setSuccess(true);
                return result;
            }
        } catch (Exception e) {
            logger.error("Method[obtainConnectToken]: Obtain Connect Token Error!", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    @Override
    public Result handleConnectToken(BaseOpenApiParams baseOpenApiParams, int clientId, String clientKey) {
        //1.获取token信息
        Result result = obtainConnectToken(baseOpenApiParams, clientId, clientKey);
        //2.判断token是否过期，是否需要refreshToken刷新

        //3.access_Token刷新成功后，access_token要双写搜狗和搜狐数据库
        return result;
    }

    /**
     * 验证Token是否失效
     */
    private boolean isValidToken(long tokenValidTime) {
        long currentTime = System.currentTimeMillis();
        return tokenValidTime > currentTime;
    }
}
