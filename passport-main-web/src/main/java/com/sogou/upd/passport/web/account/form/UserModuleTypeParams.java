package com.sogou.upd.passport.web.account.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-24 Time: 下午12:03 To change this template
 * use File | Settings | File Templates.
 */
public class UserModuleTypeParams extends BaseUserParams {
    // TODO 参数类如何定义？
    @NotBlank
    protected String module;

    // mode:1表示注册发送验证码；2表示绑定手机验证新手机号；3表示
    @NotBlank
    protected String mode;
    protected String passport_id;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getPassport_id() {
        return passport_id;
    }

    public void setPassport_id(String passport_id) {
        this.passport_id = passport_id;
    }
}
