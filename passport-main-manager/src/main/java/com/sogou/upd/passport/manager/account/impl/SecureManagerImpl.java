package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.parameter.PasswordTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.common.utils.SMSUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.account.vo.AccountSecureInfoVO;
import com.sogou.upd.passport.manager.api.account.BindApiManager;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.SecureApiManager;
import com.sogou.upd.passport.manager.api.account.form.AppAuthTokenApiParams;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.BindEmailApiParams;
import com.sogou.upd.passport.manager.api.account.form.BindMobileApiParams;
import com.sogou.upd.passport.manager.api.account.form.GetSecureInfoApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateBindMobileApiParams;
import com.sogou.upd.passport.manager.api.account.form.UpdateQuesApiParams;
import com.sogou.upd.passport.manager.form.MobileModifyPwdParams;
import com.sogou.upd.passport.manager.form.ResetPwdParameters;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.*;
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
 * 安全相关：修改密码、修改密保（手机、邮箱、问题）
 * ——接口代理OK——
 * .
 */
@Component
public class SecureManagerImpl implements SecureManager {

    private static Logger logger = LoggerFactory.getLogger(SecureManagerImpl.class);

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

    // 自动注入Manager
    @Autowired
    private SecureApiManager sgSecureApiManager;
    @Autowired
    private SecureApiManager proxySecureApiManager;
    @Autowired
    private BindApiManager sgBindApiManager;
    @Autowired
    private BindApiManager proxyBindApiManager;
    @Autowired
    private LoginApiManager sgLoginApiManager;
    @Autowired
    private LoginApiManager proxyLoginApiManager;

    //account与smscode映射
    private static final String CACHE_PREFIX_ACCOUNT_SMSCODE = CacheConstant.CACHE_PREFIX_MOBILE_SMSCODE;
    private static final String CACHE_PREFIX_ACCOUNT_SENDNUM = CacheConstant.CACHE_PREFIX_MOBILE_SENDNUM;

