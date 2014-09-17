package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.LogUtil;
import com.sogou.upd.passport.manager.api.account.SecureApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetSecureInfoApiParams;
import com.sogou.upd.passport.manager.api.account.form.ResetPasswordBySecQuesApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateQuesApiParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
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
    @Autowired
    private TaskExecutor discardTaskExecutor;

    @Override
    public Result updatePwd(final String passportId, final int clientId, final String oldPwd, final String newPwd, final String modifyIp) {
        Result result = sgSecureApiManager.updatePwd(passportId, clientId, oldPwd, newPwd, modifyIp);
        AccountDomainEnum domainType = AccountDomainEnum.getAccountDomain(passportId);
        //搜狗账号修改密码双写,写入搜狐逻辑为异步，避免搜狐服务不可用影响搜狗
        if (result.isSuccess() && (AccountDomainEnum.SOGOU.equals(domainType) || AccountDomainEnum.INDIVID.equals(domainType))) {
            discardTaskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    Result shResult = proxySecureApiManager.updatePwd(passportId, clientId, oldPwd, newPwd, modifyIp);
                    if (!shResult.isSuccess()) {
                        LogUtil.buildErrorLog(checkWriteLogger, AccountModuleEnum.RESETPWD, "updatePwd", CommonConstant.SGSUCCESS_SHERROR, passportId, shResult.getCode(), shResult.toString());
                    }
                }
            });
        }
        return result;
    }

    @Override
    public Result updateQues(UpdateQuesApiParams updateQuesApiParams) {
        Result result = sgSecureApiManager.updateQues(updateQuesApiParams);
        return result;
    }

    @Override
    public Result getUserSecureInfo(GetSecureInfoApiParams getSecureInfoApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result resetPasswordByQues(ResetPasswordBySecQuesApiParams resetPasswordBySecQuesApiParams) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
