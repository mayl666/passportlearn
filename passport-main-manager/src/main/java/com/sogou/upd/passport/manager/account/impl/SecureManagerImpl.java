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
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.account.vo.AccountSecureInfoVO;
import com.sogou.upd.passport.manager.account.vo.ActionRecordVO;
import com.sogou.upd.passport.manager.api.account.BindApiManager;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.SecureApiManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.*;
import com.sogou.upd.passport.manager.form.UpdatePwdParameters;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountBaseInfo;
import com.sogou.upd.passport.model.account.ActionRecord;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.*;
import com.sogou.upd.passport.service.account.dataobject.ActionStoreRecordDO;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * 安全相关：修改密码、修改密保（手机、邮箱、问题） ——接口代理OK—— .
 */
@Component
public class SecureManagerImpl implements SecureManager {

    private static Logger logger = LoggerFactory.getLogger(SecureManagerImpl.class);

    private static String SECURE_FIELDS = "sec_email,sec_mobile,sec_ques";

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

    // 自动注入Manager
    @Autowired
    private SecureApiManager sgSecureApiManager;
    @Autowired
    private SecureApiManager proxySecureApiManager;
    @Autowired
    private UserInfoApiManager proxyUserInfoApiManager;
    @Autowired
    private BindApiManager sgBindApiManager;
    @Autowired
    private BindApiManager proxyBindApiManager;
    @Autowired
    private LoginApiManager proxyLoginApiManager;
    @Autowired
    private PhotoUtils photoUtils;
    @Autowired
    private UserInfoApiManager shPlusUserInfoApiManager;

