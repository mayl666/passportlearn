package com.sogou.upd.passport.manager.api.config.form;

import com.google.common.base.Objects;

import com.sogou.upd.passport.manager.api.BaseApiParams;

import javax.validation.constraints.Min;

/**
 * 同步应用参数
 */
public class AppUpdateSyncApiParams extends BaseApiParams {
    @Min(value = 10000, message = "应用 id 错误")
    private int appId;
    private String appName;
    private String serverSecret;
    private String clientSecret;

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getServerSecret() {
        return serverSecret;
    }

    public void setServerSecret(String serverSecret) {
        this.serverSecret = serverSecret;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("appId", appId)
                .add("appName", appName)
                .add("serverSecret", serverSecret)
                .add("clientSecret", clientSecret)
                .toString();
    }
}
