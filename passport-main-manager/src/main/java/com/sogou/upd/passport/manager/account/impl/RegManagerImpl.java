package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.CommonHelper;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.parameter.AccountStatusEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.RegManager;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.*;
import com.sogou.upd.passport.manager.form.ActiveEmailParams;
import com.sogou.upd.passport.manager.form.WebRegisterParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.service.account.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 注册管理
 * User: mayan
 * Date: 13-4-15 Time: 下午4:43
 */
@Component
public class RegManagerImpl implements RegManager {

    @Autowired
    private AccountService accountService;
    @Autowired
    private EmailSenderService emailSenderService;
    @Autowired
    private RegisterApiManager sgRegisterApiManager;
    @Autowired
    private RegisterApiManager proxyRegisterApiManager;
    @Autowired
    private OperateTimesService operateTimesService;
    @Autowired
    private SnamePassportMappingService snamePassportMappingService;

    private static final Logger logger = LoggerFactory.getLogger(RegManagerImpl.class);

    @Override
    public Result webRegister(WebRegisterParams regParams, String ip) throws Exception {

        Result result = new APIResultSupport(false);
        String username;
        try {
            int clientId = Integer.parseInt(regParams.getClient_id());
            username = regParams.getUsername().trim().toLowerCase();
            String password = regParams.getPassword();
            String captcha = regParams.getCaptcha();
            String ru = regParams.getRu();

            boolean isSogou = false;//外域还是个性账号
            //判断是否是个性账号
            if (username.indexOf("@") == -1) {
                //判断是否是手机号注册
                if (!PhoneUtil.verifyPhoneNumberFormat(username)) {
                    username = username.toLowerCase() + "@sogou.com";  //个性账号不区分大小写
                    isSogou = true;
                }
            } else {
                int index = username.indexOf("@");
                username = username.substring(0, index) + username.substring(index, username.length()).toLowerCase(); //外域邮箱只处理@后面那一串为小写
            }
            //判断注册账号类型，sogou用户还是手机用户
            AccountDomainEnum emailType = AccountDomainEnum.getAccountDomain(username);

            switch (emailType) {
                case SOGOU://个性账号直接注册
                case OTHER://外域邮件注册
                case INDIVID:
                    String token = regParams.getToken();
                    //判断验证码
                    if (!accountService.checkCaptchaCode(token, captcha)) {
                        logger.debug("[webRegister captchaCode wrong warn]:username=" + username + ", ip=" + ip + ", token=" + token + ", captchaCode=" + captcha);
                        result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
                        return result;
                    }
                    //发出激活信以后跳转页面，ru为空跳到sogou激活成功页面
                    if (Strings.isNullOrEmpty(ru)) {
                        ru = isSogou ? CommonConstant.LOGIN_INDEX_URL : CommonConstant.EMAIL_REG_VERIFY_URL;
                    }
                    RegEmailApiParams regEmailApiParams = buildRegMailProxyApiParams(username, password, ip,
                            clientId, ru);
                    result = sgRegisterApiManager.regMailUser(regEmailApiParams);
                    break;
                case PHONE://手机号
                    RegMobileCaptchaApiParams regMobileCaptchaApiParams = buildProxyApiParams(username, password, captcha, clientId, ip);
                    result = sgRegisterApiManager.regMobileCaptchaUser(regMobileCaptchaApiParams);
                    break;
            }
        } catch (ServiceException e) {
            logger.error("webRegister fail,passportId:" + regParams.getUsername(), e);
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
            return result;
        }
        if (result.isSuccess()) {
            result.getModels().put("username", username);            //判断是否是外域邮箱注册 外域邮箱激活以后种cookie
        } else {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
        }
        return result;
    }

    private RegEmailApiParams buildRegMailProxyApiParams(String username, String password, String ip, int clientId, String ru) {
        return new RegEmailApiParams(username, password, ip, clientId, ru);
    }


