package com.sogou.upd.passport.manager.api.account.form;

import org.hibernate.validator.constraints.URL;

/**
 * 续种cookie
 * User: mayan
 * Date: 13-10-10
 * Time: 上午11:36
 */
public class ReNewCookieApiParams extends BaseUserApiParams {
    @URL
    private String ru;

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }
}
