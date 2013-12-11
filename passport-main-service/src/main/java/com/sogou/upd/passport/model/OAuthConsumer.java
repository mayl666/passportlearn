package com.sogou.upd.passport.model;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-26
 * Time: 下午7:41
 * To change this template use File | Settings | File Templates.
 */
public class OAuthConsumer {

    private String webUserAuthzUrl;
    private String accessTokenUrl;
    private String refreshAccessTokenUrl; // renren支持
    private String openIdUrl;  // qq支持
    private String callbackUrl;

    private String wapUserAuthzUrl;  //qq wap支持

    //======================第三方开放API=========================
    private String userInfo;

    public String getWebUserAuthzUrl() {
        return webUserAuthzUrl;
    }

    public void setWebUserAuthzUrl(String webUserAuthzUrl) {
        this.webUserAuthzUrl = webUserAuthzUrl;
    }

    public String getAccessTokenUrl() {
        return accessTokenUrl;
    }

    public void setAccessTokenUrl(String accessTokenUrl) {
        this.accessTokenUrl = accessTokenUrl;
    }

    public String getRefreshAccessTokenUrl() {
        return refreshAccessTokenUrl;
    }

    public void setRefreshAccessTokenUrl(String refreshAccessTokenUrl) {
        this.refreshAccessTokenUrl = refreshAccessTokenUrl;
    }

    public String getOpenIdUrl() {
        return openIdUrl;
    }

    public void setOpenIdUrl(String openIdUrl) {
        this.openIdUrl = openIdUrl;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    public String getWapUserAuthzUrl() {
        return wapUserAuthzUrl;
    }

    public void setWapUserAuthzUrl(String wapUserAuthzUrl) {
        this.wapUserAuthzUrl = wapUserAuthzUrl;
    }
}
