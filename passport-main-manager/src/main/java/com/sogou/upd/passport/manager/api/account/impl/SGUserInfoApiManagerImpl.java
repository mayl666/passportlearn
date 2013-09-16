package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserInfoApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserUniqnameApiParams;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-9-17
 * Time: 上午12:43
 * To change this template use File | Settings | File Templates.
 */
@Component("sgUserInfoApiManager")
public class SGUserInfoApiManagerImpl implements UserInfoApiManager {
    @Override
    public Result getUserInfo(GetUserInfoApiparams getUserInfoApiparams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result updateUserInfo(UpdateUserInfoApiParams updateUserInfoApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result checkUniqName(UpdateUserUniqnameApiParams updateUserUniqnameApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
