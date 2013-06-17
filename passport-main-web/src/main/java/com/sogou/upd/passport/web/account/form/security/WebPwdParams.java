package com.sogou.upd.passport.web.account.form.security;

import com.sogou.upd.passport.common.validation.constraints.Password;
import com.sogou.upd.passport.web.account.form.BaseWebParams;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-9 Time: 下午3:42 To change this template use
 * File | Settings | File Templates.
 */
public class WebPwdParams extends BaseWebParams {
    @NotBlank(message = "密码不允许为空!")
    @Password(message = "密码必须为字母、数字、字符且长度为6~16位!")
    protected String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
