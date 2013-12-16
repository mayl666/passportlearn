package com.sogou.upd.passport.manager.api.account.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-8
 * Time: 下午7:09
 */
public class CreateCookieApiParams extends BaseUserApiParams {

    @NotBlank(message = "ip地址不能为空")
    private String ip;

    private boolean autologin;

    public boolean isAutologin() {
        return autologin;
    }

    public void setAutologin(boolean autologin) {
        this.autologin = autologin;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
