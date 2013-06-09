package com.sogou.upd.passport.manager.api.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.AppAuthTokenApiParams;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieApiParams;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieUrlApiParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountHelper;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.MobilePassportMappingService;
import com.sogou.upd.passport.service.account.OperateTimesService;
import com.sogou.upd.passport.service.account.generator.PwdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-6-7
 * Time: 下午8:15
 * To change this template use File | Settings | File Templates.
 */
@Component("sgLoginApiManager")
public class SGLoginApiManagerImpl implements LoginApiManager {
    private static Logger logger = LoggerFactory.getLogger(SGLoginApiManagerImpl.class);
    private static final int USERTYPE_PHONE = 1;
    private static final int USERTYPE_PASSPORTID = 0;

    @Autowired
    private AccountService accountService;
    @Autowired
    private MobilePassportMappingService mobilePassportMappingService;
    @Autowired
    private OperateTimesService operateTimesService;

    @Override
    public Result webAuthUser(AuthUserApiParams authUserApiParams) {
        // TODO 当Manager里方法只调用一个service时，需要把service的返回值改为Result
        // TODO 例如这里调用AccountService的verifyUserPwdVaild（）方法，就需要把返回值改为Result
        Result result = new APIResultSupport(false);
        try {
            result = accountService.verifyUserPwdVaild(authUserApiParams.getUserid(), authUserApiParams.getPassword(), false);
        } catch (Exception e) {
            logger.error("webAuthUser fail,userId:" + authUserApiParams.getUserid(), e);
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_LOGIN_FAILED);
        }
        return result;
    }

    private Result doUserNotExist(String userid, String ip) {
        Result result = new APIResultSupport(false);
        //记录登陆失败操作
//        operateTimesService.incLoginFailedTimes(userid, ip);
        result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
        return result;
    }

    @Override
    public Result appAuthToken(AppAuthTokenApiParams appAuthTokenApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result createCookie(CreateCookieApiParams createCookieApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result buildCreateCookieUrl(CreateCookieUrlApiParams createCookieUrlApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
