package com.sogou.upd.passport.proxy.manager.from;

import org.hibernate.validator.constraints.NotBlank;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 上午10:22
 */
public class BaseApiParameters {

    @NotBlank(message = "1")
    private String clientId;

    private String code;

    private String ct;

    public String getCt() {
        return ct;
    }

    public void setCt(String ct) {
        this.ct = ct;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
