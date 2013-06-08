package com.sogou.upd.passport.web.account.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-23 Time: 下午6:39 To change this template use
 * File | Settings | File Templates.
 */
public class AccountSmsScodeParams extends BaseAccountParams {
    @NotBlank(message = "验证码不允许为空!")
    protected String smscode;
    // @NotBlank
    protected String scode;

    public String getSmscode() {
        return smscode;
    }

    public void setSmscode(String smscode) {
        this.smscode = smscode;
    }

    public String getScode() {
        return scode;
    }

    public void setScode(String scode) {
        this.scode = scode;
    }
}
