package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.LoginManager;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.CheckUserApiParams;
import com.sogou.upd.passport.manager.form.WebLoginParams;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.OperateTimesService;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * User: mayan Date: 13-4-15 Time: 下午4:34 To change this template use File | Settings | File Templates.
 */
@Component
public class LoginManagerImpl implements LoginManager {

    private static final Logger logger = LoggerFactory.getLogger(LoginManagerImpl.class);
    private static final String USERNAME_PWD_ERROR = ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_PWD_ERROR;
    public static Set<String> needCaptchaSet = new HashSet<String>();

    static {
        //目前使用sogou验证码的应用有passport、浏览器4.2及以上版本、彩票
        needCaptchaSet.add(String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID));
        needCaptchaSet.add(String.valueOf(CommonConstant.PC_CLIENTID));
        needCaptchaSet.add(String.valueOf(CommonConstant.CAIPIAO_CLIENTID));
    }

    @Autowired
    private RegisterApiManager sgRegisterApiManager;
    @Autowired
    private AccountService accountService;
    @Autowired
    private OperateTimesService operateTimesService;
    @Autowired
    private LoginApiManager loginApiManager;
    @Autowired
    private SecureManager secureManager;
    @Autowired
    private CommonManager commonManager;

    @Override
    public Result checkUser(String username, int clientId) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            //sohu域账号去sohu检查用户名，否则走sogou检查用户名的逻辑
            CheckUserApiParams checkUserApiParams = buildProxyApiParams(username, clientId);
            String passportId = commonManager.getPassportIdByUsername(username);
            if (Strings.isNullOrEmpty(passportId)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
                return result;
            }
            if (AccountDomainEnum.SOHU.equals(AccountDomainEnum.getAccountDomain(passportId))) {
                //搜狐账号登录时不用检查用户名是否存在，尽量减少对搜狐接口的依赖；如有问题再开启调用
//                result = proxyRegisterApiManager.checkUser(checkUserApiParams);
                result.setSuccess(false); //表示账号已存在
                result.setCode(ErrorUtil.ERR_CODE_USER_ID_EXIST);
            } else {
                result = sgRegisterApiManager.checkUser(checkUserApiParams);
            }
            buildLoginResult(result);
        } catch (ServiceException e) {
            logger.error("Check account is exists Exception, username:" + username, e);
            throw new ServiceException(e);
        }
        return result;
    }

    private Result buildLoginResult(Result result) {
        //注册的checkuser返回结果为false有可能是账号已存在，当提示账号已存在或已注册时，需要转成登录的结果
        if (!result.isSuccess() && (ErrorUtil.ERR_CODE_USER_ID_EXIST.equals(result.getCode()) || ErrorUtil.ERR_CODE_ACCOUNT_REGED.equals(result.getCode()))) {
            result.setSuccess(true);
            return result;
        }
        //注册checkuser返回结果为true，表示账号不存在，需要转成登录的结果
        if (result.isSuccess()) {
            result.setSuccess(false);
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
        }
        return result;
    }

    private CheckUserApiParams buildProxyApiParams(String username, int clientId) {
        CheckUserApiParams checkUserApiParams = new CheckUserApiParams();
        checkUserApiParams.setUserid(username);
        checkUserApiParams.setClient_id(clientId);
        return checkUserApiParams;
    }

    @Override
    public Result accountLogin(WebLoginParams loginParameters, String ip, String scheme) {
        Result result = new APIResultSupport(false);
        String username = loginParameters.getUsername();
        String password = loginParameters.getPassword();
        String pwdMD5 = password;
        if (loginParameters.getPwdtype() == CommonConstant.PWD_TYPE_EXPRESS) {
            pwdMD5 = DigestUtils.md5Hex(password.getBytes());
        }
        String passportId = username;
        try {
            //校验验证码
            if (needCaptchaCheck(loginParameters.getClient_id(), username, ip)) {
                String captchaCode = loginParameters.getCaptcha();
                String token = loginParameters.getToken();
                if (!accountService.checkCaptchaCodeIsVaild(token, captchaCode)) {
                    logger.debug("[accountLogin captchaCode wrong warn]:username=" + username + ", ip=" + ip + ", token=" + token + ", captchaCode=" + captchaCode);
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
                    return result;
                }
            }
            result = authUser(username, ip, pwdMD5);
        } catch (Exception e) {
            logger.error("accountLogin fail,passportId:" + passportId, e);
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_LOGIN_FAILED);
            return result;
        }
        return result;
    }

    @Override
    public Result authUser(String username, String ip, String pwdMD5) {
        //校验username是否在账户黑名单中
        Result result = new APIResultSupport(false);
        if (isLoginUserInBlackList(username, ip)) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST);
            return result;
        }
        String passportId = getIndividPassportIdByUsername(username);
        //封装参数
        AuthUserApiParams authUserApiParams = new AuthUserApiParams();
        authUserApiParams.setUserid(passportId);
        authUserApiParams.setIp(ip);
        authUserApiParams.setPassword(pwdMD5);
        authUserApiParams.setClient_id(SHPPUrlConstant.APP_ID);
        result = loginApiManager.webAuthUser(authUserApiParams);
        return result;
    }

    @Override
    public boolean needCaptchaCheck(String client_id, String username, String ip) {
        return (needCaptchaSet.contains(client_id) && operateTimesService.loginFailedTimesNeedCaptcha(username, ip));
    }

    @Override
    public boolean isLoginUserInBlackList(final String username, final String ip) {
        //校验username是否在账户黑名单中
        if (operateTimesService.isUserInBlackList(username, ip) || operateTimesService.isLoginTimesForBlackList(username, ip)) {
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
        operateTimesService.incLoginTimes(username, ip, true);
        //用户登陆记录
        secureManager.logActionRecord(passportId, clientId, AccountModuleEnum.LOGIN, ip, null);
    }

    @Override
    public void doAfterLoginFailed(final String username, final String ip, String errCode) {
        if (USERNAME_PWD_ERROR.equals(errCode)) {
            operateTimesService.incLoginTimes(username, ip, false);
        }
    }

    @Override
    public String getIndividPassportIdByUsername(String username) {
        AccountDomainEnum accountDomainEnum = AccountDomainEnum.getAccountDomain(username);
        if (AccountDomainEnum.INDIVID.equals(accountDomainEnum)) {
            return (username + CommonConstant.SOGOU_SUFFIX);
        }
        return username;
    }
}


