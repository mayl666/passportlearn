package com.sogou.upd.passport.manager.form;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.utils.PhoneUtil;

import com.sogou.upd.passport.common.validation.constraints.Password;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;

/**
 * 用于web端的登陆的参数 User: liagng201716@sogou-inc.com Date: 13-5-12 Time: 下午10:01
 */
public class WebLoginParams {

    /**
     * 登陆用户名
     */
    @Length(min = 1, max = 100, message = "用户名错误，请重新输入！")
    @NotBlank(message = "请输入用户名！")
    private String username;


    /**
     * 登陆密码
     */
    @Password(message = "密码必须为字母、数字、字符且长度为6~16位!")
    @NotBlank(message = "请输入密码！")
    private String password;


    /**
     * 是否自动登陆，自动登陆cookie时长设置两周
     */
    private int autoLogin; // 0-否  1-真

    /**
     * 验证码 用户连续3次登陆失败需要输入验证码
     */
//  @NotBlank(message = "验证码不允许为空!")
    private String captcha;//验证码
    //  @NotBlank(message = "标识码不允许为空!")
    private String token;//标识码

    private String ru;//登陆来源

    @NotBlank(message = "client_id不允许为空!")
    @Min(0)
    private String client_id;

    private String xd; // 跨域通信所用字段，直接返回

    @AssertTrue(message = "用户账号格式错误")
    private boolean checkAccount() {
        if (Strings.isNullOrEmpty(username)) {
            return true;
        }
        if (username.indexOf("@") == -1) {
            if (!PhoneUtil.verifyPhoneNumberFormat(username)) {
                //个性账号格式是否拼配
                String regx = "[a-z]([a-zA-Z0-9_.]{3,15})";
                if (!username.matches(regx)) {
                    return false;
                }
            }
        } else {
            //邮箱格式
            String regex = "(\\w)+(\\.\\w+)*@([\\w_\\-])+((\\.\\w+)+)";
            return username.matches(regex);
        }
        return true;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username != null) {
            username = username.trim();
        }
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password != null) {
            password = password.trim();
        }
        this.password = password;
    }

    public int getAutoLogin() {
        return autoLogin;
    }

    public void setAutoLogin(int autoLogin) {
        this.autoLogin = autoLogin;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getXd() {
        return xd;
    }

    public void setXd(String xd) {
        this.xd = xd;
    }
}
