package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.CookieManager;
import com.sogou.upd.passport.manager.account.RegManager;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.api.account.form.ResendActiveMailParams;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.form.ActiveEmailParams;
import com.sogou.upd.passport.manager.form.WebRegisterParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.form.MoblieCodeParams;
import com.sogou.upd.passport.web.account.form.RegUserNameParams;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;

/**
 * web注册 User: mayan Date: 13-6-7 Time: 下午5:48
 */
@Controller
@RequestMapping("/web")
public class RegAction extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger("com.sogou.upd.passport.regBlackListFileAppender");

    @Autowired
    private RegManager regManager;
    @Autowired
    private ConfigureManager configureManager;
    @Autowired
    private SecureManager secureManager;
    @Autowired
    private RegisterApiManager sgRegisterApiManager;
    @Autowired
    private CookieManager cookieManager;
    @Autowired
    private CommonManager commonManager;


    /**
     * 用户注册检查用户名是否存在
     */
    @RequestMapping(value = "/account/checkusername", method = RequestMethod.GET)
    @ResponseBody
    public String checkusername(HttpServletRequest request, RegUserNameParams checkParam)
            throws Exception {
        Result result = new APIResultSupport(false);
        int clientId = CommonConstant.SGPP_DEFAULT_CLIENTID;
        try {
            //参数验证
            String validateResult = ControllerHelper.validateParams(checkParam);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            String username = URLDecoder.decode(checkParam.getUsername(), "utf-8");
            String clientIdStr = checkParam.getClient_id();
            if (!Strings.isNullOrEmpty(clientIdStr)) {
                clientId = Integer.valueOf(clientIdStr);
            }
            if (regManager.isUserInExistBlackList(checkParam.getUsername(), getIp(request))) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST);
            } else {
                result = checkAccountNotExists(username, clientId);
                if (PhoneUtil.verifyPhoneNumberFormat(username) && ErrorUtil.ERR_CODE_ACCOUNT_REGED.equals(result.getCode())) {
                    result.setMessage("此手机号已注册或已绑定，请直接登录");
                }
            }
        } catch (Exception e) {
            logger.error("checkusername:Check Username Is Failed,Username is " + checkParam.getUsername(), e);
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog(checkParam.getUsername(), request.getRequestURI(), String.valueOf(clientId), result.getCode(), getIp(request));
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            UserOperationLogUtil.log(userOperationLog);
        }
        return result.toString();
    }

    /**
     * web页面注册
     *
     * @param regParams 传入的参数
     */
    @RequestMapping(value = "/reguser", method = RequestMethod.POST)
    @ResponseBody
    public Object reguser(HttpServletRequest request, HttpServletResponse response, WebRegisterParams regParams, Model model)
            throws Exception {
        Result result = new APIResultSupport(false);
        String ip = null;
        String uuidName = null;
        String finalCode = null;
        try {
            //参数验证
            String validateResult = ControllerHelper.validateParams(regParams);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            ip = getIp(request);
            //校验用户是否允许注册
            uuidName = ServletUtil.getCookie(request, "uuidName");
            result = regManager.checkRegInBlackList(ip, uuidName);
            if (!result.isSuccess()) {
                if (result.getCode().equals(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST)) {
                    finalCode = ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST;
                    result.setCode(ErrorUtil.ERR_CODE_REGISTER_UNUSUAL);
                    result.setMessage("注册失败");
                }
                return result.toString();
            }
            int clientId = Integer.valueOf(regParams.getClient_id());
            result = regManager.webRegister(regParams, ip);
            if (result.isSuccess()) {
                //设置来源
                String ru = regParams.getRu();
                if (Strings.isNullOrEmpty(ru)) {
                    ru = CommonConstant.DEFAULT_INDEX_URL;
                }
                String passportId = (String) result.getModels().get("username");
                Boolean isSetCookie = (Boolean) result.getModels().get("isSetCookie");
                if (isSetCookie) {
                    result = cookieManager.setCookie(response, passportId, clientId, ip, ru, -1);
                }
                result.setDefaultModel(CommonConstant.RESPONSE_RU, ru);
            }
        } catch (Exception e) {
            logger.error("reguser:User Register Is Failed,Username is " + regParams.getUsername(), e);
        } finally {
            String logCode = !Strings.isNullOrEmpty(finalCode) ? finalCode : result.getCode();
            regManager.incRegTimes(ip, uuidName);
            String userId = (String) result.getModels().get("userid");
            if (!Strings.isNullOrEmpty(userId) && AccountDomainEnum.getAccountDomain(userId) != AccountDomainEnum.OTHER) {
                if (result.isSuccess()) {
                    // 非外域邮箱用户不用验证，直接注册成功后记录登录记录
                    int clientId = Integer.parseInt(regParams.getClient_id());
                    secureManager.logActionRecord(userId, clientId, AccountModuleEnum.LOGIN, ip, null);
                }
            }
            //用户注册log
            //验证码信息先输出到warning，不记录到日志中，省得报警
            if (ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED.equals(logCode)) {
                logger.warn("ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED, username:" + regParams.getUsername() + " clientId:" + regParams.getClient_id() + " ip:" + getIp(request) + " requestURI:" + request.getRequestURI());
            } else {
                UserOperationLog userOperationLog = new UserOperationLog(regParams.getUsername(), request.getRequestURI(), regParams.getClient_id(), logCode, getIp(request));
                String referer = request.getHeader("referer");
                userOperationLog.putOtherMessage("ref", referer);
                UserOperationLogUtil.log(userOperationLog);
            }
        }
        return result.toString();
    }

    /**
     * 重新发送激活邮件
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/resendActiveMail", method = RequestMethod.POST)
    @ResponseBody
    public Object resendActiveMail(HttpServletRequest request, ResendActiveMailParams params) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            //参数验证
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            //检查client_id是否存在
            int clientId = Integer.parseInt(params.getClient_id());
            if (!configureManager.checkAppIsExist(clientId)) {
                result.setCode(ErrorUtil.INVALID_CLIENTID);
                return result;
            }
            String username = params.getUsername();
            //如果账号存在并且状态为未激活，则重新发送激活邮件
            Account account = commonManager.queryAccountByPassportId(username);
            if (account != null) {
                switch (account.getFlag()) {
                    case 0:
                        //未激活，发送激活邮件
                        result = regManager.resendActiveMail(params);
                        break;
                    case 1:
                        //正式用户，可直接登录
                        result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGED);
                        break;
                    case 2:
                        //用户已经被封杀
                        result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_KILLED);
                        break;
                }
            } else {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            }
        } catch (Exception e) {
            logger.error("method[resendActiveMail] send mobile sms error.{}", e);
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog(params.getUsername(), request.getRequestURI(), params.getClient_id(), result.getCode(), getIp(request));
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            UserOperationLogUtil.log(userOperationLog);
        }
        return result.toString();
    }

    /**
     * 邮件激活
     *
     * @param activeParams 传入的参数
     */
    @RequestMapping(value = "/activemail", method = RequestMethod.GET)
    public void activeEmail(HttpServletRequest request, HttpServletResponse response, ActiveEmailParams activeParams, Model model)
            throws Exception {
        Result result;
        //参数验证
        String validateResult = ControllerHelper.validateParams(activeParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            response.sendRedirect(CommonConstant.EMAIL_REG_VERIFY_URL + "?code=" + ErrorUtil.ERR_CODE_COM_REQURIE);
            return;
        }
        //验证client_id
        int clientId = Integer.parseInt(activeParams.getClient_id());
        //检查client_id是否存在
        if (!configureManager.checkAppIsExist(clientId)) {
            response.sendRedirect(CommonConstant.EMAIL_REG_VERIFY_URL + "?code=" + ErrorUtil.INVALID_CLIENTID);
            return;
        }
        String ip = getIp(request);
        //邮件激活
        result = regManager.activeEmail(activeParams, ip);
        if (result.isSuccess()) {
            // 种sogou域cookie
            result = cookieManager.setCookie(response, activeParams.getPassport_id(), clientId, ip, activeParams.getRu(), -1);
            if (result.isSuccess()) {
                String ru = activeParams.getRu();
                if (Strings.isNullOrEmpty(ru) || CommonConstant.EMAIL_REG_VERIFY_URL.equals(ru)) {
                    ru = CommonConstant.DEFAULT_INDEX_URL;
                }
                response.sendRedirect(CommonConstant.EMAIL_REG_VERIFY_URL + "?code=0&ru=" + ru);
                return;
            }
        }
        response.sendRedirect(CommonConstant.EMAIL_REG_VERIFY_URL + "?code=" + result.getCode());
        return;

    }

    /**
     * web页面手机账号注册时发送的验证码
     *
     * @param reqParams 传入的参数
     */
    @RequestMapping(value = {"/sendsms"}/*, method = RequestMethod.POST*/)
    @ResponseBody
    public Object sendMobileCode(MoblieCodeParams reqParams, HttpServletRequest request)
            throws Exception {
        Result result = new APIResultSupport(false);
        String finalCode = null;
        String ip = getIp(request);
        try {
            //参数验证
            String validateResult = ControllerHelper.validateParams(reqParams);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            //验证client_id
            int clientId = Integer.parseInt(reqParams.getClient_id());
            //检查client_id是否存在
            if (!configureManager.checkAppIsExist(clientId)) {
                result.setCode(ErrorUtil.INVALID_CLIENTID);
                return result.toString();
            }
            String mobile = reqParams.getMobile();
            result = commonManager.checkMobileSendSMSInBlackList(mobile, reqParams.getClient_id());
            //需要弹出验证码
            if (!result.isSuccess()) {
                //如果token和captcha都不为空，则校验是否匹配
                if (!Strings.isNullOrEmpty(reqParams.getToken()) && !Strings.isNullOrEmpty(reqParams.getCaptcha())) {
                    result = regManager.checkCaptchaToken(reqParams.getToken(), reqParams.getCaptcha());
                    //如果验证码校验失败，则提示
                    if (!result.isSuccess()) {
                        result.setDefaultModel("token", RandomStringUtils.randomAlphanumeric(48));
                        result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
                        return result.toString();
                    }
                } else {
                    result.setDefaultModel("token", RandomStringUtils.randomAlphanumeric(48));
                    return result.toString();
                }
            }
            //校验用户ip是否中了黑名单
            result = commonManager.checkMobileSendSMSInBlackList(ip, reqParams.getClient_id());
            if (!result.isSuccess()) {
                finalCode = ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST;
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND);
                result.setMessage("发送短信失败");
                return result.toString();
            }
            BaseMoblieApiParams baseMobileApiParams = buildProxyApiParams(clientId, mobile);
            result = sgRegisterApiManager.sendMobileRegCaptcha(baseMobileApiParams);
        } catch (Exception e) {
            logger.error("method[sendMobileCode] send mobile sms error.{}", e);
        } finally {
            String logCode;
            if (!Strings.isNullOrEmpty(finalCode)) {
                logCode = finalCode;
            } else {
                logCode = result.getCode();
            }
            //web页面手机注册时，发送手机验证码
            UserOperationLog userOperationLog = new UserOperationLog(reqParams.getMobile(), request.getRequestURI(), reqParams.getClient_id(), logCode, ip);
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            UserOperationLogUtil.log(userOperationLog);
        }
        commonManager.incSendTimesForMobile(ip);
        commonManager.incSendTimesForMobile(reqParams.getMobile());
        return result.toString();
    }

    private BaseMoblieApiParams buildProxyApiParams(int clientId, String mobile) {
        BaseMoblieApiParams baseMoblieApiParams = new BaseMoblieApiParams();
        baseMoblieApiParams.setMobile(mobile);
        baseMoblieApiParams.setClient_id(clientId);
        return baseMoblieApiParams;
    }

    //检查用户是否存在
    protected Result checkAccountNotExists(String username, int clientId) throws Exception {
        Result result = new APIResultSupport(false);
        //校验是否是搜狐域内用户
        if (AccountDomainEnum.SOHU.equals(AccountDomainEnum.getAccountDomain(username))) {
            result.setCode(ErrorUtil.ERR_CODE_NOTSUPPORT_SOHU_REGISTER);
            return result;
        }
        //校验是否是搜狗用户
        if (AccountDomainEnum.SOGOU.equals(AccountDomainEnum.getAccountDomain(username))) {
            result.setCode(ErrorUtil.ERR_CODE_NOTSUPPORT_SOGOU_REGISTER);
            return result;
        }
        //检查用户名是否存在
        result = regManager.isAccountNotExists(username, clientId);
        return result;
    }

    /*
     外域邮箱用户激活成功的页面
   */
    @RequestMapping(value = "/reg/emailverify", method = RequestMethod.GET)
    public String emailVerifySuccess(String code, String ru, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        if ("0".equals(code)) {
            result.setSuccess(true);
            result.setDefaultModel("ru", ru);
        } else {
            result.setCode(code);
        }
        model.addAttribute("data", result.toString());
        //状态码参数
        return "reg/emailsuccess";
    }
}
