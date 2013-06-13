package com.sogou.upd.passport.manager.api.account.form;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 绑定手机账号的参数类
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 上午10:25
 */
public class BindMobileApiParams extends BaseMoblieApiParams {

    @NotBlank(message = "passport_id不允许为空")
    private String userid;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

}
