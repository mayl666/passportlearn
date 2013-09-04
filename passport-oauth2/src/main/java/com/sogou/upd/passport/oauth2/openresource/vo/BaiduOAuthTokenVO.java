package com.sogou.upd.passport.oauth2.openresource.vo;

/**
 * 淘宝OAuth授权返回的token对象
 * User: shipengzhi
 * Date: 13-8-26
 * Time: 上午12:59
 * To change this template use File | Settings | File Templates.
 */
public class BaiduOAuthTokenVO {

    private String access_token;
    private long expires_in;
    private String refresh_token;

    /*=============可选参数================*/
    private String scope;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
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
