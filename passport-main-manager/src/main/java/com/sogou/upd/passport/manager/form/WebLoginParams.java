package com.sogou.upd.passport.manager.form;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.validation.constraints.Ru;
import com.sogou.upd.passport.oauth2.common.types.ConnectDomainEnum;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.AssertTrue;

/**
 * 用于web端的登陆的参数
 * User: liagng201716@sogou-inc.com
 * Date: 13-5-12 Time: 下午10:01
 */
public class WebLoginParams extends BaseLoginParams {

    private int pwdtype = CommonConstant.PWD_TYPE_EXPRESS; //密码类型，1为md5后的口令，缺省为明文密码
    /**
     * 是否自动登陆，自动登陆cookie时长设置两周
     */
    private int autoLogin; // 0-否  1-真

    @URL
    @Ru
    private String ru;//登陆来源

    private String xd; // 跨域通信所用字段，直接返回 TODO 应该加ru限制，需要完备测试后再加

    private String module; // 登录类型（非密码型），quicklogin--已检测到登录态的快速登录

    private String key; //其他登录类型（非密码型）需验证的登录标识

    private String domain;  // 非sogou.com域名的业务线使用，登录成功后种非sogou.com域的cookie

    @AssertTrue(message = "不支持的domain")
    private boolean isSupportDomain() {
        if (domain != null && !ConnectDomainEnum.isSupportDomain(domain)) {
            return false;
        }
        return true;
    }

    public int getAutoLogin() {
        return autoLogin;
    }

    public void setAutoLogin(int autoLogin) {
        this.autoLogin = autoLogin;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public String getXd() {
        return xd;
    }

    public void setXd(String xd) {
        this.xd = xd;
    }

    public int getPwdtype() {
        return pwdtype;
    }

    public void setPwdtype(int pwdtype) {
        this.pwdtype = pwdtype;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
