package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonHelper;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.AccountInfoManager;
import com.sogou.upd.passport.manager.account.PCAccountManager;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.api.connect.SessionServerManager;
import com.sogou.upd.passport.manager.form.ObtainAccountInfoParams;
import com.sogou.upd.passport.manager.form.PcAuthTokenParams;
import com.sogou.upd.passport.manager.form.PcPairTokenParams;
import com.sogou.upd.passport.manager.form.PcRefreshTokenParams;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.OperateTimesService;
import com.sogou.upd.passport.service.account.PCAccountTokenService;
import com.sogou.upd.passport.service.app.AppConfigService;
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
    private LoginApiManager loginApiManager;
    @Autowired
    private PCAccountTokenService pcAccountService;
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private OperateTimesService operateTimesService;
    @Autowired
    private SessionServerManager sessionServerManager;
    @Autowired
    private AccountInfoManager accountInfoManager;

    @Override
    public Result createPairToken(PcPairTokenParams pcTokenParams, String ip) {
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
                authUserApiParams.setNeedsgid(0);
                //根据域名判断是否代理，一期全部走代理
                Result result = new APIResultSupport(false);
                if (isLoginUserInBlackList(clientId, passportId, ip)) {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST);
                    return result;
                }
                result = loginApiManager.webAuthUser(authUserApiParams);
                if (!result.isSuccess()) {
                    doAuthUserFailed(clientId, passportId, ip, result.getCode());
                    return result;
                }
                passportId = (String) result.getModels().get("userid");
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
    public boolean isLoginUserInBlackList(final int clientId, final String username, final String ip) {
        if (CommonHelper.isIePinyinToken(clientId)) {
            //校验username是否在账户黑名单中
            if (operateTimesService.isUserInGetPairtokenBlackList(username, ip)) {
                //是否在白名单中
                if (!operateTimesService.checkLoginUserInWhiteList(username, ip)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void doAuthUserFailed(final int clientId, final String username, final String ip, String errCode) {
        if (CommonHelper.isIePinyinToken(clientId) && ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_PWD_ERROR.equals(errCode)) {
            operateTimesService.incGetPairTokenTimes(username, ip);
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
            if (!verifyRefreshToken(passportId, clientId, instanceId, refreshToken)) {
                result.setCode(ErrorUtil.ERR_REFRESH_TOKEN);
                return result;
            }
            if (CommonHelper.isExplorerToken(clientId)) {
                pcAccountService.saveOldRefreshToken(passportId, instanceId, appConfig, refreshToken);
            }
            return updateAccountToken(passportId, instanceId, appConfig);
        } catch (Exception e) {
            logger.error("authRefreshToken fail", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    @Override
    public Result authToken(PcAuthTokenParams authPcTokenParams) {
        Result result = new APIResultSupport(true);
        try {
            //验证accessToken
            int clientId = Integer.parseInt(authPcTokenParams.getAppid());
            String passportId = authPcTokenParams.getUserid();
            String instanceId = authPcTokenParams.getTs();
            if (!pcAccountService.verifyAccessToken(passportId, clientId, instanceId, authPcTokenParams.getToken())) {
                result.setSuccess(false);
                result.setCode(ErrorUtil.ERR_ACCESS_TOKEN);
            }
        } catch (Exception e) {
            logger.error("authToken fail", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
        return result;
    }

    @Override
    public boolean verifyRefreshToken(String passportId, int clientId, String instanceId, String refreshToken) {
        try {
            if (CommonHelper.isExplorerToken(clientId)) {
                return (pcAccountService.verifyRefreshToken(passportId, clientId, instanceId, refreshToken) ||
                        pcAccountService.verifyPCOldRefreshToken(passportId, clientId, instanceId, refreshToken));
            } else if (CommonHelper.isPinyinMACToken(clientId)) {
                return (pcAccountService.verifyRefreshToken(passportId, clientId, instanceId, refreshToken));
            } else {
                return pcAccountService.verifyRefreshToken(passportId, clientId, instanceId, refreshToken);
            }
        } catch (Exception e) {
            logger.error("verifyRefreshToken fail", e);
            return false;
        }
    }

    @Override
    public Result swapRefreshToken(String passportId, int clientId, String instanceId, String refreshToken) {
        Result result = new APIResultSupport(false);
        try {
            //验证refreshtoken
            if (pcAccountService.verifyRefreshToken(passportId, clientId, instanceId, refreshToken) ||
                    pcAccountService.verifyPCOldRefreshToken(passportId, clientId, instanceId, refreshToken)) {
                //生成sgid
                Result sessionResult = sessionServerManager.createSession(passportId);
                if (sessionResult.isSuccess()) {
                    String sgid = (String) sessionResult.getModels().get(LoginConstant.COOKIE_SGID);
                    result.setDefaultModel(LoginConstant.COOKIE_SGID, sgid);
                    result.setSuccess(true);
                } else {
                    result.setCode(ErrorUtil.ERR_CODE_CREATE_SGID_FAILED);
                    return result;
                }
                //获取用户信息
                ObtainAccountInfoParams params = new ObtainAccountInfoParams(String.valueOf(clientId), passportId, "uniqname,avatarurl");
                Result accountInfoResult = accountInfoManager.getUserInfo(params);
                if (accountInfoResult.isSuccess()) {
                    String uniqname = (String) accountInfoResult.getModels().get("uniqname");
                    String avatarurl = (String) accountInfoResult.getModels().get("avatarurl");
                    result.setDefaultModel("avatarurl", Strings.isNullOrEmpty(avatarurl) ? "" : avatarurl);
                    result.setDefaultModel("uniqname", Strings.isNullOrEmpty(uniqname) ? "" : uniqname);
                }
            } else {
                result.setCode(ErrorUtil.ERR_REFRESH_TOKEN);
            }
        } catch (Exception e) {
            logger.error("swap refreshToken fail,passportId:{},refreshtoken:{},instanceId:{},clientId:{}",
                    passportId, refreshToken, instanceId, clientId, e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    @Override
    public String getSig(String passportId, int clientId, String refresh_token, String timestamp) throws Exception {
        AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
        if (appConfig == null) {
            return null;
        }
        return getSig(passportId, clientId, refresh_token, timestamp, appConfig.getClientSecret());
    }

    @Override
    public Result createAccountToken(String passportId, String instanceId, int clientId) {
        Result finalResult = new APIResultSupport(false);
        try {
            AppConfig appConfig = appConfigService.queryAppConfigByClientId(clientId);
            if (appConfig == null) {
                finalResult.setCode(ErrorUtil.INVALID_CLIENTID);
                return finalResult;
            }
            return initialAccountToken(passportId, instanceId, appConfig);
        } catch (ServiceException e) {
            logger.error("createAccountToken fail", e);
            finalResult.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return finalResult;
        }
    }

    private Result initialAccountToken(String passportId, String instanceId, AppConfig appConfig) {
        Result finalResult = new APIResultSupport(false);
        AccountToken accountToken = pcAccountService.initialAccountToken(passportId, instanceId, appConfig);
        if (accountToken != null) {
            finalResult.setSuccess(true);
            finalResult.setDefaultModel(accountToken);
        } else {
            finalResult.setCode(ErrorUtil.CREATE_TOKEN_FAIL);
        }
        return finalResult;
    }

    @Override
    public Result updateAccountToken(String passportId, String instanceId, AppConfig appConfig) {
        Result finalResult = new APIResultSupport(false);
        AccountToken accountToken = pcAccountService.updateAccountToken(passportId, instanceId, appConfig);
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
        boolean isPCTokenSig = verifySigByPCToken(passportId, clientId, instanceId, timestamp, clientSecret, sig);
        if (!isPCTokenSig) {
            if (CommonHelper.isExplorerToken(clientId)) {
                return (verifySigByPCOldToken(passportId, clientId, instanceId, timestamp, clientSecret, sig));
            }
        }
        return isPCTokenSig;
    }

    //通过sh token校验sig
    private boolean verifySigByPCToken(String passportId, int clientId, String instanceId, String timestamp, String clientSecret, String sig) throws Exception {
        AccountToken accountToken = pcAccountService.queryAccountToken(passportId, clientId, instanceId);
        if (accountToken == null) {
            return false;
        }
        if (!isValidToken(accountToken.getRefreshValidTime())) {
            return false;
        }
        String refreshToken = accountToken.getRefreshToken();
        return isEqualSig(passportId, clientId, refreshToken, timestamp, clientSecret, sig);
    }

    private boolean verifySigByPCOldToken(String passportId, int clientId, String instanceId, String timestamp, String clientSecret, String sig) throws Exception {
        String oldPCToken = pcAccountService.queryOldPCToken(passportId, clientId, instanceId);
        return isEqualSig(passportId, clientId, oldPCToken, timestamp, clientSecret, sig);
    }

    private boolean isEqualSig(String passportId, int clientId, String refreshToken, String timestamp, String clientSecret, String sig) throws Exception {
        String actualSig = getSig(passportId, clientId, refreshToken, timestamp, clientSecret);
        return actualSig.equalsIgnoreCase(sig);
    }

    /**
     * 验证Token是否失效
     */
    private boolean isValidToken(long tokenValidTime) {
        long currentTime = System.currentTimeMillis();
        return tokenValidTime > currentTime;
    }

    private String getSig(String passportId, int clientId, String refreshToken, String timestamp, String clientSecret) throws Exception {
        return Coder.encryptMD5(passportId + clientId + refreshToken + timestamp + clientSecret);
    }
}
