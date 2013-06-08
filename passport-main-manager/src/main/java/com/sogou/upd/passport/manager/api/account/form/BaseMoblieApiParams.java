package com.sogou.upd.passport.manager.api.account.form;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.manager.api.BaseApiParameters;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.AssertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-6-7
 * Time: 下午9:25
 * To change this template use File | Settings | File Templates.
 */
public class BaseMoblieApiParams extends BaseApiParameters {

    @NotBlank(message = "手机号不允许为空")
    private String mobile;

    @AssertTrue(message = "请输入正确的手机号!")
    private boolean isValidPhone() {
        if (Strings.isNullOrEmpty(mobile)) {   // NotBlank已经校验过了，无需再校验
            return true;
        }
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
}
