package com.sogou.upd.passport.model.connect;

import com.sogou.upd.passport.common.lang.StringUtil;

import java.util.Date;

/**
 * 存储第三方Token和个人资料的connect_token表
 * User: shipengzhi
 * Date: 13-3-24
 * Time: 下午5:03
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
    private String connectUniqname;  // 第三方昵称   写入数据库时，一定要转码为utf8，不然中文乱码插入失败
    private String avatarSmall;   // 第三方头像（小图）
    private String avatarMiddle;  // 第三方头像（中图）
    private String avatarLarge;  // 第三方头像（大图）
    private String gender;   // 性别。 0-女，1-男，默认为1
    private Date updateTime; // 修改时间。当首次创建时为创建时间，以后为每次修改时间

    public ConnectToken() {
    }

    public ConnectToken(String openid, String accessToken) {
        this.openid = openid;
        this.accessToken = accessToken;
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getConnectUniqname() {
        return connectUniqname;
    }

    public void setConnectUniqname(String connectUniqname) {
        this.connectUniqname = connectUniqname;
    }

    public String getAvatarSmall() {
        return StringUtil.replaceHttpToHttps(avatarSmall);
    }

    public void setAvatarSmall(String avatarSmall) {
        this.avatarSmall = avatarSmall;
    }

    public String getAvatarMiddle() {
        return StringUtil.replaceHttpToHttps(avatarMiddle);
    }

    public void setAvatarMiddle(String avatarMiddle) {
        this.avatarMiddle = avatarMiddle;
    }

    public String getAvatarLarge() {
        return StringUtil.replaceHttpToHttps(avatarLarge);
    }

    public void setAvatarLarge(String avatarLarge) {
        this.avatarLarge = avatarLarge;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
