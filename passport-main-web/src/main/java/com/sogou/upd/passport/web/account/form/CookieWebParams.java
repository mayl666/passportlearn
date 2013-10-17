package com.sogou.upd.passport.web.account.form;

import com.sogou.upd.passport.common.validation.constraints.Ru;
import com.sogou.upd.passport.common.validation.constraints.UserName;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

/**
 * 种cookie参数类
 * <p/>
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-10-10
 * Time: 下午12:12
 * To change this template use File | Settings | File Templates.
 */
public class CookieWebParams {
    @NotBlank(message = "用户id不允许为空")
    @UserName
    private String userid;
    @NotBlank(message = "应用id不允许为空")
    private String client_id; //分配给应用的id        todo sohu的应用id待分配
    @NotBlank(message = "code不允许为空")
    private String code; // MD5（userid+client_id+ct+server_secret)方法签名
    @NotBlank(message = "ct不允许为空")
    private String ct; //单位为毫秒
    @Ru
    @URL
    private String ru;      //回跳的URL
    private int persistentcookie;  //是否自动登录（0：否 1：是）或是否使用持久cookie 0:session级别的cookie 1:长时间有效的cookie，目前是两天
    private String domain;      //sogou只种sogou域cookie，后台默认赋值sogou域

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCt() {
        return ct;
    }

    public void setCt(String ct) {
        this.ct = ct;
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
}
