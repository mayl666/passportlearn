package com.sogou.upd.passport.model.account;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 14-5-23
 * Time: 上午12:40
 * To change this template use File | Settings | File Templates.
 */
public class UserInfoTmp {

    private String userid;
    private String password;
    private String passwordtype;
    private String flag;

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
