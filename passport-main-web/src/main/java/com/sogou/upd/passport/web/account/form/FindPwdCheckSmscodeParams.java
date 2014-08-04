package com.sogou.upd.passport.web.account.form;

import com.sogou.upd.passport.common.validation.constraints.Phone;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-7-3
 * Time: 下午4:45
 * To change this template use File | Settings | File Templates.
 */
public class FindPwdCheckSmscodeParams extends BaseWapResetPwdParams {

    @NotBlank(message = "手机号不可为空")
    @Phone
    private String mobile;
    @NotBlank(message = "手机验证码不可为空")
    private String smscode;
    private String captcha;//验证码
    private String token;//标识码

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSmscode() {
        return smscode;
    }

    public void setSmscode(String smscode) {
        this.smscode = smscode;
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
