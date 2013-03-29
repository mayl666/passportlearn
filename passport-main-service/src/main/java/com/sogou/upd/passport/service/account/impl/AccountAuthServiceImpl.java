package com.sogou.upd.passport.service.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.model.account.AccountAuth;
import com.sogou.upd.passport.service.account.AccountAuthService;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-3-29
 * Time: 上午1:20
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AccountAuthServiceImpl implements AccountAuthService {

    @Override
    public boolean verifyRefreshToken(String refreshToken) {
        // TODO refresh_token也采用对称加密算法可减少一次数据库操作
        if (!Strings.isNullOrEmpty(refreshToken)) {
            AccountAuth accountAuth = getAccountAuthByRefreshToken(refreshToken);
            if (accountAuth != null && accountAuth.getAccessValidTime() > System.currentTimeMillis()) {
                return true;
            }
        }
        return false;
    }

    private AccountAuth getAccountAuthByRefreshToken(String RefreshToken) {
        // TODO implement
        return null;
    }
}
