package com.sogou.upd.passport.model.connect;

public class OAuthToken {

    protected String accessToken;
    protected long expiresIn;
    protected String refreshToken;
    protected String scope;
    protected String connectUid;

    public OAuthToken() {}

    public OAuthToken(String accessToken, long expiresIn, String refreshToken, String scope) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.scope = scope;
    }

    public OAuthToken(String accessToken, long expiresIn, String refreshToken, String scope, String connectUid) {
        this(accessToken, expiresIn, refreshToken, scope);
        this.connectUid = connectUid;
    }

    public OAuthToken(String accessToken) {
        this(accessToken, 0, null, null);
    }
    public OAuthToken(String accessToken, long expiresIn) {
        this(accessToken, expiresIn, null, null);
    }
    public OAuthToken(String accessToken, long expiresIn, String scope) {
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
    public String getConnectUid() {
        return connectUid;
    }
    public void setConnectUid(String connectUid) {
        this.connectUid = connectUid;
    }

}


