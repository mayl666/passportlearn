package com.sogou.upd.passport.manager.app.impl;

import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.app.AppConfigService;
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

    @Autowired
    private AppConfigService appConfigService;

    @Override
    public boolean checkAppIsExist(int clientId) {
        AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
        if (appConfig != null) {
            return true;
        }
        return false;
    }

}
