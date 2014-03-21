package com.sogou.upd.passport.manager.account.impl;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.OperateTimesService;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-27
 * Time: 下午8:36
 * To change this template use File | Settings | File Templates.
 */
@Component
public class CommonManagerImpl implements CommonManager {
    private static Logger log = LoggerFactory.getLogger(CommonManagerImpl.class);

    @Autowired
    private AccountService accountService;
    @Autowired
    private OperateTimesService operateTimesService;
    @Autowired
    private AppConfigService appConfigService;

    @Override
    public boolean isCodeRight(String firstStr, int clientId, long ct, String originalCode) {
        String code = getCode(firstStr.toString(), clientId, ct);
        boolean isCodeEqual = code.equalsIgnoreCase(originalCode);
        return isCodeEqual;
    }

    @Override
    public boolean isMillCtValid(long ct) {
        long currentTime = System.currentTimeMillis();
        boolean timeRight = ct > currentTime - CommonConstant.COOKIE_REQUEST_VAILD_TERM_IN_MILLI;
        return timeRight;
    }


    @Override
    public boolean isSecCtValid(long ct) {
        long currentTime = System.currentTimeMillis() / 1000;
        boolean timeRight = ct > currentTime - CommonConstant.COOKIE_REQUEST_VAILD_TERM;
        return timeRight;
    }

    @Override
    public String getCode(String firstStr, int clientId, long ct) {
        AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
        if (appConfig == null) {
            return null;
        }
        String secret = appConfig.getServerSecret();
        String code = ManagerHelper.generatorCode(firstStr.toString(), clientId, secret, ct);
        return code;
    }

    @Override
    public void incRegTimesForInternal(String ip) {
        operateTimesService.incRegTimesForInternal(ip);
    }

    @Override
    public void incRegTimes(String ip, String cookieStr) {
        operateTimesService.incRegTimes(ip, cookieStr);
    }
}