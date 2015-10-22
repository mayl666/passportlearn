package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.TokenRedisUtils;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.LoginManager;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.AuthUserApiParams;
import com.sogou.upd.passport.manager.form.WebLoginParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.WebRoamDO;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.OperateTimesService;
import com.sogou.upd.passport.service.account.TokenService;
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
    public static String QUICKLOGIN_MODULE = "quicklogin";

    static {
        //目前使用sogou验证码的应用有passport、浏览器4.2及以上版本、彩票
        needCaptchaSet.add(String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID));
        needCaptchaSet.add(String.valueOf(CommonConstant.PC_CLIENTID));
        needCaptchaSet.add(String.valueOf(CommonConstant.CAIPIAO_CLIENTID));
        needCaptchaSet.add(String.valueOf(CommonConstant.TEEMO_CLIENTID));
    }

    @Autowired
    private RegisterApiManager registerApiManager;
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
    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenRedisUtils tokenRedisUtils;


    @Override
    public Result checkUser(String username, int clientId) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            String passportId = commonManager.getPassportIdByUsername(username);
            if (Strings.isNullOrEmpty(passportId)) {
                result.setCode(ErrorUtil.INVALID_ACCOUNT);
                return result;
            }
            if (AccountDomainEnum.SOHU.equals(AccountDomainEnum.getAccountDomain(passportId))) {
                //检查是否是输入法泄露账号
                try {
                    if (registerApiManager.isSogouLeakList(passportId, null)) {
                        result.setSuccess(false);
                        result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_LEAKLIST_RISK);
                        return result;
                    }
                } catch (Exception e) {
                    logger.error("sohu leak passportid search  error : " + username);
                }

                result.setSuccess(false); //表示账号已存在
                result.setCode(ErrorUtil.ERR_CODE_USER_ID_EXIST);
            } else {
                result = registerApiManager.checkAccountExist(username, clientId);
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

        //风险账号处理
        if (!result.isSuccess() && ErrorUtil.ERR_CODE_ACCOUNT_LEAKLIST_RISK.equals(result.getCode())) {
            result.setSuccess(false);
            return result;
        }

        //注册checkuser返回结果为true，表示账号不存在，需要转成登录的结果
        if (result.isSuccess()) {
            result.setSuccess(false);
            result.setCode(ErrorUtil.INVALID_ACCOUNT);
        }
        return result;
    }

    @Override
    public Result accountLogin(WebLoginParams loginParameters, String ip, String scheme) {
        Result result = new APIResultSupport(false);
        String module = loginParameters.getModule();
        if (!Strings.isNullOrEmpty(module) && module.equals(QUICKLOGIN_MODULE)) {
            // 验证已登录标识的快速登录方式
            result = quickAuthUser(loginParameters.getKey(), ip);
        } else {
            // 出验证码的密码验登录方式
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
        }
        return result;
    }


    @Override
    public Result authUser(String username, String ip, String pwdMD5) {
        Result result = new APIResultSupport(false);
        String passportId = getIndividPassportIdByUsername(username);

        //校验username是否在账户黑名单中
        if (isLoginUserInBlackList(username, ip)) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST);
            return result;
        }

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
    public Result quickAuthUser(String key, String ip) {
        Result result = new APIResultSupport(false);
        if (isLoginUserInBlackList(null, ip)) {    //ip是否中了安全限制
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST);
            return result;
        }
        if (Strings.isNullOrEmpty(key)) {
            result.setCode(ErrorUtil.ERR_CODE_ROAM_INFO_NOT_EXIST);
            return result;
        }
        WebRoamDO webRoamDO = tokenService.getWebRoamDOByToken(key);
        if (webRoamDO != null) {
            String roamPassportId = webRoamDO.getPassportId();
            Account account = accountService.queryNormalAccount(roamPassportId);
            String uniqname = "";
            if (account != null) {
                uniqname = account.getUniqname();
            } else if (AccountDomainEnum.SOHU == AccountDomainEnum.getAccountDomain(roamPassportId)) {
                accountService.initSOHUAccount(roamPassportId, ip);
            } else {
                result.setCode(ErrorUtil.INVALID_ACCOUNT);
                return result;
            }
            result.setSuccess(true);
            result.setMessage("登录成功");
            result.setDefaultModel("userid", roamPassportId);
            result.setDefaultModel("uniqName", StringUtil.defaultIfEmpty(uniqname, ""));
            return result;
        } else {
            //漫游用户信息取不到 返回对应状态码的Result
            result.setCode(ErrorUtil.ERR_CODE_ROAM_INFO_NOT_EXIST);
            return result;
        }
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
        if (USERNAME_PWD_ERROR.equals(errCode) || ErrorUtil.ERROR_CODE_SMS_CODE_ERROR.equalsIgnoreCase(errCode)) {
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


