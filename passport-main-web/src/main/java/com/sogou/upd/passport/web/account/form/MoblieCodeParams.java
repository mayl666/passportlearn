package com.sogou.upd.passport.web.account.form;

import com.sogou.upd.passport.common.validation.constraints.Phone;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * 手机验证码请求参数类
 * User: shipengzhi
 * Date: 13-4-8
 * Time: 下午5:57
 * To change this template use File | Settings | File Templates.
 */
public class MoblieCodeParams {

    @Phone
    @NotBlank(message = "手机号码不允许为空!")
    private String mobile;
    @Min(0)
    @NotBlank(message = "client_id不允许为空!")
    private String client_id;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

}
