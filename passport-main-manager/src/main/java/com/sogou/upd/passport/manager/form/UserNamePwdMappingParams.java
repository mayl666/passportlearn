package com.sogou.upd.passport.manager.form;

/**
 * 手机重置密码时用户名和密码实体类
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-4-24
 * Time: 下午3:11
 * To change this template use File | Settings | File Templates.
 */
public class UserNamePwdMappingParams {

    private String mobile;
    private String pwd;

    public UserNamePwdMappingParams() {
    }

    public UserNamePwdMappingParams(String mobile, String pwd) {
        this.mobile = mobile;
        this.pwd = pwd;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
