package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.common.utils.ToolUUIDUtil;
import com.sogou.upd.passport.manager.account.AccountInfoManager;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.RegManager;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.account.SmsCodeLoginManager;
import com.sogou.upd.passport.manager.api.connect.SessionServerManager;
import com.sogou.upd.passport.manager.form.WapSmsCodeLoginParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountInfo;
import com.sogou.upd.passport.service.account.AccountInfoService;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.MobilePassportMappingService;
import com.sogou.upd.passport.service.account.OperateTimesService;
import com.sogou.upd.passport.service.account.SmsCodeLoginService;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 短信登录
 * User: chengang
 * Date: 15-6-8
 * Time: 下午3:32
 */
@Component
public class SmsCodeLoginManagerImpl implements SmsCodeLoginManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmsCodeLoginManagerImpl.class);

    @Autowired
    private CommonManager commonManager;
    @Autowired
    private RegManager regManager;
    @Autowired
    private SessionServerManager sessionServerManager;
    @Autowired
    private SecureManager secureManager;

    @Autowired
    private SmsCodeLoginService smsCodeLoginService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountInfoService accountInfoService;
    @Autowired
    private MobilePassportMappingService mobilePassportMappingService;

    @Autowired
    private OperateTimesService operateTimesService;
    
    @Autowired
    private AccountInfoManager accountInfoManager;
    
    /**
     * 下发短信校验码
     *
     * @param mobile
     * @param client_id
     * @return
     */
    @Override
    public Result sendSmsCode(final String mobile, final int client_id, final String token, final String captcha) {
        Result result = new APIResultSupport(false);
        try {
//            //必须验证验证码 ，若token、captcha为空，则提示错误“请输入验证码”
//            if((Strings.isNullOrEmpty(token)||Strings.isNullOrEmpty(captcha))){
//                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_NEED_CODE);
//                result.setDefaultModel("token", RandomStringUtils.randomAlphanumeric(48));
//                return result;
//            }


            //验证请求验证码是否超限制
//            result = commonManager.checkMobileSendSMSInBlackList(mobile, String.valueOf(client_id));
            boolean needSmsCaptcha=operateTimesService.checkSMSnNeedCaptcha(mobile,client_id);
            if (needSmsCaptcha) {
//            if (!result.isSuccess()) {
                //如果token和captcha都不为空，则校验是否匹配
                if (!Strings.isNullOrEmpty(token) && !Strings.isNullOrEmpty(captcha)) {
                    result = regManager.checkCaptchaToken(token, captcha);
                    //如果验证码校验失败，则提示
                    if (!result.isSuccess()) {
                        result.setDefaultModel("token", RandomStringUtils.randomAlphanumeric(48));
                        result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
                        return result;
                    }
                } else {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_NEED_CODE);
                    result.setDefaultModel("token", RandomStringUtils.randomAlphanumeric(48));
                    return result;
                }
            }

            //生成校验码
            result = secureManager.sendMobileCode(mobile, client_id, AccountModuleEnum.LOGIN);

        } catch (Exception e) {
            LOGGER.error("SmsCodeLoginManagerImpl sendSmsCode error,message:{}", e.getMessage());
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }


    /**
     * 短信登录
     *
     * @param smsCodeLoginParams
     * @param ip
     * @return
     */
    @Override
    public Result smsCodeLogin(WapSmsCodeLoginParams smsCodeLoginParams, String ip) {
        Result result = new APIResultSupport(false);
        String mobile = smsCodeLoginParams.getMobile();
        String smsCode = smsCodeLoginParams.getSmsCode();
        String token = smsCodeLoginParams.getToken();
        String captchaCode = smsCodeLoginParams.getCaptcha();
        int clientId = Integer.parseInt(smsCodeLoginParams.getClient_id());

        try {
            //校验是否需要验证码
            if (operateTimesService.smsCodeLoginFailedNeedCaptcha(mobile, ip)) {
                if (Strings.isNullOrEmpty(captchaCode)) {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_NEED_CODE);
                    return result;
                }
                if (!accountService.checkCaptchaCodeIsVaild(token, captchaCode)) {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
                    return result;
                }
            }

            if (PhoneUtil.verifyPhoneNumberFormat(mobile)) {
                //1、验证短信校验码
                result = smsCodeLoginService.checkSmsCode(mobile, smsCode, clientId);
                if (result.isSuccess()) {
                    //2、判定手机号是否注册，如果未注册，则初始化账号信息
                    String passportId = mobilePassportMappingService.queryPassportIdByMobile(mobile);
                    if (Strings.isNullOrEmpty(passportId)) {
                        //初始化账号信息 Account、 AccountInfo、 MobilePassportMapping
                        Account account = accountService.initialAccount(mobile, ToolUUIDUtil.genreateUUidWithOutSplit(), true, ip, AccountTypeEnum.MESSAGELOGIN.getValue());
                        if (account != null) {
                            passportId = account.getPassportId();
                            AccountInfo accountInfo = new AccountInfo(passportId, new Date(), new Date());
                            if (!Strings.isNullOrEmpty(ip)) {
                                accountInfo.setModifyip(ip);
                            }
                            boolean insertSuccess = accountInfoService.updateAccountInfo(accountInfo);
                            if (!insertSuccess) {
                                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
                                return result;
                            }
                        } else {
                            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
                            return result;
                        }
                    }
                    
                    //3、写session 数据库
                    Result sessionResult = sessionServerManager.createSession(passportId);
                    String sgid = null;
                    if (sessionResult.isSuccess()) {
                        sgid = (String) sessionResult.getModels().get(LoginConstant.COOKIE_SGID);
                        result.getModels().put(CommonConstant.USERID, passportId);
                        if (!Strings.isNullOrEmpty(sgid)) {
                            result.getModels().put(LoginConstant.COOKIE_SGID, sgid);
                        }
                    }
                }
            } else {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONEERROR);
                result.setMessage(CommonConstant.MOBILE_FORMAT_ERROR);
            }
        } catch (Exception e) {
            LOGGER.error("SmsCodeLoginManagerImpl smsCodeLogin error,message:{}", e.getMessage());
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_LOGIN_FAILED);
            result.setSuccess(false);
        }
        return result;
    }

    @Override
    public boolean needCaptchaCheck(String client_id, String username, String ip) {
        if (operateTimesService.smsCodeLoginFailedNeedCaptcha(username, ip)) {
            return true;
        }
        return false;
    }

    @Override
    public void doAfterLoginSuccess(final String username, final String ip, final String passportId, final int clientId) {
        //记录登陆次数
        operateTimesService.incSmsCodeLoginTimes(username, ip, true);
        //用户登陆记录
        secureManager.logActionRecord(passportId, clientId, AccountModuleEnum.LOGIN, ip, null);
    }

    @Override
    public void doAfterLoginFailed(final String mobile, final String ip, String errCode) {
        if (ErrorUtil.ERROR_CODE_SMS_CODE_ERROR.equalsIgnoreCase(errCode)) {
            operateTimesService.incSmsCodeLoginTimes(mobile, ip, false);
        }
    }
}
