package com.sogou.upd.passport.web.account.form.mapp;

import com.sogou.upd.passport.web.BaseWebParams;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * 移动端数据上报接口
 * User: shipengzhi
 * Date: 14-11-22
 * Time: 下午6:02
 */
public class MappStatReportParams {

    @Min(0)
    private int client_id; //应用ID
    @Min(0)
    protected long ct; //单位为毫秒
    @NotBlank(message = "data不允许为空！")
    private String data;
    @NotBlank(message = "code不允许为空！")
    private String code;

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public long getCt() {
        return ct;
    }

    public void setCt(long ct) {
        this.ct = ct;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
