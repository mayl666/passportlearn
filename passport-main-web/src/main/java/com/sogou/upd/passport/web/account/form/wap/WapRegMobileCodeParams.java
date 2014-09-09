package com.sogou.upd.passport.web.account.form.wap;

import com.sogou.upd.passport.common.validation.constraints.Ru;
import com.sogou.upd.passport.common.validation.constraints.Skin;
import com.sogou.upd.passport.common.validation.constraints.V;
import com.sogou.upd.passport.web.account.form.MoblieCodeParams;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

/**
 * wap2.0注册参数类
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-9-9
 * Time: 下午8:12
 * To change this template use File | Settings | File Templates.
 */
public class WapRegMobileCodeParams extends MoblieCodeParams {
    @V
    @NotBlank(message = "版本号不允许为空!")
    private String v; //版本号
    @URL
    @Ru
    private String ru; //登录来源
    @Skin
    private String skin;//皮肤参数
    private String errorMsg;//错误信息
    private int needCaptcha;//是否需要输入验证码:0-不需要；1-需要

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
}
