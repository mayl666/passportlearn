package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserInfoApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateUserUniqnameApiParams;
import org.springframework.stereotype.Component;

/**
 * sohu+个人资料获取
 * User: mayan
 * Date: 13-11-27
 * Time: 下午2:27
 * To change this template use File | Settings | File Templates.
 */
@Component("shPlusUserInfoApiManager")
public class SohuPlusUserInfoApiManagerImpl extends BaseProxyManager implements UserInfoApiManager {

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
