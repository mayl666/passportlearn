package com.sogou.upd.passport.manager.api.account.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-9
 * Time: 上午10:38
 */
public class CreateCookieUrlApiParams {

    @NotBlank(message = "回调地址不能为空")
    private String ru;

    @NotBlank(message = "用户id不能为空")
    private String userid;

    //是否使用持久cookie
    private int persistentcookie;

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
}
