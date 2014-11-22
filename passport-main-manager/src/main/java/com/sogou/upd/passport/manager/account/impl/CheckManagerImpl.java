package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.SignatureUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.CheckManager;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.AccountSecureService;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-3 Time: 上午10:52 To change this template use
 * File | Settings | File Templates.
 */
@Component
public class CheckManagerImpl implements CheckManager {

    private static Logger logger = LoggerFactory.getLogger(CheckManagerImpl.class);

    @Autowired
    private AppConfigService appConfigService;
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

    @Override
    public Result checkMappLogoutCode(String sgid, String client_id, String instance_id, String actualCode) {
        Result result = new APIResultSupport(false);
        int clientId = Integer.parseInt(client_id);
        AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
        if (appConfig != null) {
            String secret = appConfig.getClientSecret();

            TreeMap map = new TreeMap();
            map.put(LoginConstant.COOKIE_SGID, sgid);
            map.put("client_id", client_id);
            map.put("instance_id", instance_id);

            //计算默认的code
            String code = "";
            try {
                code = SignatureUtils.generateSignature(map, secret);
            } catch (Exception e) {
                logger.error("calculate default code error", e);
            }

            if (code.equalsIgnoreCase(actualCode)) {
                result.setSuccess(true);
                result.setMessage("内部接口code签名正确！");
            } else {
                result.setCode(ErrorUtil.INTERNAL_REQUEST_INVALID);
            }
        } else {
            result.setCode(ErrorUtil.INVALID_CLIENTID);
        }
        return result;
    }

    @Override
    public boolean checkMappCode(String uniqSign, int clientId, long ct, String actualCode) {
        if (!Strings.isNullOrEmpty(actualCode)) {
            AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
            if (appConfig != null) {
                String clientSecret = appConfig.getClientSecret();
                String expectCode = ManagerHelper.generatorCode(uniqSign, clientId, clientSecret, ct);
                return expectCode.equals(actualCode);

            }
        }
        return false;
    }
}
