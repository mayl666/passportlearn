package com.sogou.upd.passport.manager.api.account.form;

import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.manager.api.BaseApiParams;
import org.hibernate.validator.constraints.NotBlank;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-13
 * Time: 上午10:32
 */
public class BaseUserApiParams extends BaseApiParams {

    @NotBlank(message = "用户id（userid）不能为空")
    protected String userid;

    private String third_appid;

    public String getThird_appid() {
        return third_appid;
    }

    public void setThird_appid(String third_appid) {
        this.third_appid = third_appid;
    }

    public String getUserid() {
        String internalUsername = AccountDomainEnum.getInternalCase(userid);
        setUserid(internalUsername);
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
