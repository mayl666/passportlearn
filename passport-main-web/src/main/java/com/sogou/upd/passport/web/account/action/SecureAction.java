package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.CheckManager;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.ResetPwdManager;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.account.vo.AccountSecureInfoVO;
import com.sogou.upd.passport.manager.form.ResetPwdParameters;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.account.form.AccountBindEmailParams;
import com.sogou.upd.passport.web.account.form.AccountScodeParams;
import com.sogou.upd.passport.web.account.form.BaseWebParams;
import com.sogou.upd.passport.web.account.form.security.WebBindEmailParams;
import com.sogou.upd.passport.web.account.form.security.WebBindMobileParams;
import com.sogou.upd.passport.web.account.form.security.WebBindQuesParams;
import com.sogou.upd.passport.web.account.form.security.WebMobileParams;
import com.sogou.upd.passport.web.account.form.security.WebModifyMobileParams;
import com.sogou.upd.passport.web.account.form.security.WebSmsParams;
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

    // TODO:GET或POST方法不支持时，前端页面

    @RequestMapping(method = RequestMethod.GET)
    @LoginRequired
    public String querySecureInfo(BaseWebParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            model.addAttribute("data", result.toString());
            return "safe/index"; // TODO:返回错误页面
        }
        String userId = hostHolder.getPassportId();
        int clientId = Integer.parseInt(params.getClient_id());

        result = secureManager.queryAccountSecureInfo(userId, clientId, true);
        result.setDefaultModel("username", userId);
        model.addAttribute("data", result.toString());

        return "safe/index";
    }

    @RequestMapping(value = "/email", method = RequestMethod.GET)
    @LoginRequired
    public String bindEmailView(BaseWebParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            model.addAttribute("data", result.toString());
            return "safe/email"; // TODO:错误页面
        }
        String userId = hostHolder.getPassportId();
        int clientId = Integer.parseInt(params.getClient_id());

        result = secureManager.queryAccountSecureInfo(userId, clientId, true);

        result.setSuccess(true);
        result.setDefaultModel("username", userId);
        model.addAttribute("data", result.toString());
        return "safe/email";
    }

    @RequestMapping(value = "/mobile", method = RequestMethod.GET)
    @LoginRequired
    public String bindMobileView(BaseWebParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            model.addAttribute("data", result.toString());
            return "safe/tel"; // TODO:错误页面
        }
        String userId = hostHolder.getPassportId();
        int clientId = Integer.parseInt(params.getClient_id());

        result = secureManager.queryAccountSecureInfo(userId, clientId, true);

        result.setSuccess(true);
        result.setDefaultModel("username", userId);
        model.addAttribute("data", result.toString());
        return "safe/tel";
    }


    @RequestMapping(value = "/question", method = RequestMethod.GET)
    @LoginRequired
    public String bindQuestionView(BaseWebParams params, Model model) throws Exception {
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

        result = secureManager.queryAccountSecureInfo(userId, clientId, true);

        result.setSuccess(true);
        result.setDefaultModel("username", userId);
        model.addAttribute("data", result.toString());
        return "safe/question";
    }

    @RequestMapping(value = "/password", method = RequestMethod.GET)
    @LoginRequired
    public String modifyPasswordView(BaseWebParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            model.addAttribute("data", result.toString());
            return "safe/password"; // TODO:错误页面
        }
        String userId = hostHolder.getPassportId();
        int clientId = Integer.parseInt(params.getClient_id());

        result.setSuccess(true);
        result.setDefaultModel("username", userId);
        model.addAttribute("data", result.toString());
        return "safe/password";
    }

    @RequestMapping(value = "/history", method = RequestMethod.GET)
    @LoginRequired
    public String showHistoryView(BaseWebParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            model.addAttribute("data", result.toString());
            return "safe/history"; // TODO:错误页面
        }
        String userId = hostHolder.getPassportId();
        int clientId = Integer.parseInt(params.getClient_id());

        // TODO:登录历史

        result.setSuccess(true);
        result.setDefaultModel("username", userId);
        model.addAttribute("data", result.toString());
        return "safe/history";
    }

    /**
     * 修改密码
     *
     * @param resetParams 传入的参数
     */
    @RequestMapping(value = "/resetpwd", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public Object resetpwd(HttpServletRequest request, ResetPwdParameters resetParams)
            throws Exception {
        Result result = new APIResultSupport(false);

        String validateResult = ControllerHelper.validateParams(resetParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result;
        }

        resetParams.setPassport_id(hostHolder.getPassportId());
        String modifyIp = getIp(request);
        resetParams.setIp(modifyIp);

        result = secureManager.resetWebPassword(resetParams);
        return result;
    }

    @RequestMapping(value = "/sendemail", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public String sendEmailForBind(WebBindEmailParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String userId = hostHolder.getPassportId();
        int clientId = Integer.parseInt(params.getClient_id());
        String password = params.getPassword();
        String newEmail = params.getNew_email();
        String oldEmail = params.getOld_email();
        result = secureManager.sendEmailForBinding(userId, clientId, password, newEmail, oldEmail);
        return result.toString();
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

        // result = secureManager.sendMobileCodeByPassportId(userId, clientId);
        result = secureManager.sendMobileCodeOld(userId, clientId);
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
        String userId = hostHolder.getPassportId();
        int clientId = Integer.parseInt(params.getClient_id());
        String newMobile = params.getNew_mobile();

        // result = secureManager.sendMobileCode(newMobile, clientId);
        result = secureManager.sendMobileCodeNew(userId, clientId, newMobile);
        return result.toString();
    }

    @RequestMapping(value = "/bindmobile", method = RequestMethod.POST)
    @LoginRequired
    public String bindMobile(WebBindMobileParams params, HttpServletRequest request, Model model) throws Exception {
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
        String smsCode = params.getSmscode();
        String newMobile = params.getNew_mobile();
        String password = params.getPassword();
        String modifyIp = getIp(request);

        if (AccountDomainEnum.getAccountDomain(userId) == AccountDomainEnum.PHONE) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_MOBILEUSER_NOTALLOWED);
            model.addAttribute("data", result.toString());
            return ""; // TODO:错误页面
        }

        result = secureManager.bindMobileByPassportId(userId, clientId, newMobile, smsCode, password, modifyIp);
        model.addAttribute("data", result.toString());
        if (result.isSuccess()) {
            return ""; // TODO:成功页面
        } else {
            return ""; // TODO:错误页面
        }
    }

    @RequestMapping(value = "/checksms", method = RequestMethod.POST)
    @LoginRequired
    public String checkSmsSecMobile(WebSmsParams params, Model model) throws Exception {
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
        String smsCode = params.getSmscode();

        if (AccountDomainEnum.getAccountDomain(userId) == AccountDomainEnum.PHONE) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_MOBILEUSER_NOTALLOWED);
            model.addAttribute("data", result.toString());
            return ""; // TODO:错误页面
        }

        result = secureManager.checkMobileCodeOldForBinding(userId, clientId, smsCode);
        model.addAttribute("data", result.toString());
        if (result.isSuccess()) {
            return ""; // TODO:成功页面
        } else {
            return ""; // TODO:错误页面
        }
    }

    @RequestMapping(value = "bindmobilenew", method = RequestMethod.POST)
    @LoginRequired
    public String modifyMobile(WebModifyMobileParams params, HttpServletRequest request, Model model) throws Exception {
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
        String smsCode = params.getSmscode();
        String newMobile = params.getNew_mobile();
        String scode = params.getScode();
        String modifyIp = getIp(request);

        if (AccountDomainEnum.getAccountDomain(userId) == AccountDomainEnum.PHONE) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_MOBILEUSER_NOTALLOWED);
            model.addAttribute("data", result.toString());
            return ""; // TODO:错误页面
        }

        result = secureManager.modifyMobileByPassportId(userId, clientId, newMobile, smsCode, scode, modifyIp);
        model.addAttribute("data", result.toString());
        if (result.isSuccess()) {
            return ""; // TODO:成功页面
        } else {
            return ""; // TODO:错误页面
        }
    }

    @RequestMapping(value = "/bindques", method = RequestMethod.POST)
    @LoginRequired
    public String bindQues(WebBindQuesParams params, HttpServletRequest request, Model model) throws Exception {
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
        String newQues = params.getNew_ques();
        String newAnswer = params.getNew_answer();
        String modifyIp = getIp(request);

        result = secureManager.modifyQuesByPassportId(userId, clientId, password, newQues, newAnswer, modifyIp);
        model.addAttribute("data", result.toString());
        if (result.isSuccess()) {
            return ""; // TODO:成功页面
        } else {
            return ""; // TODO:错误页面
        }
    }
}
