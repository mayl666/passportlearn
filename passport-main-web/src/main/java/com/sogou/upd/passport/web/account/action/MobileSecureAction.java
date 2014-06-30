package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.AccountInfoManager;
import com.sogou.upd.passport.manager.account.CheckManager;
import com.sogou.upd.passport.manager.account.RegManager;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.BaseWebParams;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.form.security.WebBindMobileParams;
import com.sogou.upd.passport.web.account.form.security.WebMobileParams;
import com.sogou.upd.passport.web.account.form.security.WebModifyMobileParams;
import com.sogou.upd.passport.web.account.form.security.WebSmsParams;
import com.sogou.upd.passport.web.annotation.LoginRequired;
import com.sogou.upd.passport.web.inteceptor.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 密保手机相关的安全页面
 * User: shipengzhi
 * Date: 14-6-30
 * Time: 上午2:48
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/web/security")
public class MobileSecureAction extends BaseController{

    @Autowired
    private SecureManager secureManager;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private RegManager regManager;

    /*
     * 修改绑定手机，发送短信验证码至原绑定手机
     */
    @RequestMapping(value = "/sendsms", method = RequestMethod.GET)
    @ResponseBody
    @LoginRequired
    public Object sendSmsSecMobile(BaseWebParams params, HttpServletRequest request) throws Exception {
        Result result = new APIResultSupport(false);
        String finalCode = null;
        String ip = getIp(request);
        String userIdInLog = null;
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            String userId = hostHolder.getPassportId();
            userIdInLog = userId;
            int clientId = Integer.parseInt(params.getClient_id());
            switch (AccountDomainEnum.getAccountDomain(userId)) {
                case SOHU:
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SOHU_NOTALLOWED);
                    return result.toString();
                case THIRD:
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_THIRD_NOTALLOWED);
                    return result.toString();
                case PHONE:
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_MOBILEUSER_NOTALLOWED);
                    return result.toString();
            }
            result = secureManager.sendMobileCodeOld(userId, clientId);
        } catch (Exception e) {
            logger.error("method[sendSmsSecMobile] send mobile sms to old mobile error.{}", e);
        } finally {
            String logCode;
            if (!Strings.isNullOrEmpty(finalCode)) {
                logCode = finalCode;
            } else {
                logCode = result.getCode();
            }
            //web页面手机注册时，发送手机验证码
            UserOperationLog userOperationLog = new UserOperationLog(userIdInLog, request.getRequestURI(), params.getClient_id(), logCode, ip);
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            UserOperationLogUtil.log(userOperationLog);
        }
        return result.toString();
    }

    /*
     * 绑定和修改密保手机，发送短信验证码至新绑定手机
     */
    @RequestMapping(value = "/sendsmsnew", method = RequestMethod.GET)
    @ResponseBody
    @LoginRequired
    public Object sendSmsNewMobile(WebMobileParams params, HttpServletRequest request) throws Exception {
        Result result = new APIResultSupport(false);
        String ip = getIp(request);
        int clientId = Integer.parseInt(params.getClient_id());
        String newMobile = params.getNew_mobile();
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            // TODO:要不要在检验smscode时，验证userId
            String userId = hostHolder.getPassportId();
            switch (AccountDomainEnum.getAccountDomain(userId)) {
                case SOHU:
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SOHU_NOTALLOWED);
                    return result.toString();
                case THIRD:
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_THIRD_NOTALLOWED);
                    return result.toString();
                case PHONE:
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_MOBILEUSER_NOTALLOWED);
                    return result.toString();
            }
            //检测手机号是否已经注册或绑定
