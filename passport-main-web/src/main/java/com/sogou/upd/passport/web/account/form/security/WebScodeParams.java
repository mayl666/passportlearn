package com.sogou.upd.passport.web.account.form.security;

import com.sogou.upd.passport.web.account.form.BaseWebParams;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-9 Time: 下午3:39 To change this template use
 * File | Settings | File Templates.
 */
public class WebScodeParams extends BaseWebParams {
    @NotBlank(message = "scode不允许为空!")
    protected String scode;

    public String getScode() {
        return scode;
    }

    public void setScode(String scode) {
        this.scode = scode;
    }
}
