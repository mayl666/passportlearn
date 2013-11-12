package com.sogou.upd.passport.manager.form;

import com.sogou.upd.passport.common.validation.constraints.Ru;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;

/**
 * 用于web端的登陆的参数 User: liagng201716@sogou-inc.com Date: 13-5-12 Time: 下午10:01
 */
public class WebLoginParams extends BaseLoginParams{
    /**
     * 是否自动登陆，自动登陆cookie时长设置两周
     */
    private int autoLogin; // 0-否  1-真

    @URL
    @Ru
    private String ru;//登陆来源

    private String xd; // 跨域通信所用字段，直接返回

    public int getAutoLogin() {
        return autoLogin;
    }

    public void setAutoLogin(int autoLogin) {
        this.autoLogin = autoLogin;
    }

    public String getXd() {
        return xd;
    }

    public void setXd(String xd) {
        this.xd = xd;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }
}
