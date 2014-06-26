package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.LogUtil;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.api.account.SecureApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetSecureInfoApiParams;
import com.sogou.upd.passport.manager.api.account.form.ResetPasswordBySecQuesApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdatePwdApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateQuesApiParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-6-26
 * Time: 下午6:02
 * To change this template use File | Settings | File Templates.
 */
@Component("secureApiManager")
public class SecureApiManagerImpl implements SecureApiManager {

    private static final Logger logger = LoggerFactory.getLogger(SecureApiManagerImpl.class);

    private static final Logger checkWriteLogger = LoggerFactory.getLogger("com.sogou.upd.passport.bothWriteSyncErrorLogger");

    @Autowired
    private SecureApiManager sgSecureApiManager;
    @Autowired
    private SecureApiManager proxySecureApiManager;

    @Override
    public Result updatePwd(UpdatePwdApiParams updatePwdApiParams) {
        Result result;
        if (ManagerHelper.writeSohuSwitcher()) {
            result = proxySecureApiManager.updatePwd(updatePwdApiParams);
        } else {
            //搜狗账号修改密码双写
            if (AccountDomainEnum.SOGOU.equals(AccountDomainEnum.getAccountDomain(updatePwdApiParams.getUserid()))) {
                result = bothUpdatePwd(updatePwdApiParams);
            } else {
                //其它账号修改密码依然只写SH
                result = proxySecureApiManager.updatePwd(updatePwdApiParams);
            }
        }
        return result;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * 修改密码双写
     *
     * @param updatePwdApiParams
     * @return
     */
    private Result bothUpdatePwd(UpdatePwdApiParams updatePwdApiParams) {
        Result result = new APIResultSupport(false);
        try {
            result = sgSecureApiManager.updatePwd(updatePwdApiParams);
            Result shResult = proxySecureApiManager.updatePwd(updatePwdApiParams);
            if (!result.isSuccess()) {
                String message = shResult.isSuccess() ? CommonConstant.SGERROR_SHSUCCESS : CommonConstant.SGERROR_SHERROR;
                LogUtil.buildErrorLog(checkWriteLogger, AccountModuleEnum.RESETPWD, "updatePwd", message, updatePwdApiParams.getUserid(), result.getCode(), shResult.toString());
            }
        } catch (Exception e) {
            logger.error("bothUpdatePwd Exception", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    @Override
    public Result updateQues(UpdateQuesApiParams updateQuesApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result getUserSecureInfo(GetSecureInfoApiParams getSecureInfoApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result resetPasswordByQues(ResetPasswordBySecQuesApiParams resetPasswordBySecQuesApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void resetPwd(UpdatePwdApiParams updatePwdApiParams) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
