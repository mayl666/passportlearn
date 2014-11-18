package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 密保手机相关的安全操作
 * User: shipengzhi
 * Date: 14-6-30
 * Time: 上午2:48
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/web/security")
public class MobileSecureAction extends BaseController {

    @Autowired
    private SecureManager secureManager;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private RegisterApiManager sgRegisterApiManager;

    /*
     * 修改绑定手机，发送短信验证码至原绑定手机
     */
    @RequestMapping(value = "/sendsms", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public Object sendSmsSecMobile(BaseWebParams params, HttpServletRequest request) throws Exception {
        Result result = new APIResultSupport(false);
        String ip = getIp(request);
        String passportId = hostHolder.getPassportId();
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            int clientId = Integer.parseInt(params.getClient_id());
            result = verifyMobileSecureIsAllowed(result, passportId);
            if (!result.isSuccess()) {
                return result.toString();
            }
            result = secureManager.sendMobileCodeOld(passportId, clientId);
            return result.toString();
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog(passportId, request.getRequestURI(), params.getClient_id(), result.getCode(), ip);
            userOperationLog.putOtherMessage("ref", request.getHeader("referer"));
            UserOperationLogUtil.log(userOperationLog);
        }
    }

    /*
     * 首次绑定和修改密保手机，发送短信验证码至新绑定手机
     */
    @RequestMapping(value = "/sendsmsnew", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public Object sendSmsNewMobile(WebMobileParams params, HttpServletRequest request) throws Exception {
        Result result = new APIResultSupport(false);
        String ip = getIp(request);
        String passportId = hostHolder.getPassportId();
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            int clientId = Integer.parseInt(params.getClient_id());
            String newMobile = params.getNew_mobile();
            result = verifyMobileSecureIsAllowed(result, passportId);
            if (!result.isSuccess()) {
                return result.toString();
            }
            //双读，检查新手机是否允许绑定
            result = sgRegisterApiManager.checkUser(newMobile, clientId);
            if (!result.isSuccess()) {
                result.setSuccess(false);
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_BINDED);
                result.setMessage("手机号已绑定其他账号");
                return result.toString();
            }
            result = secureManager.sendMobileCode(newMobile, clientId, AccountModuleEnum.SECURE);
            return result.toString();
        } finally {
            //web页面手机注册时，发送手机验证码
            UserOperationLog userOperationLog = new UserOperationLog(passportId, request.getRequestURI(), params.getClient_id(), result.getCode(), ip);
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
    public Object checkSmsSecMobile(WebSmsParams params, HttpServletRequest request) throws Exception {
        Result result = new APIResultSupport(false);
        String passportId = hostHolder.getPassportId();
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            String smsCode = params.getSmscode();
            int clientId = Integer.parseInt(params.getClient_id());
            result = verifyMobileSecureIsAllowed(result, passportId);
            if (!result.isSuccess()) {
                return result.toString();
            }
            result = secureManager.checkMobileCodeOldForBinding(passportId, clientId, smsCode);
            return result.toString();
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog(passportId, request.getRequestURI(), params.getClient_id(), result.getCode(), getIp(request));
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            UserOperationLogUtil.log(userOperationLog);
        }
    }

    /*
     * 首次绑定密保手机
     */
    @RequestMapping(value = "/bindmobile", method = RequestMethod.POST)
    @LoginRequired
    @ResponseBody
    public Object bindMobile(WebBindMobileParams params, HttpServletRequest request) throws Exception {
        Result result = new APIResultSupport(false);
        String passportId = hostHolder.getPassportId();
        String modifyIp = getIp(request);
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            int clientId = Integer.parseInt(params.getClient_id());
            String smsCode = params.getSmscode();
            String newMobile = params.getNew_mobile();
            String password = params.getPassword();
            result = verifyMobileSecureIsAllowed(result, passportId);
            if (!result.isSuccess()) {
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
     * 修改密保手机
     */
    @RequestMapping(value = "/bindmobilenew", method = RequestMethod.POST)
    @LoginRequired
    @ResponseBody
    public Object modifyMobile(WebModifyMobileParams params, HttpServletRequest request) throws Exception {
        Result result = new APIResultSupport(false);
        String passportId = hostHolder.getPassportId();
        String ip = getIp(request);
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            String smsCode = params.getSmscode();
            String newMobile = params.getNew_mobile();
            String scode = params.getScode();
            int clientId = Integer.parseInt(params.getClient_id());
            result = verifyMobileSecureIsAllowed(result, passportId);
            if (!result.isSuccess()) {
                return result.toString();
            }
            result = secureManager.modifyMobileByPassportId(passportId, clientId, newMobile, smsCode, scode, ip);
            return result.toString();
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog(passportId, request.getRequestURI(), params.getClient_id(), result.getCode(), ip);
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            UserOperationLogUtil.log(userOperationLog);
        }
    }

    /*
     * 搜狐域、手机、第三方账号不允许绑定或修改密保手机
     */
    private static Result verifyMobileSecureIsAllowed(Result result, String passportId) {
        switch (AccountDomainEnum.getAccountDomain(passportId)) {
            case PHONE:
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_MOBILEUSER_NOTALLOWED);
                return result;
            case SOHU:
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SOHU_NOTALLOWED);
                return result;
            case THIRD:
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_THIRD_NOTALLOWED);
                return result;
        }
        result.setSuccess(true);
        return result;
    }
}
