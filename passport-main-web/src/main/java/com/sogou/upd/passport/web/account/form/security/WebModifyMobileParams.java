package com.sogou.upd.passport.web.account.form.security;

import com.sogou.upd.passport.common.validation.constraints.Phone;
import com.sogou.upd.passport.web.account.form.BaseWebParams;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-9 Time: 下午4:27 To change this template use
 * File | Settings | File Templates.
 */
public class WebModifyMobileParams extends BaseWebParams {
    @NotBlank(message = "验证码不允许为空!")
    protected String smscode;
    @NotBlank(message = "scode不允许为空")
    protected String scode;
    @NotBlank(message = "新手机号不能为空")
    @Phone
    protected String new_mobile;

    public String getSmscode() {
        return smscode;
    }

    public void setSmscode(String smscode) {
        this.smscode = smscode;
    }

    public String getScode() {
        return scode;
    }

    public void setScode(String scode) {
        this.scode = scode;
    }

    public String getNew_mobile() {
        return new_mobile;
    }

    public void setNew_mobile(String new_mobile) {
        this.new_mobile = new_mobile;
    }
}
