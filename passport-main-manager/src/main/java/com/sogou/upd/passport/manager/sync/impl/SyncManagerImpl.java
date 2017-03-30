package com.sogou.upd.passport.manager.sync.impl;

import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.config.form.AppAddSyncApiParams;
import com.sogou.upd.passport.manager.api.config.form.AppUpdateSyncApiParams;
import com.sogou.upd.passport.manager.api.config.form.ThirdAddSyncApiParams;
import com.sogou.upd.passport.manager.api.config.form.ThirdUpdateSyncApiParams;
import com.sogou.upd.passport.manager.sync.SyncManager;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.app.AppConfigService;
import com.sogou.upd.passport.service.app.ConnectConfigService;

import org.apache.commons.lang.StringUtils;
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
    @Autowired
    private ConnectConfigService connectConfigService;

    @Override
    public Result addApp(AppAddSyncApiParams appAddSyncApiParams) {
        Result result = new APIResultSupport(false);

        int appId = appAddSyncApiParams.getAppId();
        String appName = appAddSyncApiParams.getAppName();
        String serverSecret = appAddSyncApiParams.getServerSecret();
        String clientSecret = appAddSyncApiParams.getClientSecret();

        try {
            AppConfig existsAppConfig = appConfigService.queryAppConfigByClientId(appId);
            if(existsAppConfig != null) {   // 应用已存在
                result.setCode(ErrorUtil.ERR_CODE_SYNC_APP_EXISTS);
                return result;
            }

            AppConfig appConfig = new AppConfig();
            appConfig.setClientId(appId);
            appConfig.setClientName(appName);
            appConfig.setServerSecret(serverSecret);
            appConfig.setClientSecret(clientSecret);

            boolean insertResult = appConfigService.insertAppConfig(appId, appName, serverSecret, clientSecret);

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

    @Override
    public Result updateApp(AppUpdateSyncApiParams appUpdateSyncApiParams) {
        Result result = new APIResultSupport(false);

        int appId = appUpdateSyncApiParams.getAppId();
        String appName = appUpdateSyncApiParams.getAppName();
        if(StringUtils.isBlank(appName)) {
            result.setCode(ErrorUtil.ERR_CODE_SYNC_APP_UPDATE_FAILED);
            return result;
        }

        try {
            AppConfig existsAppConfig = appConfigService.queryAppConfigByClientId(appId);
            if(existsAppConfig == null) {   // 应用不存在
                result.setCode(ErrorUtil.ERR_CODE_SYNC_APP_NOT_EXISTS);
                return result;
            }

            boolean updateResult = appConfigService.updateAppConfigName(appId, appName);

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

    @Override
    public Result deleteApp(int appId) {
        Result result = new APIResultSupport(false);

        try {
            AppConfig existsAppConfig = appConfigService.queryAppConfigByClientId(appId);
            if(existsAppConfig == null) {   // 应用不存在
                result.setCode(ErrorUtil.ERR_CODE_SYNC_APP_NOT_EXISTS);
                return result;
            }

            boolean deleteResult = appConfigService.deleteAppConfig(appId);

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

    @Override
    public Result addThird(ThirdAddSyncApiParams thirdAddSyncApiParams) {
        Result result = new APIResultSupport(false);

        int appId = thirdAddSyncApiParams.getAppId();
        int provider = thirdAddSyncApiParams.getProvider();
        String appKey = thirdAddSyncApiParams.getAppKey();
        String appSecret = thirdAddSyncApiParams.getAppSecret();
        String scope = thirdAddSyncApiParams.getScope();

        try {
            AppConfig existsAppConfig = appConfigService.queryAppConfigByClientId(appId);
            if(existsAppConfig == null) {   // 应用不存在
                result.setCode(ErrorUtil.ERR_CODE_SYNC_APP_NOT_EXISTS);
                return result;
            }

            boolean insertResult = connectConfigService.insertConnectConfig(appId, provider, appKey, appSecret, scope);

            if(!insertResult) {
                result.setCode(ErrorUtil.ERR_CODE_SYNC_THIRD_ADD_FAILED);
                return result;
            }

            result.setSuccess(true);
            result.setMessage("同步添加第三方成功");
            return result;
        } catch (Exception e) {
            logger.error("同步添加第三方失败", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    @Override
    public Result deleteThird(ThirdUpdateSyncApiParams thirdUpdateSyncApiParams) {
        Result result = new APIResultSupport(false);

        int appId = thirdUpdateSyncApiParams.getAppId();
        int provider = thirdUpdateSyncApiParams.getProvider();
        String appKey = thirdUpdateSyncApiParams.getAppKey();

        try {
            AppConfig existsAppConfig = appConfigService.queryAppConfigByClientId(appId);
            if(existsAppConfig == null) {   // 应用不存在
                result.setCode(ErrorUtil.ERR_CODE_SYNC_APP_NOT_EXISTS);
                return result;
            }

            boolean deleteResult = connectConfigService.deleteConnectConfig(appId, provider, appKey);

            if(!deleteResult) {
                result.setCode(ErrorUtil.ERR_CODE_SYNC_THIRD_DELETE_FAILED);
                return result;
            }

            result.setSuccess(true);
            result.setMessage("同步删除第三方成功");
            return result;
        } catch (Exception e) {
            logger.error("同步删除第三方失败", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }
}
