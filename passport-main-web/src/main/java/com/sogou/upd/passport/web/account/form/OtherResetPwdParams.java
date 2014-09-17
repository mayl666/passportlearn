package com.sogou.upd.passport.web.account.form;

import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-7-4
 * Time: 下午5:24
 * To change this template use File | Settings | File Templates.
 */
public class OtherResetPwdParams extends BaseWapResetPwdParams {

    @NotBlank(message = "用户名不可为空")
    protected String username;
    @NotBlank(message = "页面验证码不可为空")
    protected String captcha;
    protected String token;

    public String getUsername() {
        String internalUsername = AccountDomainEnum.getInternalCase(username);
        setUsername(internalUsername);
        return username;
    }

    public void setUsername(String username) {
        if (username != null) {
            username = username.trim();
        }
        this.username = username;
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
}
