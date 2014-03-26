package com.sogou.upd.passport.dao.connect;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 14-2-24
 * Time: 下午8:37
 * To change this template use File | Settings | File Templates.
 */
public class OpenTokenInfo {

    public long id;
    public String token;
    public String secret;
    public String platform;
    public String refuserid;
    public String authed;
    public Date createtime;
    public String appid;
    public int expireTime;
    public String refresh;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getRefuserid() {
        return refuserid;
    }

    public void setRefuserid(String refuserid) {
        this.refuserid = refuserid;
    }

    public String getAuthed() {
        return authed;
    }

    public void setAuthed(String authed) {
        this.authed = authed;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public int getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }

    public String getRefresh() {
        return refresh;
    }

    public void setRefresh(String refresh) {
        this.refresh = refresh;
    }
}
