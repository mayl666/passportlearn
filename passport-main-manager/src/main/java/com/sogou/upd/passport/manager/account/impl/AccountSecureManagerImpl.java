package com.sogou.upd.passport.manager.account.impl;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.AccountSecureManager;
import com.sogou.upd.passport.manager.account.parameters.RegisterParameters;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountAuth;
import com.sogou.upd.passport.service.account.AccountAuthService;
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

    @Inject
    private AccountAuthService accountAuthService;

    @Override
    public Result sendMobileCode(String mobile, int clientId) {
        //判断账号是否被缓存
        String cacheKey = mobile + "_" + clientId;
        boolean isExistFromCache = accountService.checkCacheKeyIsExist(cacheKey);
        Result result = null;
        if (isExistFromCache) {
            //更新缓存状态
            result = accountService.updateSmsCacheInfoByKeyAndClientId(cacheKey, clientId);
            return result;
        } else {
            Account account = accountService.getAccountByUserName(mobile);
            if (account == null) {
                //未注册过
                result = accountService.handleSendSms(mobile, clientId);
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
    }


    @Override
    public Result findPassword(String mobile, int clientId) {
        Account account = accountService.getAccountByUserName(mobile);
        if (account == null) {
            return Result.buildError(ErrorUtil.ERR_CODE_COM_NOUSER);
        }
        //判断账号是否被缓存
        String cacheKey = mobile + "_" + clientId;
        boolean isExistFromCache = accountService.checkCacheKeyIsExist(cacheKey);
        Result mapResult;
        if (isExistFromCache) {
            //更新缓存状态
            mapResult = accountService.updateSmsCacheInfoByKeyAndClientId(cacheKey, clientId);
        } else {
            mapResult = accountService.handleSendSms(mobile, clientId);
        }
        return mapResult;
    }

    @Override
    public Result resetPassword(RegisterParameters regParams) throws Exception {
        //TODO 入口参数验证?
        String mobile = regParams.getMobile();
        String smsCode = regParams.getSmscode();
        String password = regParams.getPassword();
        int clientId = regParams.getClient_id();
        String instanceId = regParams.getInstance_id();
        //验证手机号码与验证码是否匹配
        boolean checkSmsInfo = accountService.checkSmsInfoFromCache(mobile, smsCode, String.valueOf(clientId));
        if (!checkSmsInfo) {
            return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOT_MATCH_SMSCODE);
        }
        //重置密码
        Account account = accountService.resetPassword(mobile, password);
        //先更新当前客户端实例对应的access_token和refresh_token，再异步更新该用户其它客户端的两个token
        AccountAuth accountAuthResult = null;
        if (account != null) {
            accountAuthResult = accountAuthService.updateAccountAuth(account.getId(), account.getPassportId(), clientId, instanceId);
            //TODO 存在分库分表问题
            accountAuthService.asynUpdateAccountAuthBySql(mobile, clientId, instanceId);
        }
        if (accountAuthResult != null) {
            //清除验证码的缓存
            accountService.deleteSmsCache(mobile, String.valueOf(clientId));
            return Result.buildSuccess("重置密码成功！", null, null);
        } else {
            return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_FAILED);
        }
    }
}
