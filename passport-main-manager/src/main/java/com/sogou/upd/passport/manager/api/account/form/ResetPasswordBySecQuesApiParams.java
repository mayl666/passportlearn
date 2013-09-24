package com.sogou.upd.passport.manager.api.account.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-8
 * Time: 下午5:22
 */
public class ResetPasswordBySecQuesApiParams extends BaseUserApiParams {

    @NotBlank(message = "密保答案不能为空")
    private String answer;

    @NotBlank(message = "用户新密码不能为空")
    private String newpassword;

    @NotBlank(message = "修改密码的IP")
    private String modifyip;

    public String getModifyip() {
        return modifyip;
    }

    public void setModifyip(String modifyip) {
        this.modifyip = modifyip;
    }

    public String getNewpassword() {
        return newpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
