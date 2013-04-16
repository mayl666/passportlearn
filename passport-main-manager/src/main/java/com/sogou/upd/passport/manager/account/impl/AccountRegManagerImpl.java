package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.exception.SystemException;
import com.sogou.upd.passport.common.parameter.AccountStatusEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.manager.account.AccountRegManager;
import com.sogou.upd.passport.manager.account.parameters.RegisterParameters;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountAuth;
import com.sogou.upd.passport.service.account.AccountAuthService;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import com.sogou.upd.passport.service.account.generator.PwdGenerator;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Date;
import java.util.Map;

/**
 * User: mayan
 * Date: 13-4-15
 * Time: 下午4:43
 * To change this template use File | Settings | File Templates.
 */
@Component
public class AccountRegManagerImpl implements AccountRegManager {

    @Inject
    private AccountService accountService;

    @Inject
    private AccountAuthService accountAuthService;

    @Override
    public Result mobileRegister(RegisterParameters regParams) throws Exception {
        Result result = new Result();
        String errorCode =  null;
        if (regParams == null) {
            //todo 返回result格式的错误信息 “提示用户注册信息参数有误”
            return null;
        }
        String mobile = regParams.getMobile();
        String smsCode = regParams.getSmscode();
        String password = regParams.getPassword();
        String ip = regParams.getIp();
        int clientId = regParams.getClientId();
        String instanceId = regParams.getInstanceId();
        //直接查询Account的mobile字段
        Account existAccount = accountService.getAccountByUserName(mobile);
        if (existAccount != null) {
//            result.smsCode
            //todo 返回result格式的错误信息“这个帐号已经注册过啦”
            return null;
        }
        //验证手机号码与验证码是否匹配
        boolean checkSmsInfo = accountService.checkSmsInfoFromCache(mobile, smsCode, String.valueOf(clientId));
        if (!checkSmsInfo) {
            // todo 返回result格式的错误信息  "手机号码和验证码不匹配"
            return null;
        }
        Account account = createAccount(mobile, password, ip, AccountTypeEnum.PHONE.getValue());
        if (account != null) {  //     如果插入account表成功，则插入用户授权信息表
            //生成token并向account_auth表里插一条用户状态记录
            AccountAuth accountAuth = accountAuthService.initialAccountAuth(account.getId(), account.getPassportId(), clientId, instanceId);
            if (accountAuth != null) {   //如果用户授权信息表插入也成功，则说明注册成功
                accountService.addPassportIdMapUserIdToCache(account.getPassportId(), Long.toString(account.getId()));
                //清除验证码的缓存
                accountService.deleteSmsCache(mobile, String.valueOf(clientId));
                String accessToken = accountAuth.getAccessToken();
                long accessValidTime = accountAuth.getAccessValidTime();
                String refreshToken = accountAuth.getRefreshToken();
                Map<String, Object> mapResult = Maps.newHashMap();
                mapResult.put("access_token", accessToken);
                mapResult.put("expires_time", accessValidTime);
                mapResult.put("refresh_token", refreshToken);
                //todo 返回result格式的用户注册成功后的提示信息   “用户注册成功”
                return null;
            } else {
                //todo 返回result格式的用户注册成功后的提示信息   “用户注册失败”
                return null;
            }
        } else {
            //todo 返回result格式的用户注册成功后的提示信息   “用户注册失败”
            return null;
        }
    }

    @Override
    public Result findPassword(String mobile, int clientId) {
        Account account = accountService.getAccountByUserName(mobile);
        if (account == null) {
            //todo 返回result格式的用户注册成功后的提示信息   “该手机用户不存在”
            return null;
        }
        //判断账号是否被缓存
        String cacheKey = mobile + "_" + clientId;
        boolean isExistFromCache = accountService.checkCacheKeyIsExist(cacheKey);
        Map<String, Object> mapResult;
        if (isExistFromCache) {
            //更新缓存状态
            mapResult = accountService.updateSmsInfoByCacheKeyAndClientid(cacheKey, clientId);
            //todo 返回result格式的用户注册成功后的提示信息   “mapResult对象”
            return null;
        } else {
            mapResult = accountService.handleSendSms(mobile, clientId);
        }
        //todo 返回result格式的用户注册成功后的提示信息   “mapResult对象或‘手机验证码发送失败’);”
        return null;
    }

