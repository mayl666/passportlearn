package com.sogou.upd.passport.manager.account.impl;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.AccountSecureManager;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * User: mayan
 * Date: 13-4-15
 * Time: 下午4:31
 * To change this template use File | Settings | File Templates.
 */
@Component
public class AccountSecureManagerImpl implements AccountSecureManager {

    @Inject
    private AccountService accountService;

    @Override
    public Result sendMobileCode(String mobile, int clientId) {

        //判断账号是否被缓存
        String cacheKey = mobile + "_" + clientId;
        boolean isExistFromCache = accountService.checkCacheKeyIsExist(cacheKey);
        Result result = new Result();
        String error_code=null;
        if (isExistFromCache) {
            //更新缓存状态
            result = accountService.updateSmsCacheInfoByKeyAndClientId(cacheKey, clientId);
            return result;
        } else {
            Account account = accountService.getAccountByUserName(mobile);
            if (account == null) {
                //未注册过
                result = accountService.handleSendSms(mobile, clientId);
                if (result!=null) {
                    return result;
                } else {
                    error_code=ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND;
                    result.setStatus(error_code);
                    result.setStatusText(ErrorUtil.getERR_CODE_MSG(error_code));
                    return result;
                }
            } else {
                error_code=ErrorUtil.ERR_CODE_ACCOUNT_REGED;
                result.setStatus(error_code);
                result.setStatusText(ErrorUtil.getERR_CODE_MSG(error_code));
                return result;
            }
        }
    }
}
