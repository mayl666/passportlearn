package com.sogou.upd.passport.web.account.form;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.AssertTrue;

/**
 * 浏览器桌面端注册名验证
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-9-15
 * Time: 下午11:07
 * To change this template use File | Settings | File Templates.
 */
public class PCAccountCheckRegNameParams {
    @NotBlank(message = "用户名不允许为空!")
    private String username;

    @AssertTrue(message = "用户账号格式错误")
    public boolean isCheckAccount() {
        if (Strings.isNullOrEmpty(username)) {
            return true;
        }
        if (!PhoneUtil.verifyPhoneNumberFormat(username)) {
            //个性账号格式是否拼配
            String regx = "[a-z]([a-zA-Z0-9_.]{4,16})";
            if (!username.matches(regx)) {
                return false;
            }
        }
        return true;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
