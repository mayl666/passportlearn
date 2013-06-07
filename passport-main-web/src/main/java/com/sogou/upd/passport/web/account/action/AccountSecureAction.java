package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.AccountCheckManager;
import com.sogou.upd.passport.manager.account.AccountManager;
import com.sogou.upd.passport.manager.account.AccountSecureManager;
import com.sogou.upd.passport.web.BaseController;
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
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-4-28 Time: 下午1:51 To change this template use
 * File | Settings | File Templates.
 */
@Controller
@RequestMapping("/web")
public class AccountSecureAction extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AccountSecureAction.class);

    @Autowired
    private AccountManager accountManager;
    @Autowired
    private AccountSecureManager accountSecureManager;
    @Autowired
    private AccountCheckManager accountCheckManager;

    /**
     * 显示找回密码界面
     */
    @RequestMapping(value = "/findpwd", method = { RequestMethod.POST, RequestMethod.GET })
    public String findPwd() throws Exception {
        return "recover/index";
    }

    @RequestMapping(value = "/findpwd/getsecinfo", method = RequestMethod.POST)
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
        String passportId = accountManager.getPassportIdByUsername(username);
        if (passportId == null) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            model.addAttribute(result.toString());
        }

        // TODO:需要修改为代理接口
        result = accountSecureManager.queryAccountSecureInfo(passportId, clientId, true);
        model.addAttribute(result.toString());
        return "recover/type";
    }

    @RequestMapping(value = "/findpwd/sendremail", method = RequestMethod.POST)
    public String sendEmailRegResetPwd(BaseAccountParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String passportId = params.getPassport_id();
        int clientId = Integer.parseInt(params.getClient_id());
        return accountSecureManager.sendEmailResetPwdByPassportId(passportId, clientId, true).toString();
    }

    /**
     * 查询账号所拥有的密码找回方式
     *
     * @param params 传入的参数
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public String queryPassportId(AccountPwdScodeParams params, Model model) throws Exception {
        int clientId = Integer.parseInt(params.getClient_id());
        String passportId = accountManager.getPassportIdByUsername(params.getPassport_id());
        if (passportId == null) {

        }
        params.setPassport_id(passportId);
//        // model.addAttribute("exist", true);
//        Result result = accountSecureManager.queryAccountSecureInfo(
//                passportId, clientId);
//        if (result.getStatus().equals("0")) {
//            model.addAttribute("secInfo",result.getData());
//        } else {
//            model.addAttribute("error", result);
//        }
        model.addAttribute("passportId", passportId);
        model.addAttribute("clientId", clientId);
        return "forward:";
    }

    @RequestMapping(value = "/mobile", method = RequestMethod.POST)
    public String resetPasswordByMobile(@RequestParam("username") String passportId, @RequestParam("client_id") int clientId,
                                        @RequestParam("password") String password, @RequestParam("smscode") String smsCode, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        if (Strings.isNullOrEmpty(passportId)) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            model.addAttribute("error", result.toString());
            return "forward:";
        }
        if (Strings.isNullOrEmpty(password) || Strings.isNullOrEmpty(smsCode)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            model.addAttribute("error", result);
            return "forward:";
        }
        result = accountSecureManager.resetPasswordByMobile(passportId, clientId, password, smsCode);
        model.addAttribute("error", result);
        if (result.isSuccess()) {
            // 重置密码成功
            // TODO
            return "success";
        } else {
            return "forward:";
        }
    }

    @RequestMapping(value = "/sendemail", method = RequestMethod.POST)
    public String sendEmail(@RequestParam("username") String passportId, @RequestParam("client_id") String client_id,
                            Model model) throws Exception {
        Result result = new APIResultSupport(false);
        if (!StringUtil.checkIsDigit(client_id)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            model.addAttribute("error", result);
            return "forward:";
        }
        int clientId = Integer.parseInt(client_id);
        result = accountSecureManager.sendEmailResetPwdByPassportId(passportId, clientId, false);
        model.addAttribute("error", result);
        if (result.isSuccess()) {
            return "forward:";
        }
        return "forward:";
    }

    @RequestMapping(value = "/findpwd/checkemail", method = RequestMethod.GET)
    public String checkEmailForResetPwd(AccountScodeParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            model.addAttribute("error", result);
        }
        String passportId = params.getPassport_id();
        int clientId = Integer.parseInt(params.getClient_id());
        String scode = params.getScode();
        result = accountSecureManager.checkEmailResetPwd(passportId, clientId, scode);
        model.addAttribute("error", result);
        if (result.isSuccess()) {
            model.addAttribute("passport_id", passportId);
            model.addAttribute("client_id", clientId);
            model.addAttribute("scode", scode);
            return "resetpwd";
        }
        return "forward:";
    }

    @RequestMapping(value = "/email", method = RequestMethod.POST)
    public String resetPasswordByEmail(@RequestParam("username") String passportId, @RequestParam("client_id") String client_id,
                                       @RequestParam("password") String password, @RequestParam("token") String token, Model model) throws Exception {
        int clientId = Integer.parseInt(client_id);
        Result result = accountSecureManager.resetPasswordByEmail(passportId, clientId, password, token);
        model.addAttribute("error", result);
        return "success";
    }

    @RequestMapping(value = "/ques", method = RequestMethod.POST)
    public String resetPasswordByQues(@RequestParam("username") String passportId, @RequestParam("client_id") String client_id,
                                      @RequestParam("password") String password, String answer, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        if (Strings.isNullOrEmpty(passportId) || Strings.isNullOrEmpty(password) || Strings.isNullOrEmpty(answer)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            model.addAttribute("error", result);
            return "forward:";
        }
        int clientId = Integer.parseInt(client_id);

        if (!accountCheckManager.checkLimitResetPwd(passportId, clientId)) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_LIMITED);
            model.addAttribute("error", result);
        }
        result = accountSecureManager.resetPasswordByQues(passportId, clientId, password, answer);
        model.addAttribute("error", result);
        if (result.isSuccess()) {
            return "success";
        } else {
            return "forward:";
        }
    }

}
