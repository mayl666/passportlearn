package com.sogou.upd.passport.manager.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-23 Time: 下午6:39 To change this template use
 * File | Settings | File Templates.
 */
public class AccountSmsParams extends BaseAccountParams {
    @NotBlank(message = "验证码不允许为空!")
    protected String smscode;

    public String getSmscode() {
        return smscode;
    }

    public void setSmscode(String smscode) {
        this.smscode = smscode;
    }
}
