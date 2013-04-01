package com.sogou.upd.passport.web.form;

import com.sogou.upd.passport.common.utils.PhoneUtil;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 桌面/移动客户端手机账号注册请求参数类
 * User: shipengzhi
 * Date: 13-3-30
 * Time: 下午3:03
 * To change this template use File | Settings | File Templates.
 */
public class MobileRegParams {

    @NotNull(message = "手机号码不允许为空！")
    private String mobile;
    @NotNull(message = "密码不允许为空！")
    private String password;
    @NotNull(message = "验证码不允许为空！")
    private String smscode;
    @Min(value = 1, message = "client_id不允许为空")
    private int client_id;
    @NotNull(message = "instance_id不允许为空！")
    private String instance_id;

    @AssertTrue(message = "不支持的第三方")
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

    public String getInstance_id() {
        return instance_id;
    }

    public void setInstance_id(String instance_id) {
        this.instance_id = instance_id;
    }

}
