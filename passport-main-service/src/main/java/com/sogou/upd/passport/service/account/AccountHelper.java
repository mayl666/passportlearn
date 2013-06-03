package com.sogou.upd.passport.service.account;

import com.sogou.upd.passport.common.parameter.AccountStatusEnum;
import com.sogou.upd.passport.model.account.Account;

/**
 * Created with IntelliJ IDEA. User: chenjiameng Date: 13-6-3 Time: 下午4:52 To change this template
 * use File | Settings | File Templates.
 */
public class AccountHelper {
  public static boolean isNormalAccount(Account account) {
    return account.getStatus() == AccountStatusEnum.REGULAR.getValue();
  }
}
