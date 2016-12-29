package com.sogou.upd.passport.manager.sync;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.config.form.AppSyncApiParams;

/**
 * 同步 manager
 */
public interface SyncManager {
    /**
     * 新增应用配置
     *
     * @param appSyncApiParams 需要新增的应用
     */
    public Result addApp(AppSyncApiParams appSyncApiParams);
    
    /**
     * 修改应用配置
     *
     * @param appSyncApiParams 需要修改的应用
     */
    public Result updateApp(AppSyncApiParams appSyncApiParams);
    
    /**
     * 删除应用配置
     *
     * @param clientId 需要删除的应用
     */
    public Result deleteApp(int clientId);
}
