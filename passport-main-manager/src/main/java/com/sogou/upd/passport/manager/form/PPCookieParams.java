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
public class PPCookieParams {
    @NotBlank(message = "ppinf不允许为空")
    private String ppinf;
    @NotBlank(message = "pprdig不允许为空")
    private String pprdig;
    @NotBlank(message = "passport不允许为空")
    private String passport;
    @NotBlank(message = "code1不允许为空")
    protected String code1;
    @NotBlank(message = "code2不允许为空")
    protected String code2;
    @NotBlank(message = "code不允许为空")
    protected String code;


    @NotBlank(message = "lastdomain不允许为空")
    private String lastdomain;

    @NotBlank(message = "时间戳不允许为空")
    private String s = "0"; //时间戳，单位毫秒

    private String livetime = "0"; //cookie有效期  1为2周

    @NotBlank(message = "ru不允许为空")
    @Ru
    protected String ru;

    public String getPpinf() {
        return ppinf;
    }

    public void setPpinf(String ppinf) {
        this.ppinf = ppinf;
    }

    public String getPprdig() {
        return pprdig;
    }

    public void setPprdig(String pprdig) {
        this.pprdig = pprdig;
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
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

    public String getLastdomain() {
        return lastdomain;
    }

    public void setLastdomain(String lastdomain) {
        this.lastdomain = lastdomain;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLivetime() {
        return livetime;
    }

    public void setLivetime(String livetime) {
        this.livetime = livetime;
    }
}
