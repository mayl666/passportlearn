package com.sogou.upd.passport.web.account.form;

import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.validation.constraints.Ru;
import com.sogou.upd.passport.web.BaseWebParams;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-23 Time: 下午2:57 To change this template use
 * File | Settings | File Templates.
 */
public class BaseAccountParams extends BaseWebParams {

    @NotBlank(message = "账号不允许为空!")
    protected String username;
    @URL
    @Ru
    protected String ru;
    @NotBlank(message = "安全码不能为空")
    protected String scode;

    public String getScode() {
        return scode;
    }

    public void setScode(String scode) {
        this.scode = scode;
    }


    public String getUsername() {
        String internalUsername = AccountDomainEnum.getInternalCase(username);
        setUsername(internalUsername);
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
