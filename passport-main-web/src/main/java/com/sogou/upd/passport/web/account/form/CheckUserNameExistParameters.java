package com.sogou.upd.passport.web.account.form;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.common.validation.constraints.UserName;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.AssertTrue;

/**
 * User: mayan
 * Date: 13-4-15 Time: 下午5:15
 */
public class CheckUserNameExistParameters {
    @Length(min = 1, max = 100, message = "用户名错误！")
    @NotBlank(message = "用户名不允许为空!")
    private String username;

    private String client_id;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }
}
