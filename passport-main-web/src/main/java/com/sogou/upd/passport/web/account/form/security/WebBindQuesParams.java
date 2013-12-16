package com.sogou.upd.passport.web.account.form.security;

import com.sogou.upd.passport.common.validation.constraints.SafeInput;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-9 Time: 下午4:35 To change this template use
 * File | Settings | File Templates.
 */
public class WebBindQuesParams extends WebPwdParams {
    @NotBlank(message = "密保问题不允许为空")
    @SafeInput(message = "输入内容中包含非法字符，请重新输入！")
    protected String new_ques;
    @NotBlank(message = "密保答案不允许为空")
    @SafeInput(message = "输入内容中包含非法字符，请重新输入！")
    protected String new_answer;

    public String getNew_ques() {
        return new_ques;
    }

    public void setNew_ques(String new_ques) {
        this.new_ques = new_ques;
    }

    public String getNew_answer() {
        return new_answer;
    }

    public void setNew_answer(String new_answer) {
        this.new_answer = new_answer;
    }
}
