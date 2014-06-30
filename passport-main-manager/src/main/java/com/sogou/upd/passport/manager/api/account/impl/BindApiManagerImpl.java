package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.LogUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.api.account.BindApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.api.account.form.BindEmailApiParams;
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
            Account account = accountService.queryNormalAccount(passportId);
            if (account == null) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
                return result;
            }
            boolean isSgBind = accountService.bindMobile(account, newMobile);
            if (isSgBind) {
                result.setSuccess(true);
                result.setMessage("操作成功");
                Result shResult = proxyBindApiManager.bindMobile(passportId, newMobile);
                if (!shResult.isSuccess()) {
                    LogUtil.buildErrorLog(checkWriteLogger, AccountModuleEnum.SECURE, "bindMobile", CommonConstant.SGSUCCESS_SHERROR, passportId, result.getCode(), shResult.toString());
                }
            } else {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDMOBILE_FAILED);
            }
        } catch (Exception e) {
            logger.error("bothBindMobile Exception", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    @Override
    public Result modifyBindMobile(String passportId, String newMobile) {
        Result result = new APIResultSupport(false);
        try {
            Account account = accountService.queryNormalAccount(passportId);
            if (account == null) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
                return result;
            }
            String oldMobile = account.getMobile();
            if (ManagerHelper.writeSohuSwitcher()) {
                //解除原绑定手机
                result = proxyBindApiManager.unBindMobile(oldMobile);
                if (!result.isSuccess()) {
                    return result;
                }
                //绑定新手机
                result = proxyBindApiManager.bindMobile(passportId, newMobile);
            } else {
                // 修改绑定手机，checkCode为secureCode  TODO 不知道scode是干嘛用的
//            if (!accountSecureService.checkSecureCodeModSecInfo(passportId, clientId, scode)) {
//                result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BIND_FAILED);
//                return result;
//            }
                AccountDomainEnum domainType = AccountDomainEnum.getAccountDomain(passportId);
                //搜狗账号修改密码双写
                if (AccountDomainEnum.SOGOU.equals(domainType) || AccountDomainEnum.INDIVID.equals(domainType)) {
                    result = bothModifyBindMobile(account, newMobile);
                } else {
                    //其它账号修改密码依然只写SH
                    result = proxyBindApiManager.unBindMobile(oldMobile);
                    if (!result.isSuccess()) {
                        return result;
                    }
                    result = proxyBindApiManager.bindMobile(passportId, newMobile);
                }
            }
        } catch (ServiceException e) {
            logger.error("modifyBindMobile Exception", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    private Result bothModifyBindMobile(Account account, String newMobile) {
        Result result = new APIResultSupport(false);
        try {
            boolean isSgModifyBind = accountService.modifyBindMobile(account, newMobile);
            if (isSgModifyBind) {
                result.setSuccess(true);
                result.setMessage("操作成功");
                String oldModify = account.getMobile();
                String passportId = account.getPassportId();
                Result shUnBindResult = proxyBindApiManager.unBindMobile(oldModify);
                if (!shUnBindResult.isSuccess()) {
                    LogUtil.buildErrorLog(checkWriteLogger, AccountModuleEnum.SECURE, "modifyBindMobile", CommonConstant.SGSUCCESS_SHUNBINDERROR, passportId, result.getCode(), shUnBindResult.toString());
                    return result;
                }
                Result shModifyBindResult = proxyBindApiManager.bindMobile(account.getPassportId(), newMobile);
                if (!shModifyBindResult.isSuccess()) {
                    LogUtil.buildErrorLog(checkWriteLogger, AccountModuleEnum.SECURE, "modifyBindMobile", CommonConstant.SGSUCCESS_SHERROR, passportId, result.getCode(), shModifyBindResult.toString());
                }
            } else {
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
