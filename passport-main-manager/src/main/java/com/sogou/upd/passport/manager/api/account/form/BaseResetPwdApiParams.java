package com.sogou.upd.passport.manager.api.account.form;

import com.sogou.upd.passport.manager.api.BaseApiParams;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * 手机重置密码用户名和密码列表
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-4-24
 * Time: 下午3:10
 * To change this template use File | Settings | File Templates.
 */
public class BaseResetPwdApiParams extends BaseApiParams {
    @Min(0)
    @NotBlank(message = "code不允许为空")
    private String mobile;

    private String lists;

    public String getLists() {
        return lists;
    }

    public void setLists(String lists) {
        this.lists = lists;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
