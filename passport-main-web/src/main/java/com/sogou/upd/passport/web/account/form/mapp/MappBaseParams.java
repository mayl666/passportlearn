package com.sogou.upd.passport.web.account.form.mapp;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 14-11-24
 * Time: 下午11:19
 * To change this template use File | Settings | File Templates.
 */
public class MappBaseParams {

    @Min(0)
    private int client_id; //应用ID
    @Min(0)
    protected long ct; //单位为毫秒
    @NotBlank(message = "code不允许为空！")
    private String code;

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public long getCt() {
        return ct;
    }

    public void setCt(long ct) {
        this.ct = ct;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