//            result = proxyBindApiManager.getPassportIdByMobile(baseMoblieApiParams);
            //双读，检查新手机是否允许绑定
            result = regManager.isAccountNotExists(newMobile, clientId);
            if (!result.isSuccess()) {
                result.setSuccess(false);
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_BINDED);
                result.setMessage("手机号已绑定其他账号");
                return result.toString();
            }
            result = secureManager.sendMobileCode(newMobile, clientId, AccountModuleEnum.SECURE);
        } catch (Exception e) {
            logger.error("method[sendSmsNewMobile] send mobile sms to new mobile error.{}", e);
        } finally {
            //web页面手机注册时，发送手机验证码
            UserOperationLog userOperationLog = new UserOperationLog(newMobile, request.getRequestURI(), params.getClient_id(), result.getCode(), ip);
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            UserOperationLogUtil.log(userOperationLog);
        }
        return result.toString();
    }

    /*
     * 绑定密保手机
     */
    @RequestMapping(value = "/bindmobile", method = RequestMethod.POST)
    @LoginRequired
    @ResponseBody
    public Object bindMobile(WebBindMobileParams params, HttpServletRequest request, Model model)
            throws Exception {
        Result result = new APIResultSupport(false);
        String passportId = hostHolder.getPassportId();
        int clientId = Integer.parseInt(params.getClient_id());
        String smsCode = params.getSmscode();
        String newMobile = params.getNew_mobile();
        String password = params.getPassword();
        String modifyIp = getIp(request);

        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            switch (AccountDomainEnum.getAccountDomain(passportId)) {
                case PHONE:
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_MOBILEUSER_NOTALLOWED);
                    return result.toString();
                case SOHU:
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SOHU_NOTALLOWED);
                    return result.toString();
                case THIRD:
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_THIRD_NOTALLOWED);
                    return result.toString();
            }
            result = secureManager.bindMobileByPassportId(passportId, clientId, newMobile, smsCode, password, modifyIp);
            return result.toString();
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog(passportId, request.getRequestURI(), params.getClient_id(), result.getCode(), getIp(request));
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            UserOperationLogUtil.log(userOperationLog);
        }
    }

    /*
     * 验证原绑定手机短信验证码
     */
    @RequestMapping(value = "/checksms", method = RequestMethod.POST)
    @LoginRequired
    @ResponseBody
    public Object checkSmsSecMobile(WebSmsParams params, Model model, HttpServletRequest request) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String userId = hostHolder.getPassportId();
        int clientId = Integer.parseInt(params.getClient_id());
        String smsCode = params.getSmscode();

        switch (AccountDomainEnum.getAccountDomain(userId)) {
            case PHONE:
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_MOBILEUSER_NOTALLOWED);
                return result.toString();
            case SOHU:
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SOHU_NOTALLOWED);
                return result.toString();
            case THIRD:
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_THIRD_NOTALLOWED);
                return result.toString();
        }

        result = secureManager.checkMobileCodeOldForBinding(userId, clientId, smsCode);

        UserOperationLog userOperationLog = new UserOperationLog(userId, request.getRequestURI(), String.valueOf(clientId), result.getCode(), getIp(request));
        String referer = request.getHeader("referer");
        userOperationLog.putOtherMessage("ref", referer);
        UserOperationLogUtil.log(userOperationLog);

        return result.toString();
    }

    /*
     * 修改密保手机
     */
    @RequestMapping(value = "bindmobilenew", method = RequestMethod.POST)
    @LoginRequired
    @ResponseBody
    public Object modifyMobile(WebModifyMobileParams params, HttpServletRequest request,
                               Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String userId = hostHolder.getPassportId();
        int clientId = Integer.parseInt(params.getClient_id());
        String smsCode = params.getSmscode();
        String newMobile = params.getNew_mobile();
        String scode = params.getScode();
        String modifyIp = getIp(request);

        switch (AccountDomainEnum.getAccountDomain(userId)) {
            case PHONE:
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_MOBILEUSER_NOTALLOWED);
                return result.toString();
            case SOHU:
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SOHU_NOTALLOWED);
                return result.toString();
            case THIRD:
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_THIRD_NOTALLOWED);
                return result.toString();
        }

        result = secureManager.modifyMobileByPassportId(userId, clientId, newMobile, smsCode, scode, modifyIp);

        UserOperationLog userOperationLog = new UserOperationLog(userId, request.getRequestURI(), String.valueOf(clientId), result.getCode(), getIp(request));
        String referer = request.getHeader("referer");
        userOperationLog.putOtherMessage("ref", referer);
        UserOperationLogUtil.log(userOperationLog);

        return result.toString();
    }
}
