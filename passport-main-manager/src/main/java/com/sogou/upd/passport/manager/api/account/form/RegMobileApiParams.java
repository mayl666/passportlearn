package com.sogou.upd.passport.manager.api.account.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-7-4 Time: 上午11:19 To change this template use File | Settings | File Templates.
 */
public class RegMobileApiParams extends BaseMoblieApiParams {

    @NotBlank(message = "密码不允许为空")
    private String password;  //必须为md5
    @NotBlank(message = "注册IP不允许为空")
    private String ip;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
