package com.sogou.upd.passport.web.account.form.security;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-9 Time: 下午3:41 To change this template use
 * File | Settings | File Templates.
 */
public class WebBindEmailParams extends WebPwdParams {
    @NotBlank(message = "新绑定邮箱不能为空")
    @Email
    private String new_email;
    @Email
    private String old_email;

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
