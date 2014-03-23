package com.sogou.upd.passport.web.account.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 14-3-23
 * Time: 下午9:36
 * To change this template use File | Settings | File Templates.
 */
public class ResetPwdParams extends AccountPwdParams {
    @NotBlank(message = "scode不允许为空!")
    protected String scode;

    public String getScode() {
        return scode;
    }

    public void setScode(String scode) {
        this.scode = scode;
    }
}
