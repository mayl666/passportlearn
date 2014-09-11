package com.sogou.upd.passport.web.account.form.wap;

import com.sogou.upd.passport.common.validation.constraints.Ru;
import com.sogou.upd.passport.common.validation.constraints.Skin;
import com.sogou.upd.passport.common.validation.constraints.V;
import com.sogou.upd.passport.manager.api.account.form.RegMobileParams;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

/**
 * wap2.0正式注册参数类
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-9-11
 * Time: 上午12:40
 * To change this template use File | Settings | File Templates.
 */
public class WapV2RegParams extends RegMobileParams {

    @V
    @NotBlank(message = "版本号不允许为空!")
    private String v;
    @NotBlank(message = "安全校验码不允许为空")
    private String scode;
    @Skin
    private String skin;
    @URL
    @Ru
    private String ru;
    private String errorMsg;//错误信息
    private int needCaptcha;//是否需要输入验证码:0-不需要；1-需要

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getScode() {
        return scode;
    }

    public void setScode(String scode) {
        this.scode = scode;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
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
