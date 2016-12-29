package com.sogou.upd.passport.manager.api.config.form;

import com.google.common.base.Objects;

import com.sogou.upd.passport.manager.api.BaseApiParams;

import javax.validation.constraints.Min;

/**
 * 同步删除应用参数
 */
public class AppDeleteSyncApiParams extends BaseApiParams {
    @Min(value = 10000, message = "应用 id 错误")
    private int appId;
    
    public int getAppId() {
        return appId;
    }
    
    public void setAppId(int appId) {
        this.appId = appId;
    }
    
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("appId", appId)
                .toString();
    }
}
