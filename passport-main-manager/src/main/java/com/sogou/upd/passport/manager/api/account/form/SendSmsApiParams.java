package com.sogou.upd.passport.manager.api.account.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by xieyilun on 2016/5/13.
 */
public class SendSmsApiParams extends BaseMoblieApiParams {
    @NotBlank(message = "sgid不允许为空")
    String sgid;
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
}
