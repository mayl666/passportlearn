package com.sogou.upd.passport.manager.proxy.account.impl;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.proxy.account.LoginApiManager;
import com.sogou.upd.passport.manager.proxy.account.form.AppAuthTokenApiParams;
import com.sogou.upd.passport.manager.proxy.account.form.AuthUserApiParams;
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
        // TODO 当Manager里方法只调用一个service时，需要把service的返回值改为Result
        // TODO 例如这里调用AccountService的verifyUserPwdVaild（）方法，就需要把返回值改为Result
        return null;
    }

    @Override
    public Result appAuthToken(AppAuthTokenApiParams appAuthTokenApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
