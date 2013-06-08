package com.sogou.upd.passport.manager.proxy.account.form;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.manager.proxy.BaseApiParameters;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.AssertTrue;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 上午10:25
 */
public class BindMobileApiParams extends BaseApiParameters {

    @NotBlank(message = "passport_id不允许为空")
    private String userid;

    @NotBlank(message = "新手机号不允许为空")
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

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
