package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-6-16
 * Time: 下午10:09
 * Date: 14-6-24
 * Time: 下午8:01
 * To change this template use File | Settings | File Templates.
 */
@Component("registerApiManager")
public class RegisterApiManagerImpl extends BaseProxyManager implements RegisterApiManager {

    @Autowired
    private RegisterApiManager sgRegisterApiManager;

    @Override
    public Result regMailUser(RegEmailApiParams regEmailApiParams) {
        Result result = sgRegisterApiManager.regMailUser(regEmailApiParams);
        return result;
    }

    @Override
    public Result regMobileCaptchaUser(RegMobileCaptchaApiParams regMobileCaptchaApiParams) {
        return null;
    }

    @Override
    public Result sendMobileRegCaptcha(BaseMoblieApiParams baseMoblieApiParams) {
        return null;
    }

    @Override
    public Result checkUser(CheckUserApiParams checkUserApiParams) {
        return null;
    }

    @Override
    public Result regMobileUser(RegMobileApiParams regMobileApiParams) {
        return null;
    }
}
