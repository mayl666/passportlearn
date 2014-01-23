package com.sogou.upd.passport.web.account.form;

import com.sogou.upd.passport.common.parameter.AccountDomainEnum;

/**
 * sohu桌面应用token登录流程的参数类
 * 应用于：/getpairtoken接口
 * User: shipengzhi
 * Date: 13-7-24
 * Time: 下午11:21
 * To change this template use File | Settings | File Templates.
 */
public class PcAccountWebParams {

    private String userid="";   //之前的登录账号
    private String appid = "1044";   //产品在passport申请的id，为四位数字，默认为浏览器
    private String ts = "";  //客户端的实例id
    private String refresh_token;  //用refresh刷新token
    private String v = "0"; //用于浏览器版本判断
    private String openapptype;

    public String getUserid() {
        String internalUsername = AccountDomainEnum.getInternalCase(userid);
        setUserid(internalUsername);
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getOpenapptype() {
        return openapptype;
    }

    public void setOpenapptype(String openapptype) {
        this.openapptype = openapptype;
    }
}
