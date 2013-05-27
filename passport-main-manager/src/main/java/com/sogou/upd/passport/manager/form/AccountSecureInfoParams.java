package com.sogou.upd.passport.manager.form;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-20 Time: 上午11:18 To change this template
 * use File | Settings | File Templates.
 */
public class AccountSecureInfoParams extends BaseUserParams {
    @NotBlank
    private String token;
    @NotBlank(message = "验证码不允许为空!")
    private String captcha;

    private String sec_mobile;
    private String sec_email;
    private String reg_email;
    private String sec_ques;

    public String getSec_mobile() {
        return sec_mobile;
    }

    public void setSec_mobile(String sec_mobile) {
        this.sec_mobile = sec_mobile;
    }

    public String getSec_email() {
        return sec_email;
    }

    public void setSec_email(String sec_email) {
        this.sec_email = sec_email;
    }

    public String getReg_email() {
        return reg_email;
    }

    public void setReg_email(String reg_email) {
        this.reg_email = reg_email;
    }

    public String getSec_ques() {
        return sec_ques;
    }

    public void setSec_ques(String sec_ques) {
        this.sec_ques = sec_ques;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
