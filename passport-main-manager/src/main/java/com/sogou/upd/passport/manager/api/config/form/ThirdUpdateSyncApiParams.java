package com.sogou.upd.passport.manager.api.config.form;

import com.sogou.upd.passport.manager.api.BaseApiParams;

import javax.validation.constraints.Min;

/**
 * 同步应用参数
 */
public class ThirdUpdateSyncApiParams extends BaseApiParams {
    @Min(0)
    private int appId;
    private int provider;
    private String appKey;
    private String appSecret;
    private String scope;

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public int getProvider() {
        return provider;
    }

    public void setProvider(int provider) {
        this.provider = provider;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "com.sogou.upd.passport.manager.api.config.form.ThirdAddSyncApiParams{" +
               "appId=" + appId +
               ", provider='" + provider + '\'' +
               ", appKey='" + appKey + '\'' +
               ", appSecret='" + appSecret + '\'' +
               ", scope='" + scope + '\'' +
               '}';
    }
}
