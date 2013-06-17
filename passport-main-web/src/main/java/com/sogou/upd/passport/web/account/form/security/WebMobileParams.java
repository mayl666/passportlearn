package com.sogou.upd.passport.web.account.form.security;

import com.sogou.upd.passport.common.validation.constraints.Phone;
import com.sogou.upd.passport.web.account.form.BaseWebParams;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-9 Time: 下午3:56 To change this template use
 * File | Settings | File Templates.
 */
public class WebMobileParams extends BaseWebParams {
    @NotBlank(message = "手机号不允许为空!")
    @Phone
    protected String new_mobile;

    public String getNew_mobile() {
        return new_mobile;
    }

    public void setNew_mobile(String new_mobile) {
        this.new_mobile = new_mobile;
    }
}
