package com.sogou.upd.passport.manager.form;

import com.sogou.upd.passport.common.validation.constraints.Ru;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-12-31
 * Time: 下午4:09
 * To change this template use File | Settings | File Templates.
 */
public class SSOCookieParams {
    @NotBlank(message = "sginf不允许为空")
    private String sginf;
    @NotBlank(message = "sgrdig不允许为空")
    private String sgrdig;
    @NotBlank(message = "code1不允许为空")
    protected String code1;
    @NotBlank(message = "code2不允许为空")
    protected String code2;
    @NotBlank(message = "domain不允许为空")
    private String domain;       //所种cookie的域
    @Ru
    protected String ru;

    private String cb; //非搜狗域种cookie成功后的jsonp回调函数，如果cb不为空，则返回json，不做ru跳转

    public String getSginf() {
        return sginf;
    }

    public void setSginf(String sginf) {
        this.sginf = sginf;
    }

    public String getSgrdig() {
        return sgrdig;
    }

    public void setSgrdig(String sgrdig) {
        this.sgrdig = sgrdig;
    }

    public String getCode1() {
        return code1;
    }

    public void setCode1(String code1) {
        this.code1 = code1;
    }

    public String getCode2() {
        return code2;
    }

    public void setCode2(String code2) {
        this.code2 = code2;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getCb() {
        return cb;
    }

    public void setCb(String cb) {
        this.cb = cb;
    }
}
