package com.sogou.upd.passport.web.account.form;

import com.sogou.upd.passport.common.WapConstant;
import com.sogou.upd.passport.common.validation.constraints.Ru;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-11-14
 * Time: 下午2:39
 * To change this template use File | Settings | File Templates.
 */
public class WapIndexParams {
    @NotBlank(message = "v is null")
    private String v = WapConstant.WAP_COLOR;//wap版本:1-简易版；2-炫彩版；5-触屏版

    @NotBlank(message = "client_id is null")
    @Min(0)
    private String client_id;

    @NotBlank(message = "ru is null")
    @URL
    @Ru
    private String ru = WapConstant.WAP_INDEX;//登陆来源

    private String errorMsg;//错误信息
    private int needCaptcha;//是否需要输入验证码:0-不需要；1-需要

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
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
}
