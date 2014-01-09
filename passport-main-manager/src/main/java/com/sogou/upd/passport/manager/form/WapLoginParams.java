package com.sogou.upd.passport.manager.form;

import com.sogou.upd.passport.common.WapConstant;
import com.sogou.upd.passport.common.validation.constraints.Password;
import com.sogou.upd.passport.common.validation.constraints.Ru;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;

/**
 * 用于web端的登陆的参数 User: liagng201716@sogou-inc.com Date: 13-5-12 Time: 下午10:01
 */
public class WapLoginParams {

    /**
     * 登陆用户名
     */
    @Length(min = 1, max = 100, message = "用户名错误，请重新输入！")
    @NotBlank(message = "请输入用户名！")
    private String username;

    /**
     * 登陆密码
     */
    @NotBlank(message = "请输入密码！")
    private String password;

    /**
     * 验证码 用户连续3次登陆失败需要输入验证码
     */
    private String captcha;//验证码
    private String token;//标识码

    @NotBlank(message = "client_id不允许为空!")
    @Min(0)
    private String client_id;

    @NotBlank
    @URL
//    @Ru
    private String ru;//登陆来源


    @NotBlank(message = "v is null")
    private String v = WapConstant.WAP_COLOR;//wap版本:0-简易版；1-炫彩版；2-触屏版

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }
}
