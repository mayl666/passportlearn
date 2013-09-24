package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.CommonHelper;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.PCAccountManager;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.form.PcAuthTokenParams;
import com.sogou.upd.passport.manager.form.PcPairTokenParams;
import com.sogou.upd.passport.manager.form.PcRefreshTokenParams;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.PCAccountTokenService;
import com.sogou.upd.passport.service.account.SHTokenService;
import com.sogou.upd.passport.service.account.generator.TokenDecrypt;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 桌面端登录流程Manager
 * User: chenjiameng
 * Date: 13-7-28
 * Time: 上午11:50
 * To change this template use File | Settings | File Templates.
 */
@Component
public class PCAccountManagerImpl implements PCAccountManager {

    private static final long SIG_EXPIRES = 60 * 60 * 1000; //sig里的timestamp有效期，一小时，单位毫秒
    private static final Logger logger = LoggerFactory.getLogger(PCAccountManagerImpl.class);

    @Autowired
    private LoginApiManager proxyLoginApiManager;
    @Autowired
    private LoginApiManager sgLoginApiManager;
    @Autowired
    private PCAccountTokenService pcAccountService;
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private SHTokenService shTokenService;

    @Override
    public Result createPairToken(PcPairTokenParams pcTokenParams) {
        Result finalResult = new APIResultSupport(false);
        try {
            int clientId = Integer.parseInt(pcTokenParams.getAppid());
            String passportId = pcTokenParams.getUserid();
            String password = pcTokenParams.getPassword();
            String instanceId = pcTokenParams.getTs();
            AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
            if (appConfig == null) {
                finalResult.setCode(ErrorUtil.INVALID_CLIENTID);
                return finalResult;
            }
            if (!Strings.isNullOrEmpty(password)) {   //校验用户名和密码
                AuthUserApiParams authUserApiParams = new AuthUserApiParams(clientId, passportId, password);
                //根据域名判断是否代理，一期全部走代理
                Result result = new APIResultSupport(false);
                if (ManagerHelper.isInvokeProxyApi(passportId)) {
                    result = proxyLoginApiManager.webAuthUser(authUserApiParams);
                } else {
                    result = sgLoginApiManager.webAuthUser(authUserApiParams);
                }
                if (!result.isSuccess()) {
                    return result;
                }
            } else {    //校验签名
                String sig = pcTokenParams.getSig();
                String timestamp = pcTokenParams.getTimestamp();
                String clientSecret = appConfig.getClientSecret();
                if (!verifySig(passportId, clientId, instanceId, timestamp, clientSecret, sig)) {
                    finalResult.setCode(ErrorUtil.ERR_SIGNATURE_OR_TOKEN);
                    return finalResult;
                }
            }
            return initialAccountToken(passportId, instanceId, appConfig);
        } catch (Exception e) {
            logger.error("createPairToken fail", e);
            finalResult.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return finalResult;
        }
    }

