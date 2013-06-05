package com.sogou.upd.passport.web.form.internal;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-31 Time: 下午12:32 To change this template
 * use File | Settings | File Templates.
 */
public class BaseAccountPwdInternalParams extends BaseAccountInternalParams {

    @NotBlank(message = "密码不允许为空")
    protected String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
