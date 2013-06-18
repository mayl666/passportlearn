package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.PasswordTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.LoginManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieUrlApiParams;
import com.sogou.upd.passport.manager.form.WebLoginParameters;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.oauth2.authzserver.request.OAuthTokenASRequest;
import com.sogou.upd.passport.oauth2.common.types.GrantTypeEnum;
import com.sogou.upd.passport.service.account.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * User: mayan Date: 13-4-15 Time: 下午4:34 To change this template use File | Settings | File Templates.
 */
@Component
public class LoginManagerImpl implements LoginManager {

    private static final Logger logger = LoggerFactory.getLogger(LoginManagerImpl.class);

    private static final String SOHU_LOGIN_INDEX_URL = "https://passport.sohu.com";
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
                    Account account =  (Account) result.getDefaultModel();
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
    public Result accountLogin(WebLoginParameters loginParameters, String ip,String scheme) {
        Result result = new APIResultSupport(false);
        String username = loginParameters.getUsername();
        String password = loginParameters.getPassword();
        String pwdMD5 = DigestUtils.md5Hex(password.getBytes());
        String passportId = username;
        boolean needCaptcha = needCaptchaCheck(loginParameters.getClient_id());
        try {
            AccountDomainEnum accountDomainEnum =  AccountDomainEnum.getAccountDomain(username);
            //设置来源
            String ru =  loginParameters.getRu();
            if(Strings.isNullOrEmpty(ru)){
                if (AccountDomainEnum.SOHU.equals(accountDomainEnum)) {
                    loginParameters.setRu(SOHU_LOGIN_INDEX_URL);
                }else{
                    loginParameters.setRu(scheme+LOGIN_INDEX_URLSTR);
                }
            }

            //默认是sogou.com
            if (AccountDomainEnum.UNKNOWN.equals(accountDomainEnum)) {
                passportId = passportId+"@sogou.com";
            }

            //校验验证码
            if(needCaptcha) {
                if (operateTimesService.loginFailedTimesNeedCaptcha(passportId, ip)) {
                    String captchaCode = loginParameters.getCaptcha();
                    String token = loginParameters.getToken();
                    if (!this.checkCaptcha(passportId, captchaCode, token)) {
                        result.setDefaultModel("needCaptcha", true);
                        result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
                        return result;
                    }
                }
            }

            //校验是否在账户黑名单或者IP黑名单之中
            if (operateTimesService.checkLoginUserInBlackList(passportId, ip)){
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
            if (result.isSuccess()){
                operateTimesService.incLoginSuccessTimes(passportId,ip);
                // 种sohu域cookie
                result=commonManager.createCookieUrl(result,passportId,scheme,loginParameters.getAutoLogin());
                result.setDefaultModel("ru",loginParameters.getRu());

            } else {
                operateTimesService.incLoginFailedTimes(passportId, ip);
                //3次失败需要输入验证码
                if(needCaptcha) {
                    if (operateTimesService.loginFailedTimesNeedCaptcha(passportId, ip)){
                        result.setDefaultModel("needCaptcha", true);
                    }
                }
            }
        } catch (Exception e) {
            operateTimesService.incLoginFailedTimes(passportId,ip);
            logger.error("accountLogin fail,passportId:" + loginParameters.getUsername(), e);
            //3次失败需要输入验证码
            if (needCaptcha) {
                if (operateTimesService.loginFailedTimesNeedCaptcha(passportId, ip)) {
                    result.setDefaultModel("needCaptcha", true);
                }
            }
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_LOGIN_FAILED);
            return result;
        }
        return result;
    }

    private boolean needCaptchaCheck(String client_id) {
        if (Integer.parseInt(client_id) == SHPPUrlConstant.APP_ID) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkCaptcha(String passportId, String captcha, String token) {
        //校验验证码
        if (!accountService.checkCaptchaCodeIsVaild(token, captcha)) {
            return false;
        }
        return true;
    }

    /**
     * 用户在登陆的时候是否需要输入验证码
     * 目前的策略
     * 1.连续3次输入密码错误
     *
     * @param passportId
     * @return
     */
    @Override
    public boolean loginNeedCaptcha(String passportId, String ip) {
        boolean loginFailed = accountService.loginFailedNumNeedCaptcha(passportId, ip);
        return loginFailed;
    }
}
