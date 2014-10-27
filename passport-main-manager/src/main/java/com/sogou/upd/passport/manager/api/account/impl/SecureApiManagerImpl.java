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

    @Autowired
    private SecureApiManager sgSecureApiManager;

    @Override
    public Result updatePwd(final String passportId, final int clientId, final String oldPwd, final String newPwd, final String modifyIp) {
        Result result = sgSecureApiManager.updatePwd(passportId, clientId, oldPwd, newPwd, modifyIp);
        return result;
    }

    @Override
    public Result updateQues(UpdateQuesApiParams updateQuesApiParams) {
        Result result = sgSecureApiManager.updateQues(updateQuesApiParams);
        return result;
    }

    @Override
    public Result getUserSecureInfo(GetSecureInfoApiParams getSecureInfoApiParams) {
        return null;
    }

    @Override
    public Result resetPasswordByQues(ResetPasswordBySecQuesApiParams resetPasswordBySecQuesApiParams) {
        return null;
    }

}
