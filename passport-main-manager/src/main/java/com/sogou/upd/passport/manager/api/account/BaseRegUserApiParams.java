package com.sogou.upd.passport.manager.api.account;

import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.validation.constraints.UserName;
import com.sogou.upd.passport.manager.api.BaseApiParams;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-6-4
 * Time: 下午4:23
 * To change this template use File | Settings | File Templates.
 */
public class BaseRegUserApiParams extends BaseApiParams{
    @NotBlank(message = "用户id（userid）不能为空")
    @UserName
    protected String userid;

    public String getUserid() {
        String internalUsername = AccountDomainEnum.getInternalCase(userid);
        setUserid(internalUsername);
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
