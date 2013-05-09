package com.sogou.upd.passport.manager.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-4-28 Time: 下午1:59 To change this template use
 * File | Settings | File Templates.
 */
public class AccountSecureParams {

    @NotBlank(message = "账号不允许为空!")
    private String passport_id;
    @NotBlank(message = "client_id不允许为空!")
    private String client_id;
    private String password;
    private String smscode;
    private String answer;
    // @NotNull(message = "密保方式不允许为空!") TODO：此参数是否有必要使用?
    private int secure_mode;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSmscode() {
        return smscode;
    }

    public void setSmscode(String smscode) {
        this.smscode = smscode;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getSecure_mode() {
        return secure_mode;
    }

    public void setSecure_mode(int secure_mode) {
        this.secure_mode = secure_mode;
    }

    public String getPassport_id() {
        return passport_id;
    }

    public void setPassport_id(String passport_id) {
        this.passport_id = passport_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }
}
