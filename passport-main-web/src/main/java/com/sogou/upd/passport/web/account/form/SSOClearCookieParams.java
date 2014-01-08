package com.sogou.upd.passport.web.account.form;

import com.sogou.upd.passport.common.validation.constraints.Ru;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-12-31
 * Time: 下午4:09
 * To change this template use File | Settings | File Templates.
 */
public class SSOClearCookieParams {
    @Ru
    protected String ru;

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }


}
