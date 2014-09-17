package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.AccountSecureService;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.MobilePassportMappingService;
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
@Component("commonManager")
public class CommonManagerImpl implements CommonManager {
    private static final Logger logger = LoggerFactory.getLogger(CommonManagerImpl.class);

    @Autowired
    private OperateTimesService operateTimesService;
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private MobilePassportMappingService mobilePassportMappingService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountSecureService accountSecureService;

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
    public String getPassportIdByUsername(String username) throws Exception {
        //根据username获取passportID
        String passportId = username;
        if (AccountDomainEnum.isPhone(username)) {
            passportId = username + CommonConstant.SOHU_SUFFIX;
        }
        if (AccountDomainEnum.isIndivid(username)) {
            passportId = username + CommonConstant.SOGOU_SUFFIX;
        }
        try {
            //如果是手机号，需要查询该手机绑定的主账号
            if (PhoneUtil.verifyPhoneNumberFormat(username)) {
                passportId = mobilePassportMappingService.queryPassportIdByMobile(username);
                if (Strings.isNullOrEmpty(passportId)) {
                    return null;
                }
            }
        } catch (Exception e) {
            logger.error("getPassportIdByUsername Exception", e);
            throw new Exception(e);
        }
        return passportId;
    }

    @Override
    public String getSecureCodeResetPwd(String passportId, int clientId) throws ServiceException {
        return accountSecureService.getSecureCodeResetPwd(passportId, clientId);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Account queryAccountByPassportId(String passportId) throws ServiceException {
        return accountService.queryAccountByPassportId(passportId);  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public boolean isAccessAccept(int clientId, String requestIp, String apiName) {
        try {
            AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
            if (appConfig == null) {
                return false;
            }
            String scope = appConfig.getScope();
            if (!Strings.isNullOrEmpty(apiName) && !StringUtil.splitStringContains(scope, ",", apiName)) {
                return false;
            }
            String serverIp = appConfig.getServerIp();
            if (!Strings.isNullOrEmpty(requestIp) && !StringUtil.splitStringContains(serverIp, ",", requestIp)) {
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("isAccessAccept error, api:" + apiName, e);
            return false;
        }
    }

    @Override
    public Result checkMobileSendSMSInBlackList(String ipOrMobile, String client_id) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            //检查ip或者mobile是否中了限制
            if (operateTimesService.isMobileSendSMSInBlackList(ipOrMobile)) {
                if (PhoneUtil.verifyPhoneNumberFormat(ipOrMobile)) {
                    //todo 此处暂时将浏览器1044的情况排除掉，不校验是否需要弹出验证码；上完线后此bug是要修复的
                    if (!Strings.isNullOrEmpty(client_id) && CommonConstant.PC_CLIENTID == Integer.parseInt(client_id)) {
                         result.setSuccess(true);
                    } else {
                        //如果是手机号，则提示需要输入验证码
                        result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_NEED_CODE);
                        return result;
                    }
                } else {
                    //如果是ip，则还需要检查ip是否在白名单中
                    if (!operateTimesService.checkRegInWhiteList(ipOrMobile)) {
                        result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST);
                        return result;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("[manager]method isMobileSendSMSInBlackList error", e);
            throw new Exception(e);
        }
        result.setSuccess(true);
        return result;
    }

    @Override
    public void incSendTimesForMobile(String ipOrMobile) throws Exception {
        try {
            operateTimesService.incSendTimesForMobile(ipOrMobile);
        } catch (ServiceException e) {
            logger.error("register incSendTimesForMobile Exception", e);
            throw new Exception(e);
        }
    }

}