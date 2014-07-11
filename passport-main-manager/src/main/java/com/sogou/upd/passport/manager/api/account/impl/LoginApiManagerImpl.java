package com.sogou.upd.passport.manager.api.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
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

    private static final Logger logger = LoggerFactory.getLogger(LoginApiManagerImpl.class);

    private static final Logger checkLogger = LoggerFactory.getLogger("com.sogou.upd.passport.bothCheckSyncErrorLogger");

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
        Result result = new APIResultSupport(false);
        try {
            String userId = authUserApiParams.getUserid();
            //第三方账号不允许此操作
            if (AccountDomainEnum.THIRD.equals(AccountDomainEnum.getAccountDomain(authUserApiParams.getUserid()))) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_THIRD_NOTALLOWED);
                return result;
            }
            //主要是为了查询手机号绑定的主账号是否是sohu域的及主账号是否有修改密码或绑定手机的操作，读写彻底分离后，查主账号的逻辑可去除
            String passportId = commonManager.getPassportIdByUsername(userId);
            if (Strings.isNullOrEmpty(passportId)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOBIND);
                return result;
            }
            AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(passportId);
            if (AccountDomainEnum.SOHU.equals(domain)) {
                //主账号是sohu域账号调用sohu api校验用户名和密码
                result = proxyLoginApiManager.webAuthUser(authUserApiParams);
            } else {
                if (!AccountDomainEnum.SOGOU.equals(domain) && accountSecureService.getUpdateSuccessFlag(passportId)) {
                    //搜狗账号写分离阶段，主账号有更新密码或绑定手机的操作时，非搜狗账号调用sohu api校验用户名和密码；搜狗账号走双读
                    result = updatePwdOrBindMobile(authUserApiParams, userId, passportId);
                } else if (AccountDomainEnum.SOGOU.equals(domain) && accountSecureService.getUpdateSuccessFlag(passportId)) {
                    //写分离阶段，1.主账号是搜狗域且有找回密码操作时，只验证sg库，因为找回密码无法双写，只写了SG库;
                    // 2.搜狗账号修改密码只读SG校验用户名和密码
                    result = findPwdForSogouAccount(authUserApiParams, userId, passportId);
                } else {
                    //没有更新密码时，走正常的双读流程
                    result = authUser(authUserApiParams, userId, passportId);
                }
            }
        } catch (Exception e) {
            logger.error("bothAuthUser Exception", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    /**
     * 为了解决主账号有修改密码，修改绑定手机操作的情况
     *
     * @param authUserApiParams
     * @param userId
     * @param passportId
     * @return
     */
    private Result updatePwdOrBindMobile(AuthUserApiParams authUserApiParams, String userId, String passportId) {
        Result result = proxyLoginApiManager.webAuthUser(authUserApiParams);
        LogUtil.buildErrorLog(checkLogger, AccountModuleEnum.LOGIN, "webAuthUser", CommonConstant.AUTH_MESSAGE, userId, passportId, result.toString());
        return result;
    }

    /**
     * 为了解决写分离阶段搜狗账号找回密码写SG库，线上回滚后，找回密码也需跳去sohu找回
     *
     * @param authUserApiParams
     * @param userId
     * @param passportId
     * @return
     */
    private Result findPwdForSogouAccount(AuthUserApiParams authUserApiParams, String userId, String passportId) {
        Result result = sgLoginApiManager.webAuthUser(authUserApiParams);
        LogUtil.buildErrorLog(checkLogger, AccountModuleEnum.LOGIN, "webAuthUser", CommonConstant.SOGOU_RESETPWD_MESSAGE, userId, passportId, result.toString());
        return result;
    }

    /**
     * 走正常的双读校验用户名和密码流程
     *
     * @param authUserApiParams
     * @param userId
     * @param passportId
     * @return
     */
    private Result authUser(AuthUserApiParams authUserApiParams, String userId, String passportId) {
        Result result = sgLoginApiManager.webAuthUser(authUserApiParams);
        if (!result.isSuccess()) { //读SG库，校验用户名、密码失败，此时读SH校验
            result = proxyLoginApiManager.webAuthUser(authUserApiParams);
            if (result.isSuccess()) {
                //读SG失败，读SH成功，记录userid，便于验证数据同步情况
                //日志记录可能存在的情况：新注册用户登录时，同步延迟；用户找回密码后登录；用户校验密码失败等
                LogUtil.buildErrorLog(checkLogger, AccountModuleEnum.LOGIN, "webAuthUser", CommonConstant.AUTH_SGE_SHS_MESSAGE, userId, passportId, result.toString());
            } else {
                //记录下来SH验证失败的情况:去除真正是用户名和密码都不匹配的情况
                if (!ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_PWD_ERROR.equals(result.getCode())) {
                    LogUtil.buildErrorLog(checkLogger, AccountModuleEnum.LOGIN, "webAuthUser", CommonConstant.AUTH_SGE_SHE_MESSAGE, userId, passportId, result.toString());
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
