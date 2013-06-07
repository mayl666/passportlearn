package com.sogou.upd.passport.manager.proxy.account.impl;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.proxy.account.LoginApiManager;
import com.sogou.upd.passport.manager.proxy.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.proxy.account.form.MobileAuthTokenApiParams;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-6-7
 * Time: 下午8:15
 * To change this template use File | Settings | File Templates.
 */
@Component("sgLoginApiManager")
public class SGLoginApiManagerImpl implements LoginApiManager {

    @Override
    public Result webAuthUser(AuthUserApiParams authUserApiParams) {
        return null;
    }

    @Override
    public Result mobileAuthToken(MobileAuthTokenApiParams mobileAuthTokenApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
