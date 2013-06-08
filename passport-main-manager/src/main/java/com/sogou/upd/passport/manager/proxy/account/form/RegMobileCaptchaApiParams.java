package com.sogou.upd.passport.manager.proxy.account.form;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.manager.proxy.BaseApiParameters;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.AssertTrue;

/**
 * 带验证码的手机号注册参数类
 * User: shipengzhi
 * Date: 13-6-7
 * Time: 下午9:13
 * To change this template use File | Settings | File Templates.
 */
public class RegMobileCaptchaApiParams extends BaseApiParameters {

    @NotBlank(message = "手机号码不允许为空")
    private String mobile;
    @NotBlank(message = "密码不允许为空")
    private String password;  //必须为md5
    @NotBlank(message = "手机验证码不允许为空")
    private String captcha;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
