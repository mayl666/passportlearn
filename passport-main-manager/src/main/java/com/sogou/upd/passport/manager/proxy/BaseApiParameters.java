package com.sogou.upd.passport.manager.proxy;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * 内部API参数基类
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-6
 * Time: 上午10:22
 */
public class BaseApiParameters {

    @Min(0)
    @NotBlank(message = "client_id不允许为空")
    private int client_id; //应用id
    @NotBlank(message = "code不允许为空")
    private String code; // MD5（passport_id+client_id+ct+server_secret+ct)方法签名
    @Min(0)
    @NotBlank(message = "ct不允许为空")
    private long ct; //单位为秒

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public long getCt() {
        return ct;
    }

    public void setCt(long ct) {
        this.ct = ct;
    }
}
