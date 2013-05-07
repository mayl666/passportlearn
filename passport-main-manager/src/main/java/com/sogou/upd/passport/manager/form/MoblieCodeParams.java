package com.sogou.upd.passport.manager.form;

import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sun.javafx.beans.annotations.Default;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-8
 * Time: 下午5:57
 * To change this template use File | Settings | File Templates.
 */
public class MoblieCodeParams {

    @NotNull(message = "手机号码不允许为空!")
    private String mobile;
    @Min(value = 1, message = "client_id不允许为空!")
    private int client_id=0;

    @AssertTrue(message = "不支持的手机号格式!")
    private boolean isValidPhone() {
        if (PhoneUtil.verifyPhoneNumberFormat(mobile)) {
            return true;
        }
        return false;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }
}
