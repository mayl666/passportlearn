package com.sogou.upd.passport.manager.api.config.form;

import com.sogou.upd.passport.manager.api.BaseApiParams;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * 同步应用参数
 */
public class ThirdAddSyncApiParams extends BaseApiParams {
    @Min(value = 10000, message = "应用 id 错误")
    private int appId;
    @NotBlank(message = "第三方平台提供者不允许为空")
    @Min(1)
    private int provider;
    @NotBlank(message = "第三方 app key 不允许为空")
    private String appKey;
    @NotBlank(message = "第三方 app secret 不允许为空")
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
