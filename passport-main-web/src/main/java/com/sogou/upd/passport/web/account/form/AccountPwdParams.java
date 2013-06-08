package com.sogou.upd.passport.web.account.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-23 Time: 下午6:45 To change this template use
 * File | Settings | File Templates.
 */
public class AccountPwdParams extends BaseAccountParams {
    @NotBlank(message = "密码不允许为空!")
    protected String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
