package com.sogou.upd.passport.manager.account.impl;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.parameter.AccountStatusEnum;
import com.sogou.upd.passport.common.parameter.AccountTypeEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.common.utils.SMSUtil;
import com.sogou.upd.passport.exception.ServiceException;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.RegManager;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.RegEmailApiParams;
import com.sogou.upd.passport.manager.api.account.form.RegMobileApiParams;
import com.sogou.upd.passport.manager.api.account.form.ResendActiveMailParams;
import com.sogou.upd.passport.manager.api.connect.SessionServerManager;
import com.sogou.upd.passport.manager.form.ActiveEmailParams;
import com.sogou.upd.passport.manager.form.WebRegisterParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.model.account.AccountInfo;
import com.sogou.upd.passport.oauth2.common.types.ConnectTypeEnum;
import com.sogou.upd.passport.service.account.*;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
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
    private AccountInfoService accountInfoService;
    @Autowired
    private RegisterApiManager sgRegisterApiManager;
    @Autowired
    private CommonManager commonManager;
    @Autowired
    private SessionServerManager sessionServerManager;
    @Autowired
    private MobileCodeSenderService mobileCodeSenderService;
    @Autowired
    private OperateTimesService operateTimesService;

    private static final Logger logger = LoggerFactory.getLogger(RegManagerImpl.class);

    private static final String LOGIN_INDEX_URL = "https://account.sogou.com";


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
            //判断是否是个性账号
            if (username.indexOf("@") == -1) {
                //判断是否是手机号注册
                if (!PhoneUtil.verifyPhoneNumberFormat(username)) {
                    username = username + CommonConstant.SOGOU_SUFFIX;
                }
            } else {
                int index = username.indexOf("@");
                username = username.substring(0, index) + username.substring(index, username.length()).toLowerCase();
            }
            //判断注册账号类型，sogou用户还是手机用户
            AccountDomainEnum emailType = AccountDomainEnum.getAccountDomain(username);
            switch (emailType) {
                case SOGOU://个性账号直接注册
                case OTHER://外域邮件注册
                case INDIVID:
                    //校验验证码
                    result = checkCaptchaToken(regParams.getToken(), captcha);
                    if (!result.isSuccess()) {
                        return result;
                    }
                    ru = Strings.isNullOrEmpty(ru) ? LOGIN_INDEX_URL : ru;
                    RegEmailApiParams regEmailApiParams = buildRegMailProxyApiParams(username, password, ip,
                            clientId, ru);
                    result = sgRegisterApiManager.regMailUser(regEmailApiParams);
                    break;
                case PHONE://手机号
                    result = registerMobile(username, password, clientId, captcha, null);
                    if (result.isSuccess()) {
                        username = (String) result.getModels().get("userid");
                    }
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

    @Override
    public Result checkCaptchaToken(String token, String captcha) {
        Result result = new APIResultSupport(false);
        //判断验证码
        if (!accountService.checkCaptchaCode(token, captcha)) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
            return result;
        }
        result.setSuccess(true);
        return result;
    }

    @Override
    public Result fastRegisterPhone(String mobile, int clientId, String createip, String type) {
        Result result = new APIResultSupport(false);
        // 检查ip安全限制
        try {
            String passportId = commonManager.getPassportIdByUsername(mobile);
            if (!Strings.isNullOrEmpty(passportId)) { //手机号已经注册或绑定
                if (!Strings.isNullOrEmpty(type) && ConnectTypeEnum.WAP.toString().equals(type)) {
                    Result sessionResult = sessionServerManager.createSession(passportId);
                    if (!sessionResult.isSuccess()) {
                        result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
                        return result;
                    }
                    String sgid = (String) sessionResult.getModels().get(LoginConstant.COOKIE_SGID);
                    result.getModels().put(LoginConstant.COOKIE_SGID, sgid);
                }
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_BINDED);
                result.setDefaultModel("userid", passportId);
                return result;
            }
            //生成随机数密码
            String randomPwd = RandomStringUtils.randomNumeric(6);
            //注册手机号
            RegMobileApiParams regApiParams = new RegMobileApiParams(mobile, randomPwd, clientId);
            Result regMobileResult = sgRegisterApiManager.regMobileUser(regApiParams);
            if (regMobileResult.isSuccess()) {
                passportId = (String) regMobileResult.getModels().get("userid");
                //发送短信验证码
                //短信内容，TODO 目前只有小说使用，文案先写死
                String smsText = "搜狗通行证注册成功，密码为" + randomPwd + "， 请用本机号码登录。";
                if (!Strings.isNullOrEmpty(smsText) && SMSUtil.sendSMS(mobile, smsText)) {
                    if (!Strings.isNullOrEmpty(type) && ConnectTypeEnum.WAP.toString().equals(type)) {
                        Result sessionResult = sessionServerManager.createSession(passportId);
                        if (!sessionResult.isSuccess()) {
                            result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
                            return result;
                        }
                        String sgid = (String) sessionResult.getModels().get(LoginConstant.COOKIE_SGID);
                        result.getModels().put(LoginConstant.COOKIE_SGID, sgid);
                    }
                    result.setSuccess(true);
                    result.setMessage("注册成功，并发送短信至手机号：" + mobile);
                    result.setDefaultModel("userid", passportId);
                } else {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND);
                }
            } else {
                result.setCode(ErrorUtil.ERR_CODE_REGISTER_UNUSUAL);
            }
        } catch (Exception e) {
            logger.error("fast register mobile Fail, mobile:" + mobile, e);
            result.setCode(ErrorUtil.ERR_CODE_REGISTER_UNUSUAL);
        }
        return result;
    }

    @Override
    public Result registerMobile(String username, String password, int clientId, String captcha, String type) throws Exception {
        Result result = new APIResultSupport(false);
        if (!Strings.isNullOrEmpty(type) && !ConnectTypeEnum.WAP.toString().equals(type)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage("type参数有误！");
            return result;
        }
        result = mobileCodeSenderService.checkSmsCode(username, clientId, AccountModuleEnum.REGISTER, captcha);
        if (!result.isSuccess()) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOT_MATCH_SMSCODE);
            return result;
        }
        RegMobileApiParams regApiParams = new RegMobileApiParams(username, password, clientId);
        result = sgRegisterApiManager.regMobileUser(regApiParams);
        if (result.isSuccess()) {
            if (!Strings.isNullOrEmpty(type)) {
                if (ConnectTypeEnum.WAP.toString().equals(type)) {
                    String sgid;
                    String passportId = PassportIDGenerator.generator(username, AccountTypeEnum.PHONE.getValue());
                    Result sessionResult = sessionServerManager.createSession(passportId);
                    if (!sessionResult.isSuccess()) {
                        result.setCode(ErrorUtil.SYSTEM_UNKNOWN_EXCEPTION);
                        return result;
                    }
                    sgid = (String) sessionResult.getModels().get(LoginConstant.COOKIE_SGID);
                    result.setSuccess(true);
                    result.getModels().put(LoginConstant.COOKIE_SGID, sgid);
                } else {
                    result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                    result.setMessage("type参数有误！");
                    return result;
                }
            }
        }
        return result;
    }

    private RegEmailApiParams buildRegMailProxyApiParams(String username, String password, String ip, int clientId, String ru) {
        return new RegEmailApiParams(username, password, ip, clientId, ru);
    }

    @Override
    public Result activeEmail(ActiveEmailParams activeParams, String ip) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            String username = activeParams.getPassport_id();
            String token = activeParams.getToken();
            int clientId = Integer.parseInt(activeParams.getClient_id());
            //激活邮件
            if (accountService.activeEmail(username, token, clientId)) {
                //激活成功
                Account account = accountService.initialEmailAccount(username, ip);
                if (account != null) {
                    result = insertAccountInfo(account, result, ip);
                    return result;
                } else {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
                    return result;
                }
            } else {
                //激活失败
                Account account = accountService.queryAccountByPassportId(username);
                if (account != null) {
                    if (account.getFlag() == AccountStatusEnum.REGULAR.getValue()) {
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

    private Result insertAccountInfo(Account account, Result result, String ip) {
        AccountInfo accountInfo = new AccountInfo(account.getPassportId(), new Date(), new Date());
        if (!Strings.isNullOrEmpty(ip)) {
            accountInfo.setModifyip(ip);
        }
        boolean isUpdateSuccess = accountInfoService.updateAccountInfo(accountInfo);
        if (!isUpdateSuccess) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGISTER_FAILED);
        } else {
            result.setSuccess(true);
            result.setDefaultModel(account);
            result.setDefaultModel("userid", account.getPassportId());
            result.setMessage("注册成功");
            result.setDefaultModel("isSetCookie", true);
        }
        return result;
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
                    result.setMessage("重新发送激活邮件成功，请立即激活您的账户！");
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
    public Result checkRegInBlackListByIpForInternal(String ip, int clientId) throws Exception {
        Result result = new APIResultSupport(false);
        //如果在黑名单，也在白名单，允许注册；如果在黑名单不在白名单，不允许注册
        if (operateTimesService.checkRegInBlackListForInternal(ip, clientId)) {
            if (!operateTimesService.checkRegInWhiteList(ip)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST);
                return result;
            }
        }
        result.setSuccess(true);
        return result;
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
    public void incRegTimes(String ip, String cookieStr) {
        try {
            operateTimesService.incRegTimes(ip, cookieStr);
        } catch (ServiceException e) {
            logger.error("register incRegTimes Exception", e);
        }
    }

    @Override
    public boolean isUserInExistBlackList(final String username, final String ip) {
        //校验username是否在账户黑名单中
        if (operateTimesService.isUserInExistBlackList(username, ip)) {
            //是否在白名单中
            if (!operateTimesService.checkLoginUserInWhiteList(username, ip)) {
                return true;
            }
        }
        //次数累加
        operateTimesService.incExistTimes(username, ip);
        return false;
    }

    @Override
    public boolean checkUserExistInBlack(String username, String ip) {
        if (operateTimesService.checkUserInBlackListForInternal(ip, username)) {
            //检查账号是否在白名单中
            if (!operateTimesService.checkRegInWhiteList(ip)) {
                return true;
            }
        }
        operateTimesService.incInterCheckUserTimes(username, ip);
        return false;
    }

    @Override
    public void incRegTimesForInternal(String ip, int client_id) {
        operateTimesService.incRegTimesForInternal(ip, client_id);
    }
}
