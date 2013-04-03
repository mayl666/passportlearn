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
    private int clientId; // 应用id
    private int accountRelation; // 0-登录账号；1-绑定账号
    private int accountType; // 第三方来源
    private String connectUid; // 第三方openid
    private String connectAccessToken; // 第三方access_token
    private long connectExpiresIn; // 第三方access_token有效期
    private String connectRefreshToken; // 第三方refresh_token
    private Date updateTime; // 更新时间
    private Date createTime; // 创建时间
    
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
	public int getClientId() {
		return clientId;
	}
	public void setClientId(int clientId) {
		this.clientId = clientId;
	}
	public int getAccountRelation() {
		return accountRelation;
	}
	public void setAccountRelation(int accountRelation) {
		this.accountRelation = accountRelation;
	}
	public int getAccountType() {
		return accountType;
	}
	public void setAccountType(int accountType) {
		this.accountType = accountType;
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
	public long getConnectExpiresIn() {
		return connectExpiresIn;
	}
	public void setConnectExpiresIn(long connectExpiresIn) {
		this.connectExpiresIn = connectExpiresIn;
	}
	public String getConnectRefreshToken() {
		return connectRefreshToken;
	}
	public void setConnectRefreshToken(String connectRefreshToken) {
		this.connectRefreshToken = connectRefreshToken;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}
