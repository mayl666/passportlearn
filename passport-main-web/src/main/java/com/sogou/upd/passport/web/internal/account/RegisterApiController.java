package com.sogou.upd.passport.web.internal.account;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.RegManager;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseMobileApiParams;
import com.sogou.upd.passport.manager.api.account.form.RegEmailApiParams;
import com.sogou.upd.passport.manager.api.account.form.RegMobileApiParams;
import com.sogou.upd.passport.manager.api.account.form.RegMobileCaptchaApiParams;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
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
    private RegManager regManager;
    @Autowired
    private ConfigureManager configureManager;
    @Autowired
    private CommonManager commonManager;

    /**
     * 注册手机账号时，发送手机验证码
     *
     * @param request
     * @param params
     * @return
     */
//    @InterfaceSecurity
    @RequestMapping(value = "/sendregcaptcha", method = RequestMethod.POST)
    @ResponseBody
    public Object sendRegCaptcha(HttpServletRequest request, BaseMobileApiParams params) {
        Result result = new APIResultSupport(false);
        try {
            // 参数校验
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            //验证client_id是否存在
            int clientId = params.getClient_id();
            if (!configureManager.checkAppIsExist(clientId)) {
                result.setCode(ErrorUtil.INVALID_CLIENTID);
                return result.toString();
            }
            String mobile = params.getMobile();
            //已注册或绑定的手机号不允许再注册，因此不允许发送手机验证码
            result = regManager.isAccountNotExists(mobile, clientId);
            if (!result.isSuccess()) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGED);
                return result.toString();
            }
            // 调用内部接口
            result = sgRegisterApiManager.sendMobileRegCaptcha(params);
        } catch (Exception e) {
            logger.error("sendregcaptcha:send reg captcha is failed", e);
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
//    @InterfaceSecurity
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
            //验证client_id是否存在
            int clientId = params.getClient_id();
            if (!configureManager.checkAppIsExist(clientId)) {
                result.setCode(ErrorUtil.INVALID_CLIENTID);
                return result.toString();
            }
            String mobile = params.getMobile();
            //检查账户是否存在，也即该手机号是否已经注册或绑定
            result = regManager.isAccountNotExists(mobile, clientId);
            if (!result.isSuccess()) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_BINDED);
                return result.toString();
            }
            // 调用内部接口
            result = sgRegisterApiManager.regMobileCaptchaUser(params);
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
//    @InterfaceSecurity
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
            String userid = regSmallPieceParams(params.getUserid());
            //检查注册账号是否已经存在
            result = regManager.isAccountNotExists(userid, params.getClient_id());
            if (!result.isSuccess()) {
                result.setMessage(ErrorUtil.getERR_CODE_MSG(ErrorUtil.ERR_CODE_ACCOUNT_REGED));
                return result.toString();
            }
            // 调用内部接口
            result = sgRegisterApiManager.regMailUser(params);
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
     * 小纸条规则，如果登录名保护.，则将注册名中的.换成_，再判断该用户名是否已经注册
     *
     * @param str
     * @return
     */
    private String regSmallPieceParams(String str) {
        if (str.indexOf("@") != -1) {
            String userid = str.substring(0, str.indexOf("@"));
            if (userid.indexOf(".") != -1) {
                userid = userid.replace(".", "_");
                String useridString = userid + str.substring(str.indexOf("@"), str.length());
                return useridString;
            }
        }
        return str;
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
//    @InterfaceSecurity
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
            //验证client_id是否存在
            int clientId = params.getClient_id();
            if (!configureManager.checkAppIsExist(clientId)) {
                result.setCode(ErrorUtil.INVALID_CLIENTID);
                return result.toString();
            }
            String mobile = params.getMobile();
            //检查账户是否存在，也即该手机号是否已经注册或绑定
            result = regManager.isAccountNotExists(mobile, clientId);
            if (result.isSuccess()) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_BINDED);
                return result.toString();
            }
            // 调用内部接口
            result = sgRegisterApiManager.regMobileUser(params);

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
}
