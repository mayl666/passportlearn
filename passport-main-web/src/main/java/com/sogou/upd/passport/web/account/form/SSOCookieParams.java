package com.sogou.upd.passport.web.account.form;

import com.sogou.upd.passport.common.validation.constraints.Ru;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

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

    @Ru
    protected String ru;

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


}
