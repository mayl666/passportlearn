package com.sogou.upd.passport.manager.form;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.utils.PhoneUtil;

import com.sogou.upd.passport.common.validation.constraints.Password;
import com.sogou.upd.passport.common.validation.constraints.Ru;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;

/**
 * 用于web端的登陆的参数 User: liagng201716@sogou-inc.com Date: 13-5-12 Time: 下午10:01
 */
public class WebLoginParams extends BaseLoginParams{
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

    private int pwdtype = CommonConstant.PWD_TYPE_EXPRESS; //密码类型，1为md5后的口令，缺省为明文密码
    /**
     * 是否自动登陆，自动登陆cookie时长设置两周
     */
    private int autoLogin; // 0-否  1-真

    /**
     * 验证码 用户连续3次登陆失败需要输入验证码
     */
    private String captcha;//验证码
    private String token;//标识码
    @URL
    @Ru
    private String ru;//登陆来源

    private String xd; // 跨域通信所用字段，直接返回

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

    public int getPwdtype() {
        return pwdtype;
    }

    public void setPwdtype(int pwdtype) {
        this.pwdtype = pwdtype;
    }
}
