package com.sogou.upd.passport.manager.api.account.impl;

import com.google.common.collect.Maps;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.parameter.SohuPasswordType;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.account.SecureApiManager;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountInfo;
import com.sogou.upd.passport.service.account.AccountHelper;
import com.sogou.upd.passport.service.account.AccountInfoService;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.OperateTimesService;

import org.apache.commons.codec.digest.DigestUtils;
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

    @Autowired
    private AccountService accountService;
    @Autowired
    private OperateTimesService operateTimesService;
    @Autowired
    private AccountInfoService accountInfoService;

    @Override
    public Result updatePwd(String passportId, int clientId, String oldPwd, String newPwd, String modifyIp) {
        Result result = new APIResultSupport(false);
        Account account;
        if (passportId.matches(".+@qq\\.sohu\\.com$")) {    // QQ 账号
            // 第三方账号由于使用进使用 oauth2 协议，所以不需要验证原密码
            account = accountService.queryAccountByPassportId(passportId);
            if (account == null) {
                result.setSuccess(false);
                result.setCode(ErrorUtil.INVALID_ACCOUNT);
                return result;
            }
            if (AccountHelper.isDisabledAccount(account)) {
                result.setSuccess(false);
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NO_ACTIVED_FAILED);
                return result;
            }
            if (AccountHelper.isKilledAccount(account)) {
                result.setSuccess(false);
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_KILLED);
                return result;
            }

            result.setSuccess(true);
            result.setMessage("操作成功");
            result.setDefaultModel("userid", account.getPassportId());
            result.setDefaultModel("uniqName", account.getUniqname());
            result.setDefaultModel(account);
        } else {    // 验证密码
            result = accountService.verifyUserPwdVaild(passportId, oldPwd, true, SohuPasswordType.TEXT);
            if (!result.isSuccess()) {
                operateTimesService.incLimitCheckPwdFail(passportId, clientId, AccountModuleEnum.RESETPWD);
                return result;
            }
            account = (Account) result.getDefaultModel();
        }
        // 密码强度校验
        if(!accountService.isPasswordStrengthStrong(clientId, newPwd)) {
            result = new APIResultSupport(false);
            result.setCode(ErrorUtil.ERR_CODE_PASSWORD_STRENGTH_WEAK);
            return result;
        }

        result.setModels(Maps.newHashMap());
        if (!accountService.resetPassword(passportId,account, newPwd, true)) {
            result = new APIResultSupport(false);
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_FAILED);
            result.setSuccess(false);
        }
        return result;
    }

    @Override
    public Result updateQues(String passportId, int clientId, String password, String newQues, String newAnswer, String modifyIp) {
        Result result = new APIResultSupport(false);
        try {
            Result authUserResult = accountService.verifyUserPwdVaild(passportId, password, true,SohuPasswordType.TEXT);
            authUserResult.setDefaultModel(null);
            if (!authUserResult.isSuccess()) {
                operateTimesService.incLimitCheckPwdFail(passportId, clientId, AccountModuleEnum.SECURE);
                return authUserResult;
            }
            newAnswer = DigestUtils.md5Hex(newAnswer.getBytes(CommonConstant.DEFAULT_CHARSET));
            AccountInfo accountInfo = accountInfoService.modifyQuesByPassportId(passportId, newQues, newAnswer);
            if (accountInfo == null) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDQUES_FAILED);
                return result;
            }
            result.setSuccess(true);
            result.setMessage("操作成功");
            return result;
        } catch (Exception e) {
            logger.error("Update Question fail! passportId:" + passportId, e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

}
