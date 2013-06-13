package com.sogou.upd.passport.manager.api.account.form;

import com.sogou.upd.passport.manager.api.BaseApiParameters;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 上午11:53
 */
public class BindEmailApiParams extends BaseUserApiParams {


    @NotBlank(message = "密码不能为空！")
    private String password;

    @Email(message = "新绑定邮箱格式错误！")
    @NotBlank(message = "新绑定邮箱不能为空！")
    private String newbindemail;

    @Email
    private String oldbindemail;

    @Min(0)
    private int pwdtype; //密码类型，1为md5后的口令，缺省为明文密码

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewbindemail() {
        return newbindemail;
    }

    public void setNewbindemail(String newbindemail) {
        this.newbindemail = newbindemail;
    }

    public int getPwdtype() {
        return pwdtype;
    }

    public void setPwdtype(int pwdtype) {
        this.pwdtype = pwdtype;
    }

    public String getOldbindemail() {
        return oldbindemail;
    }

    public void setOldbindemail(String oldbindemail) {
        this.oldbindemail = oldbindemail;
    }
}
