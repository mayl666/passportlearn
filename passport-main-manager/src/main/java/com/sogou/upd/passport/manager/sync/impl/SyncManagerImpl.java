package com.sogou.upd.passport.manager.sync.impl;

import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.config.form.AppSyncApiParams;
import com.sogou.upd.passport.manager.sync.SyncManager;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.app.AppConfigService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 同步 manager 实现类
 */
@Component
public class SyncManagerImpl implements SyncManager {
    private static final Logger logger = LoggerFactory.getLogger(SyncManagerImpl.class);
    
    @Autowired
    private AppConfigService appConfigService;

    public Result addApp(AppSyncApiParams appSyncApiParams) {
        Result result = new APIResultSupport(false);
        
        int clientId = appSyncApiParams.getAppId();
        String clientName = appSyncApiParams.getAppName();
        String serverSecret = appSyncApiParams.getServerSecret();
        String clientSecret = appSyncApiParams.getClientSecret();

        try {
            AppConfig existsAppConfig = appConfigService.queryAppConfigByClientId(clientId);
            if(existsAppConfig != null) {   // 应用已存在
                result.setCode(ErrorUtil.ERR_CODE_SYNC_APP_EXISTS);
                return result;
            }
            
            AppConfig appConfig = new AppConfig();
            appConfig.setClientId(clientId);
            appConfig.setClientName(clientName);
            appConfig.setServerSecret(serverSecret);
            appConfig.setClientSecret(clientSecret);
    
            boolean insertResult = appConfigService.insertAppConfig(clientId, clientName, serverSecret, clientSecret);
            
            if(!insertResult) {
                result.setCode(ErrorUtil.ERR_CODE_SYNC_APP_ADD_FAILED);
                return result;
            }
            
            result.setSuccess(true);
            result.setMessage("同步添加应用成功");
            return result;
        } catch (Exception e) {
            logger.error("同步添加应用失败", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }
    
    public Result updateApp(AppSyncApiParams appSyncApiParams) {
        Result result = new APIResultSupport(false);
    
        int clientId = appSyncApiParams.getAppId();
        String clientName = appSyncApiParams.getAppName();
    
        try {
            AppConfig existsAppConfig = appConfigService.queryAppConfigByClientId(clientId);
            if(existsAppConfig == null) {   // 应用已存在
                result.setCode(ErrorUtil.ERR_CODE_SYNC_APP_NOT_EXISTS);
                return result;
            }
        
            AppConfig appConfig = new AppConfig();
            appConfig.setClientId(clientId);
            appConfig.setClientName(clientName);
        
            boolean updateResult = appConfigService.updateAppConfigName(clientId, clientName);
        
            if(!updateResult) {
                result.setCode(ErrorUtil.ERR_CODE_SYNC_APP_UPDATE_FAILED);
                return result;
            }
        
            result.setSuccess(true);
            result.setMessage("同步修改应用成功");
            return result;
        } catch (Exception e) {
            logger.error("同步修改应用失败", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }
    
    public Result deleteApp(int clientId) {
        Result result = new APIResultSupport(false);
    
        try {
            AppConfig existsAppConfig = appConfigService.queryAppConfigByClientId(clientId);
            if(existsAppConfig == null) {   // 应用已存在
                result.setCode(ErrorUtil.ERR_CODE_SYNC_APP_NOT_EXISTS);
                return result;
            }
        
            boolean deleteResult = appConfigService.deleteAppConfig(clientId);
        
            if(!deleteResult) {
                result.setCode(ErrorUtil.ERR_CODE_SYNC_APP_DELETE_FAILED);
                return result;
            }
        
            result.setSuccess(true);
            result.setMessage("同步删除应用成功");
            return result;
        } catch (Exception e) {
            logger.error("同步删除应用失败", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

}
