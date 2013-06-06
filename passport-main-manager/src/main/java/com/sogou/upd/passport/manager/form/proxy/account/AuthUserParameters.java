package com.sogou.upd.passport.manager.form.proxy.account;

import com.sogou.upd.passport.manager.form.proxy.BaseApiParameters;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 用于内部接口的用户名密码校验
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 上午10:21
 */
public class AuthUserParameters extends BaseApiParameters{

    @NotBlank(message = "1")
    private String passport_id;

    @NotBlank(message = "1")
    private String password;

    //密码是否经过加密
    private String pwdtype;


    public String getPassport_id() {
        return passport_id;
    }

    public void setPassport_id(String passport_id) {
        this.passport_id = passport_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPwdtype() {
        return pwdtype;
    }

    public void setPwdtype(String pwdtype) {
        this.pwdtype = pwdtype;
    }
}
