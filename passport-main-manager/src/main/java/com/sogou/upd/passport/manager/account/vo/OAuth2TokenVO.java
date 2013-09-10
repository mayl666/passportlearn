package com.sogou.upd.passport.manager.account.vo;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-9-10
 * Time: 上午2:02
 * To change this template use File | Settings | File Templates.
 */
public class OAuth2TokenVO {

    private String access_token;  // 	由授权服务器分发的访问令牌
    private String token_type = "None"; // 令牌类型，描述了令牌的使用方式（一般访问受保护资源时的加密方式），如none或hmac-sha1
    private long expires_time; // 令牌过期的截止时间的时间戳,单位毫秒
    private String refresh_token; // 	新的刷新令牌
    private String scope = "all"; //授权服务器真正授予的作用域

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public long getExpires_time() {
        return expires_time;
    }

    public void setExpires_time(long expires_time) {
        this.expires_time = expires_time;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
