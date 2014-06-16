package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.LogUtil;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.AppAuthTokenApiParams;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieUrlApiParams;
import com.sogou.upd.passport.service.account.AccountSecureService;
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

    private static final Logger readLogger = LoggerFactory.getLogger("com.sogou.upd.passport.bothReadSyncErrorLogger");

    @Autowired
    private LoginApiManager proxyLoginApiManager;
    @Autowired
    private LoginApiManager sgLoginApiManager;
    @Autowired
    private CommonManager commonManager;
    @Autowired
    private AccountSecureService accountSecureService;

    @Override
    public Result webAuthUser(AuthUserApiParams authUserApiParams) {
        Result result;
        if (ManagerHelper.readSohuSwitcher()) {
            //回滚操作时，调用sohu api校验用户名和密码
            result = proxyLoginApiManager.webAuthUser(authUserApiParams);
        } else {
            //正常流程
            result = bothAuthUser(authUserApiParams);
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
        if (AccountDomainEnum.SOHU.equals(AccountDomainEnum.getAccountDomain(passportId))) {
            //主账号是sohu域账号调用sohu api校验用户名和密码
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
                        String message = "SoGouError-SoHuSuccess";
                        LogUtil.buildErrorLog(readLogger, message, authUserApiParams.getUserid(), passportId, result.toString());
                    } else {
                        //记录下来SH验证失败的情况:去除真正是用户名和密码都不匹配的情况
                        if (!ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_PWD_ERROR.equals(result.getCode())) {
                            String message = "SoGouError-SoHuError";
                            LogUtil.buildErrorLog(readLogger, message, authUserApiParams.getUserid(), passportId, result.toString());
                        }
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
