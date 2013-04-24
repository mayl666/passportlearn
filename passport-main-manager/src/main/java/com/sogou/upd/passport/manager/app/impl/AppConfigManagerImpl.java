package com.sogou.upd.passport.manager.app.impl;

import com.sogou.upd.passport.common.exception.ServiceException;
import com.sogou.upd.passport.manager.app.AppConfigManager;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * User: mayan
 * Date: 13-4-16
 * Time: 下午4:49
 * To change this template use File | Settings | File Templates.
 */
@Component
public class AppConfigManagerImpl implements AppConfigManager {

    @Inject
    private AppConfigService appConfigService;

    @Override
    public boolean verifyClientVaild(int clientId, String clientSecret) {
        try {
            boolean resultFlag = appConfigService.verifyClientVaild(clientId, clientSecret);
            return resultFlag;
        } catch (ServiceException e) {
            return false;
        }
    }
}
