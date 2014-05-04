package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.AppAuthTokenApiParams;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieUrlApiParams;
import com.sogou.upd.passport.service.account.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-4-21
 * Time: 下午7:47
 * To change this template use File | Settings | File Templates.
 */
@Component("loginApiManager")
public class LoginApiManagerImpl extends BaseProxyManager implements LoginApiManager {

    private static final Logger logger = LoggerFactory.getLogger(LoginApiManagerImpl.class);
    private static final Logger readLogger = LoggerFactory.getLogger("com.sogou.upd.passport.bothReadSyncErrorLogger");

    @Autowired
    private LoginApiManager proxyLoginApiManager;
    @Autowired
    private LoginApiManager sgLoginApiManager;
    @Autowired
    private AccountService accountService;

    @Override
    public Result webAuthUser(AuthUserApiParams authUserApiParams) {
        Result result;
        String passportId = authUserApiParams.getUserid();
        if (ManagerHelper.readSohuSwitcher()) {
            result = proxyLoginApiManager.webAuthUser(authUserApiParams);
        } else {
            result = bothAuthUser(authUserApiParams);
        }
        if (result.isSuccess()) {
            //SOHU域账号登录，在SG库中创建一条只有账号没密码的记录
            if (AccountDomainEnum.SOHU.equals(AccountDomainEnum.getAccountDomain(authUserApiParams.getUserid()))) {
                //创建sohu域账号
                accountService.initialAccount(passportId, null, false, authUserApiParams.getIp(), AccountTypeEnum.SOHU.getValue());
            }
        }
        return result;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * 内部双读SG，SH库，校验用户名和密码
     *
     * @param authUserApiParams
     * @return
     */
    private Result bothAuthUser(AuthUserApiParams authUserApiParams) {
        Result result;
        //sohu账号调用sohu api校验用户名和密码
        if (AccountDomainEnum.SOHU.equals(AccountDomainEnum.getAccountDomain(authUserApiParams.getUserid()))) {
            result = proxyLoginApiManager.webAuthUser(authUserApiParams);
        } else {
            //其它账号走sogou api 目前是双读阶段
            result = sgLoginApiManager.webAuthUser(authUserApiParams);
            if (!result.isSuccess()) { //读SG库，校验用户名、密码失败，此时读SH校验
                result = proxyLoginApiManager.webAuthUser(authUserApiParams);
                if (result.isSuccess()) {
                    //读SG失败，读SH成功，记录userid，便于验证数据同步情况
                    readLogger.error("userId:" + authUserApiParams.getUserid());
                }
            }
        }
        return result;
    }

    @Override
    public Result appAuthToken(AppAuthTokenApiParams appAuthTokenApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result buildCreateCookieUrl(CreateCookieUrlApiParams createCookieUrlApiParams, boolean isRuEncode, boolean isHttps) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result getCookieInfoWithRedirectUrl(CreateCookieUrlApiParams createCookieUrlApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result getCookieInfo(CookieApiParams cookieApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
