package com.sogou.upd.passport.manager.api.connect.impl;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.connect.ConnectProxyOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.QQProxyOpenApiManager;
import com.sogou.upd.passport.manager.api.connect.form.proxy.ConnectProxyOpenApiParams;
import com.sogou.upd.passport.manager.api.connect.form.proxy.OpenApiParams;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.model.app.ConnectConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-1-4
 * Time: 下午4:40
 * To change this template use File | Settings | File Templates.
 */
@Component("connectProxyOpenApiManager")
public class ConnectProxyOpenApiManagerImpl extends BaseProxyManager implements ConnectProxyOpenApiManager {

    private static final Logger logger = LoggerFactory.getLogger(ConnectProxyOpenApiManagerImpl.class);

    @Autowired
    private ConfigureManager configureManager;
    @Autowired
    private QQProxyOpenApiManager qqProxyOpenApiManager;

    @Override
    public Result handleConnectOpenApi(String openId, String accessToken, String providerStr, String interfaceName, ConnectProxyOpenApiParams params) {
        Result result = new APIResultSupport(false);
        try {
            //获取搜狗在第三方开放平台的appkey和appsecret
            ConnectConfig connectConfig = configureManager.obtainConnectConfig(CommonConstant.SGPP_DEFAULT_CLIENTID, AccountTypeEnum.getProvider(providerStr));
            String sgAppKey = connectConfig.getAppKey();
            String sgAppSecret = connectConfig.getAppSecret();
            OpenApiParams openApiParams = new OpenApiParams(openId, accessToken, providerStr, interfaceName, sgAppKey, sgAppSecret, null);
            if (AccountTypeEnum.getProvider(providerStr) == AccountTypeEnum.QQ.getValue()) {
                result = qqProxyOpenApiManager.executeQQOpenApi(openApiParams, params);
            } else if (AccountTypeEnum.getProvider(providerStr) == AccountTypeEnum.SINA.getValue()) {
            }
        } catch (ServiceException e) {
            logger.error("Service Method querySpecifyConnectConfig error.{}", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        } catch (Exception e) {
            logger.error("handleConnectOpenApi Is Failed:openId is " + openId, e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

}
