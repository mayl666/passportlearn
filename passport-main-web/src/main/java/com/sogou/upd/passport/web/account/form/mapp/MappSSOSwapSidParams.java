package com.sogou.upd.passport.web.account.form.mapp;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: nahongxu
 * Date: 15-2-17
 * Time: 下午3:50
 * To change this template use File | Settings | File Templates.
 */
public class MappSSOSwapSidParams extends MappBaseParams {
    @NotBlank(message = "stoken不允许为空!")
    private String stoken;

    public String getStoken() {
        return stoken;
    }

    public void setStoken(String stoken) {
        this.stoken = stoken;
    }
}
