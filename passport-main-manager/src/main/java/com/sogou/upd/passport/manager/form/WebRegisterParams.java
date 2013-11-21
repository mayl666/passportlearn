package com.sogou.upd.passport.manager.form;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.CommonHelper;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.utils.PhoneUtil;

import com.sogou.upd.passport.common.validation.constraints.Password;
import com.sogou.upd.passport.common.validation.constraints.Ru;
import com.sogou.upd.passport.common.validation.constraints.UserName;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;

/**
 * User: mayan
 * Date: 13-4-15 Time: 下午5:15
 */
public class WebRegisterParams {
    @NotBlank(message = "client_id不允许为空!")
    @Min(0)
    private String client_id;

    @Length(min = 1, max = 100, message = "用户名错误！")
    @NotBlank(message = "注册账号不允许为空!")
    @UserName
    private String username;

    @Password(message = "密码必须为字母、数字、字符且长度为6~16位!")
    @NotBlank(message = "请输入密码!")
    private String password;
    private String captcha;//验证码
    //  @NotBlank(message = "标识码不允许为空!")
    private String token;//标识码
    @URL
    @Ru
    private String ru;//回跳url

    @AssertTrue(message = "暂不支持sohu账号注册")
    private boolean isSohuUserName() {
        if (Strings.isNullOrEmpty(username)) {   // NotBlank已经校验过了，无需再校验
            return true;
        }
        return StringUtil.isSohuUserName(username);
    }

//    @AssertTrue(message = "用户账号格式错误")
//    private boolean isCheckAccount() {
//        if (Strings.isNullOrEmpty(username)) {
//            return true;
//        }
//        if (username.indexOf("@") == -1) {
//            if (!PhoneUtil.verifyPhoneNumberFormat(username)) {
//                //个性账号格式是否拼配
//                String regx = "[a-z]([a-zA-Z0-9_.-]{4,16})";
//                if (!username.matches(regx)) {
//                    return false;
//                }
//            }
//        } else {
//            //邮箱格式
//            String regex = "^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$";
//            return username.matches(regex);
//        }
//        return true;
//    }

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }
}
