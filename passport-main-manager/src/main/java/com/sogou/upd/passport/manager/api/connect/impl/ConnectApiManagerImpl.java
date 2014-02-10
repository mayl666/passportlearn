package com.sogou.upd.passport.manager.api.connect.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import com.sogou.upd.passport.manager.form.connect.ConnectLoginParams;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.vo.ConnectUserInfoVO;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import com.sogou.upd.passport.service.connect.ConnectAuthService;
import com.sogou.upd.passport.service.connect.ConnectTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-2-7
 * Time: 上午11:15
 * To change this template use File | Settings | File Templates.
 */
@Component("connectTokenApiManager")
public class ConnectApiManagerImpl implements ConnectApiManager {

    private static final Logger logger = LoggerFactory.getLogger(ConnectApiManagerImpl.class);

    @Autowired
    private ConnectApiManager proxyConnectApiManager;
    @Autowired
    private ConnectApiManager sgConnectApiManager;
    @Autowired
    private ConnectAuthService connectAuthService;
    @Autowired
    private ConnectTokenService connectTokenService;


    @Override
    public String buildConnectLoginURL(ConnectLoginParams connectLoginParams, String uuid, int provider, String ip, String httpOrHttps) throws OAuthProblemException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public Result buildConnectAccount(String appKey, String providerStr, OAuthTokenVO oAuthTokenVO) {
        Result result = new APIResultSupport(false);
        try {
            result = sgConnectApiManager.buildConnectAccount(appKey, providerStr, oAuthTokenVO);
            if (result.isSuccess()) {
                result = proxyConnectApiManager.buildConnectAccount(appKey, providerStr, oAuthTokenVO);
                if (!result.isSuccess()) {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
                    return result;
                }
            } else {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
                return result;
            }
        } catch (Exception e) {
            logger.error("[ConnectToken] manager method buildConnectAccount error.{}", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;  //To change body of implemented methods use File | Settings | File Templates.
    }


    /**
     * 此方法功能有以下三点：1.获取token信息  2.判断token是否过期，是否需要refreshToken刷新    3.没有过期或access_Token刷新成功后，写SG DB或access_token双写
     *
     * @param baseOpenApiParams 调用sohu接口参数类
     * @param clientId
     * @param clientKey
     * @return
     */
    @Override
    public Result obtainConnectToken(BaseOpenApiParams baseOpenApiParams, int clientId, String clientKey) {
        Result result = new APIResultSupport(false);
        try {
            Result tokenResult;
            //先查SG方有无此用户token信息,其中实现是先缓存再搜狗数据库
            tokenResult = sgConnectApiManager.obtainConnectToken(baseOpenApiParams, clientId, clientKey);
            ConnectToken connectToken;
            if (tokenResult.isSuccess()) {
                connectToken = (ConnectToken) tokenResult.getModels().get("connectToken");
                if (!isValidToken(connectToken.getCreateTime(), connectToken.getExpiresIn())) {
                    String refreshToken = connectToken.getRefreshToken();
                    if (!Strings.isNullOrEmpty(refreshToken)) {  //如果refreshToken不为空，则刷新token
                        //todo refreshToken刷新accessToken
//                        connectToken = connectAuthService.refreshAccessToken();
                        //todo 如果SG库中有token信息，但是过期了，此时使用refreshToken刷新成功了，这时要双写搜狗、搜狐数据库
                        result.setSuccess(true);
                        result.setDefaultModel("connectToken", connectToken);
                    } else {
                        result.setCode(ErrorUtil.ERR_CODE_CONNECT_REFRESHTOKEN_NOT_EXIST);
                        return result;
                    }
                }
            } else {
                tokenResult = proxyConnectApiManager.obtainConnectToken(baseOpenApiParams, clientId, clientKey);
                //获取accessToken成功
                if (tokenResult.isSuccess()) {
                    //如果sohu有此用户token信息
                    if (tokenResult.isSuccess()) {
                        Map<String, String> accessTokenMap = (Map<String, String>) tokenResult.getModels().get("result");
                        String openId = accessTokenMap.get("open_id").toString();
                        String accessToken = accessTokenMap.get("access_token").toString();
                        long expiresIn = Long.parseLong(accessTokenMap.get("expires_in").toString());
                        //写SG DB及缓存
                        ConnectToken tokenInfo = new ConnectToken();
                        tokenInfo.setOpenid(openId);
                        tokenInfo.setAccessToken(accessToken);
                        tokenInfo.setExpiresIn(expiresIn);
                        boolean isInitialSuccess = connectTokenService.initialConnectToken(tokenInfo);
                        if (isInitialSuccess) {
                            result.setDefaultModel("connectToken", tokenInfo);
                            result.setSuccess(true);
                        }
                    } else {
                        result = tokenResult;
                        return result;
                    }
                }
            }
        } catch (ServiceException se) {
            logger.error("ServiceException:", se);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        } catch (Exception e) {
            logger.error("Method[obtainConnectToken]: Obtain Connect Token Error!", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    @Override
    public Result insertOrUpdateConnectToken(int clientId, int provider, String passportId, OAuthTokenVO oAuthTokenVO) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    /**
     * 验证Token是否失效,返回true表示有效，false表示过期
     */
    private boolean isValidToken(Date createTime, long expiresIn) {
        long currentTime = System.currentTimeMillis() / (1000);
        long tokenTime = createTime.getTime() / (1000);
        return currentTime < tokenTime + expiresIn;
    }
}
