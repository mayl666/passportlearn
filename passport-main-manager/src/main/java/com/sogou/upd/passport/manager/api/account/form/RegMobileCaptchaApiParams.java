package com.sogou.upd.passport.manager.api.account.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 带验证码的手机号注册参数类
 * User: shipengzhi
 * Date: 13-6-7
 * Time: 下午9:13
 * To change this template use File | Settings | File Templates.
 */
public class RegMobileCaptchaApiParams extends BaseMobileApiParams {

    @NotBlank(message = "密码不允许为空")
    private String password;  //必须为md5
    @NotBlank(message = "手机验证码不允许为空")
    private String captcha;
    private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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

}
