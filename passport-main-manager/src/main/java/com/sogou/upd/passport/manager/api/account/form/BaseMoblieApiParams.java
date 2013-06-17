package com.sogou.upd.passport.manager.api.account.form;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.common.validation.constraints.Phone;
import com.sogou.upd.passport.manager.api.BaseApiParams;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.AssertTrue;

/**
 * 涉及到手机号相关的参数类的基类
 * 参数只有手机号
 * User: shipengzhi
 * Date: 13-6-7
 * Time: 下午9:25
 * To change this template use File | Settings | File Templates.
 */
public class BaseMoblieApiParams extends BaseApiParams {

    @Phone
    @NotBlank(message = "手机号不允许为空")
    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
