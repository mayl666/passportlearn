package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.manager.account.CheckManager;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.ResetPwdManager;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.web.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * User: mayan
 * Date: 13-6-7 Time: 下午5:49
 * 重置密码 （通过注册邮箱，密保邮箱，密保手机，密保问题）
 */
@Controller
@RequestMapping(value = "/web/findpwd")
public class ResetPwdAction extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ResetPwdAction.class);

    private static final String SOHU_FINDPWD_URL = SHPPUrlConstant.SOHU_FINDPWD_URL;

    @Autowired
    private CommonManager commonManager;
    @Autowired
    private SecureManager secureManager;
    @Autowired
    private CheckManager checkManager;
    @Autowired
    private ResetPwdManager resetPwdManager;

    // TODO:不允许SOHU域执行此操作
    // TODO:暂时不用

    @RequestMapping(method = RequestMethod.GET)
    public String findPwdView(String ru, RedirectAttributes redirectAttributes) throws Exception {
        if (Strings.isNullOrEmpty(ru)) {
            ru = CommonConstant.DEFAULT_CONNECT_REDIRECT_URL;
        }
        redirectAttributes.addAttribute("ru", ru);
        return "redirect:" + SOHU_FINDPWD_URL + "?ru={ru}";
    }

/*    @RequestMapping(value = "/getsecinfo", method = RequestMethod.POST)
    public Object findPwd(UserCaptchaParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String username = params.getUsername();
        int clientId = Integer.parseInt(params.getClient_id());
        String captcha = params.getCaptcha();
        String token = params.getToken();

        if (!checkManager.checkCaptcha(captcha, token)) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
            return result.toString();
        }

        // TODO:已修改为代理接口
        result = resetPwdManager.queryAccountSecureInfo(username, clientId, true);
        return result.toString();
    }*/

/*
    @RequestMapping(value = "/sendremail", method = RequestMethod.POST)
    public String sendEmailRegResetPwd(BaseAccountParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            model.addAttribute("data", result.toString());
            return "recover/type";
        }
        String passportId = params.getUserid();
        int clientId = Integer.parseInt(params.getClient_id());
        result = resetPwdManager.sendEmailResetPwdByPassportId(passportId, clientId, true);
        model.addAttribute("data", result.toString());
        if (result.isSuccess()) {
            return ""; // TODO:
        }
        return "";
    }

    @RequestMapping(value = "/sendbemail", method = RequestMethod.POST)
    public String sendEmailBindResetPwd(BaseAccountParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String passportId = params.getUserid();
        int clientId = Integer.parseInt(params.getClient_id());
        result = resetPwdManager.sendEmailResetPwdByPassportId(passportId, clientId, false);
        model.addAttribute("data", result.toString());
        if (result.isSuccess()) {
            return ""; // TODO:
        }
        return "";
    }

    @RequestMapping(value = "/checkemail", method = RequestMethod.GET)
    public String checkEmailResetPwd(AccountScodeParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String passportId = params.getUserid();
        int clientId = Integer.parseInt(params.getClient_id());
        String scode = params.getScode();

        checkManager.checkLimitResetPwd(passportId, clientId);

        return "";
    }

    @RequestMapping(value = "/reset", method = RequestMethod.POST)
    public String resetPwd(AccountPwdParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String passportId = params.getUserid();
        int clientId = Integer.parseInt(params.getClient_id());
        String password = params.getPassword();
        String scode = params.getScode();

        if (!checkManager.checkLimitResetPwd(passportId, clientId)) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_LIMITED);
            return result.toString();
        }

        result = resetPwdManager.resetPasswordByEmail(passportId, clientId, password, scode);
        return "";
    }

    @RequestMapping(value = "/mobile", method = RequestMethod.POST)
    public String mobile() throws Exception {
        return "";
    }

    @RequestMapping(value = "/email", method = RequestMethod.GET)
    public String email() throws Exception {
        return "recover/email";
    }*/
}
