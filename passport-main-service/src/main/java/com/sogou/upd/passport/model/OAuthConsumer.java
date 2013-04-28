package com.sogou.upd.passport.model;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-26
 * Time: 下午7:41
 * To change this template use File | Settings | File Templates.
 */
public class OAuthConsumer {

    private String userAuthzUrl;
    private String accessTokenUrl;
    private String refreshAccessTokenUrl; // renren支持
    private String openIdUrl;  // qq支持
    private String callbackUrl;

    public String getUserAuthzUrl() {
        return userAuthzUrl;
    }

    public void setUserAuthzUrl(String userAuthzUrl) {
        this.userAuthzUrl = userAuthzUrl;
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
}
