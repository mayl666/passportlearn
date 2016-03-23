package com.sogou.upd.passport.manager.form.connect;

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
    private String openid;
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
    private String code;
    //微信SDK未封装code换取token接口，需要服务端封装
    private String tcode; //微信的code，如果不为空，则accesstoken、refreshtoken、openid、code为空
    @Min(0)
    private Integer appid_type; //如果appidtype=1，则根据应用传入的client_id查询相关的第三方appid；如果appidtype=0，则使用sogou passport的appid；
    private String third_appid; //如果应用使用独立appid，需要传入不同第三方对应的appid； 如果不传，表示使用passport的appid；
    private String uniqname; //华为账号登陆时会传入uniqanme，服务端存储
    private String large_avatar;//facebook、line账号登录时会传入，服务端存储
    private String mid_avatar;//facebook、line账号登录时会传入，服务端存储
    private String tiny_avatar;//facebook、line账号登录时会传入，服务端存储

    private String type= "wap"; //wap表示手机app使用第三方SSO登录，返回sgid； token表示桌面客户端拿到第三方openid、accesstoken来登录，返回token；

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

    public Integer getAppid_type() {
        return appid_type;
    }

    public void setAppid_type(Integer appid_type) {
        this.appid_type = appid_type;
    }

    public String getTcode() {
        return tcode;
    }

    public void setTcode(String tcode) {
        this.tcode = tcode;
    }

    public String getThird_appid() {
        return third_appid;
    }

    public void setThird_appid(String third_appid) {
        this.third_appid = third_appid;
    }

    public String getUniqname() {
        return uniqname;
    }

    public void setUniqname(String uniqname) {
        this.uniqname = uniqname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLarge_avatar() {
        return large_avatar;
    }

    public void setLarge_avatar(String large_avatar) {
        this.large_avatar = large_avatar;
    }

    public String getMid_avatar() {
        return mid_avatar;
    }

    public void setMid_avatar(String mid_avatar) {
        this.mid_avatar = mid_avatar;
    }

    public String getTiny_avatar() {
        return tiny_avatar;
    }

    public void setTiny_avatar(String tiny_avatar) {
        this.tiny_avatar = tiny_avatar;
    }
}
