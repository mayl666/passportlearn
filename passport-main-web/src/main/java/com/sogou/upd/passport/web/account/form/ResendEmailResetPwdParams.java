package com.sogou.upd.passport.web.account.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-8-7
 * Time: 下午2:38
 * To change this template use File | Settings | File Templates.
 */
public class ResendEmailResetPwdParams extends BaseWebResetPwdParams {

    @NotBlank(message = "账号不允许为空!")
    protected String to_email;  //发送激活邮件的主账号

    public String getTo_email() {
        return to_email;
    }

    public void setTo_email(String to_email) {
        this.to_email = to_email;
    }
}
