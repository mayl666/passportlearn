package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.parameter.PasswordTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.LoginManager;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.form.WebLoginParameters;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.oauth2.authzserver.request.OAuthTokenASRequest;
import com.sogou.upd.passport.oauth2.common.types.GrantTypeEnum;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.AccountTokenService;
import com.sogou.upd.passport.service.account.MobilePassportMappingService;
import com.sogou.upd.passport.service.account.OperateTimesService;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * User: mayan Date: 13-4-15 Time: 下午4:34 To change this template use File | Settings | File Templates.
 */
@Component
public class LoginManagerImpl implements LoginManager {

    private static final Logger logger = LoggerFactory.getLogger(LoginManagerImpl.class);
    private static final String LOGIN_INDEX_URLSTR = "://account.sogou.com";

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountTokenService accountTokenService;
    @Autowired
    private MobilePassportMappingService mobilePassportMappingService;
    @Autowired
    private OperateTimesService operateTimesService;

    @Autowired
    private LoginApiManager proxyLoginApiManager;
    @Autowired
    private LoginApiManager sgLoginApiManager;
    @Autowired
    private CommonManager commonManager;
    @Autowired
    private SecureManager secureManager;

    @Override
    public Result authorize(OAuthTokenASRequest oauthRequest) {
        Result result = new APIResultSupport(false);
        int clientId = oauthRequest.getClientId();
        String instanceId = oauthRequest.getInstanceId();

        try {
            // 檢查不同的grant types是否正確
            // TODO 消除if-else
            AccountToken renewAccountToken;
            if (GrantTypeEnum.PASSWORD.toString().equals(oauthRequest.getGrantType())) {
                String passportId = mobilePassportMappingService.queryPassportIdByUsername(oauthRequest.getUsername());
                if (Strings.isNullOrEmpty(passportId)) {
                    result.setCode(ErrorUtil.INVALID_ACCOUNT);
                    return result;
                }
                int pwdType = oauthRequest.getPwdType();
                boolean needMD5 = pwdType == PasswordTypeEnum.Plaintext.getValue() ? true : false;
                result = accountService
                        .verifyUserPwdVaild(passportId, oauthRequest.getPassword(), needMD5);
                if (!result.isSuccess()) {
                    return result;
                } else {
                    Account account = (Account) result.getDefaultModel();
                    result.setDefaultModel(null);
                    // 为了安全每次登录生成新的token
                    renewAccountToken = accountTokenService.updateOrInsertAccountToken(account.getPassportId(), clientId, instanceId);
                }
            } else if (GrantTypeEnum.REFRESH_TOKEN.toString().equals(oauthRequest.getGrantType())) {
                String refreshToken = oauthRequest.getRefreshToken();
                AccountToken accountToken = accountTokenService.verifyRefreshToken(refreshToken, clientId, instanceId);
                if (accountToken == null) {
                    result.setCode(ErrorUtil.INVALID_REFRESH_TOKEN);
                    return result;
                } else {
                    String passportId = accountToken.getPassportId();
                    renewAccountToken = accountTokenService.updateOrInsertAccountToken(passportId, clientId, instanceId);
                }
            } else {
                result.setCode(ErrorUtil.UNSUPPORTED_GRANT_TYPE);
                return result;
            }

            if (renewAccountToken != null) { // 登录成功
                Map<String, Object> mapResult = Maps.newHashMap();
                mapResult.put("access_token", renewAccountToken.getAccessToken());
                mapResult.put("expires_time", renewAccountToken.getAccessValidTime());
                mapResult.put("refresh_token", renewAccountToken.getRefreshToken());
                result.setSuccess(true);
                result.setModels(mapResult);
                return result;
            } else { // 登录失败，更新AccountToken表发生异常
                result.setCode(ErrorUtil.AUTHORIZE_FAIL);
                return result;
            }
        } catch (ServiceException e) {
            logger.error("OAuth Authorize Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    @Override
    public Result accountLogin(WebLoginParameters loginParameters, String ip, String scheme) {
        Result result = new APIResultSupport(false);
        String username = loginParameters.getUsername();
        String password = loginParameters.getPassword();
        String pwdMD5 = DigestUtils.md5Hex(password.getBytes());
        String passportId = username;
        boolean needCaptcha = needCaptchaCheck(loginParameters.getClient_id(), username, ip);
        try {
            //校验验证码
            if (needCaptcha) {
                String captchaCode = loginParameters.getCaptcha();
                String token = loginParameters.getToken();
                if (!accountService.checkCaptchaCodeIsVaild(token, captchaCode)) {
                    logger.info("[accountLogin captchaCode wrong warn]:username="+username+", ip="+ip+", token="+token+", captchaCode="+captchaCode);
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
                    return result;
                }
            }

            //默认是sogou.com
            AccountDomainEnum accountDomainEnum = AccountDomainEnum.getAccountDomain(username);
            if (AccountDomainEnum.INDIVID.equals(accountDomainEnum)) {
                passportId = passportId + "@sogou.com";
            }

            //校验username是否在账户黑名单中
            if (operateTimesService.checkLoginUserInBlackList(username)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST);
                return result;
            }

            //封装参数
            AuthUserApiParams authUserApiParams = new AuthUserApiParams();
            authUserApiParams.setUserid(passportId);
            authUserApiParams.setPassword(pwdMD5);
            authUserApiParams.setClient_id(SHPPUrlConstant.APP_ID);
            //根据域名判断是否代理，一期全部走代理
            if (ManagerHelper.isInvokeProxyApi(passportId)) {
                result = proxyLoginApiManager.webAuthUser(authUserApiParams);
            } else {
                result = sgLoginApiManager.webAuthUser(authUserApiParams);
            }

            //记录返回结果
            if (result.isSuccess()) {
                result = commonManager.createCookieUrl(result, passportId, loginParameters.getAutoLogin());
                //设置来源
                String ru = loginParameters.getRu();
                if (Strings.isNullOrEmpty(ru)) {
                    ru = scheme + LOGIN_INDEX_URLSTR;
                }
                result.setDefaultModel("ru", ru);
            }
        } catch (Exception e) {
            logger.error("accountLogin fail,passportId:" + passportId, e);
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_LOGIN_FAILED);
            return result;
        }
        return result;
    }

    @Override
    public boolean needCaptchaCheck(String client_id, String username, String ip) {
        if (Integer.parseInt(client_id) == SHPPUrlConstant.APP_ID) {
            if (operateTimesService.loginFailedTimesNeedCaptcha(username, ip)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void doAfterLoginSuccess(final String username, final String ip, final String passportId, final int clientId) {
        //记录登陆次数
        operateTimesService.incLoginSuccessTimes(username, ip);
        //用户登陆记录
        secureManager.logActionRecord(passportId, clientId, AccountModuleEnum.LOGIN, ip, null);
    }

    @Override
    public void doAfterLoginFailed(final String username, final String ip) {
        operateTimesService.incLoginFailedTimes(username, ip);
    }
}


