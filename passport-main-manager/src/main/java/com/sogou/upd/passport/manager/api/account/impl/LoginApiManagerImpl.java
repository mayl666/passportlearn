package com.sogou.upd.passport.manager.api.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.CommonManager;
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

    @Autowired
    private LoginApiManager sgLoginApiManager;
    @Autowired
    private LoginApiManager proxyLoginApiManager;
    @Autowired
    private CommonManager commonManager;
    @Autowired
    private AccountService accountService;

    @Override
    public Result webAuthUser(AuthUserApiParams authUserApiParams) {
        Result result = new APIResultSupport(false);
        try {
            String userId = authUserApiParams.getUserid();
            //第三方账号不允许此操作
            if (AccountDomainEnum.THIRD.equals(AccountDomainEnum.getAccountDomain(userId))) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_THIRD_NOTALLOWED);
                return result;
            }
            String passportId = commonManager.getPassportIdByUsername(userId);
            if (Strings.isNullOrEmpty(passportId)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOBIND);
                return result;
            }
            AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(passportId);
            if (AccountDomainEnum.SOHU.equals(domain)) {
                //正常时，开关值为true，调用搜狐API校验搜狐域账号用户名和密码;当搜狐接口异常时，开关值false，返回异常；
                if (ManagerHelper.authUserBySOHUSwitcher()) {
                    //主账号是sohu域账号调用sohu api校验用户名和密码
                    result = proxyLoginApiManager.webAuthUser(authUserApiParams);
                    //sohu域账号校验密码成功后，初始化一条sohu域记录
                    if (result.isSuccess()) {
                        accountService.initSOHUAccount(passportId, authUserApiParams.getIp());
                    }
                } else {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SOHU_API_FAILED);
                    return result;
                }
            } else {
                result = sgLoginApiManager.webAuthUser(authUserApiParams);
            }
        } catch (Exception e) {
            logger.error("bothAuthUser Exception", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;  //To change body of implemented methods use File | Settings | File Templates.
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

    @Override
    public Result getSGCookieInfoForAdapter(CookieApiParams cookieApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
