package com.sogou.upd.passport.service.app.impl;

import com.sogou.upd.passport.dao.app.AppConfigMapper;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-3-26
 * Time: 下午8:22
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AppConfigServiceImpl implements AppConfigService {

    private Logger logger = LoggerFactory.getLogger(AppConfigService.class);

    @Override
    public boolean verifyClientVaild(String clientId, String clientSecret) {
        try {
            int client_id = Integer.parseInt(clientId);
            AppConfig appConfig = getAppConfig(client_id);
            // TODO 如果不存在返回的是null还是new AppConfig？
            if (appConfig == null) {
                return false;
            } else if (!clientSecret.equals(appConfig.getClientSecret())) {
                return false;
            }
        } catch (NumberFormatException e) {
            logger.error("{} is not Number", clientId);
            return false;
        }
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Inject
    private AppConfigMapper appConfigMapper;

    @Override
    public long getMaxClientId() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public AppConfig regApp(AppConfig app) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public AppConfig getAppConfig(int clientId) {
        AppConfig appConfig = null;
        if(clientId != 0){
            appConfig = appConfigMapper.getAppConfigByClientId(clientId);
            return appConfig == null ? null : appConfig;
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getAccessTokenExpiresIn(int clientId) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getRefreshTokenExpiresIn(int clientId) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
