package com.sogou.upd.passport.manager.form;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonHelper;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;

/**
 * 桌面/移动客户端手机账号重置密码请求参数类
 * User: shipengzhi
 * Date: 13-3-30
 * Time: 下午3:03
 * To change this template use File | Settings | File Templates.
 */
public class MobileModifyPwdParams {

    @NotBlank(message = "手机号码不允许为空!")
    private String mobile;
    @NotBlank(message = "密码不允许为空!")
    private String password;
    @NotBlank(message = "验证码不允许为空!")
    private String smscode;
    @Min(value = 1, message = "client_id不允许为空!")
    private int client_id;

    @AssertTrue(message = "不支持的手机号格式!")
    private boolean isValidPhone() {
        if (Strings.isNullOrEmpty(mobile)) {   // NotBlank已经校验过了，无需再校验
            return true;
        }
        if (PhoneUtil.verifyPhoneNumberFormat(mobile)) {
            return true;
        }
        return false;
    }

    @AssertTrue(message = "密码必须为字母或数字且长度大于6位!")
    private boolean isValidPassword() {
        return CommonHelper.checkPasswd(this.password);
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

    public String getSmscode() {
        return smscode;
    }

    public void setSmscode(String smscode) {
        this.smscode = smscode;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

}
