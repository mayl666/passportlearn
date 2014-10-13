package com.sogou.upd.passport.web.account.form.wap;

import com.sogou.upd.passport.common.WapConstant;
import com.sogou.upd.passport.common.validation.constraints.PasswordValidator;
import com.sogou.upd.passport.common.validation.constraints.Ru;
import com.sogou.upd.passport.common.validation.constraints.Skin;
import com.sogou.upd.passport.common.validation.constraints.V;
import com.sogou.upd.passport.manager.api.account.form.RegMobileParams;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.AssertTrue;

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
    @NotBlank(message = "请阅读《注册服务协议》")
    private String agreement;//用户是否同意《注册服务协议》，非空同意，空表示不同意

    @AssertTrue(message = "password格式错误")
    private boolean isPasswordNotAllowedWrongFormat() {
        if (WapConstant.WAP_COLOR.equals(v)) {   // wap2.0的密码需要校验；html5的密码是前端做的md5，所以此处不能直接添加validator
            PasswordValidator pv = new PasswordValidator();
            boolean flag = pv.isValid(password, null);
            return flag;
        }
        return true;
    }

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

    public String getAgreement() {
        return agreement;
    }

    public void setAgreement(String agreement) {
        this.agreement = agreement;
    }
}
