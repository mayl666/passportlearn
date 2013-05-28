package com.sogou.upd.passport.manager.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-24 Time: 下午3:10 To change this template use
 * File | Settings | File Templates.
 */
public class AccountSmsNewScodeParams extends AccountSmsScodeParams {
    @NotBlank(message = "新手机号不能为空")
    protected String new_mobile;

    public String getNew_mobile() {
        return new_mobile;
    }

    public void setNew_mobile(String new_mobile) {
        this.new_mobile = new_mobile;
    }
}
