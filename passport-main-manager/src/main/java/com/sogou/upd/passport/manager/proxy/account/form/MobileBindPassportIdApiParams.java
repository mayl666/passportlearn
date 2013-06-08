package com.sogou.upd.passport.manager.proxy.account.form;

import com.sogou.upd.passport.manager.proxy.BaseApiParameters;
import org.hibernate.validator.constraints.NotBlank;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-8
 * Time: 上午11:26
 */
public class MobileBindPassportIdApiParams extends BaseApiParameters {

    @NotBlank(message = "手机号不能为空")
    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
