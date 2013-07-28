package com.sogou.upd.passport.manager.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * sohu桌面应用token登录流程的参数类
 * 应用于：/getpairtoken、/authtoken、/refreshtoken
 * User: shipengzhi
 * Date: 13-7-24
 * Time: 下午11:21
 * To change this template use File | Settings | File Templates.
 */
public class PcAuthTokenParams {

    @NotBlank(message = "userid不允许为空")
    private String userid;   //登录账号
    @NotBlank(message = "appid不允许为空")
    private String appid;   //产品在passport申请的id，为四位数字

    private long livetime;  //token的生存期，单位为秒
    private int authtype = 0;  //0其它，1正常登录，2记住密码登录，3输入法同步信息，4用户点击链接
    private String ts="";  //客户端的实例id

    private String token; //用户获取到的token
    private String ru; //token正确时的调整地址

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public long getLivetime() {
        return livetime;
    }

    public void setLivetime(long livetime) {
        this.livetime = livetime;
    }

    public int getAuthtype() {
        return authtype;
    }

    public void setAuthtype(int authtype) {
        this.authtype = authtype;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }
}
