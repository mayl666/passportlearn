package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.*;
import com.sogou.upd.passport.service.account.AccountSecureService;
import com.sogou.upd.passport.service.account.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

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
    private CommonManager commonManager;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountSecureService accountSecureService;

    @Override
    public Result webAuthUser(AuthUserApiParams authUserApiParams) {
        Result result;
        String passportId = authUserApiParams.getUserid();
        if (ManagerHelper.readSohuSwitcher()) {
            //回滚操作时，调用sohu api校验用户名和密码
            result = proxyLoginApiManager.webAuthUser(authUserApiParams);
        } else {
            //正常流程
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
        String passportId = commonManager.getPassportIdByUsername(authUserApiParams.getUserid());
        if (AccountDomainEnum.SOHU.equals(AccountDomainEnum.getAccountDomain(authUserApiParams.getUserid()))) {
            //sohu账号调用sohu api校验用户名和密码
            result = proxyLoginApiManager.webAuthUser(authUserApiParams);
        } else {
            if (accountSecureService.getUpdateSuccessFlag(passportId)) {
                //主账号有更新密码或绑定手机的操作时，调用sohu api校验用户名和密码
                result = proxyLoginApiManager.webAuthUser(authUserApiParams);
            } else {
                //没有更新密码时，走正常的双读流程
                result = sgLoginApiManager.webAuthUser(authUserApiParams);
                if (!result.isSuccess()) { //读SG库，校验用户名、密码失败，此时读SH校验
                    result = proxyLoginApiManager.webAuthUser(authUserApiParams);
                    if (result.isSuccess()) {
                        //读SG失败，读SH成功，记录userid，便于验证数据同步情况
                        //日志记录可能存在的情况：新注册用户登录时，同步延迟；用户找回密码后登录；用户校验密码失败等
                        readLogger.error("auth sogou error,auth sohu success,userId:[{}];time:[{}]", authUserApiParams.getUserid(), new Date());
                    }
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
