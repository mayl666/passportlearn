package com.sogou.upd.passport.manager.form;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.validation.constraints.Domain;
import com.sogou.upd.passport.common.validation.constraints.Ru;

import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

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

    @Ru
    private String xd; // 跨域通信所用字段，直接返回 TODO 应该加ru限制，需要完备测试后再加

    private String module; // 登录类型（非密码型），quicklogin--已检测到登录态的快速登录

    private String key; //其他登录类型（非密码型）需验证的登录标识
    @Domain
    private String domain;  // 非sogou.com域名的业务线使用，登录成功后种非sogou.com域的cookie

    @Min(0)
    @Max(1)
    private int needsgid = 1; //是否需要 sgid

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

    public int getNeedsgid() {
        return needsgid;
    }

    public void setNeedsgid(int needsgid) {
        this.needsgid = needsgid;
    }
}
