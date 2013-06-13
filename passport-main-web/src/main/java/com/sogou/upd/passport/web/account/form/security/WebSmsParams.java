package com.sogou.upd.passport.web.account.form.security;

import com.sogou.upd.passport.web.account.form.BaseWebParams;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-9 Time: 下午4:21 To change this template use
 * File | Settings | File Templates.
 */
public class WebSmsParams extends BaseWebParams {
    @NotBlank(message = "验证码不允许为空!")
    protected String smscode;

    public String getSmscode() {
        return smscode;
    }

    public void setSmscode(String smscode) {
        this.smscode = smscode;
    }
}
