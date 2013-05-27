package com.sogou.upd.passport.manager.form;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-5-25
 * Time: 下午10:07
 * To change this template use File | Settings | File Templates.
 */
public class AccountPwdScodeParams extends BaseAccountParams {

    private String password;
    private String scode;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getScode() {
        return scode;
    }

    public void setScode(String scode) {
        this.scode = scode;
    }
}
