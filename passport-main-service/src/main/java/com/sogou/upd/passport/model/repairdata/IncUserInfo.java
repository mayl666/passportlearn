package com.sogou.upd.passport.model.repairdata;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 14-7-15
 * Time: 下午6:30
 * To change this template use File | Settings | File Templates.
 */
public class IncUserInfo {

    private String inc_type;
    private String userid;
    private String password;
    private String passwordtype;
    private String flag;

    public String getInc_type() {
        return inc_type;
    }

    public void setInc_type(String inc_type) {
        this.inc_type = inc_type;
    }

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

    public String getPasswordtype() {
        return passwordtype;
    }

    public void setPasswordtype(String passwordtype) {
        this.passwordtype = passwordtype;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
