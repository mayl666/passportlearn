package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.exception.ServiceException;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.AccountSecureManager;
import com.sogou.upd.passport.manager.account.parameters.RegisterParameters;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.service.account.AccountTokenService;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.MobileCodeSenderService;

import com.sogou.upd.passport.service.account.MobilePassportMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User: mayan Date: 13-4-15 Time: 下午4:31 To change this template use File | Settings | File Templates.
 */
@Component
public class AccountSecureManagerImpl implements AccountSecureManager {

    private static Logger logger = LoggerFactory.getLogger(AccountSecureManager.class);

    @Autowired
    private MobileCodeSenderService mobileCodeSenderService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountTokenService accountTokenService;
    @Autowired
    private MobilePassportMappingService mobilePassportMappingService;

    @Override
    public Result sendMobileCode(String mobile, int clientId) {
        //判断账号是否被缓存
        String cacheKey = mobile + "_" + clientId;
        try {
            boolean isExistFromCache = mobileCodeSenderService.checkIsExistMobileCode(cacheKey);
            Result result = null;
            if (isExistFromCache) {
                //更新缓存状态
                result = mobileCodeSenderService.updateSmsCacheInfoByKeyAndClientId(cacheKey, clientId);
                return result;
            } else {
                String passportId = mobilePassportMappingService.queryPassportIdByMobile(mobile);
                if (Strings.isNullOrEmpty(passportId)) {
                    //未注册过
                    result = mobileCodeSenderService.handleSendSms(mobile, clientId);
                    if (result != null) {
                        return result;
                    } else {
                        result = Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND);
                        return result;
                    }
                } else {
                    result = Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_REGED);
                    return result;
                }

            }
        } catch (ServiceException e) {
            logger.error("send mobile code Fail:", e);
            return Result.buildError(ErrorUtil.ERR_CODE_COM_EXCEPTION, "unknown error");
        }
    }


    @Override
    public Result findPassword(String mobile, int clientId) {
        // TODO refactoring 是否和sendMobileCode一样？已经存在的就要复用
        try {
            String passportId = mobilePassportMappingService.queryPassportIdByMobile(mobile);
            if (Strings.isNullOrEmpty(passportId)) {
                return Result.buildError(ErrorUtil.ERR_CODE_COM_NOUSER);
            }
            //判断账号是否被缓存
            String cacheKey = mobile + "_" + clientId;
            boolean isExistFromCache = mobileCodeSenderService.checkIsExistMobileCode(cacheKey);
            Result mapResult;
            if (isExistFromCache) {
                //更新缓存状态
                mapResult = mobileCodeSenderService.updateSmsCacheInfoByKeyAndClientId(cacheKey, clientId);
            } else {
                mapResult = mobileCodeSenderService.handleSendSms(mobile, clientId);
            }
            return mapResult;
        } catch (ServiceException e) {
            logger.error("find passport Fail:", e);
            return Result.buildError(ErrorUtil.ERR_CODE_COM_EXCEPTION, "unknown error");
        }
    }

    @Override
    public Result resetPassword(RegisterParameters regParams) throws Exception {
        String mobile = regParams.getMobile();
        String smsCode = regParams.getSmscode();
        String password = regParams.getPassword();
        int clientId = regParams.getClient_id();
        String instanceId = regParams.getInstance_id();
        try {
            //验证手机号码与验证码是否匹配
            boolean checkSmsInfo = mobileCodeSenderService.checkSmsInfoFromCache(mobile, smsCode, clientId);
            if (!checkSmsInfo) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOT_MATCH_SMSCODE);
            }
            //重置密码
            String passportId = mobilePassportMappingService.queryPassportIdByMobile(mobile);
            if (Strings.isNullOrEmpty(passportId)) {
                return Result.buildError(ErrorUtil.ERR_CODE_COM_NOUSER);
            }
            Account account = accountService.resetPassword(passportId, password);
            //先更新当前客户端实例对应的access_token和refresh_token，再异步更新该用户其它客户端的两个token
            AccountToken accountAuthResult = null;
            if (account != null) {
                accountAuthResult = accountTokenService.updateAccountToken(account.getPassportId(), clientId, instanceId);
                // todo refactoring 这个方法应该放在manager里，不应该放在service里
                accountTokenService.asynUpdateAccountAuthBySql(mobile, clientId, instanceId);
            }
            if (accountAuthResult != null) {
                //清除验证码的缓存
                mobileCodeSenderService.deleteSmsCache(mobile, clientId);
                return Result.buildSuccess("重置密码成功！", null, null);
            } else {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_FAILED);
            }
        } catch (ServiceException e) {
            logger.error("rest password Fail:", e);
            return Result.buildError(ErrorUtil.ERR_CODE_COM_EXCEPTION, "unknown error");
        }
    }
}
