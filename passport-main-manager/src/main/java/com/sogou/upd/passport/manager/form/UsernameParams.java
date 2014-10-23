package com.sogou.upd.passport.manager.form;

import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.validation.constraints.UserName;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 14-1-20
 * Time: 下午2:29
 * To change this template use File | Settings | File Templates.
 */
public class UsernameParams {
    @Length(min = 0, max = 100, message = "用户名长度错误")
//    @NotBlank(message = "用户名不允许为空")
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
