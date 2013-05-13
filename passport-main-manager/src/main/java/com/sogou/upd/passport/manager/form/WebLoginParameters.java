package com.sogou.upd.passport.manager.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 用于web端的登陆的参数
 * User: liagng201716@sogou-inc.com
 * Date: 13-5-12
 * Time: 下午10:01
 */
public class WebLoginParameters {

    /**
     * 登陆用户名
     */
    @Length(min = 1,max = 200,message = "用户名或密码错误，请重新输入！")
    @NotBlank(message = "请输入用户名！")
    private String account;


    /**
     * 登陆密码
     */
    @Length(min = 1,max = 200,message = "用户名或密码错误，请重新输入！")
    @NotBlank(message = "请输入密码！")
    private String password;


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
