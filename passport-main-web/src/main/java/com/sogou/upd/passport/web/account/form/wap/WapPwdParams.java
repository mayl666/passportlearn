package com.sogou.upd.passport.web.account.form.wap;

import com.sogou.upd.passport.common.validation.constraints.Password;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-7-7
 * Time: 下午5:02
 * To change this template use File | Settings | File Templates.
 */
public class WapPwdParams extends WapCheckEmailParams {
    @NotBlank(message = "密码不允许为空!")
    @Password(message = "密码必须为字母、数字、字符且长度为6~16位!")
    protected String password;
    protected String captcha;  //wap2.0用到的短信验证码

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