    @Override
    public Result resetPassword(RegisterParameters regParams) throws Exception {
        if (regParams == null) {
            //todo 返回result格式的错误信息 “提示用户注册信息参数有误”
            return null;
        }
        String mobile = regParams.getMobile();
        String smsCode = regParams.getSmscode();
        String password = regParams.getPassword();
        String ip = regParams.getIp();
        int clientId = regParams.getClientId();
        String instanceId = regParams.getInstanceId();
        //验证手机号码与验证码是否匹配
        boolean checkSmsInfo = accountService.checkSmsInfoFromCache(mobile, smsCode, String.valueOf(clientId));
        if (!checkSmsInfo) {
            //todo 返回result格式的错误信息 “手机号码与验证码不匹配”
            return null;
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
            //todo 返回result格式的错误信息 “手机号码与验证码不匹配”
            return null;
        } else {
            //todo 返回result格式的错误信息 “重置密码失败”
            return null;
        }
    }

    @Override
    public Result getUserIdByPassportId(String passportId) {
        long userId;
        if (passportId != null) {
            userId = accountService.getUserIdByPassportId(passportId);
            //todo 成功则返回result格式的userId
            return null;
        }
        //todo 失败则返回result格式的错误信息，为空
        return null;
    }


    /**
     * 构造一个新的account对象
     *
     * @param mobile   手机
     * @param password 密码
     * @param ip       ip地址
     * @param provider 用户类型
     * @return 返回构造的新对象
     * @throws SystemException
     */
    private Account createAccount(String mobile, String password, String ip, int provider) throws SystemException {
        Account account = new Account();
        account.setPassportId(PassportIDGenerator.generator(mobile, provider));
        String passwordSign = null;
        if (!Strings.isNullOrEmpty(password)) {
            passwordSign = PwdGenerator.generatorPwdSign(password);
        }
        account.setPasswd(passwordSign);
        account.setRegTime(new Date());
        account.setRegIp(ip);
        account.setAccountType(provider);
        account.setStatus(AccountStatusEnum.REGULAR.getValue());
        account.setVersion(Account.NEW_ACCOUNT_VERSION);
        String mobileResult = null;
        if (AccountTypeEnum.isPhone(mobile, provider)) {
            mobileResult = mobile;
        }
        account.setMobile(mobileResult);
        //todo 此处调用service层的save方法保存数据至account表中
//        long id = accountService.saveAccount(account);
        long id = 0;
        if (id != 0) {
            return account;
        }
        return null;
    }

    @Override
    public Result checkAccountIsValid(long userId) {
        //todo 此处调用service层相应的方法，Account account = accountService.getAccountByUserId(userId);
        Account account = null;
        if (account.isNormalAccount()) {
            //todo 成功则返回result格式的account对象
            return null;
        }
        //todo 失败则返回result格式的提示信息
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result getAccountByUserName(String username) {
        // TODO 加缓存,两个方法可以合并，采用动态查询sql,但合并的话缓存写起来不太方便
        Account account;
        if (PhoneUtil.verifyPhoneNumberFormat(username)) {
           // todo 调用service的方法 account = accountService.getAccountByMobile(username);
        } else {
            //todo 调用service的方法 account = accountService.getAccountByPassportId(username);
        }
        //todo 返回result格式的提示信息,account对象
        return null;
    }

    @Override
    public Result checkUserPwdIsValid(String username, String password) {
        if (!Strings.isNullOrEmpty(username) && !Strings.isNullOrEmpty(password)) {
            String pwdSign;
            try {
                pwdSign = PwdGenerator.generatorPwdSign(password);
            } catch (SystemException e) {
                e.printStackTrace();
                return null;
            }
            Result userAccount = getAccountByUserName(username);
            //todo 调用userAccount里的account对象的密码，做比较       userAccount.getPasswd()
            if (userAccount != null && pwdSign.equals("")) {
                return userAccount;
            }
        }
        return null;

    }
}
