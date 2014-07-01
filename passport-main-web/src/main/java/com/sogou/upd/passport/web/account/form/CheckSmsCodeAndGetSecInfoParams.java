package com.sogou.upd.passport.web.account.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-6-29
 * Time: 下午4:22
 * To change this template use File | Settings | File Templates.
 */
public class CheckSmsCodeAndGetSecInfoParams extends FindPwdCheckSmscodeParams {

    @NotBlank(message = "密保手机不可为空!")
    protected String sec_mobile;

    public String getSec_mobile() {
        return sec_mobile;
    }

    public void setSec_mobile(String sec_mobile) {
        this.sec_mobile = sec_mobile;
    }
}