package com.sogou.upd.passport.manager.proxy.account.form;

import com.sogou.upd.passport.manager.proxy.BaseApiParameters;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * 用户名密码校验的内部接口参数类
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 上午10:21
 */
public class AuthUserApiParams extends BaseApiParameters{

    @NotBlank(message = "passport_id不允许为空")
    private String passport_id;
    @NotBlank(message = "密码不允许为空")
    private String password;
    @Min(0)
    private int pwdtype; //密码类型，1为md5后的口令，缺省为明文密码

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

    public int getPwdtype() {
        return pwdtype;
    }

    public void setPwdtype(int pwdtype) {
        this.pwdtype = pwdtype;
    }
}
