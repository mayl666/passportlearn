package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.RegManager;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.account.WapResetPwdManager;
import com.sogou.upd.passport.service.account.AccountSecureService;
import com.sogou.upd.passport.service.account.MobileCodeSenderService;
import com.sogou.upd.passport.service.account.MobilePassportMappingService;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-7-4
 * Time: 下午1:53
 * To change this template use File | Settings | File Templates.
 */
@Component
public class WapResetPwdManagerImpl implements WapResetPwdManager {

    private static Logger logger = LoggerFactory.getLogger(WapResetPwdManagerImpl.class);

    @Autowired
    private SecureManager secureManager;
    @Autowired
    private CommonManager commonManager;
    @Autowired
    private RegManager regManager;
    @Autowired
    private MobilePassportMappingService mobilePassportMappingService;
    @Autowired
    private MobileCodeSenderService mobileCodeSenderService;
    @Autowired
    private AccountSecureService accountSecureService;

    @Override
    public Result checkMobileCodeResetPwd(String mobile, int clientId, String smsCode, boolean needScode) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            String passportId = mobilePassportMappingService.queryPassportIdByMobile(mobile);
            if (Strings.isNullOrEmpty(passportId)) {
                result.setCode(ErrorUtil.INVALID_ACCOUNT);
                return result;
            }
            result = mobileCodeSenderService.checkSmsCode(mobile, clientId, AccountModuleEnum.RESETPWD, smsCode);
            if (!result.isSuccess()) {
                result.setDefaultModel("userid", passportId);
                return result;
            }
            result.setSuccess(true);
            result.setMessage("手机号与验证码匹配成功");
            if (needScode) {
                result.setDefaultModel("scode", accountSecureService.getSecureCode(passportId, clientId, CacheConstant.CACHE_PREFIX_PASSPORTID_RESETPWDSECURECODE));
            }
            result.setDefaultModel("userid", passportId);
            return result;
        } catch (ServiceException e) {
            logger.error("check mobile code reset pwd Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    @Override
    public Result sendMobileCaptcha(String mobile, String client_id, String token, String captcha) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            //检测手机号是否已经注册或绑定
            String passportId = mobilePassportMappingService.queryPassportIdByMobile(mobile);
            if (Strings.isNullOrEmpty(passportId)) {
                result.setCode(ErrorUtil.INVALID_ACCOUNT);
                return result;
            }
            result = commonManager.checkMobileSendSMSInBlackList(mobile, client_id);
            if (!result.isSuccess()) {
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
                    result.setDefaultModel("token", RandomStringUtils.randomAlphanumeric(48));
                    return result;
                }
            }
            result = secureManager.sendMobileCode(mobile, Integer.parseInt(client_id), AccountModuleEnum.RESETPWD);
            if (result.isSuccess()) {
                result.setDefaultModel("userid", passportId);
            }
        } catch (Exception e) {
            logger.error("send mobile code Fail, mobile:" + mobile, e);
        } finally {
            commonManager.incSendTimesForMobile(mobile);
        }
        return result;
    }
}
