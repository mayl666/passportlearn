package com.sogou.upd.passport.manager.sync;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.config.form.AppAddSyncApiParams;
import com.sogou.upd.passport.manager.api.config.form.AppUpdateSyncApiParams;
import com.sogou.upd.passport.manager.api.config.form.ThirdAddSyncApiParams;
import com.sogou.upd.passport.manager.api.config.form.ThirdUpdateSyncApiParams;

/**
 * 同步 manager
 */
public interface SyncManager {
    /**
     * 新增应用配置
     *
     * @param appAddSyncApiParams 需要新增的应用
     */
    public Result addApp(AppAddSyncApiParams appAddSyncApiParams);

    /**
     * 修改应用配置
     *
     * @param appUpdateSyncApiParams 需要修改的应用
     */
    public Result updateApp(AppUpdateSyncApiParams appUpdateSyncApiParams);

    /**
     * 删除应用配置
     *
     * @param clientId 需要删除的应用
     */
    public Result deleteApp(int clientId);

    /**
     * 新增第三方配置
     *
     * @param thirdAddSyncApiParams 需要新增的第三方信息
     */
    public Result addThird(ThirdAddSyncApiParams thirdAddSyncApiParams);

    /**
     * 删除第三方配置
     *
     * @param thirdUpdateSyncApiParams 需要删除的第三方信息
     */
    public Result deleteThird(ThirdUpdateSyncApiParams thirdUpdateSyncApiParams);
}
