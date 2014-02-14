package com.sogou.upd.passport.manager.api.connect.impl;

import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.CommonHelper;
import com.sogou.upd.passport.common.parameter.AccountStatusEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.api.connect.ConnectApiManager;
import com.sogou.upd.passport.manager.api.connect.form.BaseOpenApiParams;
import com.sogou.upd.passport.manager.form.connect.ConnectLoginParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.model.connect.ConnectRelation;
import com.sogou.upd.passport.model.connect.ConnectToken;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.openresource.vo.OAuthTokenVO;
import com.sogou.upd.passport.service.app.ConnectConfigService;
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
@Component("connectApiManager")
public class ConnectApiManagerImpl implements ConnectApiManager {

    private static final Logger logger = LoggerFactory.getLogger(ConnectApiManagerImpl.class);

    @Autowired
    private ConnectApiManager proxyConnectApiManager;
    @Autowired
    private ConnectApiManager sgConnectApiManager;
    @Autowired
    private ConnectConfigService connectConfigService;
    @Autowired
    private RedisUtils redisUtils;


    @Override
    public String buildConnectLoginURL(ConnectLoginParams connectLoginParams, String uuid, int provider, String ip, String httpOrHttps) throws OAuthProblemException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * 创建第三方账号同时第三方个人资料写搜狗缓存
     *
     * @param appKey
     * @param provider
     * @param oAuthTokenVO
     * @return
     */
    @Override
    public Result buildConnectAccount(String appKey, int provider, OAuthTokenVO oAuthTokenVO, boolean isQueryConnectRelation) {
        Result result = new APIResultSupport(false);
        try {
            result = sgConnectApiManager.buildConnectAccount(appKey, provider, oAuthTokenVO, false);
            if (result.isSuccess()) {
                result = proxyConnectApiManager.buildConnectAccount(appKey, provider, oAuthTokenVO, false);
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
     * 此方法功能有以下三点：
     * 1.获取token信息(双查SG、SH)
     * 2.SH有，SG无的情况下，重新写SG DB
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
//            if (!tokenResult.isSuccess()) {         //用于测试
//                tokenResult = proxyConnectApiManager.obtainConnectToken(baseOpenApiParams, clientId, clientKey);
//                //如果sohu有此用户token信息,写SG库
//                if (tokenResult.isSuccess()) {
//                    Map<String, String> accessTokenMap = (Map<String, String>) tokenResult.getModels().get("result");
//                    String openId = accessTokenMap.get("open_id").toString();
//                    String accessToken = accessTokenMap.get("access_token").toString();
//                    long expiresIn = Long.parseLong(String.valueOf(accessTokenMap.get("expires_in")));
//                    String passportId = baseOpenApiParams.getUserid();
//                    int provider = AccountTypeEnum.getAccountType(passportId).getValue();
//                    ConnectConfig connectConfig = connectConfigService.queryConnectConfig(clientId, provider);
//                    if (connectConfig == null) {
//                        result.setCode(ErrorUtil.ERR_CODE_CONNECT_CLIENTID_PROVIDER_NOT_FOUND);
//                        return result;
//                    }
//                    OAuthTokenVO oAuthTokenVO = new OAuthTokenVO(accessToken, expiresIn, null);
//                    oAuthTokenVO.setOpenid(openId);
//                    //写SG DB，其中需要查connect_relation表,最后一项参数值为true
//                    result = sgConnectApiManager.buildConnectAccount(connectConfig.getAppKey(), provider, oAuthTokenVO, true);
//                } else {
//                    result = tokenResult;
//                }
//            } else {
                result = tokenResult;
//            }
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
    public void testEstimatedCapacity(String openId, String passportId) {
        try {
//            String passportId = "fdsjfjeiur@sogou.com";
            int provider = 3;
            String appKey = "100294784";
//            String openId = "TGF81A5T13A94663D83FEC36AC117933";
            String accessToken = "0CD2495BB6UJ5EDE1D5D97D02E2809B1";
            String refreshToken = "33B7D25DA4F5TGD9F5DB7B4EE9136E67";
            //创建Account对象
            Account account = new Account();
            account.setPassportId(passportId);
            account.setRegTime(new Date());
            account.setRegIp("127.0.0.1");
            account.setAccountType(3);
            account.setFlag(AccountStatusEnum.REGULAR.getValue());
            account.setPasswordtype(Account.NO_PASSWORD);
            account.setMobile("13526525896");
            String accountCacheKey = CacheConstant.CACHE_PREFIX_PASSPORT_ACCOUNT + passportId;
            redisUtils.set(accountCacheKey, account);
            //创建ConnectToken对象
            ConnectToken connectToken = new ConnectToken();
            connectToken.setPassportId(passportId);
            connectToken.setAppKey(appKey);
            connectToken.setProvider(provider);
            connectToken.setOpenid(openId);
            connectToken.setAccessToken(accessToken);
            connectToken.setExpiresIn(7776000);
            connectToken.setRefreshToken(refreshToken);
            connectToken.setUpdateTime(new Date());
            String tokenCacheKey = CacheConstant.CACHE_PREFIX_PASSPORTID_CONNECTTOKEN + passportId + "_" + provider + "_" + appKey;
            redisUtils.set(tokenCacheKey, connectToken);
            //创建ConnectRelation对象
            ConnectRelation connectRelation = new ConnectRelation();
            connectRelation.setAppKey(appKey);
            connectRelation.setOpenid(openId);
            connectRelation.setPassportId(passportId);
            connectRelation.setProvider(provider);
            String relationCacheKey = CacheConstant.CACHE_PREFIX_OPENID_CONNECTRELATION + openId + "_" + provider;
            redisUtils.hPut(relationCacheKey, appKey, connectRelation);
        } catch (Exception e) {
            logger.error("set redis object error.{}", e);
        }

    }


}
