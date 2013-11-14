package com.sogou.upd.passport.web.account.form;

import com.sogou.upd.passport.common.WapConstant;
import com.sogou.upd.passport.common.validation.constraints.Ru;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-11-14
 * Time: 下午2:39
 * To change this template use File | Settings | File Templates.
 */
public class WapIndexParams {
    @NotBlank(message = "v不允许为空!")
    @Min(0)
    private String v = WapConstant.WAP_COLOR;//wap版本:0-简易版；1-炫彩版；2-触屏版

    @NotBlank(message = "client_id不允许为空!")
    @Min(0)
    private String client_id;

    @NotBlank
    @URL
    @Ru
    private String ru;//登陆来源

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }
}
