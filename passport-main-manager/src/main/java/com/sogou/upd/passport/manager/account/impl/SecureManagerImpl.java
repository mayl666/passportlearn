package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.common.utils.PhotoUtils;
import com.sogou.upd.passport.common.utils.SMSUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.RegManager;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.account.vo.AccountSecureInfoVO;
import com.sogou.upd.passport.manager.account.vo.ActionRecordVO;
import com.sogou.upd.passport.manager.api.account.BindApiManager;
import com.sogou.upd.passport.manager.api.account.SecureApiManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.BindEmailApiParams;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.account.form.UpdateQuesApiParams;
import com.sogou.upd.passport.manager.form.UpdatePwdParameters;
import com.sogou.upd.passport.manager.form.UserNamePwdMappingParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.ActionRecord;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.*;
import com.sogou.upd.passport.service.account.dataobject.ActionStoreRecordDO;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 安全相关：修改密码、修改密保（手机、邮箱、问题） ——接口代理OK—— .
 */
@Component
public class SecureManagerImpl implements SecureManager {

    private static Logger logger = LoggerFactory.getLogger(SecureManagerImpl.class);

    //搜狗安全信息字段:密保邮箱、密保手机、密保问题
    private static final String SOGOU_SECURE_FIELDS = "email,mobile,question,uniqname,avatarurl";

    @Autowired
    private MobileCodeSenderService mobileCodeSenderService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountInfoService accountInfoService;
    @Autowired
    private MobilePassportMappingService mobilePassportMappingService;
    @Autowired
    private EmailSenderService emailSenderService;
    @Autowired
    private AccountSecureService accountSecureService;
    @Autowired
    private OperateTimesService operateTimesService;
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private BindApiManager bindApiManager;
    @Autowired
    private SecureApiManager secureApiManager;
    @Autowired
    private UserInfoApiManager sgUserInfoApiManager;
    @Autowired
    private CommonManagerImpl commonManager;
    @Autowired
    private RegManager regManager;
    @Autowired
    private PhotoUtils photoUtils;

    private ExecutorService service = Executors.newFixedThreadPool(10);

