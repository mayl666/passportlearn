package com.sogou.upd.passport.manager.form;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.WapConstant;
import com.sogou.upd.passport.common.validation.constraints.PasswordValidator;
import com.sogou.upd.passport.common.validation.constraints.Ru;
import com.sogou.upd.passport.common.validation.constraints.V;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;

/**
 * 用于web端的登陆的参数 User: liagng201716@sogou-inc.com Date: 13-5-12 Time: 下午10:01
 */
public class WapLoginParams extends UsernameParams {
    /**
     * 登陆密码
     */
    @NotBlank(message = "请输入密码！")
    private String password;

    /**
     * 验证码 用户连续3次登陆失败需要输入验证码
     */
    private String captcha;//验证码
    private String token;//标识码

    @NotBlank(message = "client_id不允许为空!")
    @Min(0)
    private String client_id;

    //    @NotBlank
    @URL
    @Ru
    private String ru = WapConstant.WAP_INDEX;//登陆来源


    @NotBlank(message = "v is null")
    @V
    private String v = WapConstant.WAP_COLOR;//wap版本:1-简易版；2-炫彩版；5-触屏版

    @AssertTrue(message = "username不能为空")
    private boolean isUserNameNotAllowedNull() {
        if (Strings.isNullOrEmpty(username)) {   // wap登录时，username不能为空
            return false;
        }
        return true;
    }

    @AssertTrue(message = "password格式错误")
    private boolean isPasswordNotAllowedWrongFormat() {
        if (WapConstant.WAP_COLOR.equals(v)) {   // wap2.0的密码需要校验；html5的密码是前端做的md5，所以此处不能直接添加validator
            PasswordValidator pv = new PasswordValidator();
            boolean flag = pv.isValid(password, null);
            return flag;
        }
        return true;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
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

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }
}
