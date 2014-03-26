package com.sogou.upd.passport.manager.api.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.parameter.AccountStatusEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.*;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.MobileCodeSenderService;
import com.sogou.upd.passport.service.account.MobilePassportMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 注册
 * User: mayan
 * Date: 13-6-8
 * Time: 下午9:50
 */
@Component("sgRegisterApiManager")
public class SGRegisterApiManagerImpl implements RegisterApiManager {

    private static Logger logger = LoggerFactory.getLogger(SGRegisterApiManagerImpl.class);

    @Autowired
    private AccountService accountService;
    @Autowired
    private SecureManager secureManager;
    @Autowired
    private MobileCodeSenderService mobileCodeSenderService;
    @Autowired
    private MobilePassportMappingService mobilePassportMappingService;

    @Override
    public Result regMailUser(RegEmailApiParams params) {
        Result result = new APIResultSupport(false);
        try {
            String username = params.getUserid();
            String password = params.getPassword();
            String ip = params.getCreateip();
            int clientId = params.getClient_id();

            //判断注册账号类型，外域用户还是个性用户
            AccountDomainEnum emailType = AccountDomainEnum.getAccountDomain(username);
            switch (emailType) {
                case SOGOU://个性账号直接注册
                case INDIVID:
                    Account account = accountService.initialAccount(username, password, true, ip, AccountTypeEnum
                            .EMAIL.getValue());
                    if (account != null) {
                        result.setSuccess(true);
                        result.setCode("0");
                        result.setDefaultModel("userid", account.getPassportId());
                        result.setMessage("注册成功！");
                        result.setDefaultModel("isSetCookie", true);
                        result.setDefaultModel(account);
                    } else {
                        result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
                    }
                    break;
                case OTHER://外域邮件注册
                    String ru = params.getRu();
                    boolean isSendSuccess = accountService.sendActiveEmail(username, password, clientId, ip, ru);
                    if (isSendSuccess) {
                        result.setSuccess(true);
                        result.setCode("0");
                        result.setMessage("感谢注册，请立即激活账户！");
                        result.setDefaultModel("isSetCookie", false);
                    } else {
                        result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
                    }
                    break;
            }
            return result;
        } catch (Exception e) {
            logger.error("mail register account Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }


    @Override
    public Result regMobileCaptchaUser(RegMobileCaptchaApiParams regParams) {
        Result result = new APIResultSupport(false);
        try {
            int clientId = regParams.getClient_id();
            String mobile = regParams.getMobile();
            String password = regParams.getPassword();
            String ip = regParams.getIp();

            String captcha = regParams.getCaptcha();
            //验证手机号码与验证码是否匹配
            result = mobileCodeSenderService.checkSmsCode(mobile, clientId, AccountModuleEnum.REGISTER, captcha);
            if (!result.isSuccess()) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOT_MATCH_SMSCODE);
                return result;
            }

            Account account = accountService.initialAccount(mobile, password, true, ip, AccountTypeEnum
                    .PHONE.getValue());
            if (account != null) {
                result.setSuccess(true);
                result.setDefaultModel("userid", account.getPassportId());
                result.setMessage("注册成功！");
                result.setDefaultModel("isSetCookie", true);
//                result.setDefaultModel(account);
            } else {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
            }
        } catch (Exception e) {
            logger.error("mobile register phone account Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    @Override
    public Result checkUser(CheckUserApiParams checkUserApiParams) {
        Result result = new APIResultSupport(false);
        String username = null;
        try {
            username = checkUserApiParams.getUserid();
            //判断是否是个性账号
            if (PhoneUtil.verifyPhoneNumberFormat(username)) {
                String passportId = mobilePassportMappingService.queryPassportIdByMobile(username);
                if (Strings.isNullOrEmpty(passportId)) {
                    //不存在返回false
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_BIND_NOTEXIST);
                    return result;
                } else {
                    //存在返回true
                    result.getModels().put("flag", AccountStatusEnum.REGULAR.getValue());
                    result.getModels().put("userid", passportId);
                    result.setSuccess(true);
                    return result;
                }
            }
            if (AccountDomainEnum.INDIVID.equals(AccountDomainEnum.getAccountDomain(username))) {
                username = username.toLowerCase() + "@sogou.com";
            }
            //外域邮箱只处理@后面那一串为小写
            if (username.indexOf("@") != -1) {
                int index = username.indexOf("@");
                username = username.substring(0, index) + username.substring(index, username.length()).toLowerCase();
            }
            Account account = accountService.queryAccountByPassportId(username);
            if (account == null) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
                return result;
            }
        } catch (ServiceException e) {
            logger.error("Check account is exists Exception, username:" + username, e);
            throw new ServiceException(e);
        }
        result.getModels().put("userid", username);
        result.setSuccess(true);
        return result;
    }

    @Override
    public Result sendMobileRegCaptcha(BaseMobileApiParams params) {
        Result result = new APIResultSupport(false);
        String mobile = params.getMobile();
        try {
            result = secureManager.sendMobileCode(params.getMobile(), params.getClient_id(), AccountModuleEnum.REGISTER);
        } catch (Exception e) {
            logger.error("send mobile code Fail, mobile:" + mobile, e);
        }
        return result;
    }

    @Override
    public Result regMobileUser(RegMobileApiParams regMobileApiParams) {
        Result result = new APIResultSupport(false);
        String mobile = regMobileApiParams.getMobile();
        String password = regMobileApiParams.getPassword();
        try {
            if (PhoneUtil.verifyPhoneNumberFormat(mobile)) {
                String passportId = mobilePassportMappingService.queryPassportIdByMobile(mobile);
                if (!Strings.isNullOrEmpty(passportId)) {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGED);
                    return result;
                }
                Account account = accountService.initialAccount(mobile, password, true, null, AccountTypeEnum
                        .PHONE.getValue());
                if (account != null) {
                    result.setSuccess(true);
                    result.setDefaultModel("userid", account.getPassportId());
                    result.setMessage("注册成功！");
                    result.setDefaultModel("isSetCookie", true);
//                    result.setDefaultModel(account);
                } else {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
                    return result;
                }
            } else {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONEERROR);
                return result;
            }
        } catch (Exception e) {
            logger.error("mobile reg without captcha failed,mobile " + mobile, e);
            result.setCode(ErrorUtil.ERR_CODE_REGISTER_UNUSUAL);
        }
        return result;
    }
}
