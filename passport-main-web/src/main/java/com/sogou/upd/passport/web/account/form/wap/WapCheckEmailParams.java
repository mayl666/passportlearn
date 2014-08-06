package com.sogou.upd.passport.web.account.form.wap;

import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.web.account.form.BaseWapResetPwdParams;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-7-7
 * Time: 下午4:38
 * To change this template use File | Settings | File Templates.
 */
public class WapCheckEmailParams extends BaseWapResetPwdParams {

    @NotBlank(message = "用户名不可为空")
    protected String username;
    @NotBlank(message = "安全码不可为空")
    protected String scode;

    public String getUsername() {
        String internalUsername = AccountDomainEnum.getInternalCase(username);
        setUsername(internalUsername);
        return username;
    }

    public void setUsername(String username) {
        if (username != null) {
            username = username.trim();
        }
        this.username = username;
    }

    public String getScode() {
        return scode;
    }

    public void setScode(String scode) {
        this.scode = scode;
    }
}
