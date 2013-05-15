package com.sogou.upd.passport.manager.form;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonHelper;
import com.sogou.upd.passport.common.parameter.PasswordTypeEnum;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.AssertTrue;

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
    @NotBlank(message = "请输入密码!")
    private String password;
    @NotBlank(message = "验证码不允许为空!")
    private String smscode;
    @NotBlank(message = "client_id不允许为空!")
    private String client_id;
    private int pwd_type = 0; // 可选项，缺省为MD5，1-明文

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

    @AssertTrue(message = "密码必须为字母、数字、字符且长度为6~16位!")
    private boolean isValidPassword() {
        if (this.pwd_type == PasswordTypeEnum.Plaintext.getValue()) {
            return CommonHelper.checkPasswd(this.password);
        } else {
            return true;
        }
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

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public int getPwd_type() {
        return pwd_type;
    }

    public void setPwd_type(int pwd_type) {
        this.pwd_type = pwd_type;
    }
}
