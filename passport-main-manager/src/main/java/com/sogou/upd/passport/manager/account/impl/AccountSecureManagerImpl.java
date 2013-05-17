package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.PasswordTypeEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
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
            String passportId = mobilePassportMappingService.queryPassportIdByMobile(mobile);
            if (Strings.isNullOrEmpty(passportId)) {
                return sendSmsCodeToMobile(mobile, clientId);
            } else {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_REGED);
            }
        } catch (ServiceException e) {
            logger.error("send mobile code Fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }

    @Override
    public Result sendSmsCodeToMobile(String mobile, int clientId) {
        try {
            if (Strings.isNullOrEmpty(mobile) || !PhoneUtil.verifyPhoneNumberFormat(mobile)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_PHONEERROR);
            }
            // 验证错误次数是否小于限制次数
            boolean checkFailLimited = mobileCodeSenderService.checkSmsFailLimited(mobile, clientId);
            if (!checkFailLimited) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_CHECKSMSCODE_LIMIT);
            }

            String cacheKey = mobile + "_" + clientId;
            boolean isExistFromCache = mobileCodeSenderService.checkIsExistMobileCode(cacheKey);
            if (isExistFromCache) {
                return updateSmsCacheInfo(cacheKey, clientId);
            } else {
                return mobileCodeSenderService.handleSendSms(mobile, clientId);
            }
        } catch (ServiceException e) {
            logger.error("send sms code to mobile Fail:", e);
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
            return sendSmsCodeToMobile(mobile, clientId);
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
                                        String.valueOf(curtime), randomCode);
                                result = Result.buildSuccess("验证码已发送至" + mobile);
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
        int pwdType = regParams.getPwd_type();
        boolean needMD5 = pwdType == PasswordTypeEnum.Plaintext.getValue() ? true : false;

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

            if (!accountService.resetPassword(passportId, password, needMD5)) {
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

    /*---------------------------------------重置密码相关-----------------------------------------*/
    @Override
    public Result queryAccountSecureInfo(String passportId, int clientId) throws Exception {
        Map<String, Object> mapResult = Maps.newHashMap();

        mapResult.put("sec_mobile", "");
        mapResult.put("sec_email", "");
        mapResult.put("sec_ques", "");
        mapResult.put("reg_email", "");

        try {
            Account account = accountService.queryAccountByPassportId(passportId);
            if (account == null) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            }
            if (!accountService.checkResetPwdLimited(passportId)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_LIMITED);
            }
            AccountInfo accountInfo;
            if (!Strings.isNullOrEmpty(account.getMobile())) {
                mapResult.put("sec_mobile", account.getMobile().substring(0, 3).concat("*****").
                        concat(account.getMobile().substring(account.getMobile().length() - 3)));
            }
            accountInfo = accountInfoService.queryAccountInfoByPassportId(passportId);
            if (accountInfo != null) {
                String processEmail = "";
                if (!Strings.isNullOrEmpty(accountInfo.getEmail())) {
                    String email = accountInfo.getEmail();
                    processEmail = email.substring(0, 2).concat("*****").
                            concat(email.substring(email.indexOf("@") - 1));
                }
                mapResult.put("sec_email", processEmail);
                mapResult.put("sec_ques", StringUtil.defaultIfEmpty(accountInfo.getQuestion(), ""));
            }
            if (AccountDomainEnum.getAccountDomain(passportId) == AccountDomainEnum.getDomain("other")) {
                mapResult.put("reg_email", passportId.substring(0, 2).concat("*****").
                        concat(passportId.substring(passportId.indexOf("@") - 1)));
            }
        } catch (ServiceException e) {
            logger.error("query account_secure_info Fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }

        return Result.buildSuccess("查询成功", "sec_info", mapResult);
    }

    @Override
    public Result sendEmailResetPwdByPassportId(String passportId, int clientId, int mode) throws Exception {
        try {
            if (accountService.queryAccountByPassportId(passportId) == null) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            }
            if (mode == 1) {
                // 使用注册邮箱
                boolean isOtherDomain = (AccountDomainEnum.getAccountDomain(passportId) == AccountDomainEnum.getDomain("other"));
                if (isOtherDomain) {
                    // 外域用户无绑定邮箱
                    return sendEmailResetPwd(passportId, clientId, passportId);
                } else {
                    return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNTSECURE_RESETPWD_EMAIL_FAILED);
                }
            } else {
                // 使用绑定邮箱
                AccountInfo accountInfo = accountInfoService.queryAccountInfoByPassportId(passportId);
                if (accountInfo == null || Strings.isNullOrEmpty(accountInfo.getEmail())) {
                    return Result.buildError(ErrorUtil.NOTHAS_BINDINGEMAIL);
                } else {
                    return sendEmailResetPwd(passportId, clientId, accountInfo.getEmail());
                }
            }
        } catch (ServiceException e) {
            logger.error("send email for reset pwd by passportId fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }

    private Result sendEmailResetPwd(String passportId, int clientId, String email) throws Exception {
        try {
            if (Strings.isNullOrEmpty(email)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNTSECURE_RESETPWD_EMAIL_FAILED);
            }
            if (!emailSenderService.checkSendEmailForPwdLimited(email, clientId)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_SENDEMAIL_LIMITED);
            }
            if (!emailSenderService.sendEmailForResetPwd(passportId, clientId, email)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNTSECURE_SENDEMAIL_FAILED);
            }
            return Result.buildSuccess("重置密码申请邮件发送成功");
        } catch (ServiceException e) {
            logger.error("send email for reset pwd fail:", e);
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
            if (!accountService.resetPassword(passportId, password, true)) {
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
            if (!accountService.resetPassword(passportId, password, true)) {
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

            if (!accountService.resetPassword(passportId, password, true)) {
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

    /* --------------------------------------------修改密保内容-------------------------------------------- */
    /*
     * 验证手机短信随机码——用于新手机验证
     */
    @Override
    public Result checkMobileCode(String mobile, int clientId, String smsCode) throws Exception {
        try {
            if (Strings.isNullOrEmpty(smsCode)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE);
            }

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

            //清除验证码的缓存
            mobileCodeSenderService.deleteSmsCache(mobile, clientId);
            return Result.buildSuccess("短信随机码验证成功！");
        } catch (ServiceException e) {
            logger.error("check mobile code Fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }

    /*
     * 验证手机短信随机码——用于原绑定手机验证
     */
    @Override
    public Result checkMobileCodeByPassportId(String passportId, int clientId, String smsCode) throws Exception {
        try {
            Account account = accountService.queryAccountByPassportId(passportId);
            if (account == null) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            }
            String mobile = account.getMobile();

            return checkMobileCode(mobile, clientId, smsCode);
        } catch (ServiceException e) {
            logger.error("check mobile code Fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }

    /*
     * 修改绑定手机
     */
    @Override
    public Result modifyMobileByPassportId(String passportId, int clientId, String newMobile) throws Exception {
        try {
            if (Strings.isNullOrEmpty(newMobile)) {
                return Result.buildError(ErrorUtil.ERR_CODE_COM_REQURIE);
            }
            Account account = accountService.queryAccountByPassportId(passportId);
            if (account == null) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            }

            if (accountService.modifyMobile(passportId, newMobile)) {
                mobilePassportMappingService.deleteMobilePassportMapping(account.getMobile());
                mobilePassportMappingService.initialMobilePassportMapping(newMobile, passportId);
                // TODO:事务安全问题，暂不解决
            }

            return Result.buildSuccess("修改绑定手机成功！");
        } catch (ServiceException e) {
            logger.error("modify mobile Fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }

    /*
     * 发送邮件至待绑定邮箱
     */
    @Override
    public Result sendEmailForBinding(String passportId, int clientId, String newEmail, String oldEmail)
            throws Exception {
        try {
            if (Strings.isNullOrEmpty(newEmail)) {
                return Result.buildError(ErrorUtil.ERR_CODE_COM_REQURIE);
            }
            Account account = accountService.queryAccountByPassportId(passportId);
            if (account == null) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            }
            AccountInfo accountInfo = accountInfoService.queryAccountInfoByPassportId(passportId);
            // 有绑定邮箱，检测是否与oldEmail相同；无原邮箱，则不检测
            if (accountInfo != null && !Strings.isNullOrEmpty(accountInfo.getEmail())) {
                if (Strings.isNullOrEmpty(oldEmail) || !oldEmail.equals(accountInfo.getEmail())) {
                    return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNTSECURE_CHECKOLDEMAIL_FAILED);
                }
            }

            if (!emailSenderService.checkSendEmailNumForBinding(newEmail, clientId)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_SENDEMAIL_LIMITED);
            }
            if (!emailSenderService.sendEmailForBinding(passportId, clientId, newEmail)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNTSECURE_SENDEMAIL_FAILED);
            }
            return Result.buildSuccess("绑定邮箱验证邮件发送成功！");
        } catch (ServiceException e) {
            logger.error("send email for binding Fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }

    /*
     * 根据验证链接修改绑定邮箱
     */
    @Override
    public Result modifyEmailByPassportId(String passportId, int clientId, String token) throws Exception {
        try {
            String newEmail = emailSenderService.checkEmailForBinding(passportId, clientId, token);
            if (Strings.isNullOrEmpty(newEmail)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDEMAIL_URL_FAILED);
            }
            if (accountInfoService.modifyEmailByPassportId(passportId, newEmail) == null) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDEMAIL_FAILED);
            }
            emailSenderService.deleteEmailCacheForBinding(passportId, clientId);
            return Result.buildSuccess("修改绑定邮箱成功！");
        } catch (ServiceException e) {
            logger.error("modify binding email Fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }

}