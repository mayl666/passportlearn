package com.sogou.upd.passport.manager.api.account.form;

import com.sogou.upd.passport.manager.api.BaseApiParams;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-10-10
 * Time: 下午3:13
 * To change this template use File | Settings | File Templates.
 */
public class CookieApiParams extends BaseApiParams {

    public static final int IS_ACTIVE = 1;      //激活
    public static final int IS_NOT_ACTIVE = 0;  //未激活

    @NotBlank(message = "用户id不能为空")
    private String userid;     //用户id
    private int trust;   //激活：1，未激活：0
    @URL
    private String ru;
    private String ip;
    private String persistentcookie;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public int getTrust() {
        return trust;
    }

    public void setTrust(int trust) {
        this.trust = trust;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPersistentcookie() {
        return persistentcookie;
    }

    public void setPersistentcookie(String persistentcookie) {
        this.persistentcookie = persistentcookie;
    }
}
