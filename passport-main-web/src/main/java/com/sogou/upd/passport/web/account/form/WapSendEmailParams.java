package com.sogou.upd.passport.web.account.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-7-7
 * Time: 下午3:34
 * To change this template use File | Settings | File Templates.
 */
public class WapSendEmailParams extends BaseWapResetPwdParams {

    @NotBlank(message = "用户名不可为空")
    protected String username;
    @NotBlank(message = "安全码不可为空")
    protected String scode;
    @NotBlank(message = "验证邮箱不可为空")
    protected String email;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getScode() {
        return scode;
    }

    public void setScode(String scode) {
        this.scode = scode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
