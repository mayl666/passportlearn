package com.sogou.upd.passport.web.account.form;

import com.sogou.upd.passport.manager.api.SHPPUrlConstant;

import com.sogou.upd.passport.web.BaseWebParams;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-23 Time: 下午2:57 To change this template use
 * File | Settings | File Templates.
 */
public class BaseAccountParams extends BaseWebParams {

    @NotBlank(message = "账号不允许为空!")
    protected String userid;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
