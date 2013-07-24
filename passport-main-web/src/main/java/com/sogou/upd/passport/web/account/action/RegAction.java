package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.CookieUtils;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.RegManager;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.form.ActiveEmailParameters;
import com.sogou.upd.passport.web.account.form.CheckUserNameExistParameters;
import com.sogou.upd.passport.manager.form.WebRegisterParameters;
import com.sogou.upd.passport.service.account.OperateTimesService;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;

import com.sogou.upd.passport.web.account.form.MoblieCodeParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

/**
 * web注册 User: mayan Date: 13-6-7 Time: 下午5:48
 */
@Controller
@RequestMapping("/web")
public class RegAction extends BaseController {

    //  private static final Logger logger = LoggerFactory.getLogger(RegAction.class);
    private static final Logger logger = LoggerFactory.getLogger("com.sogou.upd.passport.regBlackListFileAppender");
    private static final String LOGIN_INDEX_URL = "https://account.sogou.com";

    @Autowired
    private RegManager regManager;
    @Autowired
    private CommonManager commonManager;
    @Autowired
    private ConfigureManager configureManager;
    @Autowired
    private SecureManager secureManager;
    @Autowired
    private RegisterApiManager proxyRegisterApiManager;
    @Autowired
    private RegisterApiManager sgRegisterApiManager;
    @Autowired
    private OperateTimesService operateTimesService;

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
        result = checkAccountNotExists(username);
        if (PhoneUtil.verifyPhoneNumberFormat(username) && ErrorUtil.ERR_CODE_ACCOUNT_REGED.equals(result.getCode())) {
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
    public Object reguser(HttpServletRequest request, WebRegisterParameters regParams, Model model)
            throws Exception {
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(regParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }

        String ip = getIp(request);
        //校验用户是否允许注册
        String uuidName = CookieUtils.getCookie(request, "uuidName");
        result = regManager.checkRegInBlackList(ip, uuidName);
        if (!result.isSuccess()) {
            return result.toString();
        }

        //检验用户名是否存在
        String username = regParams.getUsername();
        result = checkAccountNotExists(username);
        if (!result.isSuccess()) {
            return result.toString();
        }

        result = regManager.webRegister(regParams, ip);
        if (result.isSuccess()) {
            //设置来源
            String ru = regParams.getRu();
            if (Strings.isNullOrEmpty(ru)) {
                ru = LOGIN_INDEX_URL;
            }
            result.setDefaultModel("ru", ru);
        }

        String domainStr = AccountDomainEnum.getAccountDomain(username).toString();
        if (domainStr.equals(AccountDomainEnum.INDIVID.toString())) {
            domainStr = AccountDomainEnum.SOGOU.toString();
        }

        //用户注册log
        UserOperationLog userOperationLog = new UserOperationLog(username, request.getRequestURI(), regParams.getClient_id(), result.getCode(), getIp(request), domainStr);
        String referer = request.getHeader("referer");
        userOperationLog.putOtherMessage("ref", referer);
        UserOperationLogUtil.log(userOperationLog);

        regManager.incRegTimes(ip, uuidName);
        String userId = (String) result.getModels().get("userid");
        if (!Strings.isNullOrEmpty(userId) && AccountDomainEnum.getAccountDomain(userId) != AccountDomainEnum.OTHER) {
            if (result.isSuccess()) {
                // 非外域邮箱用户不用验证，直接注册成功后记录登录记录
                int clientId = Integer.parseInt(regParams.getClient_id());
                secureManager.logActionRecord(userId, clientId, AccountModuleEnum.LOGIN, ip, null);
            }
        }
        return result.toString();
    }

    /**
     * 邮件激活
     *
     * @param activeParams 传入的参数
     */
    @RequestMapping(value = "/activemail", method = RequestMethod.GET)
    @ResponseBody
    public Object activeEmail(HttpServletRequest request, ActiveEmailParameters activeParams)
            throws Exception {
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(activeParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result;
        }
        //验证client_id
        int clientId = Integer.parseInt(activeParams.getClient_id());

        //检查client_id是否存在
        if (!configureManager.checkAppIsExist(clientId)) {
            result.setCode(ErrorUtil.INVALID_CLIENTID);
            return result;
        }
        String ip = getIp(request);
        //邮件激活
        result = regManager.activeEmail(activeParams, ip);
        if (result.isSuccess()) {
            // 种sohu域cookie
            result = commonManager.createCookieUrl(result, activeParams.getPassport_id(), 1);
        }
        return result;
    }

    /**
     * web页面手机账号注册时发送的验证码
     *
     * @param reqParams 传入的参数
     */
    @RequestMapping(value = {"/sendsms"}, method = RequestMethod.GET)
    @ResponseBody
    public Object sendMobileCode(MoblieCodeParams reqParams)
            throws Exception {
        Result result = new APIResultSupport(false);
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
        //为了数据迁移三个阶段，这里需要转换下参数类
        BaseMoblieApiParams baseMoblieApiParams = buildProxyApiParams(clientId, mobile);
        if (ManagerHelper.isInvokeProxyApi(mobile)) {
            result = proxyRegisterApiManager.sendMobileRegCaptcha(baseMoblieApiParams);
        } else {
            result = sgRegisterApiManager.sendMobileRegCaptcha(baseMoblieApiParams);
        }
        return result.toString();

    }

    private BaseMoblieApiParams buildProxyApiParams(int clientId, String mobile) {
        BaseMoblieApiParams baseMoblieApiParams = new BaseMoblieApiParams();
        baseMoblieApiParams.setMobile(mobile);
        baseMoblieApiParams.setClient_id(clientId);
        return baseMoblieApiParams;
    }

    //检查用户是否存在
    private Result checkAccountNotExists(String username) throws Exception {
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

        //判断是否是个性账号
        if (username.indexOf("@") == -1) {
            //判断是否是手机号注册
            if (PhoneUtil.verifyPhoneNumberFormat(username)) {
                result = regManager.isAccountNotExists(username, true);
            } else {
                username = username + "@sogou.com";
                result = regManager.isAccountNotExists(username, false);
            }
        } else {
            result = regManager.isAccountNotExists(username, false);
        }
        return result;
    }

    /*
     外域邮箱用户激活成功的页面
   */
    @RequestMapping(value = "/reg/emailverify", method = RequestMethod.GET)
    public String emailVerifySuccess(HttpServletRequest request) throws Exception {
        //状态码参数
        return "reg/emailsuccess";
    }
}
