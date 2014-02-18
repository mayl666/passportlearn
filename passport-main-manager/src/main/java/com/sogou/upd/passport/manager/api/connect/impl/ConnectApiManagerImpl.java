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
    public Result buildConnectAccount(String appKey, int provider, OAuthTokenVO oAuthTokenVO) {
        Result result = new APIResultSupport(false);
        try {
            result = sgConnectApiManager.buildConnectAccount(appKey, provider, oAuthTokenVO);
            if (result.isSuccess()) {
                result = proxyConnectApiManager.buildConnectAccount(appKey, provider, oAuthTokenVO);
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
            if (!tokenResult.isSuccess()) {
                tokenResult = proxyConnectApiManager.obtainConnectToken(baseOpenApiParams, clientId, clientKey);
                //如果sohu有此用户token信息,写SG库
                if (tokenResult.isSuccess()) {
                    Map<String, String> accessTokenMap = (Map<String, String>) tokenResult.getModels().get("result");
                    String openId = accessTokenMap.get("open_id").toString();
                    String accessToken = accessTokenMap.get("access_token").toString();
                    long expiresIn = Long.parseLong(String.valueOf(accessTokenMap.get("expires_in")));
                    String passportId = baseOpenApiParams.getUserid();
                    int provider = AccountTypeEnum.getAccountType(passportId).getValue();
                    ConnectConfig connectConfig = connectConfigService.queryConnectConfig(clientId, provider);
                    if (connectConfig == null) {
                        result.setCode(ErrorUtil.ERR_CODE_CONNECT_CLIENTID_PROVIDER_NOT_FOUND);
                        return result;
                    }
                    OAuthTokenVO oAuthTokenVO = new OAuthTokenVO(accessToken, expiresIn, null);
                    oAuthTokenVO.setOpenid(openId);
                    //写SG DB，其中需要查connect_relation表,最后一项参数值为true
                    result = sgConnectApiManager.buildConnectAccount(connectConfig.getAppKey(), provider, oAuthTokenVO);
                } else {
                    result = tokenResult;
                }
            } else {
                result = tokenResult;
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

}
