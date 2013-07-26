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
public class PcTokenParams {

    @NotBlank(message = "userid不允许为空")
    private String userid;   //登录账号
    @NotBlank(message = "密码不允许为空")
    private String password;  //密码的md5
    @NotBlank(message = "appid不允许为空")
    private String appid;   //产品在passport申请的id，为四位数字

    private long livetime;  //token的生存期，单位为秒
    private int authtype = 0;  //0其它，1正常登录，2记住密码登录，3输入法同步信息，4用户点击链接
    private String timestamp; //用于sig的时间戳
    private String sig;  //用于用refresh刷新token，userid + appid + refresh_token + timestamp + key 的md5
    private String ts;  //客户端的实例id

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }
}
