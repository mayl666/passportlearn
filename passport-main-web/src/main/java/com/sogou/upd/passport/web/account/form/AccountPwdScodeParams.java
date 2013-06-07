package com.sogou.upd.passport.web.account.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-4-28 Time: 下午1:59 To change this template use
 * File | Settings | File Templates.
 * 用于获取前端的常用参数
 */
public class AccountPwdScodeParams extends BaseAccountParams {

    @NotBlank(message = "密码不能为空！")
    private String password;
    @NotBlank(message = "scode不能为空！")
    private String scode;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getScode() {
        return scode;
    }

    public void setScode(String scode) {
        this.scode = scode;
    }
}
