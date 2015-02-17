package com.sogou.upd.passport.web.account.form.mapp;

import org.hibernate.validator.constraints.NotBlank;


/**
 * Created with IntelliJ IDEA.
 * User: nahongxu
 * Date: 15-2-15
 * Time: 下午4:56
 * To change this template use File | Settings | File Templates.
 */
public class MappCheckSSOAppParams extends MappBaseParams {

    @NotBlank(message = "package_sgin不允许为空!")
    private String sign;

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
