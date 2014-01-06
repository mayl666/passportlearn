package com.sogou.upd.passport.manager.api.connect.form.proxy;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-1-4
 * Time: 下午8:06
 * To change this template use File | Settings | File Templates.
 */
public class OpenApiParams {
    private String openId;
    private String accessToken;
    private String provider;
    private String interfaceName;
    private String appKey;
    private String appSecret;
    private String serverName;

    public OpenApiParams(String openId, String accessToken, String provider, String interfaceName, String appKey, String appSecret,String serverName) {
        this.openId = openId;
        this.accessToken = accessToken;
        this.provider = provider;
        this.interfaceName = interfaceName;
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.serverName = serverName;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
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

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }


}
