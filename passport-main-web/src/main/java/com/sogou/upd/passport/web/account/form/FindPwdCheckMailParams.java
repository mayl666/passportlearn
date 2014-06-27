package com.sogou.upd.passport.web.account.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-6-26
 * Time: 下午8:37
 * To change this template use File | Settings | File Templates.
 */
public class FindPwdCheckMailParams extends BaseAccountParams {
    @NotBlank(message = "scode不允许为空!")
    protected String scode;

    public String getScode() {
        return scode;
    }

    public void setScode(String scode) {
        this.scode = scode;
    }
}
