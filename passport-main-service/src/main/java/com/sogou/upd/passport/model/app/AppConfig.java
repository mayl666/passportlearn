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
    private long appkey;
    public String smsText;
    private int accessTokenExpiresin;
    private String appSecret;
    private String appClientSecret;
    private Date createTime;
    private Date updateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAppkey() {
        return appkey;
    }

    public void setAppkey(long appkey) {
        this.appkey = appkey;
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

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getAppClientSecret() {
        return appClientSecret;
    }

    public void setAppClientSecret(String appClientSecret) {
        this.appClientSecret = appClientSecret;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
