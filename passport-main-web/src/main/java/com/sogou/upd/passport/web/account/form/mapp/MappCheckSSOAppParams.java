package com.sogou.upd.passport.web.account.form.mapp;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;


/**
 * Created with IntelliJ IDEA.
 * User: nahongxu
 * Date: 15-2-15
 * Time: 下午4:56
 * To change this template use File | Settings | File Templates.
 */
public class MappCheckSSOAppParams {

    @Min(0)
    private int client_id; //应用ID

    @Min(0)
    protected long ct; //单位为毫秒

    @NotBlank(message = "package_sgin不允许为空!")
    private String sign;

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

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}