package com.sogou.upd.passport.web.internal.account;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.RegManager;
import com.sogou.upd.passport.manager.api.account.BindApiManager;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.*;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.annotation.InterfaceSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Web登录的内部接口
 * User: shipengzhi
 * Date: 13-6-6
 * Time: 下午2:40
 */
@Controller
@RequestMapping("/internal/account")
public class RegisterApiController extends BaseController {

    @Autowired
    private RegisterApiManager sgRegisterApiManager;
    @Autowired
    private RegisterApiManager proxyRegisterApiManager;
    @Autowired
    private BindApiManager proxyBindApiManager;
    @Autowired
    private BindApiManager sgBindApiManager;
    @Autowired
    private RegManager regManager;
    @Autowired
    private CommonManager commonManager;

    /**
     * 注册手机账号时，发送手机验证码
     *
     * @param request
     * @param params
     * @return
     */
    @InterfaceSecurity
    @RequestMapping(value = "/sendregcaptcha", method = RequestMethod.POST)
    @ResponseBody
    public Object sendRegCaptcha(HttpServletRequest request, BaseMoblieApiParams params) {
        Result result = new APIResultSupport(false);
        try {
            // 参数校验
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            // 调用内部接口
            result = sgRegisterApiManager.sendMobileRegCaptcha(params);
        } catch (Exception e) {
            logger.error("sendregcaptcha:send reg captcha is failed,mobile is " + params.getMobile(), e);
        } finally {
            //记录log
            UserOperationLog userOperationLog = new UserOperationLog(params.getMobile(), request.getRequestURI(), String.valueOf(params.getClient_id()), result.getCode(), getIp(request));
            UserOperationLogUtil.log(userOperationLog);
        }
        return result.toString();
    }

    /**
     * 注册手机号@sohu.com的账号，前提是手机号既没有注册过帐号，也没有绑定过任何账号
     * 需要/sendregcaptcha下发的验证码
     *
     * @param request
     * @param params
     * @return
     */
    @InterfaceSecurity
    @RequestMapping(value = "/regmobileuser", method = RequestMethod.POST)
    @ResponseBody
    public Object regMobileCaptchaUser(HttpServletRequest request, RegMobileCaptchaApiParams params) {
        Result result = new APIResultSupport(false);
        try {
            // 参数校验
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            // 调用内部接口
            result = regManager.registerMobile(params.getMobile(), params.getPassword(), params.getClient_id(), params.getCaptcha());
        } catch (Exception e) {
            logger.error("regMobileCaptchaUser:Mobile User With Captcha For Internal Is Failed,Mobile is " + params.getMobile(), e);
        } finally {
            //记录log
            UserOperationLog userOperationLog = new UserOperationLog(params.getMobile(), request.getRequestURI(), String.valueOf(params.getClient_id()), result.getCode(), params.getIp());
            UserOperationLogUtil.log(userOperationLog);
        }
        return result.toString();
    }

    /**
     * 注册账号为外域邮箱、个性域名、搜狗邮箱
     *
     * @param request
     * @param params
     * @return
     */
    @InterfaceSecurity
    @RequestMapping(value = "/reguser", method = RequestMethod.POST)
    @ResponseBody
    public Object regMailUser(HttpServletRequest request, RegEmailApiParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String ip = null;
        int client_id = params.getClient_id();
        try {
            // 参数校验
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            ip = params.getCreateip();
            //校验用户ip是否允许注册
            result = regManager.checkRegInBlackListByIpForInternal(ip, client_id);
            if (!result.isSuccess()) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST);
                return result.toString();
            }
            // 调用内部接口
            result = proxyRegisterApiManager.regMailUser(params);
        } catch (Exception e) {
            logger.error("regMailUser:Mail User Register Is Failed For Internal,UserId Is " + params.getUserid(), e);
        } finally {
            //记录log
            commonManager.incRegTimesForInternal(ip, client_id);
            UserOperationLog userOperationLog = new UserOperationLog(params.getUserid(), String.valueOf(params.getClient_id()), result.getCode(), ip);
            UserOperationLogUtil.log(userOperationLog);
        }
        return result.toString();
    }

    /**
     * 注册手机号@sohu.com的账号，前提是手机号既没有注册过帐号，也没有绑定过任何账号
     * 不需要验证码——供SOGOU地图使用
     * TODO:使用量少时删除此接口，不安全
     *
     * @param request
     * @param params
     * @return
     */
    @InterfaceSecurity
    @RequestMapping(value = "/regmobile", method = RequestMethod.POST)
    @ResponseBody
    public Object regMobileUser(HttpServletRequest request, RegMobileApiParams params) {
        Result result = new APIResultSupport(false);
        try {
            // 参数校验
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            // 调用内部接口
            result = proxyRegisterApiManager.regMobileUser(params);
        } catch (Exception e) {
            logger.error("regMobileUser:Mobile User Register Is Failed,Mobile Is " + params.getMobile(), e);
        } finally {
            //记录log
            UserOperationLog userOperationLog = new UserOperationLog(params.getMobile(), String.valueOf(params.getClient_id()), result.getCode(), getIp(request));
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            UserOperationLogUtil.log(userOperationLog);
        }
        return result.toString();
    }

    /**
     * 检查账号是否存在
     * 账号类型为：xxx@sogou.com、搜狐域账号、外域邮箱账号、xxx@{provider}.sohu.com
     * 手机号会返回"userid错误"
     *
     * @param request
     * @param params
     * @return
     */
    @InterfaceSecurity
    @RequestMapping(value = "/checkuser", method = RequestMethod.POST)
    @ResponseBody
    public Object checkUser(HttpServletRequest request, CheckUserApiParams params) {
        Result result = new APIResultSupport(false);
        try {
            // 参数校验
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            // 调用内部接口
            String userid = params.getUserid();
            result = regManager.isAccountNotExists(userid, params.getClient_id());
//        if (PhoneUtil.verifyPhoneNumberFormat(userid)) {
//            BaseMoblieApiParams baseMoblieApiParams = new BaseMoblieApiParams();
//            baseMoblieApiParams.setMobile(userid);
//            result = proxyBindApiManager.getPassportIdByMobile(baseMoblieApiParams);
//            //如果手机号已经被注册或被绑定其它账号，返回错误信息
//            if (result.isSuccess()) {
//                result.setSuccess(false);
//                result.setDefaultModel("flag", "1");
//                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_BINDED);
//                result.setMessage(ErrorUtil.getERR_CODE_MSG(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_BINDED));
//            } else if (result.getCode().equals(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOBIND)) {
//                //如果手机号没有被注册或绑定其它账号，返回正确
//                result = new APIResultSupport(true);
//            }
//        } else {
//            result = proxyRegisterApiManager.checkUser(params);
//        }
        } catch (Exception e) {
            logger.error("regMobileUser:Mobile User Register Is Failed,Mobile Is " + params.getUserid(), e);
        } finally {
            //记录log
            UserOperationLog userOperationLog = new UserOperationLog(params.getUserid(), String.valueOf(params.getClient_id()), result.getCode(), getIp(request));
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            UserOperationLogUtil.log(userOperationLog);
        }
        return result.toString();
    }
}
