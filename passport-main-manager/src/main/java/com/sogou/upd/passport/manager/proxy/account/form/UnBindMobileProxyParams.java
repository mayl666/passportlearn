package com.sogou.upd.passport.manager.proxy.account.form;

import com.sogou.upd.passport.manager.proxy.BaseApiParameters;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 解绑手机
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-7
 * Time: 上午11:06
 */
public class UnBindMobileProxyParams extends BaseApiParameters {

    @NotBlank(message="要解绑的手机不能为空！")
    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
