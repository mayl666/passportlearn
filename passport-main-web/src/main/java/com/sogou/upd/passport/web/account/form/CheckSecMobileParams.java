package com.sogou.upd.passport.web.account.form;

import com.sogou.upd.passport.common.validation.constraints.Phone;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-7-3
 * Time: 下午9:07
 * To change this template use File | Settings | File Templates.
 */
public class CheckSecMobileParams extends CheckMobileSmsParams {

    @NotBlank(message = "密保手机不可为空!")
    @Phone
    protected String sec_mobile;


    public String getSec_mobile() {
        return sec_mobile;
    }

    public void setSec_mobile(String sec_mobile) {
        this.sec_mobile = sec_mobile;
    }
}
