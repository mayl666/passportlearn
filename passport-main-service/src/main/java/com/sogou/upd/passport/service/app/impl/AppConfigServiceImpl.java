package com.sogou.upd.passport.service.app.impl;

import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-3-26
 * Time: 下午8:22
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AppConfigServiceImpl implements AppConfigService{

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
