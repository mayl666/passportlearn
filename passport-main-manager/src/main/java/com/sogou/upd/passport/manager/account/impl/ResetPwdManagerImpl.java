package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.ResetPwdManager;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountInfo;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.*;
import com.sogou.upd.passport.service.account.dataobject.ActiveEmailDO;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-8 Time: 上午10:46 To change this template use
 * File | Settings | File Templates.
 */
@Component
public class ResetPwdManagerImpl implements ResetPwdManager {
    private static Logger logger = LoggerFactory.getLogger(ResetPwdManagerImpl.class);

    @Autowired
    private MobileCodeSenderService mobileCodeSenderService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountInfoService accountInfoService;
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private EmailSenderService emailSenderService;
    @Autowired
    private AccountSecureService accountSecureService;
    @Autowired
    private OperateTimesService operateTimesService;
    @Autowired
    private SecureManager secureManager;

    @Override
    public Result checkEmailCorrect(String username, String to_email) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(username);
            //不支持第三方和外域
            if (domain.equals(AccountDomainEnum.THIRD) || domain.equals(AccountDomainEnum.SOHU)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTALLOWED);
                return result;
            }
            if (Strings.isNullOrEmpty(to_email)) {
                result.setCode("发送邮箱不能为空");
                return result;
            }
            String regEmail, bindEmail = null;
            Account account = accountService.queryNormalAccount(username);
            if (account != null) {
                regEmail = AccountDomainEnum.OTHER.equals(AccountDomainEnum.getAccountDomain(username)) ? username : null;
                AccountInfo accountInfo = accountInfoService.queryAccountInfoByPassportId(username);
                if (accountInfo != null) {
                    bindEmail = accountInfo.getEmail();
                }
                if (to_email.equals(regEmail) || to_email.equals(bindEmail)) {
                    result.setSuccess(true);
                }
            } else {
                result.setCode(ErrorUtil.INVALID_ACCOUNT);
                return result;
            }
        } catch (Exception e) {
            logger.error("checkEmailCorrect: username {}", username, e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
        return result;
    }

    @Override
    public Result sendEmailResetPwd(ActiveEmailDO activeEmailDO, String scode) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            String passportId = activeEmailDO.getPassportId();
            int clientId = activeEmailDO.getClientId() == 0 ? CommonConstant.SGPP_DEFAULT_CLIENTID : activeEmailDO.getClientId();
            AccountModuleEnum module = activeEmailDO.getModule();
            String toEmail = activeEmailDO.getToEmail();
            if (!emailSenderService.checkLimitForSendEmail(passportId, clientId, module, toEmail)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SENDEMAIL_LIMITED);
                return result;
            }
            //校验安全码
            if (!accountSecureService.checkSecureCode(passportId, clientId, scode, CacheConstant.CACHE_PREFIX_PASSPORTID_RESETPWDSECURECODE)) {
                result.setCode(ErrorUtil.ERR_CODE_FINDPWD_SCODE_FAILED);
                return result;
            }
            if (!emailSenderService.sendEmail(activeEmailDO)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_SENDEMAIL_FAILED);
                return result;
            }
            result.setSuccess(true);
            result.setMessage("重置密码申请邮件发送成功");
            //记录发送邮件次数
            emailSenderService.incLimitForSendEmail(passportId, clientId, module, toEmail);
            return result;
        } catch (ServiceException e) {
            logger.error("send email for reset pwd fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    /* ------------------------------------重置密码Begin------------------------------------ */
    /*
     * 重置密码（邮件方式）——1.发送重置密码申请验证邮件
     */
    @Override
    public Result sendEmailResetPwdByPassportId(String passportId, int clientId, boolean useRegEmail, String ru, String scode)
            throws Exception {
        return sendEmailResetPwdByPassportId(passportId, clientId, useRegEmail, ru, scode, true, null);
    }
    
    /*
     * 重置密码（邮件方式）——1.发送重置密码申请验证邮件
     */
    @Override
    public Result sendEmailResetPwdByPassportId(String passportId, int clientId, boolean useRegEmail, String ru,
                                                String scode, boolean rtp, String lang)
            throws Exception {
        Result result = new APIResultSupport(false);
        try {
            AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
            if (appConfig == null) {
                result.setCode(ErrorUtil.INVALID_CLIENTID);
                return result;
            }
            AccountModuleEnum module = AccountModuleEnum.RESETPWD;
            ActiveEmailDO activeEmailDO = new ActiveEmailDO(passportId, clientId, ru, module, null, false, rtp, lang);
            if (useRegEmail) {
                // 使用注册邮箱
                boolean isOtherDomain = (AccountDomainEnum.getAccountDomain(passportId) == AccountDomainEnum.OTHER);
                if (isOtherDomain) {
                    // 外域用户无绑定邮箱
                    activeEmailDO.setToEmail(passportId);
                    return sendEmailResetPwd(activeEmailDO, scode);
                } else {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_RESETPWD_EMAIL_FAILED);
                    return result;
                }
            } else {
                // 使用绑定邮箱
                AccountInfo accountInfo = accountInfoService.queryAccountInfoByPassportId(passportId);
                if (accountInfo == null || Strings.isNullOrEmpty(accountInfo.getEmail())) {
                    result.setCode(ErrorUtil.NOTHAS_BINDINGEMAIL);
                    return result;
                } else {
                    String emailBind = accountInfo.getEmail();
                    activeEmailDO.setToEmail(emailBind);
                    return sendEmailResetPwd(activeEmailDO, scode);
                }
            }
        } catch (ServiceException e) {
            logger.error("send email for reset pwd by passportId fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    /*
     * 重置密码（邮件方式）——2.验证重置密码申请链接
     */
    @Override
    public Result checkEmailResetPwd(String passportId, int clientId, String scode) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
            if (appConfig == null) {
                result.setCode(ErrorUtil.INVALID_CLIENTID);
                return result;
            }

            AccountModuleEnum module = AccountModuleEnum.RESETPWD;
            String resultStr = emailSenderService.checkScodeForEmail(passportId, clientId, module, scode, false);
            if (Strings.isNullOrEmpty(resultStr)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_RESETPWD_URL_FAILED);
                return result;
            }
            //校验成功，生成scode
            result.setDefaultModel("scode", accountSecureService.getSecureCode(passportId, clientId, CacheConstant.CACHE_PREFIX_PASSPORTID_RESETPWDSECURECODE));

            emailSenderService.deleteScodeCacheForEmail(passportId, clientId, module);
            result.setSuccess(true);
            result.setMessage("重置密码申请链接验证成功");
            return result;
        } catch (ServiceException e) {
            logger.error("check email fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    @Override
    public Result sendFindPwdMobileCode(String userId, int clientId, String sec_mobile, String token, String captcha) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
            if (appConfig == null) {
                result.setCode(ErrorUtil.INVALID_CLIENTID);
                return result;
            }
            result = secureManager.sendMobileCodeAndCheckOldMobile(userId, clientId, AccountModuleEnum.RESETPWD, sec_mobile, token, captcha);
            if (!result.isSuccess()) {
                return result;
            }
            result.setMessage("找回密码，手机验证码发送成功！");
            return result;
        } catch (ServiceException e) {
            logger.error("send mobile code old Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    /*
     * 重置密码（手机方式）——2.检查手机短信码，成功则返回secureCode记录成功标志
     *                      （1.发送见sendMobileCode***）
     */
    @Override
    public Result checkMobileCodeResetPwd(String passportId, int clientId, String smsCode)
            throws Exception {
        Result result = new APIResultSupport(false);
        try {
            Account account = accountService.queryNormalAccount(passportId);
            if (account == null) {
                result.setCode(ErrorUtil.INVALID_ACCOUNT);
                return result;
            }
            String mobile = account.getMobile();
            result = mobileCodeSenderService.checkSmsCode(mobile, clientId, AccountModuleEnum.RESETPWD, smsCode);
            if (result.isSuccess()) {
                result.setDefaultModel("scode", accountSecureService.getSecureCode(passportId, clientId, CacheConstant.CACHE_PREFIX_PASSPORTID_RESETPWDSECURECODE));
            }
            return result;
        } catch (ServiceException e) {
            logger.error("check mobile code reset pwd Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    /*
     * 重置密码（手机和密保方式）——2.根据secureCode修改密码（secureCode由上一步验证手机或密保问题成功获取）
     */
    @Override
    public Result resetPasswordByScode(String passportId, int clientId, String password,
                                       String scode, String ip) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
            if (appConfig == null) {
                result.setCode(ErrorUtil.INVALID_CLIENTID);
                return result;
            }
            //检验次数是否超限
            if (operateTimesService.isOverLimitFindPwdResetPwd(passportId, clientId, ip)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_LIMITED);
                return result;
            }
            //校验安全码
            if (!accountSecureService.checkSecureCode(passportId, clientId, scode, CacheConstant.CACHE_PREFIX_PASSPORTID_RESETPWDSECURECODE)) {
                result.setCode(ErrorUtil.ERR_CODE_FINDPWD_SCODE_FAILED);
                return result;
            }
            Account account = accountService.queryNormalAccount(passportId);
            if (account == null) {
                result.setCode(ErrorUtil.INVALID_ACCOUNT);
                return result;
            }
            if (!accountService.resetPassword(passportId,account, password, true)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_FAILED);
                return result;
            }
            operateTimesService.incLimitFindPwdResetPwd(passportId, clientId, ip);
            result.setSuccess(true);
            result.setMessage("重置密码成功！");
            return result;
            // TODO:检验checkCode，是否区分密保还是手机验证码——未区分
            // TODO:在修改绑定手机时，能否重用checkCode代码——未重用
            // TODO:能否将邮件产生token的代码提取出来统一产生checkCode?——暂时未用，可考虑
        } catch (ServiceException e) {
            logger.error("reset password Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    @Override
    public Result checkFindPwdTimes(String passportId) throws Exception {
        Result result = new APIResultSupport(false);
        if (operateTimesService.checkFindPwdTimes(passportId)) {
            result.setCode(ErrorUtil.ERR_CODE_FINDPWD_LIMITED);
            return result;
        } else {
            result.setSuccess(true);
            return result;
        }
    }

    @Override
    public void incFindPwdTimes(String passportId) throws Exception {
        operateTimesService.incFindPwdTimes(passportId);
    }

}
