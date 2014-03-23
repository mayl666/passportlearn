package com.sogou.upd.passport.web.account.form;

import com.sogou.upd.passport.common.validation.constraints.SafeInput;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-23 Time: 下午6:54 To change this template use
 * File | Settings | File Templates.
 */
public class FindPwdCheckAnswerParams extends BaseAccountParams {
    @Length(min = 1, max = 200, message = "密保答案错误，请重新输入！")
    @SafeInput(message = "输入内容中包含非法字符，请重新输入！")
    @NotBlank(message = "密保答案不允许为空!")
    protected String answer;

    @NotBlank(message = "验证码不允许为空!")
    private String captcha;//验证码
    @NotBlank(message = "标识码不允许为空!")
    private String token;//标识码

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
}
