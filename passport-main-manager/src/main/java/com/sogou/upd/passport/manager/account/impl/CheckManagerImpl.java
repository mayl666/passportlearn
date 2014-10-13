package com.sogou.upd.passport.manager.account.impl;

import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.CheckManager;
import com.sogou.upd.passport.service.account.AccountSecureService;
import com.sogou.upd.passport.service.account.AccountService;
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
    private AccountSecureService accountSecureService;

    @Override
    public boolean checkCaptcha(String captcha, String token) {
        try {
            return accountService.checkCaptchaCodeIsVaild(token, captcha);
        } catch (ServiceException e) {
            logger.error("check captcha Fail:", e);
            return false;
        }
    }

    @Override
    public boolean checkScode(String scode, String id) {
        return accountSecureService.checkSecureCodeRandom(scode, id);
    }
}
