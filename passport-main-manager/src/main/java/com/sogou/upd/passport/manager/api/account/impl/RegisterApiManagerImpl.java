package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.LogUtil;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.api.BaseProxyManager;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-6-24
 * Time: 下午8:01
 * To change this template use File | Settings | File Templates.
 */
@Component("registerApiManager")
public class RegisterApiManagerImpl extends BaseProxyManager implements RegisterApiManager {

    private static final Logger logger = LoggerFactory.getLogger(RegisterApiManagerImpl.class);

    private static final Logger checkWriteLogger = LoggerFactory.getLogger("com.sogou.upd.passport.bothWriteSyncErrorLogger");

    @Autowired
    private RegisterApiManager sgRegisterApiManager;
    @Autowired
    private RegisterApiManager proxyRegisterApiManager;

    @Override
    public Result regMailUser(RegEmailApiParams regEmailApiParams) {
        Result result;
        if (ManagerHelper.writeSohuSwitcher()) {
            //回滚操作时，都走写SH流程
            result = proxyRegisterApiManager.regMailUser(regEmailApiParams);
        } else {
            AccountDomainEnum domainType = AccountDomainEnum.getAccountDomain(regEmailApiParams.getUserid());
            if (AccountDomainEnum.SOGOU.equals(domainType) || AccountDomainEnum.INDIVID.equals(domainType)) {
                //搜狗账号走双写流程
                result = bothWriteUser(regEmailApiParams);
            } else {
                //其它账号走写SH流程
                result = proxyRegisterApiManager.regMailUser(regEmailApiParams);
            }

        }
        return result;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * 写分离时双写SG,SH
     *
     * @param regEmailApiParams
     * @return
     */
    private Result bothWriteUser(RegEmailApiParams regEmailApiParams) {
        Result result = new APIResultSupport(false);
        try {
            result = sgRegisterApiManager.regMailUser(regEmailApiParams);
            Result shResult = proxyRegisterApiManager.regMailUser(regEmailApiParams);
            if (!result.isSuccess()) {
                String message = shResult.isSuccess() ? CommonConstant.SGERROR_SHSUCCESS : CommonConstant.SGERROR_SHERROR;
                LogUtil.buildErrorLog(checkWriteLogger, AccountModuleEnum.REGISTER, "regMailUser", message, regEmailApiParams.getUserid(), result.getCode(), shResult.toString());
            }

        } catch (Exception e) {
            logger.error("bothWriteUser Exception", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;

    }

    @Override
    public Result regMobileCaptchaUser(RegMobileCaptchaApiParams regMobileCaptchaApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result sendMobileRegCaptcha(BaseMoblieApiParams baseMoblieApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result checkUser(CheckUserApiParams checkUserApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result regMobileUser(RegMobileApiParams regMobileApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
