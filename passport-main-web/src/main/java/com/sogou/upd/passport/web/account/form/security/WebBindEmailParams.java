package com.sogou.upd.passport.web.account.form.security;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.validation.constraints.Ru;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

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
    @URL
    @Ru
    private String ru = CommonConstant.DEFAULT_INDEX_URL + "/web/security/emailverify";  // TODO:以后增加判断是否SOGOU域

    @AssertTrue(message = "新密保邮箱不能与原密保邮箱相同")
    public boolean isCheckNewIsOld() {
        if (new_email.equals(old_email)) { // NotBlank已经校验过了，无需再校验
            return false;
        }
        return true;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
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
