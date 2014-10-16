package com.sogou.upd.passport.oauth2.openresource.vo;

/**
 * 统一的OAuthToken对象类
 */
public class OAuthTokenVO {

    private String accessToken;
    private long expiresIn;
    private String refreshToken;
    private long reExpiresIn; //refresh_token有效期
    private String scope;
    private String openid;
    private String nickName; //昵称
    private String openidSecret;  // openid密钥
    private String ip;
    private ConnectUserInfoVO ConnectUserInfoVO; // 第三方用户信息
    private String unionId; //微信的同一个开发者账号下多Appid对应的用户唯一标识

    public OAuthTokenVO() {
    }

    public OAuthTokenVO(String accessToken, long expiresIn, String refreshToken) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
    }

    public OAuthTokenVO(String accessToken, long expiresIn, String refreshToken, String scope) {
        this(accessToken, expiresIn, refreshToken);
        this.scope = scope;
    }

    public OAuthTokenVO(String accessToken, long expiresIn, String refreshToken, long reExpiresIn, String scope, String openid, String nickName) {
        this(accessToken, expiresIn, refreshToken, scope);
        this.reExpiresIn = reExpiresIn;
        this.openid = openid;
        this.nickName = nickName;
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

    public long getReExpiresIn() {
        return reExpiresIn;
    }

    public void setReExpiresIn(long reExpiresIn) {
        this.reExpiresIn = reExpiresIn;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getOpenidSecret() {
        return openidSecret;
    }

    public void setOpenidSecret(String openidSecret) {
        this.openidSecret = openidSecret;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public ConnectUserInfoVO getConnectUserInfoVO() {
        return ConnectUserInfoVO;
    }

    public void setConnectUserInfoVO(ConnectUserInfoVO connectUserInfoVO) {
        ConnectUserInfoVO = connectUserInfoVO;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }
}
