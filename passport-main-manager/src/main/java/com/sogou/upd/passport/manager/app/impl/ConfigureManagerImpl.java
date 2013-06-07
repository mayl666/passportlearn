package com.sogou.upd.passport.manager.app.impl;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.proxy.SHPPUrlConstant;
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

    private static final long API_REQUEST_VAILD_TERM = 5 * 60 * 1000; //接口请求的有效期为5分钟，单位为秒

    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private ConnectConfigService connectConfigService;

    @Override
    public boolean verifyClientVaild(int clientId, String clientSecret) {
        try {
            boolean resultFlag = appConfigService.verifyClientVaild(clientId, clientSecret);
            return resultFlag;
        } catch (ServiceException e) {
            log.error("Verify ClientVaild Fail:", e);
            return false;
        }
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

    @Override
    public Result verifyInternalRequest(int clientId, String passportId, long ct, String originalCode) {
        Result result = new APIResultSupport(false);
        try {
            AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
            String secret = appConfig.getServerSecret();
            String code = ManagerHelper.generatorCode(passportId, clientId, secret, ct);
            long currentTime = System.currentTimeMillis();
            if (code.equals(originalCode) && ct > currentTime - API_REQUEST_VAILD_TERM) {
                result.setSuccess(true);
            } else {
                result.setCode(ErrorUtil.ERR_CODE_COM_SING);
            }
        } catch (Exception e) {
            log.error("Verify Code And Ct Error!", e);
            result.setCode(ErrorUtil.ERR_CODE_COM_SING);
        }
        return result;
    }

}
