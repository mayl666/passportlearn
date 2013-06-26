package com.sogou.upd.passport.web.account.form.security;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.AssertTrue;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-9 Time: 下午3:41 To change this template use
 * File | Settings | File Templates.
 */
public class WebBindEmailParams extends WebPwdParams {
    @NotBlank(message = "新绑定邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String new_email;
    @Email(message = "邮箱格式不正确")
    private String old_email;

    @AssertTrue(message = "新密保邮箱不能与原密保邮箱相同")
    private boolean checkNewIsOld() {
        if (new_email.equals(old_email)) { // NotBlank已经校验过了，无需再校验
            return false;
        }
        return true;
    }

    public String getNew_email() {
        return new_email;
    }

    public void setNew_email(String new_email) {
        this.new_email = new_email;
    }

    public String getOld_email() {
        return old_email;
    }

    public void setOld_email(String old_email) {
        this.old_email = old_email;
    }
}
