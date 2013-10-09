package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.LoginManager;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieUrlApiParams;
import com.sogou.upd.passport.manager.form.WebLoginParams;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.OperateTimesService;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public Result accountLogin(WebLoginParams loginParameters, String ip, String scheme) {
        Result result = new APIResultSupport(false);
        String username = loginParameters.getUsername();
        String password = loginParameters.getPassword();
        String pwdMD5 = DigestUtils.md5Hex(password.getBytes());
        String passportId = username;
        try {
            //校验验证码
            if (needCaptchaCheck(loginParameters.getClient_id(), username, ip)) {
                String captchaCode = loginParameters.getCaptcha();
                String token = loginParameters.getToken();
                if (!accountService.checkCaptchaCodeIsVaild(token, captchaCode)) {
                    logger.info("[accountLogin captchaCode wrong warn]:username=" + username + ", ip=" + ip + ", token=" + token + ", captchaCode=" + captchaCode);
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
                    return result;
                }
            }
            //校验username是否在账户黑名单中
            if(isLoginUserInBlackList(username,ip)){
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST);
                return result;
            }

            //默认是sogou.com
            AccountDomainEnum accountDomainEnum = AccountDomainEnum.getAccountDomain(username);
            if (AccountDomainEnum.INDIVID.equals(accountDomainEnum)) {
                passportId = passportId + "@sogou.com";
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

                //设置来源
                String ru = loginParameters.getRu();
                if (Strings.isNullOrEmpty(ru)) {
                    ru = scheme + LOGIN_INDEX_URLSTR;
                }

                result = commonManager.createCookieUrl(result, passportId, loginParameters.getAutoLogin());

                result.setDefaultModel(CommonConstant.RESPONSE_RU, ru);
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
    public boolean isLoginUserInBlackList(final String username, final String ip) {
        //校验username是否在账户黑名单中
        if (operateTimesService.checkLoginUserInBlackList(username,ip)) {
            //是否在白名单中
            if (!operateTimesService.checkLoginUserInWhiteList(username, ip)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void doAfterLoginSuccess(final String username, final String ip, final String passportId, final int clientId) {
        //记录登陆次数
        operateTimesService.incLoginTimes(username, ip,true);
        //用户登陆记录
        secureManager.logActionRecord(passportId, clientId, AccountModuleEnum.LOGIN, ip, null);
    }

    @Override
    public void doAfterLoginFailed(final String username, final String ip) {
        operateTimesService.incLoginTimes(username, ip,false);
    }
}


