package com.sogou.upd.passport.manager.app.impl;

import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.model.app.ConnectConfig;
import com.sogou.upd.passport.service.app.AppConfigService;
import com.sogou.upd.passport.service.app.ConnectConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User: mayan
 * Date: 13-4-16
 * Time: 下午4:49
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ConfigureManagerImpl implements ConfigureManager {

    private static Logger log = LoggerFactory.getLogger(ConfigureManagerImpl.class);

    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private ConnectConfigService connectConfigService;

    @Override
    public AppConfig verifyClientVaild(int clientId, String clientSecret) {
        return appConfigService.verifyClientVaild(clientId, clientSecret);
    }

    @Override
    public ConnectConfig obtainConnectConfig(int clientId, int provider) {
        ConnectConfig connectConfig = null;
        try {
            connectConfig = connectConfigService.querySpecifyConnectConfig(clientId, provider);
        } catch (ServiceException e) {
            log.error("Obtain ConnectConfig Fail:", e);
        }
        return connectConfig;
    }

    @Override
    public boolean checkAppIsExist(int clientId) {
        AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
        if (appConfig != null) {
            return true;
        }
        return false;
    }

}
