package com.sogou.upd.passport.web.account.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 14-3-23
 * Time: 下午9:36
 * To change this template use File | Settings | File Templates.
 */
public class FindPwdCheckSmscodeParams extends BaseAccountParams {
    @NotBlank(message = "验证码不允许为空!")
    protected String smscode;

    public String getSmscode() {
        return smscode;
    }

    public void setSmscode(String smscode) {
        this.smscode = smscode;
    }
}

