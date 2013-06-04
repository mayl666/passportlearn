package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.PasswordTypeEnum;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.AccountLoginManager;
import com.sogou.upd.passport.manager.form.WebLoginParameters;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.oauth2.authzserver.request.OAuthTokenASRequest;
import com.sogou.upd.passport.oauth2.common.types.GrantTypeEnum;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.AccountTokenService;
import com.sogou.upd.passport.service.account.MobilePassportMappingService;
import com.sogou.upd.passport.service.account.generator.PwdGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * User: mayan Date: 13-4-15 Time: 下午4:34 To change this template use File | Settings | File Templates.
 */
@Component
public class AccountLoginManagerImpl implements AccountLoginManager {

    private static final Logger logger = LoggerFactory.getLogger(AccountLoginManagerImpl.class);

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountTokenService accountTokenService;
    @Autowired
    private MobilePassportMappingService mobilePassportMappingService;

    @Override
    public Result authorize(OAuthTokenASRequest oauthRequest) {
        int clientId = oauthRequest.getClientId();
        String instanceId = oauthRequest.getInstanceId();

        try {
            // 檢查不同的grant types是否正確
            // TODO 消除if-else
            AccountToken renewAccountToken;
            if (GrantTypeEnum.PASSWORD.toString().equals(oauthRequest.getGrantType())) {
                String passportId = mobilePassportMappingService.queryPassportIdByUsername(oauthRequest.getUsername());
                if (Strings.isNullOrEmpty(passportId)) {
                    return Result.buildError(ErrorUtil.INVALID_ACCOUNT);
                }
                int pwdType = oauthRequest.getPwdType();
                boolean needMD5 = pwdType == PasswordTypeEnum.Plaintext.getValue() ? true : false;
                Account account = accountService
                        .verifyUserPwdVaild(passportId, oauthRequest.getPassword(), needMD5);
                if (account == null) {
                    return Result.buildError(ErrorUtil.USERNAME_PWD_MISMATCH);
                } else if (!account.isNormalAccount()) {
                    return Result.buildError(ErrorUtil.INVALID_ACCOUNT);
                } else {
                    // 为了安全每次登录生成新的token
                    renewAccountToken = accountTokenService.updateOrInsertAccountToken(account.getPassportId(), clientId, instanceId);
                }
            } else if (GrantTypeEnum.REFRESH_TOKEN.toString().equals(oauthRequest.getGrantType())) {
                String refreshToken = oauthRequest.getRefreshToken();
                AccountToken accountToken = accountTokenService.verifyRefreshToken(refreshToken, clientId, instanceId);
                if (accountToken == null) {
                    return Result.buildError(ErrorUtil.INVALID_REFRESH_TOKEN);
                } else {
                    String passportId = accountToken.getPassportId();
                    renewAccountToken = accountTokenService.updateOrInsertAccountToken(passportId, clientId, instanceId);
                }
            } else {
                return Result.buildError(ErrorUtil.UNSUPPORTED_GRANT_TYPE);
            }

            if (renewAccountToken != null) { // 登录成功
                Map<String, Object> mapResult = Maps.newHashMap();
                mapResult.put("access_token", renewAccountToken.getAccessToken());
                mapResult.put("expires_time", renewAccountToken.getAccessValidTime());
                mapResult.put("refresh_token", renewAccountToken.getRefreshToken());
                return Result.buildSuccess("success", "mapResult", mapResult);
            } else { // 登录失败，更新AccountToken表发生异常
                return Result.buildError(ErrorUtil.AUTHORIZE_FAIL);
            }
        } catch (ServiceException e) {
            logger.error("OAuth Authorize Fail:", e);
            return Result.buildError(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
    }

    @Override
    public Result accountLogin(WebLoginParameters loginParameters) {
      Result result = null;
      String username=null;
       try {
         username= loginParameters.getUsername();
         String password=loginParameters.getPassword();
         //TODO 校验是否在账户黑名单或者IP黑名单之中
         //校验验证码
         if (accountService.loginFailedNumNeedCaptcha(username)) {
           String captchaCode = loginParameters.getCaptcha();
           String token = loginParameters.getToken();
           if (!this.checkCaptcha(username, captchaCode,token)) {
             return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
           }
         }
         //判断登录用户类型
         AccountDomainEnum domainEnum=AccountDomainEnum.getAccountDomain(username);

         Account account =null;
         switch (domainEnum){
           case PHONE:
             String passportId = mobilePassportMappingService.queryPassportIdByUsername(username);
             //校验用户名和密码是否匹配
             account = accountService.queryAccountByPassportId(passportId);
             break;
           case SOHU:
             account=accountService.queryAccountByPassportId(username);
             break;
         }

         if (account != null) {
           String storedPwd=account.getPasswd();
           if (PwdGenerator.verify(password, false, storedPwd)){
             //todo 登录成功种cookie
             //写缓存

             return Result.buildSuccess("登录成功");

           }else {
             accountService.incLoginFailedNum(username);
             return Result.buildError(ErrorUtil.USERNAME_PWD_MISMATCH);
           }
         }else {
           return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
         }
       }catch (Exception e) {
         accountService.incLoginFailedNum(username);
         logger.error("accountLogin fail,passportId:" + loginParameters.getUsername(), e);
         return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_LOGIN_FAILED);
       }
    }

    private boolean checkCaptcha(String passportId, String captcha,String token) {
      //校验验证码
      if (!accountService.checkCaptchaCodeIsVaild(token,captcha)) {
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
    public boolean loginNeedCaptcha(String passportId) {
        boolean loginFailed = accountService.loginFailedNumNeedCaptcha(passportId);
        return loginFailed;
    }
}
