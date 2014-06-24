package com.sogou.upd.passport.manager.form;

import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.validation.constraints.UserName;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-6-4
 * Time: 下午4:20
 * To change this template use File | Settings | File Templates.
 */
public class BaseRegUserNameParams {
    @Length(min = 1, max = 100, message = "用户名长度错误")
    @NotBlank(message = "账号不允许为空")
    @UserName
    protected String username;

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
}
