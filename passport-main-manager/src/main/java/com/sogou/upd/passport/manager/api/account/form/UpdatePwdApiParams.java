package com.sogou.upd.passport.manager.api.account.form;

import com.sogou.upd.passport.manager.api.BaseApiParams;
import org.hibernate.validator.constraints.NotBlank;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 下午12:11
 */
public class UpdatePwdApiParams extends BaseUserApiParams {

    @NotBlank(message = "原密码不能为空！")
    private String password;

    @NotBlank(message = "新密码不能为空！")
    private String newpassword;

    @NotBlank(message = "修改密保的Ip不能为空！")
    private String modifyip;

    public String getNewpassword() {
        return newpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getModifyip() {
        return modifyip;
    }

    public void setModifyip(String modifyip) {
        this.modifyip = modifyip;
    }
}
