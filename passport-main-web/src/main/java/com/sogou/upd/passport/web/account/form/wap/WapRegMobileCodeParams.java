package com.sogou.upd.passport.web.account.form.wap;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.WapConstant;
import com.sogou.upd.passport.common.validation.constraints.Phone;
import com.sogou.upd.passport.common.validation.constraints.Ru;
import com.sogou.upd.passport.common.validation.constraints.Skin;
import com.sogou.upd.passport.common.validation.constraints.V;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;

/**
 * wap2.0注册参数类
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-9-9
 * Time: 下午8:12
 * To change this template use File | Settings | File Templates.
 */
public class WapRegMobileCodeParams {

    @Phone
    @NotBlank(message = "手机号码不允许为空!")
    private String mobile;
    @V
    private String v = WapConstant.WAP_TOUCH; //版本号,默认v=5
    private String client_id = String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID);
    @URL
    @Ru
    private String ru = CommonConstant.DEFAULT_WAP_INDEX_URL; //登录来源
    @Skin
    private String skin = CommonConstant.WAP_DEFAULT_SKIN;//皮肤参数
    private String errorMsg;//错误信息
    private int needCaptcha;//是否需要输入验证码:0-不需要；1-需要
    private String captcha;//验证码
    private String token;//标识码

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
