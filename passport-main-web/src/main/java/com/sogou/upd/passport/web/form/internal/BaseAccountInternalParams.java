package com.sogou.upd.passport.web.form.internal;

import com.sogou.upd.passport.web.form.BaseAccountParams;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-31 Time: 下午12:18 To change this template
 * use File | Settings | File Templates.
 */
public class BaseAccountInternalParams extends BaseAccountParams {

    @NotBlank(message = "系统时间不允许为空!")
    protected String ct;
    @NotBlank(message = "数据签名不能为空!")
    protected String code;
    // @NotBlank(message = "IP不允许为空!")   TODO:是否必需还是可选
    protected String ip;

    public String getCt() {
        return ct;
    }

    public void setCt(String ct) {
        this.ct = ct;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
