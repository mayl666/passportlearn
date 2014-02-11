package com.sogou.upd.passport.model.connect;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-3-24
 * Time: 下午5:03
 * To change this template use File | Settings | File Templates.
 */
public class ConnectToken {

    private long id; // 主键
    private String passportId; // passport用户身份的全局唯一ID，包含@域名
    private String appKey; // 第三方appKey
    private int provider; // 第三方平台类型
    private String openid; // 第三方openid
    private String accessToken; // 第三方access_token
    private long expiresIn; // 第三方access_token有效期
    private String refreshToken; // 第三方refresh_token
    private Date createTime; // 创建时间

    public ConnectToken() {
    }

    public ConnectToken(String openid, String accessToken, long expiresIn) {
        this.openid = openid;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPassportId() {
        return passportId;
    }

    public void setPassportId(String passportId) {
        this.passportId = passportId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public int getProvider() {
        return provider;
    }

    public void setProvider(int provider) {
        this.provider = provider;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}
