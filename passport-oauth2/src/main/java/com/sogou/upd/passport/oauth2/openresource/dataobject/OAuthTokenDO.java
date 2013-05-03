package com.sogou.upd.passport.oauth2.openresource.dataobject;

public class OAuthTokenDO {

    protected String accessToken;
    protected long expiresIn;
    protected String refreshToken;
    protected String scope;
    protected String openid;
    protected String openidSecret;

    public OAuthTokenDO() {
    }

    public OAuthTokenDO(String accessToken, long expiresIn, String refreshToken, String scope) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.scope = scope;
    }

    public OAuthTokenDO(String accessToken, long expiresIn, String refreshToken, String scope, String openid) {
        this(accessToken, expiresIn, refreshToken, scope);
        this.openid = openid;
    }

    public OAuthTokenDO(String accessToken, long expiresIn, String refreshToken, String scope, String openid,
                        String openidSecret) {
        this(accessToken, expiresIn, refreshToken, scope, openid);
        this.openidSecret = openidSecret;
    }

    public OAuthTokenDO(String accessToken) {
        this(accessToken, 0, null, null);
    }

    public OAuthTokenDO(String accessToken, long expiresIn) {
        this(accessToken, expiresIn, null, null);
    }

    public OAuthTokenDO(String accessToken, long expiresIn, String scope) {
        this(accessToken, expiresIn, null, scope);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getScope() {
        return scope;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getOpenidSecret() {
        return openidSecret;
    }

    public void setOpenidSecret(String openidSecret) {
        this.openidSecret = openidSecret;
    }

}
