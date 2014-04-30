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
    private int isthird=0;
    private String refresh_token;
    @NotBlank(message = "客户端唯一标识不允许为空！")
    private String instance_id;
    @NotBlank(message = "code不允许为空！")
    private String code;

    @Min(0)
    private int appidtype=0; //如果appidtype=1，则根据应用传入的client_id查询相关的第三方appid；如果appidtype=0，则使用sogou passport的appid；


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

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public int getAppidtype() {
        return appidtype;
    }

    public void setAppidtype(int appidtype) {
        this.appidtype = appidtype;
    }
}
