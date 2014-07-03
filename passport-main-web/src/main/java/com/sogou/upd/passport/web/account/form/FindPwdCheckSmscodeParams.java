package com.sogou.upd.passport.web.account.form;

import com.sogou.upd.passport.common.validation.constraints.Phone;
import com.sogou.upd.passport.web.BaseWebParams;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-7-3
 * Time: 下午4:45
 * To change this template use File | Settings | File Templates.
 */
public class FindPwdCheckSmscodeParams extends BaseWebParams {

    @NotBlank
    @Phone
    private String mobile;
    @NotBlank
    private String smscode;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSmscode() {
        return smscode;
    }

    public void setSmscode(String smscode) {
        this.smscode = smscode;
    }
}
