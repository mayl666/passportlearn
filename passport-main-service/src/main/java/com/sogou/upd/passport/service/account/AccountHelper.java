package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.parameter.AccountStatusEnum;
import com.sogou.upd.passport.model.account.Account;

/**
 * Created with IntelliJ IDEA. User: chenjiameng Date: 13-6-3 Time: 下午4:52 To change this template
 * use File | Settings | File Templates.
 */
public class AccountHelper {
    public static boolean isNormalAccount(Account account) {
        //泄露数据刨除在外
        if(account.getFlag()== AccountStatusEnum.LEAKED.getValue()){
            return true;
        }
        return account.getFlag() == AccountStatusEnum.REGULAR.getValue();
    }

    public static boolean isDisabledAccount(Account account) {
        return account.getFlag() == AccountStatusEnum.DISABLED.getValue();
    }

    public static boolean isKilledAccount(Account account) {
        return account.getFlag() == AccountStatusEnum.KILLED.getValue();
    }

}
