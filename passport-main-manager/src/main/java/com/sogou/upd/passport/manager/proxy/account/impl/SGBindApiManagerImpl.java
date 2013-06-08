package com.sogou.upd.passport.manager.proxy.account.impl;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.proxy.account.BindApiManager;
import com.sogou.upd.passport.manager.proxy.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.proxy.account.form.BindEmailApiParams;
import com.sogou.upd.passport.manager.proxy.account.form.BindMobileApiParams;
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
    public Result unbindMobile(BaseMoblieApiParams baseMoblieApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result bindEmail(BindEmailApiParams bindEmailApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result queryPassportIdByMobile(BaseMoblieApiParams baseMoblieApiParams) {
        // TODO 当Manager里方法只调用一个service时，需要把service的返回值改为Result
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
