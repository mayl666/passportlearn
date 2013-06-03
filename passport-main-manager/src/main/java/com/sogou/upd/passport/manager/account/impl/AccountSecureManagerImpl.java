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
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.AccountSecureManager;
import com.sogou.upd.passport.manager.form.AccountSecureInfoParams;
import com.sogou.upd.passport.manager.form.MobileModifyPwdParams;
import com.sogou.upd.passport.manager.form.ResetPwdParameters;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountInfo;
import com.sogou.upd.passport.service.account.AccountInfoService;
import com.sogou.upd.passport.service.account.AccountSecureService;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.AccountTokenService;
import com.sogou.upd.passport.service.account.EmailSenderService;
import com.sogou.upd.passport.service.account.MobileCodeSenderService;
import com.sogou.upd.passport.service.account.MobilePassportMappingService;
import com.sogou.upd.passport.service.account.generator.PwdGenerator;
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
    @Autowired
    private AccountSecureService accountSecureService;
    //account与smscode映射
    private static final String CACHE_PREFIX_ACCOUNT_SMSCODE = CacheConstant.CACHE_PREFIX_MOBILE_SMSCODE;
    private static final String CACHE_PREFIX_ACCOUNT_SENDNUM = CacheConstant.CACHE_PREFIX_MOBILE_SENDNUM;

    /*
     * 发送短信至未绑定手机，只检测映射表，查询passportId不存在或为空即认定为未绑定
     */
    @Override
    public Result sendMobileCode(String mobile, int clientId) throws Exception {
        try {
            String passportId = mobilePassportMappingService.queryPassportIdByMobile(mobile);
            if (Strings.isNullOrEmpty(passportId)) {
                return sendSmsCodeToMobile(mobile, clientId);
            } else {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_BINDED);
            }
        } catch (ServiceException e) {
            logger.error("send mobile code Fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }

    @Override
    public Result sendSmsCodeToMobile(String mobile, int clientId) throws Exception {
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
    public Result sendMobileCodeByPassportId(String passportId, int clientId) throws Exception {
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
            Map<String, String>
                    mapCacheResult =
                    mobileCodeSenderService.getCacheMapByKey(cacheKeySmscode);
            if (MapUtils.isNotEmpty(mapCacheResult)) {
                //获取缓存数据
                long sendTime = Long.parseLong(mapCacheResult.get("sendTime"));
                String smsCode = mapCacheResult.get("smsCode");
                String mobile = mapCacheResult.get("mobile");
                //获取当天发送次数
                String cacheKeySendNum = CACHE_PREFIX_ACCOUNT_SENDNUM + mobile + "_" + clientId;

                Map<String, String>
                        mapCacheSendNumResult =
                        mobileCodeSenderService.getCacheMapByKey(cacheKeySendNum);
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
            boolean
                    checkSmsInfo =
                    mobileCodeSenderService.checkSmsInfoFromCache(mobile, smsCode, clientId);
            if (!checkSmsInfo) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOT_MATCH_SMSCODE);
            }
            //重置密码
            String passportId = mobilePassportMappingService.queryPassportIdByMobile(mobile);
            if (Strings.isNullOrEmpty(passportId)) {
                return Result.buildError(ErrorUtil.INVALID_ACCOUNT);
            }

            Account account = accountService.queryNormalAccount(passportId);
            if (account == null) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            }

            if (!accountService.checkResetPwdLimited(passportId)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_LIMITED);
            }

            if (!accountService.resetPassword(account, password, needMD5)) {
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
    public Result queryAccountSecureInfo(AccountSecureInfoParams params) throws Exception {
        String passportId = params.getUsername();
        int clientId = Integer.parseInt(params.getClient_id());
        String token = params.getToken();
        String captcha = params.getCaptcha();
        try {
            if (!accountService.checkCaptchaCodeIsVaild(token, captcha)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
            }
            Account account = accountService.queryNormalAccount(passportId);
            if (account == null) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            }
            if (!accountService.checkResetPwdLimited(passportId)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_LIMITED);
            }
            AccountInfo accountInfo;
            if (!Strings.isNullOrEmpty(account.getMobile())) {
                params.setSec_mobile(account.getMobile().substring(0, 3).concat("*****").
                        concat(account.getMobile().substring(account.getMobile().length() - 3)));
            }
            accountInfo = accountInfoService.queryAccountInfoByPassportId(passportId);
            if (accountInfo != null) {
                String processEmail = null;
                String emailBind = accountInfo.getEmail();
                if (!Strings.isNullOrEmpty(emailBind)) {
                    processEmail = emailBind.substring(0, 2).concat("*****").
                            concat(emailBind.substring(emailBind.indexOf("@") - 1));
                }
                params.setSec_email(processEmail);
                params.setSec_ques(StringUtil.defaultIfEmpty(accountInfo.getQuestion(), ""));
            }
            if (AccountDomainEnum.getAccountDomain(passportId) == AccountDomainEnum
                    .OTHER) {
                params.setReg_email(passportId.substring(0, 2).concat("*****").
                        concat(passportId.substring(passportId.indexOf("@") - 1)));
            }
        } catch (ServiceException e) {
            logger.error("query account_secure_info Fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }

        return Result.buildSuccess("查询成功", "sec_info", params);
    }

    private Result sendEmailResetPwd(String passportId, int clientId, String email)
            throws Exception {
        try {
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
    public Result resetPasswordByQues(String passportId, int clientId, String password,
                                      String answer) throws Exception {
        try {
            Account account = accountService.queryNormalAccount(passportId);
            if (account == null) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            }
            if (!accountService.checkResetPwdLimited(passportId)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_LIMITED);
            }
            AccountInfo accountInfo = accountInfoService.queryAccountInfoByPassportId(passportId);
            if (accountInfo == null || Strings.isNullOrEmpty(accountInfo.getAnswer())) {
                return Result.buildError(ErrorUtil.NOTHAS_BINDINGQUESTION);
            }
            String answerBind = accountInfo.getAnswer();
            if (!answer.equals(answerBind)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNTSECURE_CHECKANSWER_FAILED);
            }
            if (!accountService.resetPassword(account, password, false)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_FAILED);
            }
            return Result.buildSuccess("重置密码成功！");
        } catch (ServiceException e) {
            logger.error("reset password by ques fail:", e);
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
            Account account = accountService.queryAccountByPassportId(passportId);
            if (account == null) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            }
            String mobile = account.getMobile();
            if (Strings.isNullOrEmpty(mobile)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_OBTAIN_FIELDS);
            }

            // 验证错误次数是否小于限制次数
            boolean
                    checkFailLimited =
                    mobileCodeSenderService.checkSmsFailLimited(mobile, clientId);
            if (!checkFailLimited) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_CHECKSMSCODE_LIMIT);
            }

            // 验证手机号码与验证码是否匹配
            if (!mobileCodeSenderService.checkSmsInfoFromCache(mobile, smsCode, clientId)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOT_MATCH_SMSCODE);
            }

            if (!accountService.resetPassword(account, password, false)) {
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

    @Override
    public Result resetWebPassword(ResetPwdParameters resetPwdParameters)
            throws Exception {
        String username = null;
        try {
            username = resetPwdParameters.getPassport_id();
            String password = resetPwdParameters.getPassword();
            String newpwd = resetPwdParameters.getNewpwd();

            //校验用户名和密码是否匹配
            Account account = accountService.queryAccountByPassportId(username);
            if (account != null) {
                String oldPwd = account.getPasswd();
                if (PwdGenerator.verify(password, false, oldPwd)) {
                    account = new Account();
                    account.setPassportId(username);
                    account.setPasswd(password);
                    //不需要加密
                    if(accountService.resetPassword(account,newpwd,false)){
                        return Result.buildSuccess("重置密码成功！");
                    } else {
                        return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_FAILED);
                    }
                } else {
                    //原密码不匹配
                    return Result.buildError(ErrorUtil.USERNAME_PWD_MISMATCH);
                }
            } else {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            }
        } catch (ServiceException e) {
            logger.error("resetWebPassword Fail username:" + username, e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }
    /* ------------------------------------重置密码Begin------------------------------------ */

    /*
     * 重置密码（邮件方式）——1.发送重置密码申请验证邮件
     */
    @Override
    public Result sendEmailResetPwdByPassportId(String passportId, int clientId, int mode)
            throws Exception {
        try {
            if (accountService.queryNormalAccount(passportId) == null) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            }
            if (mode == 1) {
                // 使用注册邮箱
                boolean isOtherDomain = (AccountDomainEnum.getAccountDomain(passportId) ==
                                         AccountDomainEnum.OTHER);
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
                    String emailBind = accountInfo.getEmail();
                    return sendEmailResetPwd(passportId, clientId, emailBind);
                }
            }
        } catch (ServiceException e) {
            logger.error("send email for reset pwd by passportId fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }

    /*
     * 重置密码（邮件方式）——2.验证重置密码申请链接
     */
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

    /*
     * 重置密码（邮件方式）——3.再一次验证token，并修改密码。目前passportId与邮件申请链接中的uid一样
     */
    @Override
    public Result resetPasswordByEmail(String passportId, int clientId, String password,
                                       String token) throws Exception {
        try {
            Account account = accountService.queryNormalAccount(passportId);
            if (account == null) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            }
            if (!accountService.checkResetPwdLimited(passportId)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_LIMITED);
            }
            if (!emailSenderService.checkEmailForResetPwd(passportId, clientId, token)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNTSECURE_RESETPWD_URL_FAILED);
            }
            if (!accountService.resetPassword(account, password, false)) {
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

    /*
     * 重置密码（手机方式）——2.检查手机短信码，成功则返回secureCode记录成功标志
     *                      （1.发送见sendMobileCode***）
     */
    @Override
    public Result checkMobileCodeResetPwd(String passportId, int clientId, String smsCode)
            throws Exception {
        // TODO:与checkMobileCodeOldForBinding整合
        try {
            Result result = checkMobileCodeByPassportId(passportId, clientId, smsCode);
            if ("0".equals(result.getStatus())) {
                result.addDefaultModel("scode", accountSecureService.getSecureCodeResetPwd(passportId, clientId));
            }
            return result;
        } catch (ServiceException e) {
            logger.error("check mobile code reset pwd Fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }

    /*
     * 重置密码（密保方式）——1.验证密保答案及captcha，成功则返回secureCode记录成功标志。(可用于其他功能模块)
     */
    @Override
    public Result checkAnswerByPassportId(String passportId, int clientId, String answer,
                                          String token, String captcha) throws Exception {
        try {
            if (!accountService.checkCaptchaCodeIsVaild(token, captcha)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
            }
            // 不需要检测Account是否存在，在修改密码时检测，避免二次查询缓存/数据库
            AccountInfo accountInfo = accountInfoService.queryAccountInfoByPassportId(passportId);
            if (accountInfo == null || Strings.isNullOrEmpty(accountInfo.getAnswer())) {
                return Result.buildError(ErrorUtil.NOTHAS_BINDINGQUESTION);
            }
            String answerBind = accountInfo.getAnswer();
            if (!answer.equals(answerBind)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNTSECURE_CHECKANSWER_FAILED);
            }
            return Result.buildSuccess("验证密保答案成功！", "scode", accountSecureService
                    .getSecureCodeResetPwd(passportId, clientId));
        } catch (ServiceException e) {
            logger.error("check secure answer Fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }

    /*
     * 重置密码（手机和密保方式）——2.根据secureCode修改密码（secureCode由上一步验证手机或密保问题成功获取）
     */
    @Override
    public Result resetPasswordBySecureCode(String passportId, int clientId, String password,
                                            String secureCode) throws Exception {
        // TODO:启用后，删除ByMobile和ByQues
        try {
            if (!accountService.checkResetPwdLimited(passportId)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_LIMITED);
            }
            if (!accountSecureService.checkSecureCodeResetPwd(passportId, clientId, secureCode)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNTSECURE_RESETPWD_URL_FAILED);
            }
            Account account = accountService.queryAccountByPassportId(passportId);
            if (account == null) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            }
            if (!accountService.resetPassword(account, password, false)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_FAILED);
            }
            return Result.buildSuccess("重置密码成功！");
            // TODO:检验checkCode，是否区分密保还是手机验证码——未区分
            // TODO:在修改绑定手机时，能否重用checkCode代码——未重用
            // TODO:能否将邮件产生token的代码提取出来统一产生checkCode?——暂时未用，可考虑
        } catch (ServiceException e) {
            logger.error("reset password Fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }

    /* ------------------------------------重置密码End------------------------------------ */

    /* --------------------------------------------修改密保内容-------------------------------------------- */
    /*
     * 修改密保邮箱——1.验证原绑定邮箱及发送邮件至待绑定邮箱
     */
    @Override
    public Result sendEmailForBinding(String passportId, int clientId, String password,
                                      String newEmail,
                                      String oldEmail) throws Exception {
        try {
            Account account = accountService.verifyUserPwdVaild(passportId, password, false);
            if (account == null) {
                return Result.buildError(ErrorUtil.USERNAME_PWD_MISMATCH);
            }
            if (!account.isNormalAccount()) {
                return Result.buildError(ErrorUtil.INVALID_ACCOUNT);
            }
            AccountInfo accountInfo = accountInfoService.queryAccountInfoByPassportId(passportId);
            // 有绑定邮箱，检测是否与oldEmail相同；无原邮箱，则不检测
            if (accountInfo != null) {
                String emailBind = accountInfo.getEmail();
                if (!Strings.isNullOrEmpty(emailBind) && !emailBind.equals(oldEmail)) {
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
     * 修改密保邮箱——2.根据验证链接修改绑定邮箱
     */
    @Override
    public Result modifyEmailByPassportId(String passportId, int clientId, String token)
            throws Exception {
        try {
            String newEmail = emailSenderService.checkEmailForBinding(passportId, clientId, token);
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

    /*
     * 修改密保手机——1.检查原绑定手机短信码，成功则返回secureCode记录成功标志
     */
    @Override
    public Result checkMobileCodeOldForBinding(String passportId, int clientId, String smsCode)
            throws Exception {
        try {
            Result result = checkMobileCodeByPassportId(passportId, clientId, smsCode);
            if ("0".equals(result.getStatus())) {
                result.addDefaultModel("scode", accountSecureService.getSecureCodeModSecureInfo(passportId, clientId));
            }
            return result;
        } catch (ServiceException e) {
            logger.error("check mobile code old for binding Fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }

    /*
     * 修改密保手机——2.验证密码或secureCode、新绑定手机短信码，绑定新手机号
     */
    @Override
    public Result modifyMobileByPassportId(String passportId, int clientId, String newMobile,
            String smsCode, String checkCode, boolean firstBind) throws Exception {
        try {
            Account account = null;
            String passportIdOther = mobilePassportMappingService.queryPassportIdByMobile(newMobile);
            if (passportIdOther != null) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_BINDED);
            }
            Result result = checkMobileCodeByNewMobile(newMobile, clientId, smsCode);
            if (!"0".equals(result.getStatus())) {
                return result;
            }
            if (firstBind) {
                // 新绑定手机，checkCode为password
                String password = checkCode;
                account = accountService.verifyUserPwdVaild(passportId, password, false);
                if (account == null || !account.isNormalAccount()) {
                    return Result.buildError(ErrorUtil.USERNAME_PWD_MISMATCH);
                }
            } else {
                // 修改绑定手机，checkCode为secureCode
                String secureCode = checkCode;
                if (!accountSecureService.checkSecureCodeModSecureInfo(passportId, clientId, secureCode)) {
                    return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BIND_FAILED);
                }
                account = accountService.queryNormalAccount(passportId);
                if (account == null) {
                    return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
                }
            }

            if (!accountService.modifyMobile(account, newMobile)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDMOBILE_FAILED);
            }
            mobilePassportMappingService.deleteMobilePassportMapping(account.getMobile());
            if (!mobilePassportMappingService.initialMobilePassportMapping(newMobile, passportId)) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDMOBILE_FAILED);
            }
            // TODO:事务安全问题，暂不解决

            return Result.buildSuccess("修改绑定手机成功！");
        } catch (ServiceException e) {
            logger.error("bind mobile fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }

    @Override
    public Result modifyQuesByPassportId(String passportId, int clientId, String password,
            String newQues, String newAnswer) throws Exception {
        try {
            Account account = accountService.verifyUserPwdVaild(passportId, password, false);
            if (account == null) {
                return Result.buildError(ErrorUtil.USERNAME_PWD_MISMATCH);
            }
            if (!account.isNormalAccount()) {
                return Result.buildError(ErrorUtil.INVALID_ACCOUNT);
            }
            AccountInfo accountInfo = accountInfoService.modifyQuesByPassportId(passportId, newQues, newAnswer);
            if (accountInfo == null) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDQUES_FAILED);
            }
            return Result.buildSuccess("修改密保问题成功！");
        } catch (ServiceException e) {
            logger.error("bind secure question fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }

    /* ------------------------------------修改密保End------------------------------------ */

    /*
     * 验证手机短信随机码——用于新手机验证
     */
    @Override
    public Result checkMobileCodeByNewMobile(String mobile, int clientId, String smsCode)
            throws Exception {
        try {
            if (mobilePassportMappingService.queryPassportIdByMobile(mobile) != null) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_BINDED);
            }

            return checkSmsCode(mobile, clientId, smsCode);
        } catch (ServiceException e) {
            logger.error("check new mobile code Fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }

    private Result checkSmsCode(String mobile, int clientId, String smsCode) throws Exception {
        try {
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
    public Result checkMobileCodeByPassportId(String passportId, int clientId, String smsCode)
            throws Exception {
        try {
            Account account = accountService.queryNormalAccount(passportId);
            if (account == null) {
                return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            }
            String mobile = account.getMobile();

            return checkSmsCode(mobile, clientId, smsCode);
        } catch (ServiceException e) {
            logger.error("check existed mobile code Fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }

}