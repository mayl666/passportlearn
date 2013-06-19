package com.sogou.upd.passport.web.internal.account;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.api.account.form.CheckUserApiParams;
import com.sogou.upd.passport.manager.api.account.form.RegMobileCaptchaApiParams;
import com.sogou.upd.passport.web.ControllerHelper;
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
public class RegisterApiController {

    @Autowired
    private RegisterApiManager proxyRegisterApiManager;

    /**
     * 发送手机验证码
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
        // 参数校验
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        // 调用内部接口
        result = proxyRegisterApiManager.sendMobileRegCaptcha(params);
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
        // 参数校验
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        // 调用内部接口
        result = proxyRegisterApiManager.regMobileCaptchaUser(params);
        return result.toString();
    }

    @InterfaceSecurity
    @RequestMapping(value = "/checkuser", method = RequestMethod.POST)
    @ResponseBody
    public Object checkUser(HttpServletRequest request, CheckUserApiParams params) {
        Result result = new APIResultSupport(false);
        // 参数校验
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        // 调用内部接口
        result = proxyRegisterApiManager.checkUser(params);
//这里到底是检测用户名是否存在还是是否可注册，需要再做考虑
//        String userID=params.getUserid().trim();
//        String[] split=userID.split("@");
//        String domain=split[split.length-1];
//        if(!userID.endsWith("@sogou.com")){
//            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
//            result.setMessage(validateResult);
//            return result.toString();
//        }
        return result.toString();
    }
}