    /*
     * 发送短信至未绑定手机，只检测映射表，查询passportId不存在或为空即认定为未绑定
     */
    @Override
    public Result sendMobileCode(String mobile, int clientId, AccountModuleEnum module) {
        Result result = new APIResultSupport(false);
        try {
            //校验手机号格式
            if (Strings.isNullOrEmpty(mobile) || !PhoneUtil.verifyPhoneNumberFormat(mobile)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONEERROR);
                return result;
            }
            // 验证码验证错误次数是否小于限制次数,一天不超过10次
            boolean checkFailLimited = mobileCodeSenderService.checkLimitForSmsFail(mobile, clientId, module);
            if (!checkFailLimited) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CHECKSMSCODE_LIMIT);
                return result;
            }
            result = mobileCodeSenderService.sendSmsCode(mobile, clientId, module);
            return result;
        } catch (ServiceException e) {
            logger.error("send sms code to mobile Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }


    @Override
    public Result sendMobileCodeByPassportId(String passportId, int clientId, AccountModuleEnum module)
            throws Exception {
        Result result = new APIResultSupport(false);
        try {
            Account account = accountService.queryNormalAccount(passportId);
            if (account == null) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
                return result;
            }
            String mobile = account.getMobile();
            if (Strings.isNullOrEmpty(mobile)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_OBTAIN_FIELDS);
                return result;
            }
            return sendMobileCode(mobile, clientId, module);
        } catch (ServiceException e) {
            logger.error("send mobile code Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    @Override
    public Result sendMobileCodeAndCheckOldMobile(String passportId, int clientId, AccountModuleEnum module, String sec_mobile, String token, String captcha) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            Account account = accountService.queryNormalAccount(passportId);
            if (account == null) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
                return result;
            }
            String mobile = account.getMobile();
            if (Strings.isNullOrEmpty(mobile)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_OBTAIN_FIELDS);
                return result;
            }
            if (!Strings.isNullOrEmpty(sec_mobile) && !mobile.equals(sec_mobile)) {
                result.setCode(ErrorUtil.ERR_CODE_OLDMOBILE_SECMOBILE_NOT_MATCH);
                return result;
            }
            //web端手机方式找回密码时需要弹出动态验证码
            //如果token和captcha都不为空，则校验是否匹配
            if (!Strings.isNullOrEmpty(token) && !Strings.isNullOrEmpty(captcha)) {
                result = regManager.checkCaptchaToken(token, captcha);
                //如果验证码校验失败，则提示
                if (!result.isSuccess()) {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
                    return result;
                }
            } else {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_NEED_CODE);
                return result;
            }
            return sendMobileCode(mobile, clientId, module);
        } catch (ServiceException e) {
            logger.error("send mobile code Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        } finally {
            commonManager.incSendTimesForMobile(sec_mobile);
        }
    }

    @Override
    public Result sendMobileCodeOld(String userId, int clientId) {
        Result result = new APIResultSupport(false);
        try {
            result = sendMobileCodeByPassportId(userId, clientId, AccountModuleEnum.SECURE);
            if (!result.isSuccess()) {
                return result;
            }
            result.setMessage("密保手机验证码发送成功！");
            return result;
        } catch (Exception e) {
            logger.error("send mobile code old Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    @Override
    public Result queryAccountSecureInfo(String userId, int clientId, boolean doProcess) {
        Result result = new APIResultSupport(false);
        try {
            AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
            if (appConfig == null) {
                result.setCode(ErrorUtil.INVALID_CLIENTID);
                return result;
            }
            int score = 0; // 安全系数
            AccountSecureInfoVO accountSecureInfoVO = new AccountSecureInfoVO();
            GetUserInfoApiparams getUserInfoApiparams = new GetUserInfoApiparams();
            getUserInfoApiparams.setUserid(userId);
            getUserInfoApiparams.setClient_id(clientId);
            getUserInfoApiparams.setFields(SOGOU_SECURE_FIELDS);
            result = sgUserInfoApiManager.getUserInfo(getUserInfoApiparams);
            if (result.isSuccess()) {
                String uniqname = String.valueOf(result.getModels().get("uniqname"));
                result.getModels().put("uniqname", Coder.encode(Strings.isNullOrEmpty(uniqname) ? userId : uniqname, "UTF-8"));
                Result photoResult = photoUtils.obtainPhoto(String.valueOf(result.getModels().get("avatarurl")), "50");
                if (photoResult.isSuccess()) {
                    result.getModels().put("avatarurl", photoResult.getModels());
                }
            } else {
                result.getModels().put("uniqname", Coder.encode(userId, "UTF-8"));
            }
            Map<String, String> map = result.getModels();
            result.setModels(map);
            if (!result.isSuccess()) {
                return result;
            }
            String mobile = map.get("sec_mobile");
            String emailBind = map.get("sec_email");
            String question = map.get("sec_ques");
            if (doProcess) {
                if (!Strings.isNullOrEmpty(emailBind)) {
                    String emailProcessed = StringUtil.processEmail(emailBind);
                    accountSecureInfoVO.setSec_email(emailProcessed);
                    score += 30;
                }
                if (!Strings.isNullOrEmpty(mobile)) {
                    String mobileProcessed = StringUtil.processMobile(mobile);
                    accountSecureInfoVO.setSec_mobile(mobileProcessed);
                    score += 30;
                }
                if (!Strings.isNullOrEmpty(question)) {
                    accountSecureInfoVO.setSec_ques(question);
                    score += 30;
                }
                if (AccountDomainEnum.getAccountDomain(userId) == AccountDomainEnum.OTHER) {
                    String emailRegProcessed = StringUtil.processEmail(userId);
                    accountSecureInfoVO.setReg_email(emailRegProcessed);
                }
            } else {
                if (!Strings.isNullOrEmpty(emailBind)) {
                    accountSecureInfoVO.setSec_email(emailBind);
                    score += 30;
                }
                if (!Strings.isNullOrEmpty(mobile)) {
                    accountSecureInfoVO.setSec_mobile(mobile);
                    score += 30;
                }
                if (!Strings.isNullOrEmpty(question)) {
                    accountSecureInfoVO.setSec_ques(question);
                    score += 30;
                }
                if (AccountDomainEnum.getAccountDomain(userId) == AccountDomainEnum.OTHER) {
                    accountSecureInfoVO.setReg_email(userId);
                }
            }
            accountSecureInfoVO.setSec_score(score);
            ActionRecordVO record = queryLastActionRecordPrivate(userId, clientId, AccountModuleEnum.LOGIN);
            if (record != null) {
                accountSecureInfoVO.setLast_login_time(record.getTime());
                accountSecureInfoVO.setLast_login_loc(record.getLoc());
            }
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
    public Result updateWebPwd(UpdatePwdParameters updatePwdParameters)
            throws Exception {
        Result result = new APIResultSupport(false);
        String passportId = null;
        try {
            passportId = updatePwdParameters.getPassport_id();
            String captcha = updatePwdParameters.getCaptcha();
            String modifyIp = updatePwdParameters.getIp();
            int clientId = Integer.parseInt(updatePwdParameters.getClient_id());
            String token = updatePwdParameters.getToken();
            //修改密码时检查验证码、ip黑白名单、修改密码次数
            result = checkUpdatePwdCaptchaAndSecure(passportId, clientId, token, captcha, modifyIp);
            if (result.isSuccess()) {
                result = secureApiManager.updatePwd(passportId, clientId, updatePwdParameters.getPassword(), updatePwdParameters.getNewpwd(), modifyIp);
                if (result.isSuccess()) {
                    operateTimesService.incLimitResetPwd(passportId, clientId);
                    operateTimesService.incResetPwdIPTimes(modifyIp);
                }
            }
        } catch (ServiceException e) {
            logger.error("Modify Web Password Fail username:" + passportId, e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
        return result;
    }

    private Result checkUpdatePwdCaptchaAndSecure(String passportId, int clientId, String token, String captcha, String modifyIp) {
        Result result = new APIResultSupport(true);
        try {
            if (!accountService.checkCaptchaCode(token, captcha)) {    //判断验证码
                logger.debug("[webRegister captchaCode wrong warn]:passportId=" + passportId + ", modifyIp=" + modifyIp + ", token=" + token + ", captchaCode=" + captcha);
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
                return result;
            }

            if (operateTimesService.checkIPLimitResetPwd(modifyIp)) {    //检查是否在ip黑名单里
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST);
                return result;
            }

            if (operateTimesService.checkLimitResetPwd(passportId, clientId)) {   //检查修改密码次数是否超限
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_LIMITED);
                return result;
            }
        } catch (ServiceException e) {
            logger.error("UpdatePwd Captcha Or Secure Fail, passportId:" + passportId, e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
        return result;
    }

    @Override
    public Result resetPwd(List<UserNamePwdMappingParams> list, final int clientId) throws Exception {
        Result resultList = new APIResultSupport(true);
        List<Future<Result>> futureList = Lists.newArrayList();
        for (final UserNamePwdMappingParams params : list) {
            Future<Result> future = service.submit(new Callable<Result>() {
                public Result call() throws Exception {
                    Result result = new APIResultSupport(false);
                    String mobile = params.getMobile();
                    String newPwd = params.getPwd();
                    String smsText = "搜狗通行证提醒您：" + mobile;
                    try {
                        //查是否是手机号码
                        if (PhoneUtil.verifyPhoneNumberFormat(mobile)) {
                            if (!Strings.isNullOrEmpty(newPwd) && StringUtils.isAsciiPrintable(newPwd) && newPwd.length() >= 6 && newPwd.length() <= 16) {
                                String passportId = mobilePassportMappingService.queryPassportIdByMobile(mobile);
                                if (!Strings.isNullOrEmpty(passportId)) {
                                    //查是否进黑名单
                                    if (!operateTimesService.checkLimitResetPwd(passportId, clientId)) {
                                        //校验account是否存在
                                        Account account = accountService.queryNormalAccount(passportId);
                                        if (account != null) {
                                            if (accountService.resetPassword(account, newPwd, true)) {
                                                operateTimesService.incLimitResetPwd(passportId, clientId);
                                                smsText = smsText + "重置密码成功，请使用新密码登录。";
                                                result.setSuccess(true);
                                            } else {
                                                smsText = smsText + "重置密码失败，请再次尝试。";
                                            }
                                        } else {
                                            smsText = smsText + "账号不存在，重置密码失败。";
                                        }
                                    } else {
                                        smsText = smsText + "重置密码次数超限，请24小时后尝试。";
                                    }
                                } else {
                                    smsText = smsText + "未绑定账号或未注册，重置密码失败。";
                                }
                            } else {
                                smsText = smsText + "重置密码格式不正确，必须为字母、数字、字符且长度为6~16位!";
                            }
                            //短信通知结果
                            boolean isSendSms = false;
                            if (SMSUtil.sendSMS(mobile, smsText)) {
                                isSendSms = true;
                            }
                            result.setDefaultModel("sendSms", isSendSms);
                        }
                        result.setDefaultModel("mobile", mobile);
                        result.setDefaultModel("smsText", smsText);
                        if (!result.isSuccess()) {
                            logger.info("mobile:" + mobile + ", resetPwd is fail, smsText:" + smsText);
                        }
                    } catch (Exception e) {
                        logger.error("resetPwd Fail username:" + mobile, e);
                    }
                    return result;
                }
            });
            futureList.add(future);
        }
        List<String> smsTextList = Lists.newArrayList();
        for (Future<Result> future : futureList) {
            Result result = future.get();
            if (!result.isSuccess()) {
                String smsText = (String) result.getModels().get("smsText");
                smsTextList.add(smsText);
            }
        }
        resultList.setMessage(smsTextList.toString());
        return resultList;
    }

    /* --------------------------------------------修改密保内容-------------------------------------------- */
    /*
     * 修改密保邮箱——1.验证原绑定邮箱及发送邮件至待绑定邮箱
     */
    @Override
    public Result sendEmailForBinding(String passportId, int clientId, String password,
                                      String newEmail, String oldEmail, String modifyIp, String ru) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            if (operateTimesService.checkIPBindLimit(modifyIp)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST);
                return result;
            }
            if (!operateTimesService.checkBindLimit(passportId, clientId)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDNUM_LIMITED);
                return result;
            }
            if (!operateTimesService.checkLimitCheckPwdFail(passportId, clientId, AccountModuleEnum.SECURE)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CHECKPWDFAIL_LIMIT);
                return result;
            }
            if (!emailSenderService.checkLimitForSendEmail(passportId, clientId, AccountModuleEnum.SECURE, newEmail)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SENDEMAIL_LIMITED);
                return result;
            }
            BindEmailApiParams params = new BindEmailApiParams();
            params.setUserid(passportId);
            params.setClient_id(clientId);
            params.setPassword(password);
            params.setNewbindemail(newEmail);
            params.setOldbindemail(oldEmail);
            String flag = String.valueOf(System.currentTimeMillis());
            ru = ru + "?token=" + accountSecureService.getSecureCodeRandom(flag) + "&id=" + flag + "&username=" + passportId;
            params.setRu(ru);
            result = bindApiManager.bindEmail(params);
            if (result.isSuccess()) {
                emailSenderService.incLimitForSendEmail(passportId, clientId, AccountModuleEnum.SECURE, newEmail);
                operateTimesService.incIPBindTimes(modifyIp);
                result.setMessage("发送绑定邮箱申请邮件成功！");
            } else if (ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_PWD_ERROR.equals(result.getCode())) {
                operateTimesService.incLimitCheckPwdFail(passportId, clientId, AccountModuleEnum.SECURE);
            }
            return result;
        } catch (ServiceException e) {
            logger.error("send email for binding Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    /*
     * 修改密保邮箱——2.根据验证链接修改绑定邮箱
     */
    @Override
    public Result modifyEmailByPassportId(String userId, int clientId, String scode)
            throws Exception {
        Result result = new APIResultSupport(false);
        try {
            if (!operateTimesService.checkBindLimit(userId, clientId)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDNUM_LIMITED);
                return result;
            }
            AccountModuleEnum module = AccountModuleEnum.SECURE;
            // 注意saveEmail参数需要和bindEmail()里保持一致
            String newEmail = emailSenderService.checkScodeForEmail(userId, clientId, module, scode, true);
            if (StringUtil.isEmpty(newEmail)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDEMAIL_FAILED);
                return result;
            }
            if (accountInfoService.modifyBindEmailByPassportId(userId, newEmail) == null) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDEMAIL_FAILED);
                return result;
            }
            emailSenderService.deleteScodeCacheForEmail(userId, clientId, module);
            operateTimesService.incLimitBind(userId, clientId);
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
    public Result checkMobileCodeOldForBinding(String userId, int clientId, String smsCode)
            throws Exception {
        Result result = new APIResultSupport(false);
        try {
            if (!operateTimesService.checkBindLimit(userId, clientId)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDNUM_LIMITED);
                return result;
            }
            result = checkMobileCodeByPassportId(userId, clientId, smsCode);
            if (result.isSuccess()) {
                result.setDefaultModel("scode", accountSecureService.getSecureCodeModSecInfo(
                        userId, clientId));
            }
            return result;
        } catch (ServiceException e) {
            logger.error("check mobile code old for binding Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    @Override
    public Result bindMobileByPassportId(String passportId, int clientId, String newMobile,
                                         String smsCode, String password, String modifyIp) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            Result smsCodeAndSecureResult = checkBindMobileSmsCodeAndSecure(passportId, clientId, newMobile, smsCode, modifyIp);
            if (!smsCodeAndSecureResult.isSuccess()) {
                return smsCodeAndSecureResult;
            }
            if (!operateTimesService.checkLimitCheckPwdFail(passportId, clientId, AccountModuleEnum.SECURE)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CHECKPWDFAIL_LIMIT);
                return result;
            }
            result = accountService.verifyUserPwdVaild(passportId, password, true);
            if (!result.isSuccess()) {
                operateTimesService.incLimitCheckPwdFail(passportId, clientId, AccountModuleEnum.SECURE);
                return result;
            }
            Account account = (Account) result.getDefaultModel();
            if (account == null || !Strings.isNullOrEmpty(account.getMobile())) {
                result.setSuccess(false);
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDMOBILE_FAILED);
                return result;
            }
            boolean isSgBind = accountService.bindMobile(account, newMobile);
            if (isSgBind) {
                result.setSuccess(true);
                result.setMessage("绑定手机成功！");
            } else {
                result.setSuccess(false);
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDMOBILE_FAILED);
                return result;
            }
            operateTimesService.incLimitBind(passportId, clientId);
            operateTimesService.incIPBindTimes(modifyIp);
            return result;
        } catch (ServiceException e) {
            logger.error("bind mobile fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    private Result checkBindMobileSmsCodeAndSecure(String passportId, int clientId, String newMobile, String smsCode, String modifyIp) {
        Result result = new APIResultSupport(false);
        try {
            if (operateTimesService.checkIPBindLimit(modifyIp)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST);
                return result;
            }
            if (!operateTimesService.checkBindLimit(passportId, clientId)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDNUM_LIMITED);
                return result;
            }
            result = checkMobileCodeByNewMobile(newMobile, clientId, smsCode);
        } catch (ServiceException e) {
            logger.error("Check BindMobileSmsCode Or Secure Fail, passportId:" + passportId, e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
        return result;
    }

    /*
      * 修改密保手机——2.验证密码或secureCode、新绑定手机短信码，绑定新手机号
      */
    @Override
    public Result modifyMobileByPassportId(String passportId, int clientId, String newMobile,
                                           String smsCode, String scode, String modifyIp) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            Result smsCodeAndSecureResult = checkBindMobileSmsCodeAndSecure(passportId, clientId, newMobile, smsCode, modifyIp);
            if (!smsCodeAndSecureResult.isSuccess()) {
                return smsCodeAndSecureResult;
            }
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
                result.setMessage("修改绑定手机成功！");
            } else {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDMOBILE_FAILED);
                return result;
            }
            operateTimesService.incLimitBind(passportId, clientId);
            operateTimesService.incIPBindTimes(modifyIp);
            return result;
        } catch (ServiceException e) {
            logger.error("modify mobile fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    /*
     * 修改密保问题和答案
     */
    @Override
    public Result modifyQuesByPassportId(String userId, int clientId, String password,
                                         String newQues, String newAnswer, String modifyIp) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            if (operateTimesService.checkIPBindLimit(modifyIp)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST);
                return result;
            }

            if (!operateTimesService.checkBindLimit(userId, clientId)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDNUM_LIMITED);
                return result;
            }
            if (!operateTimesService.checkLimitCheckPwdFail(userId, clientId, AccountModuleEnum.SECURE)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CHECKPWDFAIL_LIMIT);
                return result;
            }
            // 检验账号密码，判断是否正常用户
            UpdateQuesApiParams updateQuesApiParams = new UpdateQuesApiParams();
            updateQuesApiParams.setUserid(userId);
            updateQuesApiParams.setPassword(password);
            updateQuesApiParams.setNewquestion(newQues);
            updateQuesApiParams.setNewanswer(newAnswer);
            updateQuesApiParams.setModifyip(modifyIp);
            result = secureApiManager.updateQues(updateQuesApiParams);
            if (!result.isSuccess()) {
                return result;
            }
            operateTimesService.incLimitBind(userId, clientId);
            operateTimesService.incIPBindTimes(modifyIp);
            result.setMessage("绑定密保问题成功！");
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
    public Result checkMobileCodeByNewMobile(String mobile, int clientId, String smsCode) {
        Result result = new APIResultSupport(false);
        try {
            String passportId = commonManager.getPassportIdByUsername(mobile);
            //检查手机账号能否被绑定
            if (!Strings.isNullOrEmpty(passportId)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_BINDED);
                return result;
            }
            return mobileCodeSenderService.checkSmsCode(mobile, clientId, AccountModuleEnum.SECURE, smsCode);
        } catch (Exception e) {
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
            return mobileCodeSenderService.checkSmsCode(mobile, clientId, AccountModuleEnum.SECURE, smsCode);
        } catch (ServiceException e) {
            logger.error("check existed mobile code Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    @Override
    public Result logActionRecord(String userId, int clientId, AccountModuleEnum module, String ip,
                                  String note) {
        Result result = new APIResultSupport(false);
        ActionRecord actionRecord = new ActionRecord();
        actionRecord.setUserId(userId);
        actionRecord.setClientId(clientId);
        actionRecord.setAction(module);
        actionRecord.setDate(System.currentTimeMillis());
        actionRecord.setNote(note);
        if ("127.0.0.1".equals(ip)) {
            ip = "";
        }
        actionRecord.setIp(ip);

        accountSecureService.setActionRecord(actionRecord);
        result.setSuccess(true);
        result.setMessage("记录" + module.getDescription() + "成功！");
        return result;
    }

    @Override
    public Result queryLastActionRecord(String userId, int clientId, AccountModuleEnum module) {
        Result result = new APIResultSupport(false);
        ActionStoreRecordDO actionDO = accountSecureService.getLastActionStoreRecord(userId, clientId, module);
        ActionRecordVO record = new ActionRecordVO(actionDO);
        int clientIdRes = actionDO.getClientId();
        record.setType(appConfigService.queryClientName(clientIdRes));
        result.setDefaultModel("record", record);
        return result;
    }

    @Override
    public Result queryActionRecords(String userId, int clientId, AccountModuleEnum module) {
        Result result = new APIResultSupport(false);
        List<ActionRecordVO> recordsVO = Lists.newLinkedList();
        List<ActionStoreRecordDO>
                storeRecords = accountSecureService.getActionStoreRecords(userId, clientId, module);
        if (!CollectionUtils.isEmpty(storeRecords)) {
            for (ActionStoreRecordDO actionDO : storeRecords) {
                ActionRecordVO actionVO = new ActionRecordVO(actionDO);
                if (actionDO != null) {
                    int clientIdRes = actionDO.getClientId();
                    actionVO.setType(appConfigService.queryClientName(clientIdRes));
                }
                recordsVO.add(actionVO);
            }
        }

        result.setDefaultModel("records", recordsVO);
        result.setSuccess(true);
        result.setMessage("获取" + module.getDescription() + "记录成功！");
        return result;
    }

    @Override
    public Result queryAllActionRecords(String userId, int clientId) {
        // TODO:修改返回的List<T>中的T，增加归属地
        Result result = new APIResultSupport(false);
        List<ActionRecordVO> allRecords = Lists.newLinkedList();
        for (AccountModuleEnum module : AccountModuleEnum.values()) {
            List<ActionStoreRecordDO>
                    storeRecords = accountSecureService.getActionStoreRecords(userId, clientId, module);
            if (!CollectionUtils.isEmpty(storeRecords)) {
                for (ActionStoreRecordDO actionDO : storeRecords) {
                    ActionRecordVO actionVO = new ActionRecordVO(actionDO);
                    if (actionDO != null) {
                        int clientIdRes = actionDO.getClientId();
                        actionVO.setType(appConfigService.queryClientName(clientIdRes));
                    }
                    allRecords.add(actionVO);
                }
            }
        }
        result.setDefaultModel("records", allRecords);
        result.setSuccess(true);
        result.setMessage("获取所有记录成功！");
        return result;
    }

    /*
     * 查询安全信息时需要
     */
    private ActionRecordVO queryLastActionRecordPrivate(String userId, int clientId, AccountModuleEnum module) {
        ActionStoreRecordDO actionDO = accountSecureService.getLastActionStoreRecord(userId, clientId, module);
        ActionRecordVO record = new ActionRecordVO(actionDO);
        if (actionDO != null) {
            int clientIdRes = actionDO.getClientId();
            record.setType(appConfigService.queryClientName(clientIdRes));
        }
        return record;
    }

}