    private RegMobileCaptchaApiParams buildProxyApiParams(String mobile, String password, String captcha, int clientId, String ip) {
        RegMobileCaptchaApiParams regMobileCaptchaApiParams = new RegMobileCaptchaApiParams();
        regMobileCaptchaApiParams.setMobile(mobile);
        regMobileCaptchaApiParams.setPassword(password);
        regMobileCaptchaApiParams.setCaptcha(captcha);
        regMobileCaptchaApiParams.setClient_id(clientId);
        regMobileCaptchaApiParams.setIp(ip);
        return regMobileCaptchaApiParams;
    }

    @Override
    public Result activeEmail(ActiveEmailParams activeParams, String ip) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            String username = activeParams.getPassport_id();
            String token = activeParams.getToken();
            int clientId = Integer.parseInt(activeParams.getClient_id());
            //验证token是否正确
            boolean isSuccess = accountService.checkToken(username, token, clientId);

            if (isSuccess) {
                //激活成功
                Account account = accountService.initialWebAccount(username, ip);
                if (account != null) {
                    result.setDefaultModel(account);
                    result.setDefaultModel("userid", account.getPassportId());
                    result.setSuccess(true);
                    result.setMessage("激活成功！");
                    return result;
                } else {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
                    return result;
                }
            } else {
                //激活失败
                Account account = accountService.queryAccountByPassportId(username);
                if (account != null) {
                    if (Integer.parseInt(account.getFlag()) == AccountStatusEnum.REGULAR.getValue()) {
                        //已经激活，无需再次激活
                        result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_ALREADY_ACTIVED_FAILED);
                        return result;
                    } else {
                        result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_ACTIVED_URL_FAILED);
                        return result;
                    }
                } else {
                    //无此账号
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
                    return result;
                }
            }
        } catch (ServiceException e) {
            logger.error("activeEmail fail, passportId:" + activeParams.getPassport_id() + " clientId:" + activeParams.getClient_id(), e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
            return result;
        }
    }

    @Override
    public Result resendActiveMail(ResendActiveMailParams resendActiveMailParams) {
        Result result = new APIResultSupport(false);
        try {
            String username = resendActiveMailParams.getUsername();
            int clientId = Integer.parseInt(resendActiveMailParams.getClient_id());
            //检测重发激活邮件次数是否已达上限
            boolean checkSendLimited = emailSenderService.checkLimitForSendEmail(null, clientId, AccountModuleEnum.REGISTER, username);
            if (!checkSendLimited) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SENDEMAIL_LIMITED);
                return result;
            }
            boolean isSendSuccess = accountService.sendActiveEmail(username, null, clientId, null, CommonConstant.EMAIL_REG_VERIFY_URL);
            if (isSendSuccess) {
                if (emailSenderService.incLimitForSendEmail(null, clientId, AccountModuleEnum.REGISTER, username)) {
                    result.setSuccess(true);
                    result.setMessage("重新发送激活邮件成功，请立即激活账户！");
                    result.setCode("0");
                }
            } else {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_RESEND_ACTIVED_FAILED);
            }
        } catch (Exception e) {
            logger.error("Resend Active Mail Fail:", e);
            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
        }
        return result;
    }

    @Override
    public Map<String, Object> getCaptchaCode(String code) {
        return accountService.getCaptchaCode(code);
    }

    @Override
    public Result isAccountExists(String username, int clientId) throws Exception {
        Result result;
        try {
            CheckUserApiParams checkUserApiParams = buildProxyApiParams(username);
            result = sgRegisterApiManager.checkUser(checkUserApiParams);
            if (!result.isSuccess() && CommonHelper.isExplorerToken(clientId)) {
                result = isSohuplusUser(username);
            }
        } catch (ServiceException e) {
            logger.error("Check account is exists Exception, username:" + username, e);
            throw new Exception(e);
        }
        return result;
    }

    @Override
    public Result isAccountNotExists(String username, int clientId) throws Exception {
        Result result = isAccountExists(username, clientId);
        if (result.isSuccess()) {
            //用户存在，则账号被占用，返回false
            result.setSuccess(false);
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_BINDED);
        } else {
            //用户不存在，则可以注册
            result.setSuccess(true);
            result.setCode("0");
            result.setMessage("账号未被占用，可以注册");
        }
        return result;
    }

    @Override
    public Result isSohuAccountExists(String username) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            CheckUserApiParams checkUserApiParams = buildProxyApiParams(username);
            BaseMobileApiParams params = new BaseMobileApiParams();
            params.setMobile(username);
            Result sohuNotExistResult = proxyRegisterApiManager.checkUser(checkUserApiParams);
            if(!sohuNotExistResult.isSuccess()){
                result.setSuccess(true);
                result.setDefaultModel("userid",sohuNotExistResult.getModels().get("userid"));
            }
        } catch (ServiceException e) {
            logger.error("Check account is exists Exception, username:" + username, e);
            throw new Exception(e);
        }
        return result;
    }

    @Override
    public Result isSohuOrSogouAccountExists(String username, int clientId) throws Exception {
        Result result;
        try {
            if (ManagerHelper.isInvokeProxyApi(username)) {
                result = isSohuAccountExists(username);
            } else {
                result = isAccountExists(username, clientId);
            }
        } catch (ServiceException e) {
            logger.error("Check account is exists Exception, username:" + username, e);
            throw new Exception(e);
        }
        return result;
    }

    @Override
    public Result checkRegInBlackListByIpForInternal(String ip,int clientId) throws Exception {
        Result result = new APIResultSupport(false);
        //如果在黑名单，也在白名单，允许注册；如果在黑名单不在白名单，不允许注册
        if (operateTimesService.checkRegInBlackListForInternal(ip,clientId)) {
            if (!operateTimesService.checkRegInWhiteList(ip)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST);
                return result;
            }
        }
        result.setSuccess(true);
        return result;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Result checkRegInBlackList(String ip, String cookieStr) throws Exception {

        Result result = new APIResultSupport(false);
        try {
            //检查账号是否在黑名单中
            if (operateTimesService.checkRegInBlackList(ip, cookieStr)) {
                //检查账号是否在白名单中
                if (!operateTimesService.checkRegInWhiteList(ip)) {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST);
                    return result;
                }
            }
        } catch (ServiceException e) {
            logger.error("register checkRegInBlackList Exception", e);
            throw new Exception(e);
        }
        result.setSuccess(true);
        return result;
    }

    @Override
    public void incRegTimes(String ip, String cookieStr) throws Exception {
        try {
            operateTimesService.incRegTimes(ip, cookieStr);
        } catch (ServiceException e) {
            logger.error("register incRegTimes Exception", e);
            throw new Exception(e);
        }
    }

    @Override
    public Result checkMobileSendSMSInBlackList(String ip) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            //检查ip是否在黑名单中
            if (operateTimesService.isMobileSendSMSInBlackList(ip)) {
                //检查ip是否在白名单中
                if (!operateTimesService.checkRegInWhiteList(ip)) {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST);
                    return result;
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
    public void incSendTimesForMobile(String ip) throws Exception {
        try {
            operateTimesService.incSendTimesForMobile(ip);
        } catch (ServiceException e) {
            logger.error("register incSendTimesForMobile Exception", e);
            throw new Exception(e);
        }
    }

    /*
     * client=1044的username为个性域名或手机号
     * 都有可能是sohuplus的账号，需要判断sohuplus映射表
     * 如果username包含@，则取@前面的
     */
    private Result isSohuplusUser(String username) {
        Result result = new APIResultSupport(false);
        if (username.contains("@")) {
            username = username.substring(0, username.indexOf("@"));
        }
        String sohuplus_passportId = snamePassportMappingService.queryPassportIdBySnameOrPhone(username);
        if (Strings.isNullOrEmpty(sohuplus_passportId)) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            return result;
        } else {
            result.setSuccess(true);
        }
        return result;
    }

    private CheckUserApiParams buildProxyApiParams(String username) {
        CheckUserApiParams checkUserApiParams = new CheckUserApiParams();
        checkUserApiParams.setUserid(username);
        return checkUserApiParams;
    }
}