    /*
     * 发送短信至未绑定手机，只检测映射表，查询passportId不存在或为空即认定为未绑定
     */
    @Override
    public Result sendMobileCode(String mobile, int clientId, AccountModuleEnum module) throws Exception {
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
            return sendMobileCode(mobile, clientId, module);
        } catch (ServiceException e) {
            logger.error("send mobile code Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    // 为SOHU接口修改
    @Override
    public Result sendMobileCodeNew(String userId, int clientId, String mobile) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            result = sendMobileCode(mobile, clientId, AccountModuleEnum.SECURE);
            if (!result.isSuccess()) {
                return result;
            }

            result.setMessage("绑定手机验证码发送成功！");
            return result;
        } catch (ServiceException e) {
            logger.error("send mobile code new Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    @Override
    public Result sendMobileCodeOld(String userId, int clientId) throws Exception {
        Result result = new APIResultSupport(false);
        try {

            if (ManagerHelper.isInvokeProxyApi(userId)) {
                // SOHU接口
                GetUserInfoApiparams getUserInfoApiparams = new GetUserInfoApiparams();
                getUserInfoApiparams.setUserid(userId);
                getUserInfoApiparams.setClient_id(clientId);
                getUserInfoApiparams.setFields(SECURE_FIELDS);
                result = proxyUserInfoApiManager.getUserInfo(getUserInfoApiparams);
                Map<String, String> mapResult = result.getModels();
                String mobile = mapResult.get("sec_mobile");
                result = sendMobileCode(mobile, clientId, AccountModuleEnum.SECURE);

            } else {
                result = sendMobileCodeByPassportId(userId, clientId, AccountModuleEnum.SECURE);
            }

            if (!result.isSuccess()) {
                return result;
            }

            result.setMessage("解绑手机验证码发送成功！");
            return result;
        } catch (ServiceException e) {
            logger.error("send mobile code old Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    @Override
    public Result findPassword(String mobile, int clientId) {
        Result result = new APIResultSupport(false);
        try {
            result = mobileCodeSenderService.sendSmsCode(mobile, clientId, AccountModuleEnum.RESETPWD);

            return result;
        } catch (ServiceException e) {
            logger.error("find passport Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    // 接口代理OK
    @Override
    public Result queryAccountSecureInfo(String userId, int clientId, boolean doProcess) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
            if (appConfig == null) {
                result.setCode(ErrorUtil.INVALID_CLIENTID);
                return result;
            }

            int score = 0; // 安全系数
            AccountSecureInfoVO accountSecureInfoVO = new AccountSecureInfoVO();

            if (ManagerHelper.isInvokeProxyApi(userId)) {
                // 代理接口
                GetUserInfoApiparams getUserInfoApiparams = new GetUserInfoApiparams();
                getUserInfoApiparams.setUserid(userId);
                getUserInfoApiparams.setClient_id(clientId);
//                getUserInfoApiparams.setImagesize("50");
                getUserInfoApiparams.setFields(SECURE_FIELDS /*+",uniqname,avatarurl"*/);
                result = proxyUserInfoApiManager.getUserInfo(getUserInfoApiparams);

                Result shPlusResult=shPlusUserInfoApiManager.getUserInfo(getUserInfoApiparams);
                if(shPlusResult.isSuccess()){
                    Object obj= shPlusResult.getModels().get("baseInfo");
                    if(obj!=null){
                        AccountBaseInfo baseInfo= (AccountBaseInfo) obj;
                        String uniqname=baseInfo.getUniqname();
                        result.getModels().put("uniqname",Coder.encode(Strings.isNullOrEmpty(uniqname)?userId:uniqname,"UTF-8"));
                        Result photoResult= photoUtils.obtainPhoto(baseInfo.getAvatar(),"50");
                        if(photoResult.isSuccess()){
                            result.getModels().put("avatarurl",photoResult.getModels());
                        }
                    } else {
                        result.getModels().put("uniqname",userId);
                    }
                }
            } else {
                GetSecureInfoApiParams params = new GetSecureInfoApiParams();
                params.setUserid(userId);
                params.setClient_id(clientId);
                result = sgSecureApiManager.getUserSecureInfo(params);
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
    public Result resetWebPassword(UpdatePwdParameters updatePwdParameters, String ip)
            throws Exception {
        Result result = new APIResultSupport(false);
        String username = null;
        try {

            username = updatePwdParameters.getPassport_id();

            UpdatePwdApiParams updatePwdApiParams = buildProxyApiParams(updatePwdParameters);
            int clientId = updatePwdApiParams.getClient_id();

            //检查是否在ip黑名单里
            if (operateTimesService.checkIPLimitResetPwd(ip)){
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST);
                return result;
            }

            if (operateTimesService.checkLimitResetPwd(username, clientId)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_LIMITED);
                return result;
            }

            if (ManagerHelper.isInvokeProxyApi(username)) {
                result = proxySecureApiManager.updatePwd(updatePwdApiParams);
            } else {
                result = sgSecureApiManager.updatePwd(updatePwdApiParams);
            }

            if (result.isSuccess()) {
                operateTimesService.incLimitResetPwd(updatePwdApiParams.getUserid(), updatePwdApiParams.getClient_id());
                operateTimesService.incResetPwdIPTimes(ip);
            }
        } catch (ServiceException e) {
            logger.error("resetWebPassword Fail username:" + username, e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
        return result;
    }

    private UpdatePwdApiParams buildProxyApiParams(UpdatePwdParameters updatePwdParameters) {
        UpdatePwdApiParams updatePwdApiParams = new UpdatePwdApiParams();
        updatePwdApiParams.setUserid(updatePwdParameters.getPassport_id());
        updatePwdApiParams.setPassword(updatePwdParameters.getPassword());
        updatePwdApiParams.setNewpassword(updatePwdParameters.getNewpwd());
        updatePwdApiParams.setModifyip(updatePwdParameters.getIp());
        updatePwdApiParams.setClient_id(Integer.parseInt(updatePwdParameters.getClient_id()));

        return updatePwdApiParams;
    }

    /* --------------------------------------------修改密保内容-------------------------------------------- */
    /*
     * 修改密保邮箱——1.验证原绑定邮箱及发送邮件至待绑定邮箱
     */
    @Override
    public Result sendEmailForBinding(String userId, int clientId, String password,
                                      String newEmail, String oldEmail, String modifyIp, String ru) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            if (operateTimesService.checkIPBindLimit(modifyIp)){
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST);
                return result;
            }

            if (!operateTimesService.checkLimitBind(userId, clientId)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDNUM_LIMITED);
                return result;
            }
            if (!operateTimesService.checkLimitCheckPwdFail(userId, clientId, AccountModuleEnum.SECURE)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CHECKPWDFAIL_LIMIT);
                return result;
            }
            if (!emailSenderService.checkLimitForSendEmail(userId, clientId, AccountModuleEnum.SECURE, newEmail)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SENDEMAIL_LIMITED);
                return result;
            }
            BindEmailApiParams params = new BindEmailApiParams();
            params.setUserid(userId);
            params.setClient_id(clientId);
            params.setPassword(password);
            params.setNewbindemail(newEmail);
            params.setOldbindemail(oldEmail);

            String flag = String.valueOf(System.currentTimeMillis());
            ru = ru + "?token=" + accountSecureService.getSecureCodeRandom(flag) + "&id=" + flag;
            ru = URLEncoder.encode(ru, "UTF-8");
            params.setRu(ru);

            if (ManagerHelper.isInvokeProxyApi(userId)) {
                // 代理接口,SOHU接口需要传MD5加密后的密码
                params.setPassword(Coder.encryptMD5(password));
                result = proxyBindApiManager.bindEmail(params);
            } else {
                result = sgBindApiManager.bindEmail(params);
            }
            if (result.isSuccess()) {
                emailSenderService.incLimitForSendEmail(userId, clientId, AccountModuleEnum.SECURE, newEmail);
                operateTimesService.incIPBindTimes(modifyIp);
                result.setMessage("发送绑定邮箱申请邮件成功！");
            } else if (ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_PWD_ERROR.equals(result.getCode())) {
                operateTimesService.incLimitCheckPwdFail(userId, clientId, AccountModuleEnum.SECURE);
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
    public Result modifyEmailByPassportId(String userId, int clientId, String scode)
            throws Exception {
        Result result = new APIResultSupport(false);
        try {
            if (!operateTimesService.checkLimitBind(userId, clientId)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDNUM_LIMITED);
                return result;
            }
            boolean saveEmail = true;
            AccountModuleEnum module = AccountModuleEnum.SECURE;
            String newEmail = emailSenderService.checkScodeForEmail(userId, clientId, module, scode, saveEmail);
            if(StringUtil.isEmpty(newEmail)){
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDEMAIL_FAILED);
                return result;
            }
            if (accountInfoService.modifyEmailByPassportId(userId, newEmail) == null) {
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
            if (!operateTimesService.checkLimitBind(userId, clientId)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDNUM_LIMITED);
                return result;
            }
            if (ManagerHelper.isInvokeProxyApi(userId)) {
                // 代理接口
                GetUserInfoApiparams getUserInfoApiparams = new GetUserInfoApiparams();
                getUserInfoApiparams.setUserid(userId);
                getUserInfoApiparams.setClient_id(clientId);
                getUserInfoApiparams.setFields(SECURE_FIELDS);
                result = proxyUserInfoApiManager.getUserInfo(getUserInfoApiparams);
                Map<String, String> mapResult = result.getModels();
                String mobile = mapResult.get("sec_mobile");
                result = checkMobileCodeByNewMobile(mobile, clientId, smsCode);
            } else {
                result = checkMobileCodeByPassportId(userId, clientId, smsCode);
            }

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
    public Result bindMobileByPassportId(String userId, int clientId, String newMobile,
                                         String smsCode, String password, String modifyIp) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            Account account;

            //检查是否在ip黑名单里
            if (operateTimesService.checkIPBindLimit(modifyIp)){
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST);
                return result;
            }

            if (!operateTimesService.checkLimitBind(userId, clientId)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDNUM_LIMITED);
                return result;
            }
            if (!operateTimesService.checkLimitCheckPwdFail(userId, clientId, AccountModuleEnum.SECURE)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CHECKPWDFAIL_LIMIT);
                return result;
            }
            result = checkMobileCodeByNewMobile(newMobile, clientId, smsCode);
            if (!result.isSuccess()) {
                return result;
            }
            if (ManagerHelper.isInvokeProxyApi(userId)) {
                // 代理接口
                AuthUserApiParams authParams = new AuthUserApiParams();
                authParams.setUserid(userId);
                authParams.setClient_id(clientId);
                authParams.setPassword(Coder.encryptMD5(password));
                result = proxyLoginApiManager.webAuthUser(authParams);
                if (!result.isSuccess()) {
                    operateTimesService.incLimitCheckPwdFail(userId, clientId, AccountModuleEnum.SECURE);
                    return result;
                }
                result = proxyBindApiManager.bindMobile(userId,newMobile);
            } else {
                // 直接写实现方法，不调用sgBindApiManager，因不能分拆为两个对应方法同时避免读两次Account
                result = accountService.verifyUserPwdVaild(userId, password, true);
                if (!result.isSuccess()) {
                    operateTimesService.incLimitCheckPwdFail(userId, clientId, AccountModuleEnum.SECURE);
                    return result;
                }
                account = (Account) result.getDefaultModel();
                // result.setDefaultModel(null);

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

            operateTimesService.incLimitBind(userId, clientId);
            operateTimesService.incIPBindTimes(modifyIp);

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
            if (operateTimesService.checkIPBindLimit(modifyIp)){
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST);
                return result;
            }
            if (!operateTimesService.checkLimitBind(userId, clientId)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDNUM_LIMITED);
                return result;
            }
            result = checkMobileCodeByNewMobile(newMobile, clientId, smsCode);
            if (!result.isSuccess()) {
                return result;
            }
            if (ManagerHelper.isInvokeProxyApi(userId)) {
                // 代理接口
                GetUserInfoApiparams getUserInfoApiparams = new GetUserInfoApiparams();
                getUserInfoApiparams.setUserid(userId);
                getUserInfoApiparams.setClient_id(clientId);
                getUserInfoApiparams.setFields(SECURE_FIELDS);
                result = proxyUserInfoApiManager.getUserInfo(getUserInfoApiparams);
                Map<String, String> mapResult = result.getModels();
                String mobile = mapResult.get("sec_mobile");

                //解除原绑定手机
                result = proxyBindApiManager.unBindMobile(mobile);
                if(!result.isSuccess()){
                     return result;
                }
                //绑定新手机
                result = proxyBindApiManager.bindMobile(userId,newMobile);
            } else {
                Account account;
                // 修改绑定手机，checkCode为secureCode
                if (!accountSecureService.checkSecureCodeModSecInfo(userId, clientId, scode)) {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BIND_FAILED);
                    return result;
                }

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
            operateTimesService.incLimitBind(userId, clientId);
            operateTimesService.incIPBindTimes(modifyIp);
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
            if (operateTimesService.checkIPBindLimit(modifyIp)){
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST);
                return result;
            }

            if (!operateTimesService.checkLimitBind(userId, clientId)) {
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

            if (ManagerHelper.isInvokeProxyApi(userId)) {
                // 代理接口
                result = proxySecureApiManager.updateQues(updateQuesApiParams);
            } else {
                // SOGOU接口
                result = sgSecureApiManager.updateQues(updateQuesApiParams);
            }

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
    public Result checkMobileCodeByNewMobile(String mobile, int clientId, String smsCode)
            throws Exception {
        Result result = new APIResultSupport(false);
        try {
            return mobileCodeSenderService.checkSmsCode(mobile, clientId, AccountModuleEnum.SECURE, smsCode);
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