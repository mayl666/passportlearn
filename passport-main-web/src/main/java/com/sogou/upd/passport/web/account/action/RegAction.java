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
import com.sogou.upd.passport.manager.account.AccountManager;
import com.sogou.upd.passport.manager.account.CookieManager;
import com.sogou.upd.passport.manager.account.RegManager;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseMobileApiParams;
import com.sogou.upd.passport.manager.api.account.form.ResendActiveMailParams;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.form.ActiveEmailParams;
import com.sogou.upd.passport.manager.form.WebRegisterParams;
import com.sogou.upd.passport.model.account.Account;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.form.CheckUserNameExistParameters;
import com.sogou.upd.passport.web.account.form.MoblieCodeParams;
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
    private static final String LOGIN_INDEX_URL = "https://account.sogou.com";

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
    private AccountManager accountManager;


    /**
     * 用户注册检查用户名是否存在
     */
    @RequestMapping(value = "/account/checkusername", method = RequestMethod.GET)
    @ResponseBody
    public String checkusername(CheckUserNameExistParameters checkParam)
            throws Exception {

        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(checkParam);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String username = URLDecoder.decode(checkParam.getUsername(), "utf-8");
        String clientIdStr = checkParam.getClient_id();
        int clientId = !Strings.isNullOrEmpty(clientIdStr) ? Integer.valueOf(clientIdStr) : CommonConstant.SGPP_DEFAULT_CLIENTID;
        result = checkAccountNotExists(username, clientId);
        if (PhoneUtil.verifyPhoneNumberFormat(username) && !result.isSuccess()) {
            result.setMessage("此手机号已注册或已绑定，请直接登录");
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

            //检验用户名是否存在
            String username = regParams.getUsername();
            int clientId = Integer.valueOf(regParams.getClient_id());
            result = checkAccountNotExists(username, clientId);
            if (!result.isSuccess()) {
                return result.toString();
            }

            result = regManager.webRegister(regParams, ip);
            if (result.isSuccess()) {
                //设置来源
                String ru = regParams.getRu();
                if (Strings.isNullOrEmpty(ru)) {
                    ru = CommonConstant.LOGIN_INDEX_URL;
                }
                boolean isSetCookie = (Boolean) result.getModels().get("isSetCookie");
                if (isSetCookie) {
                    String passportId = (String) result.getModels().get("userid");
                    result = cookieManager.setCookie(response, passportId, clientId, ip, ru, -1);
                } else {
                    ru = CommonConstant.EMAIL_REG_VERIFY_URL;
                }
                result.setDefaultModel(CommonConstant.RESPONSE_RU, ru);
            }
        } catch (Exception e) {
            logger.error("reguser:User Register Is Failed,Username is " + regParams.getUsername(), e);
        } finally {
            String logCode;
            if (!Strings.isNullOrEmpty(finalCode)) {
                logCode = finalCode;
            } else {
                logCode = result.getCode();
            }
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
            UserOperationLog userOperationLog = new UserOperationLog(regParams.getUsername(), request.getRequestURI(), regParams.getClient_id(), logCode, getIp(request));
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
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(activeParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            //todo 返回到错误页面
            response.sendRedirect(CommonConstant.EMAIL_REG_VERIFY_URL + "?code=" + result.getCode() + "&message=" + ErrorUtil.getERR_CODE_MSG(result.getCode()));
        }
        //验证client_id
        int clientId = Integer.parseInt(activeParams.getClient_id());
        //检查client_id是否存在
        if (!configureManager.checkAppIsExist(clientId)) {
            result.setCode(ErrorUtil.INVALID_CLIENTID);
            response.sendRedirect(CommonConstant.EMAIL_REG_VERIFY_URL + "?code=" + result.getCode() + "&message=" + ErrorUtil.getERR_CODE_MSG(result.getCode()));
        }
        String ip = getIp(request);
        //邮件激活
        result = regManager.activeEmail(activeParams, ip);
        if (result.isSuccess()) {
            // 种sogou域cookie
            result = cookieManager.setCookie(response, activeParams.getPassport_id(), clientId, ip, activeParams.getRu(), -1);
            if (result.isSuccess()) {
                if (Strings.isNullOrEmpty(activeParams.getRu()) || CommonConstant.EMAIL_REG_VERIFY_URL.equals(activeParams.getRu())) {
                    activeParams.setRu(CommonConstant.DEFAULT_INDEX_URL);
                }
                result.setDefaultModel(CommonConstant.RESPONSE_RU, activeParams.getRu());
                result.setDefaultModel(CommonConstant.CLIENT_ID, clientId);
                result.setCode("0");
                response.sendRedirect(CommonConstant.EMAIL_REG_VERIFY_URL + "?code=" + result.getCode() + "&ru=" + activeParams.getRu());
            }
        }
        response.sendRedirect(CommonConstant.EMAIL_REG_VERIFY_URL + "?code=" + result.getCode() + "&message=" + ErrorUtil.getERR_CODE_MSG(result.getCode()));

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
        Account account = accountManager.queryAccountByPassportId(username);
        if (account != null) {
            switch (Integer.parseInt(account.getFlag())) {
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

        return result.toString();
    }

    /**
     * web页面手机账号注册时发送的验证码
     *
     * @param reqParams 传入的参数
     */
    @RequestMapping(value = {"/sendsms"}, method = RequestMethod.GET)
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
            //校验用户ip是否中了黑名单
            result = regManager.checkMobileSendSMSInBlackList(ip);
            if (!result.isSuccess()) {
                if (result.getCode().equals(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST)) {
                    finalCode = ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST;
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND);
                    result.setMessage("发送短信失败");
                }
                return result.toString();
            }

            String mobile = reqParams.getMobile();
            //为了数据迁移三个阶段，这里需要转换下参数类
            BaseMobileApiParams baseMobileApiParams = buildProxyApiParams(clientId, mobile);
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
        regManager.incSendTimesForMobile(ip);
        return result.toString();
    }

    private BaseMobileApiParams buildProxyApiParams(int clientId, String mobile) {
        BaseMobileApiParams baseMobileApiParams = new BaseMobileApiParams();
        baseMobileApiParams.setMobile(mobile);
        baseMobileApiParams.setClient_id(clientId);
        return baseMobileApiParams;
    }

    //检查用户是否存在
    private Result checkAccountNotExists(String username, int clientId) throws Exception {
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
        result = regManager.isAccountNotExists(username, clientId);
        return result;
    }
}
