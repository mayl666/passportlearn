package com.sogou.upd.passport.manager.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-8-15
 * Time: 下午6:16
 * To change this template use File | Settings | File Templates.
 */
public class AccountSecureParams {
    private String subIp;

    public String getSubIp() {
        return subIp;
    }

    public void setSubIp(String subIp) {
        this.subIp = subIp;
    }
}
