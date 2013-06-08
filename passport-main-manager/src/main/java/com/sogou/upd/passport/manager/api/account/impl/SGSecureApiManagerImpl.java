package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.api.account.SecureApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetSecureInfoApiParams;
import com.sogou.upd.passport.manager.api.account.form.ResetPasswordBySecQuesApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdatePwdApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateQuesApiParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountInfoService;
import com.sogou.upd.passport.service.account.AccountService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-7 Time: 下午8:24 To change this template use
 * File | Settings | File Templates.
 */
@Component("sgSecureApiManager")
public class SGSecureApiManagerImpl implements SecureApiManager {
    private static Logger logger = LoggerFactory.getLogger(SGSecureApiManagerImpl.class);

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountInfoService accountInfoService;

    @Override
    public Result updatePwd(UpdatePwdApiParams updatePwdApiParams) {
        Result result = new APIResultSupport(false);
        String userId = updatePwdApiParams.getUserid();
        String password = updatePwdApiParams.getPassword();
        String newPassword = updatePwdApiParams.getNewpassword();
        String modifyIp = updatePwdApiParams.getModifyip();
        Account account = accountService.verifyUserPwdVaild(userId, password, false);
        if (account == null) {
            result.setCode(ErrorUtil.USERNAME_PWD_MISMATCH);
            return result;
        }

        if (!accountService.resetPassword(account, newPassword, false)) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_FAILED);
        }
        result.setSuccess(true);
        return result;
    }

    @Override
    public Result updateQues(UpdateQuesApiParams updateQuesApiParams) throws ServiceException {
        String userId = updateQuesApiParams.getUserid();
        String newQues = updateQuesApiParams.getNewquestion();
        String newAnswer = updateQuesApiParams.getNewanswer();

        Result result = accountInfoService.modifyQuesByPassportId(userId, newQues, newAnswer);
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
