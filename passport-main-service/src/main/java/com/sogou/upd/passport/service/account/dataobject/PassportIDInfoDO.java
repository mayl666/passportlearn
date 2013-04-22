package com.sogou.upd.passport.service.account.dataobject;

import com.sogou.upd.passport.common.parameter.AccountTypeEnum;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-22
 * Time: 下午6:54
 * To change this template use File | Settings | File Templates.
 */
public class PassportIDInfoDO {

    private String uid; // 手机号、邮箱、第三方openid
    private String accountTypeStr; // 账号类型

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAccountTypeStr() {
        return accountTypeStr;
    }

    public void setAccountTypeStr(String accountTypeStr) {
        this.accountTypeStr = accountTypeStr;
    }
}
