package com.sogou.upd.passport.manager.api.account.form;

import com.sogou.upd.passport.common.validation.constraints.UserName;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * Created by denghua on 14-4-28.
 */
public class RegMobileParams {


    @UserName
    @NotBlank(message = "用户名不允许为空")
    protected String username;


    @Min(0)
    protected int client_id; //应用id

    @NotBlank(message = "密码不允许为空")
    protected String password; //md5
    @NotBlank(message = "手机验证码不允许为空")
    private String captcha;
    private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

}
