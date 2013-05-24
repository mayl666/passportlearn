package com.sogou.upd.passport.manager.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-23 Time: 下午6:54 To change this template use
 * File | Settings | File Templates.
 */
public class AccountScodeParams extends BaseAccountParams {
    @NotBlank(message = "scode不允许为空!")
    protected String scode;

    public String getScode() {
        return scode;
    }

    public void setScode(String scode) {
        this.scode = scode;
    }
}
