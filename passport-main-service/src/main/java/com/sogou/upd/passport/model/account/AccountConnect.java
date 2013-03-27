package com.sogou.upd.passport.model.account;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-3-24
 * Time: 下午5:03
 * To change this template use File | Settings | File Templates.
 */
public class AccountConnect {

    public static final int STUTAS_LONGIN = 0; // 登录账号
    public static final int STUTAS_BIND = 1;  // 绑定账号

    private long id; // 主键
    private long userId; // 用户id主键
    private int provider; // 第三方来源
    private String connectUid; // 第三方openid
    private String connectAccessToken; // 第三方access_token
    private long connectExpireIn; // 第三方access_token有效期
    private String connectRefreshToken; // 第三方refresh_token
    private int accountRelation; // 0-登录账号；1-绑定账号
    private int clientId; // 应用id
    private Date update_time; // 更新时间
    private Date create_time; // 创建时间

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getProvider() {
        return provider;
    }

    public void setProvider(int provider) {
        this.provider = provider;
    }

    public String getConnectUid() {
        return connectUid;
    }

    public void setConnectUid(String connectUid) {
        this.connectUid = connectUid;
    }

    public String getConnectAccessToken() {
        return connectAccessToken;
    }

    public void setConnectAccessToken(String connectAccessToken) {
        this.connectAccessToken = connectAccessToken;
    }

    public long getConnectExpireIn() {
        return connectExpireIn;
    }

    public void setConnectExpireIn(long connectExpireIn) {
        this.connectExpireIn = connectExpireIn;
    }

    public String getConnectRefreshToken() {
        return connectRefreshToken;
    }

    public void setConnectRefreshToken(String connectRefreshToken) {
        this.connectRefreshToken = connectRefreshToken;
    }

    public int getAccountRelation() {
        return accountRelation;
    }

    public void setAccountRelation(int accountRelation) {
        this.accountRelation = accountRelation;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }
}
