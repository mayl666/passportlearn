package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.CheckManager;
import com.sogou.upd.passport.service.account.AccountSecureService;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.EmailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-3 Time: 上午10:52 To change this template use
 * File | Settings | File Templates.
 */
@Component
public class CheckManagerImpl implements CheckManager {

    private static Logger logger = LoggerFactory.getLogger(CheckManagerImpl.class);

    @Autowired
    private AccountService accountService;
    @Autowired
    private EmailSenderService emailSenderService;
    @Autowired
    private AccountSecureService accountSecureService;

    @Override
    public boolean checkCaptcha(String captcha, String token) throws Exception {
        try {
            return accountService.checkCaptchaCodeIsValid(token, captcha);
        } catch (ServiceException e) {
            logger.error("check captcha Fail:", e);
            return false;
        }
    }

    @Override
    public boolean checkLimitResetPwd(String passportId, int clientId) throws Exception {
        try {
            return accountService.checkLimitResetPwd(passportId);
        } catch (ServiceException e) {
            logger.error("check limit for reset pwd Fail:", e);
            return false;
        }
    }

    @Override
    public String checkEmailScodeReturnStr(String passportId, int clientId, AccountModuleEnum module,
            String scode) throws Exception {
        try {
            return emailSenderService.checkScodeForEmail(passportId, clientId, module, scode, true);
        } catch (ServiceException e) {
            logger.error("check scode for email Fail:", e);
            return null;
        }
    }

    @Override
    public boolean checkEmailScode(String passportId, int clientId, AccountModuleEnum module,
                                           String scode) throws Exception {
        try {
            String returnStr = emailSenderService.checkScodeForEmail(passportId, clientId, module, scode, false);
            return !Strings.isNullOrEmpty(returnStr);
        } catch (ServiceException e) {
            logger.error("check scode for email Fail:", e);
            return false;
        }
    }

    @Override
    public boolean checkScodeResetPwd(String passportId, int clientId, String scode) throws Exception {
        try {
            return accountSecureService.checkSecureCodeResetPwd(passportId, clientId, scode);
        } catch (ServiceException e) {
            logger.error("check scode for reset pwd Fail:", e);
            return false;
        }
    }

    @Override
    public boolean checkScode(String scode, String id) throws Exception {
        return accountSecureService.checkSecureCodeRandom(scode, id);
    }
}
