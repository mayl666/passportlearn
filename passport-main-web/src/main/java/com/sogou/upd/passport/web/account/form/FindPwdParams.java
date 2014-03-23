package com.sogou.upd.passport.web.account.form;

import com.sogou.upd.passport.common.validation.constraints.FindPwdType;
import com.sogou.upd.passport.web.BaseWebParams;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 14-3-23
 * Time: 下午9:34
 * To change this template use File | Settings | File Templates.
 */
public class FindPwdParams extends BaseAccountParams {
    @FindPwdType(message = "找回方式不支持")
    @NotBlank(message = "找回方式不允许为空!")
    protected String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
