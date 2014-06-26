package com.sogou.upd.passport.manager.form;

import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * User: mayan Date: 13-4-15 Time: 下午5:15 To change this template use File | Settings | File
 * Templates.
 */
public class UpdatePwdParameters {
    @NotBlank(message = "client_id不允许为空！")
    @Min(0)
    private String client_id = String.valueOf(SHPPUrlConstant.APP_ID);
    private String password;
    private String newpwd;
    private String passport_id;
    private String captcha;//验证码
    private String token;//标识码
    private String ip;

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

    public String getNewPwd() {
        return newpwd;
    }

    public void setNewPwd(String newpwd) {
        this.newpwd = newpwd;
    }

    public String getPassport_id() {
        return passport_id;
    }

    public void setPassport_id(String passport_id) {
        this.passport_id = passport_id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
