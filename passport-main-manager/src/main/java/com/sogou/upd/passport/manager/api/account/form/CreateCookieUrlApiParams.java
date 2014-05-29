package com.sogou.upd.passport.manager.api.account.form;

import com.sogou.upd.passport.common.validation.constraints.Ru;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-9
 * Time: 上午10:38
 */
public class CreateCookieUrlApiParams {

    @NotBlank(message = "回调地址不能为空")
    @Ru
    @URL
    private String ru;

    @NotBlank(message = "用户id不能为空")
    private String userid;

    //是否使用持久cookie 0:session级别的cookie 1:长时间有效的cookie，目前是两周
    private int persistentcookie;

    private String domain;

    private String clientId; //setppcookie接口参数中ru，1105不允许URLEncode，但壁纸需要URLEncode，所以传clientId区分

    public CreateCookieUrlApiParams() {
    }

    public CreateCookieUrlApiParams(String userid, String ru, int persistentcookie) {
        this.userid = userid;
        this.ru = ru;
        this.persistentcookie = persistentcookie;
    }

    public CreateCookieUrlApiParams(String userid, String ru, int persistentcookie, String domain) {
        this.userid = userid;
        this.ru = ru;
        this.persistentcookie = persistentcookie;
        this.domain = domain;
    }


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public int getPersistentcookie() {
        return persistentcookie;
    }

    public void setPersistentcookie(int persistentcookie) {
        this.persistentcookie = persistentcookie;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
