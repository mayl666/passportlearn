package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.CheckManager;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.ResetPwdManager;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.form.ResetPwdParameters;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.account.form.AccountBindEmailParams;
import com.sogou.upd.passport.web.account.form.AccountScodeParams;
import com.sogou.upd.passport.web.account.form.BaseWebParams;
import com.sogou.upd.passport.web.account.form.security.WebBindEmailParams;
import com.sogou.upd.passport.web.account.form.security.WebBindMobileParams;
import com.sogou.upd.passport.web.account.form.security.WebMobileParams;
import com.sogou.upd.passport.web.annotation.LoginRequired;
import com.sogou.upd.passport.web.inteceptor.HostHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * User: hujunfei Date: 13-4-28 Time: 下午1:51 安全中心（修改密码，修改密保手机，修改密保问题，修改密保邮箱）
 */
@Controller
@RequestMapping("/web/security")
public class SecureAction extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(SecureAction.class);

    @Autowired
    private CommonManager commonManager;
    @Autowired
    private SecureManager secureManager;
    @Autowired
    private CheckManager checkManager;
    @Autowired
    private ResetPwdManager resetPwdManager;
    @Autowired
    private HostHolder hostHolder;

    // TODO:GET方法怎么处理？

    /**
     * 修改密码
     *
     * @param resetParams 传入的参数
     */
    @RequestMapping(value = "/resetpwd", method = RequestMethod.POST)
    @ResponseBody
    public Object resetpwd(HttpServletRequest request, ResetPwdParameters resetParams)
            throws Exception {
        Result result = new APIResultSupport(false);
        //todo 注解需要判断登录
        String validateResult = ControllerHelper.validateParams(resetParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result;
        }
        result = secureManager.resetWebPassword(resetParams);
        return result;
    }

    @RequestMapping(value = "/mobile", method = RequestMethod.POST)
    @ResponseBody
    public String resetPasswordByMobile(@RequestParam("username") String passportId,
                                        @RequestParam("client_id") int clientId,
                                        @RequestParam("password") String password,
                                        @RequestParam("smscode") String smsCode, Model model)
            throws Exception {
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
        result = resetPwdManager.resetPasswordByMobile(passportId, clientId, password, smsCode);
        model.addAttribute("error", result);
        if (result.isSuccess()) {
            // 重置密码成功
            // TODO
            return "success";
        } else {
            return "forward:";
        }
    }

    @RequestMapping(value = "/getsecinfo", method = RequestMethod.POST)
    @LoginRequired
    public String querySecureInfo(BaseWebParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            model.addAttribute("data", result.toString());
            return ""; // TODO:返回错误页面
        }
        String userId = hostHolder.getPassportId();
        int clientId = Integer.parseInt(params.getClient_id());

        // TODO:已修改为代理接口
        result = secureManager.queryAccountSecureInfo(userId, clientId, true);
        model.addAttribute("data", result.toString());

        return "ucenter/index";
    }


    @RequestMapping(value = "/sendemail", method = RequestMethod.POST)
    @LoginRequired
    public String sendEmailForBind(WebBindEmailParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            model.addAttribute("data", result.toString());
            return ""; // TODO:错误页面
        }
        String userId = hostHolder.getPassportId();
        int clientId = Integer.parseInt(params.getClient_id());
        String password = params.getPassword();
        String newEmail = params.getNew_email();
        String oldEmail = params.getOld_email();
        result = secureManager.sendEmailForBinding(userId, clientId, password, newEmail, oldEmail);
        model.addAttribute("data", result.toString());
        if (result.isSuccess()) {
            return ""; // TODO:成功页面
        } else {
            return ""; // TODO：错误页面
        }
    }

    @RequestMapping(value = "checkemail", method = RequestMethod.GET)
    public String checkEmailForBind(AccountScodeParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            model.addAttribute("data", result.toString());
            return ""; // TODO:错误页面
        }
        String userId = params.getUserid();
        int clientId = Integer.parseInt(params.getClient_id());
        String scode = params.getScode();

        result = secureManager.modifyEmailByPassportId(userId, clientId, scode);
        model.addAttribute("data", result.toString());
        if (result.isSuccess()) {
            return ""; // TODO:成功页面
        } else {
            return ""; // TODO:错误页面
        }
    }

    @RequestMapping(value = "/sendsms", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    @LoginRequired
    public Object sendSmsSecMobile(BaseWebParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String userId = hostHolder.getPassportId();
        int clientId = Integer.parseInt(params.getClient_id());

        result = secureManager.sendMobileCodeByPassportId(userId, clientId);
        return result.toString();
    }

    @RequestMapping(value = "/sendsmsnew", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    @LoginRequired
    public Object sendSmsNewMobile(WebMobileParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        // TODO:要不要在检验smscode时，验证userId
        // String userId = hostHolder.getPassportId();
        int clientId = Integer.parseInt(params.getClient_id());
        String newMobile = params.getNew_mobile();

        result = secureManager.sendMobileCode(newMobile, clientId);
        return result.toString();
    }

    @RequestMapping(value = "/bindmobile", method = RequestMethod.POST)
    @LoginRequired
    public String bindMobile(WebBindMobileParams params, Model model) throws Exception {
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

        result = secureManager.bindMobileByPassportId(userId, clientId, newMobile, smsCode, scode);
        model.addAttribute("data", result.toString());
        if (result.isSuccess()) {
            return ""; // TODO:成功页面
        } else {
            return ""; // TODO:错误页面
        }
    }

    @RequestMapping(value = "/email", method = RequestMethod.POST)
    @ResponseBody
    public String resetPasswordByEmail(@RequestParam("username") String passportId,
                                       @RequestParam("client_id") String client_id,
                                       @RequestParam("password") String password,
                                       @RequestParam("token") String token, Model model)
            throws Exception {
        int clientId = Integer.parseInt(client_id);
        Result result = resetPwdManager.resetPasswordByEmail(passportId, clientId, password, token);
        model.addAttribute("error", result);
        return "success";
    }

    @RequestMapping(value = "/ques", method = RequestMethod.POST)
    @ResponseBody
    public String resetPasswordByQues(@RequestParam("username") String passportId,
                                      @RequestParam("client_id") String client_id,
                                      @RequestParam("password") String password, String answer,
                                      Model model) throws Exception {
        Result result = new APIResultSupport(false);
        if (Strings.isNullOrEmpty(passportId) || Strings.isNullOrEmpty(password) || Strings
                .isNullOrEmpty(answer)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            model.addAttribute("error", result);
            return "forward:";
        }
        int clientId = Integer.parseInt(client_id);

        if (!checkManager.checkLimitResetPwd(passportId, clientId)) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_LIMITED);
            model.addAttribute("error", result);
        }
        result = resetPwdManager.resetPasswordByQues(passportId, clientId, password, answer);
        model.addAttribute("error", result);
        if (result.isSuccess()) {
            return "success";
        } else {
            return "forward:";
        }
    }

}
