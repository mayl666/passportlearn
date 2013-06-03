package com.sogou.upd.passport.manager.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-24 Time: 下午2:05 To change this template use
 * File | Settings | File Templates.
 */
public class AccountAnswerCaptParams extends BaseAccountParams {
    @NotBlank(message = "密保答案不能为空")
    private String answer;
    @NotBlank(message = "验证码不能为空")
    private String captcha;
    @NotBlank
    private String token;
    // @NotBlank
    private String scode;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
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

    public String getScode() {
        return scode;
    }

    public void setScode(String scode) {
        this.scode = scode;
    }
}
