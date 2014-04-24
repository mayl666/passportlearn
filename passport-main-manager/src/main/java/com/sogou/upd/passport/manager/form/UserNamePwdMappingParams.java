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

    private String username;
    private String pwd;

    public UserNamePwdMappingParams() {
    }

    public UserNamePwdMappingParams(String username, String pwd) {
        this.username = username;
        this.pwd = pwd;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
