package com.sogou.upd.passport.model.app;

import java.util.Date;

/**
 * 应用配置
 * User: shipengzhi
 * Date: 13-3-25
 * Time: 下午11:19
 * To change this template use File | Settings | File Templates.
 */
public class AppConfig {

    private long id;
    private int clientId;
    public String smsText;
    private int accessTokenExpiresin;
    private int refreshTokenExpiresin;
    private String serverSecret;
    private String clientSecret;
    private Date createTime;
    private String clientName;
    private String scope;
    private String serverIp;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getSmsText() {
        return smsText;
    }

    public void setSmsText(String smsText) {
        this.smsText = smsText;
    }

    public int getAccessTokenExpiresin() {
        return accessTokenExpiresin;
    }

    public void setAccessTokenExpiresin(int accessTokenExpiresin) {
        this.accessTokenExpiresin = accessTokenExpiresin;
    }

    public int getRefreshTokenExpiresin() {
        return refreshTokenExpiresin;
    }

    public void setRefreshTokenExpiresin(int refreshTokenExpiresin) {
        this.refreshTokenExpiresin = refreshTokenExpiresin;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }
}
