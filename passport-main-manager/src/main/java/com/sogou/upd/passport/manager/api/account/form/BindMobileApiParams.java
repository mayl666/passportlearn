package com.sogou.upd.passport.manager.api.account.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by xieyilun on 2016/5/12.
 */
public class BindMobileApiParams extends BaseMoblieApiParams {
    @NotBlank(message = "sgid不允许为空")
    String sgid;
    @NotBlank(message = "密码不允许为空")
    protected String password;
    @NotBlank(message = "验证码不允许为空!")
    protected String smscode;
    @NotBlank(message = "createip不允许为空!")
    private String createip;

    public String getCreateip() {
        return createip;
    }

    public void setCreateip(String createip) {
        this.createip = createip;
    }

    public String getSgid() {
        return sgid;
    }

    public void setSgid(String sgid) {
        this.sgid = sgid;
    }

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
}
