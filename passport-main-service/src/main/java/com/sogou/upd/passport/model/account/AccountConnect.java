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
    private long userid; // 用户id主键
    private int provider; // 第三方来源
    private String connectUid; // 第三方openid
    private String connectAccessToken; // 第三方access_token
    private long connectExpireIn; // 第三方access_token有效期
    private String connectRefreshToken; // 第三方refresh_token
    private int accountRelation; // 0-登录账号；1-绑定账号
    private int appkey; // 应用id
    private Date update_time; // 更新时间
    private Date create_time; // 创建时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public Integer getProvider() {
        return provider;
    }

    public void setProvider(Integer provider) {
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

    public Long getConnectExpireIn() {
        return connectExpireIn;
    }

    public void setConnectExpireIn(Long connectExpireIn) {
        this.connectExpireIn = connectExpireIn;
    }

    public String getConnectRefreshToken() {
        return connectRefreshToken;
    }

    public void setConnectRefreshToken(String connectRefreshToken) {
        this.connectRefreshToken = connectRefreshToken;
    }

    public Integer getAccountRelation() {
        return accountRelation;
    }

    public void setAccountRelation(Integer accountRelation) {
        this.accountRelation = accountRelation;
    }

    public Integer getAppkey() {
        return appkey;
    }

    public void setAppkey(Integer appkey) {
        this.appkey = appkey;
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
