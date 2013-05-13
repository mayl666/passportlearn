package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.SMSUtil;
import com.sogou.upd.passport.common.utils.StringUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.AccountSecureManager;
import com.sogou.upd.passport.manager.form.MobileModifyPwdParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountInfo;
import com.sogou.upd.passport.service.account.AccountInfoService;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.AccountTokenService;
import com.sogou.upd.passport.service.account.EmailSenderService;
import com.sogou.upd.passport.service.account.MobileCodeSenderService;
import com.sogou.upd.passport.service.account.MobilePassportMappingService;
import com.sogou.upd.passport.service.app.AppConfigService;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * User: mayan Date: 13-4-15 Time: 下午4:31 To change this template use File | Settings | File
 * Templates.
 */
@Component
public class AccountSecureManagerImpl implements AccountSecureManager {

    private static Logger logger = LoggerFactory.getLogger(AccountSecureManager.class);

    @Autowired
    private MobileCodeSenderService mobileCodeSenderService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountInfoService accountInfoService;
    @Autowired
    private AccountTokenService accountTokenService;
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private MobilePassportMappingService mobilePassportMappingService;
    @Autowired
    private EmailSenderService emailSenderService;
    //account与smscode映射
    private static final String CACHE_PREFIX_ACCOUNT_SMSCODE = CacheConstant.CACHE_PREFIX_MOBILE_SMSCODE;
    private static final String CACHE_PREFIX_ACCOUNT_SENDNUM = CacheConstant.CACHE_PREFIX_MOBILE_SENDNUM;