    @Override
    public Result authRefreshToken(PcRefreshTokenParams pcRefreshTokenParams) {
        Result result = new APIResultSupport(false);
        int clientId = Integer.parseInt(pcRefreshTokenParams.getAppid());
        String passportId = pcRefreshTokenParams.getUserid();
        String instanceId = pcRefreshTokenParams.getTs();
        String refreshToken = pcRefreshTokenParams.getRefresh_token();
        try {
            AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
            if (appConfig == null) {
                result.setCode(ErrorUtil.INVALID_CLIENTID);
                return result;
            }
            boolean res = pcAccountService.verifyRefreshToken(passportId, clientId, instanceId, refreshToken);
            if (!res) {
                if (CommonHelper.isIePinyinToken(clientId)) {
                    if (!shTokenService.verifyShRefreshToken(passportId, clientId, instanceId, refreshToken)) {
                        result.setCode(ErrorUtil.ERR_REFRESH_TOKEN);
                        return result;
                    }
                } else {
                    result.setCode(ErrorUtil.ERR_REFRESH_TOKEN);
                    return result;
                }
            }
            AccountToken accountToken = pcAccountService.initialOrUpdateAccountToken(passportId, instanceId, appConfig);
            if (accountToken != null) {
                result.setSuccess(true);
                result.setDefaultModel(accountToken);
            } else {
                result.setCode(ErrorUtil.CREATE_TOKEN_FAIL);
            }
            return result;
        } catch (Exception e) {
            logger.error("authRefreshToken fail", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    @Override
    public Result authToken(PcAuthTokenParams authPcTokenParams) {
        Result result = new APIResultSupport(false);
        try {
            //验证accessToken
            int clientId = Integer.parseInt(authPcTokenParams.getAppid());
            String passportId = authPcTokenParams.getUserid();
            String instanceId = authPcTokenParams.getTs();
            if (pcAccountService.verifyAccessToken(passportId, clientId, instanceId, authPcTokenParams.getToken())) {
                result.setSuccess(true);
            } else {
                if (CommonHelper.isIePinyinToken(clientId)) {
                    if (shTokenService.verifyShAccessToken(passportId, clientId, instanceId, authPcTokenParams.getToken())) {
                        result.setSuccess(true);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("authToken fail", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
        return result;
    }

    @Override
    public boolean verifyRefreshToken(PcRefreshTokenParams pcRefreshTokenParams) {
        try {
            //验证refreshToken
            int client_id = Integer.parseInt(pcRefreshTokenParams.getAppid());
            return (pcAccountService.verifyRefreshToken(pcRefreshTokenParams.getUserid(), client_id,
                    pcRefreshTokenParams.getTs(), pcRefreshTokenParams.getRefresh_token()) ||
                    shTokenService.verifyShRefreshToken(pcRefreshTokenParams.getUserid(), client_id, pcRefreshTokenParams.getTs(), pcRefreshTokenParams.getRefresh_token()));
        } catch (Exception e) {
            logger.error("verifyRefreshToken fail", e);
            return false;
        }
    }

    @Override
    public String getSig(String passportId, int clientId, String refresh_token, String timestamp) throws Exception {
        AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
        if (appConfig == null) {
            return null;
        }
        String clientSecret = appConfig.getClientSecret();
        String sig = Coder.encryptMD5(passportId + clientId + refresh_token + timestamp + clientSecret);
        return sig;
    }

    @Override
    public Result createConnectToken(int clientId, String passportId, String instanceId) {
        Result finalResult = new APIResultSupport(false);
        try {
            AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
            if (appConfig == null) {
                finalResult.setCode(ErrorUtil.INVALID_CLIENTID);
                return finalResult;
            }
            return initialAccountToken(passportId, instanceId, appConfig);
        } catch (ServiceException e) {
            logger.error("createConnectToken fail", e);
            finalResult.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return finalResult;
        }
    }

    @Override
    public Result createAccountToken(String passportId, String instanceId,int  clientId) {
        Result finalResult = new APIResultSupport(false);
        try {
            AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
            if (appConfig == null) {
                finalResult.setCode(ErrorUtil.INVALID_CLIENTID);
                return finalResult;
            }
            return initialAccountToken(passportId, instanceId, appConfig);
        } catch (ServiceException e) {
            logger.error("createToken fail", e);
            finalResult.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return finalResult;
        }
    }
    @Override
    public Result queryPassportIdByAccessToken(String token,int clientId){
        Result finalResult = new APIResultSupport(false);
        try {
            AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
            if (appConfig == null) {
                finalResult.setCode(ErrorUtil.INVALID_CLIENTID);
                return finalResult;
            }
            String passportId = TokenDecrypt.decryptPcToken(token, appConfig.getClientSecret());
            if(StringUtils.isEmpty(passportId)){
                if(clientId == CommonConstant.BROWSER_CLIENTID){
                     //TODO 通过sohu+ token获取userid
                    passportId="";
                }
            }
            if(StringUtils.isEmpty(passportId)){
                finalResult.setCode(ErrorUtil.ERR_ACCESS_TOKEN);
                return finalResult;
            }
            finalResult.setSuccess(true);
            finalResult.setDefaultModel(passportId);
            return finalResult;
        } catch (ServiceException e) {
            logger.error("createToken fail", e);
            finalResult.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return finalResult;
        }
    }
    private Result initialAccountToken(String passportId, String instanceId, AppConfig appConfig) {
        Result finalResult = new APIResultSupport(false);
        AccountToken accountToken = pcAccountService.initialOrUpdateAccountToken(passportId, instanceId, appConfig);
        if (accountToken != null) {
            finalResult.setSuccess(true);
            finalResult.setDefaultModel(accountToken);
        } else {
            finalResult.setCode(ErrorUtil.CREATE_TOKEN_FAIL);
        }
        return finalResult;
    }

    /*
     * 校验签名，算法：sig=MD5(passportId + clientId + refresh_token + timestamp + clientSecret）
     */
    private boolean verifySig(String passportId, int clientId, String instanceId, String timestamp, String clientSecret, String sig) throws Exception {
        // 校验时间戳
        long curTimestamp = System.currentTimeMillis();
        long ts = Long.parseLong(timestamp);
        if (curTimestamp > ts + SIG_EXPIRES) {
            return false;
        }

        AccountToken accountToken = pcAccountService.queryAccountToken(passportId, clientId, instanceId);
        if (accountToken == null) {
            if (CommonHelper.isIePinyinToken(clientId)) {
                return verifySigByShToken(passportId, clientId, instanceId, timestamp, clientSecret, sig);
            } else {
                return false;
            }
        }
        if (!isValidToken(accountToken.getRefreshValidTime())) {
            return false;
        }
        String refreshToken = accountToken.getRefreshToken();
        return equalSig(passportId, clientId, refreshToken, timestamp, clientSecret, sig);
    }

    //通过sh token校验sig
    private boolean verifySigByShToken(String passportId, int clientId, String instanceId, String timestamp, String clientSecret, String sig) throws Exception {
        return (equalSig(passportId, clientId, shTokenService.queryRefreshToken(passportId, clientId, instanceId), timestamp, clientSecret, sig) ||
                equalSig(passportId, clientId, shTokenService.queryOldRefreshToken(passportId, clientId, instanceId), timestamp, clientSecret, sig));

    }

    private boolean equalSig(String passportId, int clientId, String refreshToken, String timestamp, String clientSecret, String sig) throws Exception {
        String sigString = passportId + clientId + refreshToken + timestamp + clientSecret;
        String actualSig = Coder.encryptMD5(sigString);
        return actualSig.equalsIgnoreCase(sig);
    }

    /**
     * 验证Token是否失效
     */
    private boolean isValidToken(long tokenValidTime) {
        long currentTime = System.currentTimeMillis();
        return tokenValidTime > currentTime;
    }

}