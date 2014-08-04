package com.sogou.upd.passport.web.account.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-6-29
 * Time: 下午4:22
 * To change this template use File | Settings | File Templates.
 */
public class CheckSmsCodeAndGetSecInfoParams extends CheckSecMobileParams {

    @NotBlank(message = "验证码不允许为空!")
    protected String smscode;

    public String getSmscode() {
        return smscode;
    }

    public void setSmscode(String smscode) {
        this.smscode = smscode;
    }

}
