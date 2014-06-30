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
import com.sogou.upd.passport.manager.api.account.BindApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.api.account.form.BindEmailApiParams;
import com.sogou.upd.passport.manager.api.account.form.BindMobileApiParams;
import com.sogou.upd.passport.manager.api.account.form.SendCaptchaApiParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-6-7
 * Time: 下午11:04
 * To change this template use File | Settings | File Templates.
 */
@Component("bindApiManager")
public class BindApiManagerImpl implements BindApiManager {
    private static Logger logger = LoggerFactory.getLogger(BindApiManagerImpl.class);
    private static final Logger checkWriteLogger = LoggerFactory.getLogger("com.sogou.upd.passport.bothWriteSyncErrorLogger");

    @Autowired
    private BindApiManager proxyBindApiManager;
    @Autowired
    private AccountService accountService;

    @Override
    public Result bindEmail(BindEmailApiParams bindEmailApiParams) {
        return null;
    }

    @Override
    public Result getPassportIdByMobile(BaseMoblieApiParams baseMoblieApiParams) {
        return null;
    }

    @Override
    public Result sendCaptcha(SendCaptchaApiParams sendCaptchaApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean cacheOldCaptcha(String mobile, int clientId, String captcha) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getOldCaptcha(String mobile, int clientId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result bindMobile(String passportId, String newMobile) {
        Result result;
        if (ManagerHelper.writeSohuSwitcher()) {
            result = proxyBindApiManager.bindMobile(passportId, newMobile);
        } else {
            AccountDomainEnum domainType = AccountDomainEnum.getAccountDomain(passportId);
            //搜狗账号修改密码双写
            if (AccountDomainEnum.SOGOU.equals(domainType) || AccountDomainEnum.INDIVID.equals(domainType)) {
                result = bothBindMobile(passportId, newMobile);
            } else {
                //其它账号修改密码依然只写SH
                result = proxyBindApiManager.bindMobile(passportId, newMobile);
            }
        }
        return result;
    }

    private Result bothBindMobile(String passportId, String newMobile) {
        Result result = new APIResultSupport(false);
        try {
            Account account = accountService.queryAccountByPassportId(passportId);
            if (account == null ) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
                return result;
            }
            boolean isSgBind = accountService.bindMobile(account, newMobile);
            if (isSgBind) {
                result.setSuccess(true);
                result.setMessage("操作成功");
                Result shResult = proxyBindApiManager.bindMobile(passportId, newMobile);
                if (!shResult.isSuccess()) {
                    LogUtil.buildErrorLog(checkWriteLogger, AccountModuleEnum.SECURE, "BindMobile", CommonConstant.SGSUCCESS_SHERROR, passportId, result.getCode(), shResult.toString());
                }
            }else{
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDMOBILE_FAILED);
            }
        } catch (Exception e) {
            logger.error("bothBindMobile Exception", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    @Override
    public Result unBindMobile(String mobile) {
        return null;
    }

}
