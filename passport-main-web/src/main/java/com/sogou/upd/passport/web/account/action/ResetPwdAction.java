package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.AccountCheckManager;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.AccountSecureManager;
import com.sogou.upd.passport.manager.account.ResetPwdManager;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.account.form.AccountPwdScodeParams;
import com.sogou.upd.passport.web.account.form.AccountScodeParams;
import com.sogou.upd.passport.web.account.form.BaseAccountParams;
import com.sogou.upd.passport.web.account.form.UserCaptchaParams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * User: mayan
 * Date: 13-6-7 Time: 下午5:49
 * 重置密码 （通过注册邮箱，密保邮箱，密保手机，密保问题）
 */
@Controller
@RequestMapping(value = "/web/findpwd")
public class ResetPwdAction {
    private static final Logger logger = LoggerFactory.getLogger(ResetPwdAction.class);

    @Autowired
    private CommonManager commonManager;
    @Autowired
    private AccountSecureManager accountSecureManager;
    @Autowired
    private AccountCheckManager accountCheckManager;
    @Autowired
    private ResetPwdManager resetPwdManager;

    @RequestMapping
    public String findPwd() throws Exception {
        return "recover/index";
    }

    @RequestMapping(value = "/getsecinfo", method = RequestMethod.POST)
    public String querySecureInfo(UserCaptchaParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            model.addAttribute("data", result.toString());
        }
        String username = params.getUsername();
        int clientId = Integer.parseInt(params.getClient_id());
        String captcha = params.getCaptcha();
        String token = params.getToken();
        if (!accountCheckManager.checkCaptcha(captcha, token)) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
            model.addAttribute("data", result.toString());
        }

        // TODO:是否允许绑定手机取得密保信息
        String passportId = commonManager.getPassportIdByUsername(username);
        if (passportId == null) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            model.addAttribute("data", result.toString());
        }

        // TODO:需要修改为代理接口
        result = accountSecureManager.queryAccountSecureInfo(passportId, clientId, true);
        model.addAttribute("data", result.toString());
        return "recover/type";
    }

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
        String passportId = params.getPassport_id();
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
        String passportId = params.getPassport_id();
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
        String passportId = params.getPassport_id();
        int clientId = Integer.parseInt(params.getClient_id());
        String scode = params.getScode();

        accountCheckManager.checkLimitResetPwd(passportId, clientId);

        return "";
    }

    @RequestMapping(value = "/reset", method = RequestMethod.POST)
    public String resetPwd(AccountPwdScodeParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String passportId = params.getPassport_id();
        int clientId = Integer.parseInt(params.getClient_id());
        String password = params.getPassword();
        String scode = params.getScode();

        if (!accountCheckManager.checkLimitResetPwd(passportId, clientId)) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_LIMITED);
            return result.toString();
        }

        result = resetPwdManager.resetPasswordByEmail(passportId, clientId, password, scode);
        return "";
    }
}
