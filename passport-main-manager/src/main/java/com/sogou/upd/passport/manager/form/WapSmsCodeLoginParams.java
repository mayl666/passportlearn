package com.sogou.upd.passport.manager.form;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.WapConstant;
import com.sogou.upd.passport.common.validation.constraints.Phone;
import com.sogou.upd.passport.common.validation.constraints.Ru;
import com.sogou.upd.passport.common.validation.constraints.Skin;
import com.sogou.upd.passport.common.validation.constraints.V;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Value;

/**
 * 短信登录参数类
 * User: chengang
 * Date: 15-6-8
 * Time: 下午3:45
 */
public class WapSmsCodeLoginParams extends UsernameParams {

    /**
     * 手机号
     */
    @Phone
    @NotBlank(message = "手机号码不允许为空!")
    private String mobile;

    /**
     * 登录校验码
     */
    @NotBlank(message = "短信校验码不允许为空!")
    private String smsCode;

    /**
     * 版本号
     */
    @V
    @Value(value = WapConstant.WAP_TOUCH)
    private String v; //版本号,默认v=5

    /**
     * client_id
     */
    @Value(value = CommonConstant.SGPP_DEFAULT_CLIENTID + "")
    private String client_id;

    /**
     * 登录来源
     */
    @URL
    @Ru
    @Value(value = CommonConstant.DEFAULT_WAP_URL)
    private String ru;//登录来源

    /**
     * 皮肤
     */
    @Skin
    @Value(value = WapConstant.WAP_SKIN_GREEN)
    private String skin;//皮肤参数

    /**
     * 错误信息
     */
    private String errorMsg;//错误信息

    /**
     * 是否需要验证码
     */
    private int needCaptcha;//是否需要输入验证码:0-不需要；1-需要

    /**
     * 验证码
     */
    private String captcha;//验证码

    /**
     * 标识码
     */
    private String token;//标识码

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getNeedCaptcha() {
        return needCaptcha;
    }

    public void setNeedCaptcha(int needCaptcha) {
        this.needCaptcha = needCaptcha;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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


}
