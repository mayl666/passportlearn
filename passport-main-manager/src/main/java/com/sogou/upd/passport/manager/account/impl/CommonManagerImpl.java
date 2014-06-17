package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.LogUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.api.account.BindApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.MobilePassportMappingService;
import com.sogou.upd.passport.service.account.OperateTimesService;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
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
@Component("commonManager")
public class CommonManagerImpl implements CommonManager {
    private static Logger log = LoggerFactory.getLogger(CommonManagerImpl.class);
    private static Logger profileErrorLogger = LoggerFactory.getLogger("profileErrorLogger");

    @Autowired
    private OperateTimesService operateTimesService;
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private MobilePassportMappingService mobilePassportMappingService;
    @Autowired
    private BindApiManager proxyBindApiManager;

    @Override
    public boolean isCodeRight(String firstStr, int clientId, long ct, String originalCode) {
        String code = getCode(firstStr.toString(), clientId, ct);
        boolean isCodeEqual = code.equalsIgnoreCase(originalCode);
        return isCodeEqual;
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
    public void incRegTimesForInternal(String ip, int client_id) {
        operateTimesService.incRegTimesForInternal(ip, client_id);
    }

    @Override
    public void incRegTimes(String ip, String cookieStr) {
        operateTimesService.incRegTimes(ip, cookieStr);
    }

    @Override
    public String getPassportIdByUsername(String username) {
        Result result;
        //根据username获取passportID
        String passportId = PassportIDGenerator.generator(username, AccountTypeEnum.getAccountType(username).getValue());
        //如果是手机号，需要查询该手机绑定的主账号
        if (PhoneUtil.verifyPhoneNumberFormat(username)) {
            passportId = mobilePassportMappingService.queryPassportIdByMobile(username);
            if (Strings.isNullOrEmpty(passportId)) {
                BaseMoblieApiParams baseMoblieApiParams = new BaseMoblieApiParams();
                baseMoblieApiParams.setMobile(username);
                result = proxyBindApiManager.getPassportIdByMobile(baseMoblieApiParams);
                if (result.isSuccess()) {
                    passportId = result.getModels().get("userid").toString();
                    if (result.isSuccess()) {
                        passportId = result.getModels().get("userid").toString();
                        String message = CommonConstant.MOBILE_MESSAGE;
                        LogUtil.buildErrorLog(profileErrorLogger, AccountModuleEnum.UNKNOWN, "getPassportIdByUsername", message, username, passportId, result.toString());
                    }
                }
            }
        }
        return passportId;
    }

}