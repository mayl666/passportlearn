package com.sogou.upd.passport.web.account.form;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * 登陆后获取登录信息接口
 * User: mayan
 * Date: 14-3-3
 * Time: 下午4:43
 * To change this template use File | Settings | File Templates.
 */
public class AfterAuthParams {
    @NotBlank(message = "openid不允许为空！")
    private String openid;
    @NotBlank(message = "access_token不允许为空！")
    private String access_token;
    @Min(0)
    private long expires_in;
    @Min(0)
    private int client_id;
    @Min(0)
    private int isthird;
    @NotBlank(message = "客户端唯一标识不允许为空！")
    private String instance_id;
    @NotBlank(message = "code不允许为空！")
    private String code;


    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public int getIsthird() {
        return isthird;
    }

    public void setIsthird(int isthird) {
        this.isthird = isthird;
    }

    public String getInstance_id() {
        return instance_id;
    }

    public void setInstance_id(String instance_id) {
        this.instance_id = instance_id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
