package com.sogou.upd.passport.manager.form;

import com.sogou.upd.passport.common.WapConstant;
import com.sogou.upd.passport.common.validation.constraints.Ru;
import com.sogou.upd.passport.common.validation.constraints.Skin;
import com.sogou.upd.passport.common.validation.constraints.V;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;

/**
 * 短信登录参数
 * User: chengang
 * Date: 15-6-5
 * Time: 上午11:12
 */
public class SmsCodeLoginParams extends UsernameParams {


    /**
     * 登录校验码
     */
    @NotBlank(message = "短信校验码不允许为空!")
    private String smsCode;

    /**
     * client_id
     */
    @NotBlank(message = "client_id不允许为空!")
    @Min(0)
    private String client_id;

    /**
     * 登录来源
     */
    @URL
    @Ru
    private String ru = WapConstant.WAP_INDEX;

    /**
     * 验证码 用户连续3次登陆失败需要输入验证码
     */
    private String captcha;

    /**
     * 标识码
     */
    private String token;

    /**
     * 皮肤
     */
    @Skin
    private String skin;

    @NotBlank(message = "v is null")
    @V
    private String v = WapConstant.WAP_COLOR;//wap版本:1-简易版；2-炫彩版；5-触屏版

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

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }
}
