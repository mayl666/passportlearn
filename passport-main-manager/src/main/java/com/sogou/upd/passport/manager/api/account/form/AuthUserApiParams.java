package com.sogou.upd.passport.manager.api.account.form;

import com.sogou.upd.passport.manager.api.BaseApiParameters;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * 用户名密码校验的内部接口参数类
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 上午10:21
 */
public class AuthUserApiParams extends BaseUserApiParams {

    @NotBlank(message = "密码不允许为空")
    private String password;
    @Min(0)
    private int pwdtype = 0; //密码类型，1为md5后的口令，缺省为明文密码 TODO 暂时没应用用到
    @Min(0)
    private int usertype; // userid为手机号时，usertype=1；usertype默认为0，userid值为全域名id，如：test-1@sohu.com 或 昵称 eg:zhangsan TODO 暂时用不到此参数

    @NotBlank(message = "用户ip不允许为空")
    private String ip;

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

    public int getUsertype() {
        return usertype;
    }

    public void setUsertype(int usertype) {
        this.usertype = usertype;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
