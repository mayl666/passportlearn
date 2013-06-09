package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.BindApiManager;
import com.sogou.upd.passport.manager.api.account.form.*;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-6-7
 * Time: 下午11:04
 * To change this template use File | Settings | File Templates.
 */
@Component("sgBindApiManager")
public class SGBindApiManagerImpl implements BindApiManager {
    @Override
    public Result bindMobile(BindMobileApiParams bindMobileApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result updateBindMobile(UpdateBindMobileApiParams updateBindMobileApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result bindEmail(BindEmailApiParams bindEmailApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result getPassportIdFromMobile(MobileBindPassportIdApiParams mobileBindPassportIdApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result queryPassportIdByMobile(BaseMoblieApiParams baseMoblieApiParams) {
        // TODO 当Manager里方法只调用一个service时，需要把service的返回值改为Result
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
