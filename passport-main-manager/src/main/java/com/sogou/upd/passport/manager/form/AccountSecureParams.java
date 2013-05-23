package com.sogou.upd.passport.manager.form;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-4-28 Time: 下午1:59 To change this template use
 * File | Settings | File Templates.
 * 用于获取前端的常用参数
 */
public class AccountSecureParams {

    @NotBlank(message = "账号不允许为空!")
    private String passport_id;
    @NotBlank(message = "client_id不允许为空!")
    @Min(0)
    private String client_id;
    private String password;
    private String scode;

    public String getPassport_id() {
        return passport_id;
    }

    public void setPassport_id(String passport_id) {
        this.passport_id = passport_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getScode() {
        return scode;
    }

    public void setScode(String scode) {
        this.scode = scode;
    }
}
