package com.sogou.upd.passport.web.account.form;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.utils.PhoneUtil;
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

    @AssertTrue(message = "用户账号格式错误")
    public boolean isCheckAccount() {
        if (Strings.isNullOrEmpty(username)) {
            return true;
        }
        if (username.indexOf("@") == -1) {
            if (!PhoneUtil.verifyPhoneNumberFormat(username)) {
                //个性账号格式是否拼配
                String regx = "[a-z]([a-zA-Z0-9_.-]{4,16})";
                if (!username.matches(regx)) {
                    return false;
                }
            }
        } else {
            //邮箱格式
            String regex = "(\\w)+(\\.\\w+)*@([\\w_\\-])+((\\.\\w+)+)";
            return username.matches(regex);
        }
        return true;
    }

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