    @Override
    public Result sendMobileCode(String mobile, int clientId) {
        //判断账号是否被缓存
        String cacheKey = mobile + "_" + clientId;
        try {
            boolean isExistFromCache = mobileCodeSenderService.checkIsExistMobileCode(cacheKey);
            Result result = null;
            if (isExistFromCache) {
                //更新缓存状态
                result = updateSmsCacheInfo(cacheKey, clientId);
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
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }

    @Override
    public Result sendMobileCodeByPassportId(String passportId, int clientId) {
        try {
            Account account = accountService.queryAccountByPassportId(passportId);
            if (account == null) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            }
            String mobile = account.getMobile();
            if (Strings.isNullOrEmpty(mobile)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_OBTAIN_FIELDS);
            }
            String cacheKey = mobile + "_" + clientId;
            boolean isExistFromCache = mobileCodeSenderService.checkIsExistMobileCode(cacheKey);
            Result result = null;
            if (isExistFromCache) {
                //更新缓存状态
                result = updateSmsCacheInfo(cacheKey, clientId);
                return result;
            } else {
                result = mobileCodeSenderService.handleSendSms(mobile, clientId);
                if (result != null) {
                    return result;
                } else {
                    result = Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND);
                    return result;
                }
            }
        } catch (ServiceException e) {
            logger.error("send mobile code Fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }

    @Override
    public Result updateSmsCacheInfo(String cacheKey, int clientId) {
        Result result = null;
        try {
            String cacheKeySmscode = CACHE_PREFIX_ACCOUNT_SMSCODE + cacheKey;
            Map<String, String> mapCacheResult = mobileCodeSenderService.getCacheMapByKey(cacheKeySmscode);
            if (MapUtils.isNotEmpty(mapCacheResult)) {
                //获取缓存数据
                long sendTime = Long.parseLong(mapCacheResult.get("sendTime"));
                String smsCode = mapCacheResult.get("smsCode");
                String mobile = mapCacheResult.get("mobile");
                //获取当天发送次数
                String cacheKeySendNum = CACHE_PREFIX_ACCOUNT_SENDNUM + mobile + "_" + clientId;

                Map<String, String> mapCacheSendNumResult = mobileCodeSenderService.getCacheMapByKey(cacheKeySendNum);
                if (MapUtils.isNotEmpty(mapCacheSendNumResult)) {
                    int sendNum = Integer.parseInt(mapCacheSendNumResult.get("sendNum"));
                    long curtime = System.currentTimeMillis();
                    boolean valid = curtime >= (sendTime + SMSUtil.SEND_SMS_INTERVAL); // 1分钟只能发1条短信
                    if (valid) {
                        if (sendNum < SMSUtil.MAX_SMS_COUNT_ONEDAY) {     //每日最多发送短信验证码条数
                            //生成随机数
                            String randomCode = RandomStringUtils.randomNumeric(5);
                            //读取短信内容
                            String smsText = appConfigService.querySmsText(clientId, randomCode);
                            if (!Strings.isNullOrEmpty(smsText) && SMSUtil.sendSMS(mobile, smsText)) {
                                //更新缓存
                                mobileCodeSenderService.updateSmsCacheInfo(cacheKeySendNum, cacheKeySmscode,
                                        String.valueOf(curtime),randomCode);
                                result = Result.buildSuccess("获取验证码成功");
                                return result;
                            } else {
                                result = Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND);
                                return result;
                            }
                        } else {
                            result = Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_CANTSENTSMS);
                            return result;
                        }
                    } else {
                        result = Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_MINUTELIMIT);
                        return result;
                    }
                }
            } else {
                result = Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND);
            }
        } catch (Exception e) {
            logger.error("[SMS] service method updateSmsCacheInfoByKeyAndClientId error.{}", e);
            result = Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND);
        }
        return result;
    }


    @Override
    public Result findPassword(String mobile, int clientId) {
        try {
            //判断账号是否被缓存
            String cacheKey = mobile + "_" + clientId;
            boolean isExistFromCache = mobileCodeSenderService.checkIsExistMobileCode(cacheKey);
            Result mapResult;
            if (isExistFromCache) {
                //更新缓存状态
                mapResult = updateSmsCacheInfo(cacheKey, clientId);
            } else {
                mapResult = mobileCodeSenderService.handleSendSms(mobile, clientId);
            }
            return mapResult;
        } catch (ServiceException e) {
            logger.error("find passport Fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }

    @Override
    public Result resetPassword(MobileModifyPwdParams regParams) throws Exception {
        String mobile = regParams.getMobile();
        String smsCode = regParams.getSmscode();
        String password = regParams.getPassword();
        int clientId = Integer.parseInt(regParams.getClient_id());
        try {
            //验证手机号码与验证码是否匹配
            boolean checkSmsInfo = mobileCodeSenderService.checkSmsInfoFromCache(mobile, smsCode, clientId);
            if (!checkSmsInfo) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOT_MATCH_SMSCODE);
            }
            //重置密码
            String passportId = mobilePassportMappingService.queryPassportIdByMobile(mobile);
            if (Strings.isNullOrEmpty(passportId)) {
                return Result.buildError(ErrorUtil.INVALID_ACCOUNT);
            }

            if (!accountService.checkResetPwdLimited(passportId)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_LIMITED);
            }

            if (!accountService.resetPassword(passportId, password)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_FAILED);
            }
            // 异步更新accountToken信息
            accountTokenService.asynbatchUpdateAccountToken(passportId, clientId);
            //清除验证码的缓存
            mobileCodeSenderService.deleteSmsCache(mobile, clientId);
            return Result.buildSuccess("重置密码成功！");
        } catch (ServiceException e) {
            logger.error("reset password Fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }

    @Override
    public Result queryAccountSecureInfo(String passportId, int clientId) throws Exception {
        Map<String, Object> mapResult = Maps.newHashMap();

        mapResult.put("secMobile", "");
        mapResult.put("secEmail", "");
        mapResult.put("secQues", "");

        try {
            Account account = accountService.queryAccountByPassportId(passportId);
            if (account == null) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            } else {
                if (!accountService.checkResetPwdLimited(passportId)) {
                    return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_LIMITED);
                }
                AccountInfo accountInfo;
                if (!Strings.isNullOrEmpty(account.getMobile())) {
                    mapResult.put("secMobile", account.getMobile().substring(0, 3).concat("*****").
                            concat(account.getMobile().substring(account.getMobile().length() - 3)));
                }
                accountInfo = accountInfoService.queryAccountInfoByPassportId(passportId);
                if (accountInfo != null) {
                    String processEmail = "";
                    if (!Strings.isNullOrEmpty(accountInfo.getEmail())) {
                        String email = accountInfo.getEmail();
                        processEmail = email.substring(0, 2).concat("*****").
                                concat(email.substring(email.indexOf("@")-1));
                    }
                    mapResult.put("secEmail", processEmail);
                    mapResult.put("secQues", StringUtil.defaultIfEmpty(accountInfo.getQuestion(), ""));
                }
                if (Strings.isNullOrEmpty((String)mapResult.get("secEmail")) &&
                    AccountDomainEnum.getAccountDomain(passportId) == AccountDomainEnum.getDomain("other")) {
                    mapResult.put("secEmail", passportId.substring(0, 2).concat("*****").
                            concat(passportId.substring(passportId.indexOf("@")-1)));
                }
            }
        } catch (ServiceException e) {
            logger.error("query account_secure_info Fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }

        return Result.buildSuccess("查询成功", "secinfo", mapResult);
    }

    @Override
    public Result sendEmailResetPwdByPassportId(String passportId, int clientId) throws Exception {
        try {
            if (accountService.queryAccountByPassportId(passportId) == null) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            }
            boolean isOtherDomain = (AccountDomainEnum.getAccountDomain(passportId) == AccountDomainEnum.getDomain("other"));
            AccountInfo accountInfo = accountInfoService.queryAccountInfoByPassportId(passportId);
            if (accountInfo == null || Strings.isNullOrEmpty(accountInfo.getEmail())) {
                if (isOtherDomain) {
                    // 外域用户无绑定邮箱
                    if (!emailSenderService.checkSendEmailForPwdLimited(passportId, clientId)) {
                        return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_RESETPWDEMAIL_LIMITED);
                    }
                    if (!emailSenderService.sendEmailForResetPwd(passportId, clientId, passportId)) {
                        return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNTSECURE_SENDEMAIL_FAILED);
                    }
                    return Result.buildSuccess("重置密码申请邮件发送成功");
                } else {
                    return Result.buildError(ErrorUtil.NOTHAS_BINDINGEMAIL);
                }
            }
            String email = accountInfo.getEmail();
            if (!emailSenderService.checkSendEmailForPwdLimited(passportId, clientId)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_RESETPWDEMAIL_LIMITED);
            }
            if (!emailSenderService.sendEmailForResetPwd(passportId, clientId, email)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNTSECURE_SENDEMAIL_FAILED);
            }
            return Result.buildSuccess("重置密码申请邮件发送成功");
        } catch (ServiceException e) {
            logger.error("send email fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }

    @Override
    public Result checkEmailResetPwd(String uid, int clientId, String token) throws Exception {
        try {
            if (!emailSenderService.checkEmailForResetPwd(uid, clientId, token)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNTSECURE_RESETPWD_URL_FAILED);
            }
            return Result.buildSuccess("重置密码申请链接验证成功");
        } catch (ServiceException e) {
            logger.error("check email fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }

    @Override
    public Result resetPasswordByQues(String passportId, int clientId, String password,
                                      String answer) throws Exception {
        try {
            if (!accountService.checkResetPwdLimited(passportId)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_LIMITED);
            }
            if (Strings.isNullOrEmpty(answer)) {
                return Result.buildError(ErrorUtil.ERR_CODE_COM_REQURIE);
            }
            if (accountService.queryAccountByPassportId(passportId) == null) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            }
            AccountInfo accountInfo = accountInfoService.queryAccountInfoByPassportId(passportId);
            if (accountInfo == null || Strings.isNullOrEmpty(accountInfo.getAnswer())) {
                return Result.buildError(ErrorUtil.NOTHAS_BINDINGQUESTION);
            }
            if (!answer.equals(accountInfo.getAnswer())) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNTSECURE_CHECKANSWER_FAILED);
            }
            if (!accountService.resetPassword(passportId, password)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_FAILED);
            }
            return Result.buildSuccess("重置密码成功！");
        } catch (ServiceException e) {
            logger.error("reset password by ques fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }

    @Override
    public Result resetPasswordByEmail(String passportId, int clientId, String password, String token) throws Exception {
        try {
            if (!accountService.checkResetPwdLimited(passportId)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_LIMITED);
            }
            if (!emailSenderService.checkEmailForResetPwd(passportId, clientId, token)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNTSECURE_RESETPWD_URL_FAILED);
            }
            if (!accountService.resetPassword(passportId, password)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_FAILED);
            }
            // 删除邮件链接token缓存
            emailSenderService.deleteEmailCacheResetPwd(passportId, clientId);
            return Result.buildSuccess("重置密码成功！");
        } catch (ServiceException e) {
            logger.error("reset password Fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }

    @Override
    public Result resetPasswordByMobile(String passportId, int clientId, String password,
                                        String smsCode) throws Exception {
        try {
            if (!accountService.checkResetPwdLimited(passportId)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_LIMITED);
            }
            if (Strings.isNullOrEmpty(smsCode)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE);
            }
            Account account = accountService.queryAccountByPassportId(passportId);
            if (account == null) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            }
            String mobile = account.getMobile();
            if (Strings.isNullOrEmpty(mobile)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_OBTAIN_FIELDS);
            }

            // 验证错误次数是否小于限制次数
            boolean checkFailLimited = mobileCodeSenderService.checkSmsFailLimited(mobile, clientId);
            if (!checkFailLimited) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_CHECKSMSCODE_LIMIT);
            }

            // 验证手机号码与验证码是否匹配
            if (!mobileCodeSenderService.checkSmsInfoFromCache(mobile, smsCode, clientId)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOT_MATCH_SMSCODE);
            }

            if (!accountService.resetPassword(passportId, password)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_FAILED);
            }
            //清除验证码的缓存
            mobileCodeSenderService.deleteSmsCache(mobile, clientId);
            return Result.buildSuccess("重置密码成功！");
        } catch (ServiceException e) {
            logger.error("reset password Fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }
}