    /*
     * 发送短信至未绑定手机，只检测映射表，查询passportId不存在或为空即认定为未绑定
     */
    @Override
    public Result sendMobileCode(String mobile, int clientId) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            String passportId = mobilePassportMappingService.queryPassportIdByMobile(mobile);
            if (Strings.isNullOrEmpty(passportId)) {
                return sendSmsCodeToMobile(mobile, clientId);
            } else {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_BINDED);
                return result;
            }
        } catch (ServiceException e) {
            logger.error("send mobile code Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    @Override
    public Result sendSmsCodeToMobile(String mobile, int clientId) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            if (Strings.isNullOrEmpty(mobile) || !PhoneUtil.verifyPhoneNumberFormat(mobile)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONEERROR);
                return result;
            }
            // 验证错误次数是否小于限制次数
            boolean checkFailLimited = mobileCodeSenderService.checkSmsFailLimited(mobile, clientId);
            if (!checkFailLimited) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CHECKSMSCODE_LIMIT);
                return result;
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
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }

    }

    @Override
    public Result sendMobileCodeByPassportId(String passportId, int clientId) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            Account account = accountService.queryAccountByPassportId(passportId);
            if (account == null) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
                return result;
            }
            String mobile = account.getMobile();
            if (Strings.isNullOrEmpty(mobile)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_OBTAIN_FIELDS);
                return result;
            }
            return sendSmsCodeToMobile(mobile, clientId);
        } catch (ServiceException e) {
            logger.error("send mobile code Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    @Override
    public Result updateSmsCacheInfo(String cacheKey, int clientId) {
        Result result = new APIResultSupport(false);
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
                                result.setSuccess(true);
                                result.setMessage("验证码已发送至" + mobile);
                                return result;
                            } else {
                                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND);
                                return result;
                            }
                        } else {
                            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CANTSENTSMS);
                            return result;
                        }
                    } else {
                        result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_MINUTELIMIT);
                        return result;
                    }
                }
            } else {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND);
            }
        } catch (Exception e) {
            logger.error("[SMS] service method updateSmsCacheInfoByKeyAndClientId error.{}", e);
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND);
        }
        return result;
    }

    @Override
    public Result checkLimitSendEmail(String passportId, int clientId, AccountModuleEnum module,
                                      String email) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            if (!emailSenderService.checkLimitForSendEmail(passportId, clientId, module, email)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SENDEMAIL_LIMITED);
                return result;
            }
            result.setSuccess(true);
            result.setMessage("发送邮件限制验证通过");
            return result;
        } catch (ServiceException e) {
            logger.error("check email limit fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    @Override
    public Result findPassword(String mobile, int clientId) {
        Result result = new APIResultSupport(false);
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
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    @Override
    public Result resetPassword(MobileModifyPwdParams regParams) throws Exception {
        Result result = new APIResultSupport(false);

        String mobile = regParams.getMobile();
        String smsCode = regParams.getSmscode();
        String password = regParams.getPassword();
        int clientId = Integer.parseInt(regParams.getClient_id());
        int pwdType = regParams.getPwd_type();
        boolean needMD5 = pwdType == PasswordTypeEnum.Plaintext.getValue() ? true : false;

        try {
            //验证手机号码与验证码是否匹配
            boolean checkSmsInfo =
                    mobileCodeSenderService.checkSmsInfoFromCache(mobile, smsCode, clientId);
            if (!checkSmsInfo) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOT_MATCH_SMSCODE);
                return result;
            }
            //重置密码
            String passportId = mobilePassportMappingService.queryPassportIdByMobile(mobile);
            if (Strings.isNullOrEmpty(passportId)) {
                result.setCode(ErrorUtil.INVALID_ACCOUNT);
                return result;
            }

            Account account = accountService.queryNormalAccount(passportId);
            if (account == null) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
                return result;
            }

            if (!accountService.checkLimitResetPwd(passportId)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_LIMITED);
                return result;
            }

            if (!accountService.resetPassword(account, password, needMD5)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_FAILED);
                return result;
            }
            // 异步更新accountToken信息
            accountTokenService.asynbatchUpdateAccountToken(passportId, clientId);
            //清除验证码的缓存
            mobileCodeSenderService.deleteSmsCache(mobile, clientId);
            result.setSuccess(true);
            result.setMessage("重置密码成功！");
            return result;
        } catch (ServiceException e) {
            logger.error("reset password Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    // 接口代理OK
    @Override
    public Result queryAccountSecureInfo(String userId, int clientId, boolean doProcess) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            AccountSecureInfoVO accountSecureInfoVO = new AccountSecureInfoVO();
            GetSecureInfoApiParams params = new GetSecureInfoApiParams();
            params.setUserid(userId);
            params.setClient_id(clientId);

            if (ManagerHelper.isInvokeProxyApi(userId)) {
                // 代理接口
                result = proxySecureApiManager.getUserSecureInfo(params);
            } else {
                result = sgSecureApiManager.getUserSecureInfo(params);
            }

            if (!result.isSuccess()) {
                return result;
            }

            Map<String, String> map = result.getModels();
            String mobile = map.get("sec_mobile");
            String emailBind = map.get("sec_email");
            String question = map.get("sec_ques");

            if (doProcess) {
                if (!Strings.isNullOrEmpty(emailBind)) {
                    String emailProcessed = StringUtil.processEmail(emailBind);
                    accountSecureInfoVO.setSec_email(emailProcessed);
                }
                if (!Strings.isNullOrEmpty(mobile)) {
                    String mobileProcessed = StringUtil.processMobile(mobile);
                    accountSecureInfoVO.setSec_mobile(mobileProcessed);
                }
                accountSecureInfoVO.setSec_ques(question);
                if (AccountDomainEnum.getAccountDomain(userId) == AccountDomainEnum.OTHER) {
                    String emailRegProcessed = StringUtil.processEmail(userId);
                    accountSecureInfoVO.setReg_email(emailRegProcessed);
                }
            } else {
                accountSecureInfoVO.setSec_email(emailBind);
                accountSecureInfoVO.setSec_mobile(mobile);
                accountSecureInfoVO.setSec_ques(question);
                if (AccountDomainEnum.getAccountDomain(userId) == AccountDomainEnum.OTHER) {
                    accountSecureInfoVO.setReg_email(userId);
                }
            }

            /*Account account = accountService.queryNormalAccount(userId);
            if (account == null) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
                return result;
            }
            AccountSecureInfoVO accountSecureInfoVO = new AccountSecureInfoVO();
            String mobile = account.getMobile();
            if (!Strings.isNullOrEmpty(mobile)) {
                if (doProcess) {
                    String mobileProcessed = StringUtil.processMobile(mobile);
                    accountSecureInfoVO.setSec_mobile(mobileProcessed);
                } else {
                    accountSecureInfoVO.setSec_mobile(mobile);
                }
            }
            AccountInfo accountInfo = accountInfoService.queryAccountInfoByPassportId(userId);
            if (accountInfo != null) {
                String emailBind = accountInfo.getEmail();
                String question = accountInfo.getQuestion();
                if (!Strings.isNullOrEmpty(emailBind)) {
                    if (doProcess) {
                        String emailBindProcessed = StringUtil.processEmail(emailBind);
                        accountSecureInfoVO.setSec_email(emailBindProcessed);
                    } else {
                        accountSecureInfoVO.setSec_email(emailBind);
                    }
                }
                if (!Strings.isNullOrEmpty(question)) {
                    accountSecureInfoVO.setSec_ques(question);
                }
            }
            if (AccountDomainEnum.getAccountDomain(userId) == AccountDomainEnum.OTHER) {
                if (doProcess) {
                    String emailRegProcessed = StringUtil.processEmail(userId);
                    accountSecureInfoVO.setReg_email(emailRegProcessed);
                } else {
                    accountSecureInfoVO.setReg_email(userId);
                }
            }*/
            result.setSuccess(true);
            result.setMessage("查询成功");
            result.setDefaultModel(accountSecureInfoVO);
            return result;
        } catch (ServiceException e) {
            logger.error("query account_secure_info Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    @Override
    public Result resetWebPassword(ResetPwdParameters resetPwdParameters)
            throws Exception {
        Result result = new APIResultSupport(false);
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
                    if (accountService.resetPassword(account, newpwd, false)) {
                        result.setSuccess(true);
                        result.setMessage("重置密码成功！");
                        return result;
                    } else {
                        result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_FAILED);
                        return result;
                    }
                } else {
                    //原密码不匹配
                    result.setCode(ErrorUtil.USERNAME_PWD_MISMATCH);
                    return result;
                }
            } else {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
                return result;
            }
        } catch (ServiceException e) {
            logger.error("resetWebPassword Fail username:" + username, e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    /* --------------------------------------------修改密保内容-------------------------------------------- */
    /*
     * 修改密保邮箱——1.验证原绑定邮箱及发送邮件至待绑定邮箱
     * TODO:发送邮件限制未做进去？接口代理OK
     */
    @Override
    public Result sendEmailForBinding(String userId, int clientId, String password,
                                      String newEmail,
                                      String oldEmail) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            BindEmailApiParams params = new BindEmailApiParams();
            params.setUserid(userId);
            params.setClient_id(clientId);
            params.setPassword(password);
            params.setNewbindemail(newEmail);
            params.setOldbindemail(oldEmail);

            if (ManagerHelper.isInvokeProxyApi(userId)) {
                // 代理接口
                result = proxyBindApiManager.bindEmail(params);
            } else {
                result = sgBindApiManager.bindEmail(params);
            }
            return result;

/*            boolean saveEmail = true;
            AccountModuleEnum module = AccountModuleEnum.SECURE;
            result = accountService.verifyUserPwdVaild(passportId, password, false);
            if (!result.isSuccess()) {
                return result;
            }
            AccountInfo accountInfo = accountInfoService.queryAccountInfoByPassportId(passportId);
            // 有绑定邮箱，检测是否与oldEmail相同；无原邮箱，则不检测
            if (accountInfo != null) {
                String emailBind = accountInfo.getEmail();
                if (!Strings.isNullOrEmpty(emailBind) && !emailBind.equals(oldEmail)) {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_CHECKOLDEMAIL_FAILED);
                    return result;
                }
            }

            if (!emailSenderService.checkLimitForSendEmail(passportId, clientId, module, newEmail)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SENDEMAIL_LIMITED);
                return result;
            }
            if (!emailSenderService.sendEmail(passportId, clientId, module, newEmail, saveEmail)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_SENDEMAIL_FAILED);
                return result;
            }
            result.setSuccess(true);
            result.setMessage("绑定邮箱验证邮件发送成功！");
            return result;*/
        } catch (ServiceException e) {
            logger.error("send email for binding Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    /*
     * 修改密保邮箱——2.根据验证链接修改绑定邮箱
     * TODO:
     */
    @Override
    public Result modifyEmailByPassportId(String passportId, int clientId, String scode)
            throws Exception {
        Result result = new APIResultSupport(false);
        try {
            boolean saveEmail = true;
            AccountModuleEnum module = AccountModuleEnum.SECURE;
            String newEmail = emailSenderService.checkScodeForEmail(passportId, clientId, module, scode, saveEmail);
            if (accountInfoService.modifyEmailByPassportId(passportId, newEmail) == null) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDEMAIL_FAILED);
                return result;
            }
            emailSenderService.deleteScodeCacheForEmail(passportId, clientId, module);
            result.setSuccess(true);
            result.setMessage("修改绑定邮箱成功！");
            return result;
        } catch (ServiceException e) {
            logger.error("modify binding email Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    /*
     * 修改密保手机——1.检查原绑定手机短信码，成功则返回secureCode记录成功标志
     */
    @Override
    public Result checkMobileCodeOldForBinding(String passportId, int clientId, String smsCode)
            throws Exception {
        Result result = new APIResultSupport(false);
        try {
            result = checkMobileCodeByPassportId(passportId, clientId, smsCode);
            if (result.isSuccess()) {
                result.setDefaultModel("scode", accountSecureService.getSecureCodeModSecInfo(
                        passportId, clientId));
            }
            return result;
        } catch (ServiceException e) {
            logger.error("check mobile code old for binding Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    // TODO:等proxyManager修改好之后修改
    public Result bindMobileByPassportId(String userId, int clientId, String newMobile,
                                           String smsCode, String password, String modifyIp) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            Account account;

            result = checkMobileCodeByNewMobile(newMobile, clientId, smsCode);
            if (!result.isSuccess()) {
                return result;
            }

            AuthUserApiParams authParams = new AuthUserApiParams();
            authParams.setUserid(userId);
            authParams.setClient_id(clientId);
            authParams.setPassword(password);

            BindMobileApiParams bindMobileApiParams = new BindMobileApiParams();
            bindMobileApiParams.setUserid(userId);
            bindMobileApiParams.setClient_id(clientId);
            bindMobileApiParams.setMobile(newMobile);
            // TODO:IP和其他成员

            if (ManagerHelper.isInvokeProxyApi(userId)) {
                // 代理接口
                result = proxyLoginApiManager.webAuthUser(authParams);
                if (!result.isSuccess()) {
                    return result;
                }
                result = proxyBindApiManager.bindMobile(bindMobileApiParams);
            } else {
                // 直接写实现方法，不调用sgBindApiManager，因不能分拆为两个对应方法同时避免读两次Account
                result = accountService.verifyUserPwdVaild(userId, password, false);
                account = (Account) result.getDefaultModel();
                result.setDefaultModel(null);

                String oldMobile = account.getMobile();
                if (!Strings.isNullOrEmpty(oldMobile)) {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_CHECKOLDEMAIL_FAILED);
                    return result;
                }

                if (!accountService.modifyMobile(account, newMobile)) {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDMOBILE_FAILED);
                    return result;
                }

                if (!mobilePassportMappingService.initialMobilePassportMapping(newMobile, userId)) {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDMOBILE_FAILED);
                    return result;
                }
                // TODO:事务安全问题，暂不解决
                result.setSuccess(true);
            }

            if (!result.isSuccess()) {
                return result;
            }

            result.setMessage("绑定手机成功！");
            return result;
        } catch (ServiceException e) {
            logger.error("bind mobile fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    /*
     * 修改密保手机——2.验证密码或secureCode、新绑定手机短信码，绑定新手机号
     */
    // TODO:等proxyManager修改好之后修改
    @Override
    public Result modifyMobileByPassportId(String userId, int clientId, String newMobile,
                                           String smsCode, String scode, String modifyIp) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            Account account;

            result = checkMobileCodeByNewMobile(newMobile, clientId, smsCode);
            if (!result.isSuccess()) {
                return result;
            }

            // 修改绑定手机，checkCode为secureCode
            if (!accountSecureService.checkSecureCodeModSecInfo(userId, clientId, scode)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BIND_FAILED);
                return result;
            }

            UpdateBindMobileApiParams updateBindMobileApiParams = new UpdateBindMobileApiParams();
            updateBindMobileApiParams.setUserid(userId);
            updateBindMobileApiParams.setClient_id(clientId);
            updateBindMobileApiParams.setNewMobile(newMobile);

            if (ManagerHelper.isInvokeProxyApi(userId)) {
                // 代理接口
                GetSecureInfoApiParams getSecureInfoApiParams = new GetSecureInfoApiParams();
                getSecureInfoApiParams.setUserid(userId);
                getSecureInfoApiParams.setClient_id(clientId);
                result = proxySecureApiManager.getUserSecureInfo(getSecureInfoApiParams);
                Map<String, String> mapResult = result.getModels();
                updateBindMobileApiParams.setOldMobile(mapResult.get("sec_mobile"));
                result = proxyBindApiManager.updateBindMobile(updateBindMobileApiParams);
            } else {
                // TODO:
                account = accountService.queryNormalAccount(userId);
                if (account == null) {
                    result.setCode(ErrorUtil.INVALID_ACCOUNT);
                    return result;
                }

                if (!accountService.modifyMobile(account, newMobile)) {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDMOBILE_FAILED);
                    return result;
                }
                String oldMobile = account.getMobile();
                mobilePassportMappingService.deleteMobilePassportMapping(oldMobile);
                if (!mobilePassportMappingService.initialMobilePassportMapping(newMobile, userId)) {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDMOBILE_FAILED);
                    return result;
                }
                result.setSuccess(true);
                // TODO:事务安全问题，暂不解决
            }

            if (!result.isSuccess()) {
                return result;
            }

            result.setMessage("修改绑定手机成功！");
            return result;
        } catch (ServiceException e) {
            logger.error("modify mobile fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    /*
     * 接口代理OK
     */
    @Override
    public Result modifyQuesByPassportId(String userId, int clientId, String password,
            String newQues, String newAnswer, String modifyIp) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            // 检验账号密码，判断是否正常用户
            UpdateQuesApiParams updateQuesApiParams = new UpdateQuesApiParams();
            updateQuesApiParams.setUserid(userId);
            updateQuesApiParams.setPassword(password);
            updateQuesApiParams.setNewquestion(newQues);
            updateQuesApiParams.setNewanswer(newAnswer);
            updateQuesApiParams.setModifyip(modifyIp);

            if (ManagerHelper.isInvokeProxyApi(userId)) {
                // 代理接口
                result = proxySecureApiManager.updateQues(updateQuesApiParams);
            } else {
                // SOGOU接口
                result = sgSecureApiManager.updateQues(updateQuesApiParams);
            }

            return result;
        } catch (ServiceException e) {
            logger.error("bind secure question fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    /* ------------------------------------修改密保End------------------------------------ */

    /*
     * 验证手机短信随机码——用于新手机验证
     */
    @Override
    public Result checkMobileCodeByNewMobile(String mobile, int clientId, String smsCode)
            throws Exception {
        Result result = new APIResultSupport(false);
        try {
            if (mobilePassportMappingService.queryPassportIdByMobile(mobile) != null) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_BINDED);
                return result;
            }

            return mobileCodeSenderService.checkSmsCode(mobile, clientId, smsCode);
        } catch (ServiceException e) {
            logger.error("check new mobile code Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    /*
     * 验证手机短信随机码——用于原绑定手机验证
     */
    @Override
    public Result checkMobileCodeByPassportId(String passportId, int clientId, String smsCode)
            throws Exception {
        Result result = new APIResultSupport(false);
        try {
            Account account = accountService.queryNormalAccount(passportId);
            if (account == null) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
                return result;
            }
            String mobile = account.getMobile();

            return mobileCodeSenderService.checkSmsCode(mobile, clientId, smsCode);
        } catch (ServiceException e) {
            logger.error("check existed mobile code Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

}