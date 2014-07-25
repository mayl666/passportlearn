package com.sogou.upd.passport.manager.api.account.impl;

import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
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

    @Autowired
    private BindApiManager sgBindApiManager;
    @Autowired
    private AccountService accountService;

    @Override
    public Result bindEmail(BindEmailApiParams bindEmailApiParams) {
        Result result;
        String password = bindEmailApiParams.getPassword();
        String pwdMD5 = password;
        try {
            pwdMD5 = Coder.encryptMD5(password);
        } catch (Exception e) {
        }
        bindEmailApiParams.setPassword(pwdMD5);   //需要传MD5加密后的密码
        result = sgBindApiManager.bindEmail(bindEmailApiParams);
        return result;
    }

    @Override
    public Result getPassportIdByMobile(BaseMoblieApiParams baseMoblieApiParams) {
        return null;
    }

    @Override
    public Result bindMobile(String passportId, String newMobile, Account account) {
        Result result = new APIResultSupport(false);
        try {
            if (account == null) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
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
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
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

    @Override
    public Result unBindMobile(String mobile) {
        return null;
    }

}
