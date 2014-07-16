package com.sogou.upd.passport.web.account.form;

import com.sogou.upd.passport.common.validation.constraints.Ru;
import com.sogou.upd.passport.web.BaseWebParams;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-7-3
 * Time: 下午2:21
 * To change this template use File | Settings | File Templates.
 */
public class CheckMobileSmsParams extends BaseWebParams {

    @NotBlank(message = "账号不允许为空!")
    protected String username;
    @URL
    @Ru
    protected String ru;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }
}
