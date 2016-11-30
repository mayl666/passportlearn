package com.sogou.upd.passport.manager.api.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.api.account.BindApiManager;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountInfoService;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.EmailSenderService;
import com.sogou.upd.passport.service.account.dataobject.ActiveEmailDO;
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

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountInfoService accountInfoService;
    @Autowired
    private EmailSenderService emailSenderService;
    @Autowired
    private LoginApiManager sgLoginApiManager;

    @Override
    public Result bindEmail(String passportId, int clientId, String password, String newEmail, String oldEmail, String ru) {
        Result result;
        String pwdMD5 = password;
        try {
            pwdMD5 = Coder.encryptMD5(password);
        } catch (Exception e) {
        }
        AuthUserApiParams authParams = new AuthUserApiParams(clientId, passportId, pwdMD5);
        result = sgLoginApiManager.webAuthUser(authParams);    //验证密码
        if (!result.isSuccess()) {
            return result;
        }
        String bindEmail = accountInfoService.queryBindEmailByPassportId(passportId);
        if (!Strings.isNullOrEmpty(bindEmail) && !bindEmail.equals(oldEmail)) {   // 验证用户输入原绑定邮箱
            result.setSuccess(false);
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_CHECKOLDEMAIL_FAILED);
            return result;
        }
        ActiveEmailDO activeEmailDO = new ActiveEmailDO(passportId, clientId, ru, AccountModuleEnum.SECURE, newEmail, true);
        if (!emailSenderService.sendEmail(activeEmailDO)) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_SENDEMAIL_FAILED);
            return result;
        }
        result.setSuccess(true);
        result.setMessage("绑定邮箱验证邮件发送成功！");
        return result;
    }

    @Override
    public Result bindMobile(String passportId, String newMobile, Account account) {
        Result result = new APIResultSupport(false);
        try {
            if (account == null) {
                result.setCode(ErrorUtil.INVALID_ACCOUNT);
                return result;
            }
            boolean isSgBind = accountService.bindMobile(account, newMobile);
            if (isSgBind) {
                result.setSuccess(true);
                result.setMessage("操作成功");
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
                result.setCode(ErrorUtil.INVALID_ACCOUNT);
                return result;
            }
            // 修改绑定手机，checkCode为secureCode  TODO 不知道scode是干嘛用的
//            if (!accountSecureService.checkSecureCodeModSecInfo(passportId, clientId, scode)) {
//                result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BIND_FAILED);
//                return result;
//            }
            boolean isSgModifyBind = accountService.modifyBindMobile(account, newMobile);
            if (isSgModifyBind) {
                result.setSuccess(true);
                result.setMessage("操作成功");
            } else {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDMOBILE_FAILED);
            }
        } catch (ServiceException e) {
            logger.error("modifyBindMobile Exception", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

}
