package com.sogou.upd.passport.manager.form;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.common.validation.constraints.Password;
import com.sogou.upd.passport.common.validation.constraints.Ru;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.AssertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-9-12
 * Time: 上午11:03
 * To change this template use File | Settings | File Templates.
 */
public class PCOAuth2RegisterParams {
    @NotBlank(message = "client_id不能为空")
    private String client_id = String.valueOf(CommonConstant.PC_CLIENTID);

    private String instance_id = null;
    @NotBlank(message = "注册账号不允许为空!")
    private String username;

    @Password(message = "密码必须为字母、数字、字符且长度为6~16位!")
    @NotBlank(message = "请输入密码!")
    private String password;
    private String captcha;//验证码
    private String token;//标识码
    @URL
    @Ru
    private String ru;//回跳url

    @AssertTrue(message = "暂不支持sohu账号注册")
    private boolean isSohuUserName() {
        if (Strings.isNullOrEmpty(username)) {
            return true;
        }
        return StringUtil.isSohuUserName(username);
    }

    @AssertTrue(message = "用户账号格式错误")
    private boolean checkAccount() {
        if (!PhoneUtil.verifyPhoneNumberFormat(username)) {
            //个性账号格式是否拼配
            String regx = "[a-z]([a-zA-Z0-9_.]{4,16})";
            if (!username.matches(regx)) {
                return false;
            }
        }
        return true;
    }


    public String getInstance_id() {
        return instance_id;
    }

    public void setInstance_id(String instance_id) {
        this.instance_id = instance_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }
}
