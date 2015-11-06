package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.AccountInfoManager;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.form.UpdatePwdParameters;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.BaseWebParams;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.form.security.WebBindQuesParams;
import com.sogou.upd.passport.web.annotation.LoginRequired;
import com.sogou.upd.passport.web.annotation.ResponseResultType;
import com.sogou.upd.passport.web.inteceptor.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * User: hujunfei Date: 13-4-28 Time: 下午1:51 安全中心（修改密码，修改密保手机，修改密保问题，修改密保邮箱）
 */
@Controller
@RequestMapping("/web/security")
public class SecureAction extends BaseController {

//    private static final String SOHU_RESETPWD_URL = SHPPUrlConstant.SOHU_RESETPWD_URL;
    private static final String SOHU_BINDEMAIL_URL = SHPPUrlConstant.SOHU_BINDEMAIL_URL;
    private static final String SOHU_BINDMOBILE_URL = SHPPUrlConstant.SOHU_BINDMOBILE_URL;
    private static final String SOHU_BINDQUES_URL = SHPPUrlConstant.SOHU_BINDQUES_URL;

    @Autowired
    private SecureManager secureManager;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private AccountInfoManager accountInfoManager;

    /*
     * 查询安全信息
     */
    @RequestMapping(method = RequestMethod.GET)
    @LoginRequired(resultType = ResponseResultType.redirect)
    public String querySecureInfo(BaseWebParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            // model.addAttribute("data", result.toString());
            return "redirect:/"; // TODO:返回错误页面
        }
        String userId = hostHolder.getPassportId();
        int clientId = Integer.parseInt(params.getClient_id());
        AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(userId);
        // 第三方账号不显示安全信息
        if (AccountDomainEnum.getAccountDomain(userId) == AccountDomainEnum.THIRD) {
            // result.setDefaultModel("disable", true);
            // result.setSuccess(true);
            // model.addAttribute("data", result.toString());
            return "redirect:/";
        } else {
            result = secureManager.queryAccountSecureInfo(userId, clientId, true);
            //查询账号安全信息页面，密保邮箱、手机模糊处理
            if (result.isSuccess()) {
                processSecureMailMobile(result);
            }
        }
        result.setDefaultModel("username", accountInfoManager.getUniqName(userId, clientId, true));
        if (domain == AccountDomainEnum.PHONE) {
            result.setDefaultModel("actype", "phone");
        }

        ControllerHelper.process(result, clientId, null);

        model.addAttribute("data", result.toString());

        return "safe/index";
    }

    /*
     * 显示绑定邮箱界面
     */
    @RequestMapping(value = "/email", method = RequestMethod.GET)
    @LoginRequired(resultType = ResponseResultType.redirect)
    public String bindEmailView(BaseWebParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            model.addAttribute("data", result.toString());
            return "redirect:/"; // TODO:错误页面
        }
        String userId = hostHolder.getPassportId();
        int clientId = Integer.parseInt(params.getClient_id());
        AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(userId);

        switch (domain) {
//            case SOHU:
//                return "redirect:" + SOHU_BINDEMAIL_URL;
            case THIRD:
                return "redirect:/";
        }
        result = secureManager.queryAccountSecureInfo(userId, clientId, true);
        //绑定邮箱页面，密保邮箱、手机模糊处理
        if (result.isSuccess()) {
            processSecureMailMobile(result);
        }
        result.setDefaultModel("username", result.getModels().get("uniqname"));
//        result.setDefaultModel("username", accountInfoManager.getUniqName(userId, clientId));
        if (domain == AccountDomainEnum.PHONE) {
            result.setDefaultModel("actype", "phone");
        }
        ControllerHelper.process(result, clientId, null);
        model.addAttribute("data", result.toString());
        return "safe/email";
    }

    /*
     * 显示绑定手机界面
     */
    @RequestMapping(value = "/mobile", method = RequestMethod.GET)
    @LoginRequired(resultType = ResponseResultType.redirect)
    public String bindMobileView(BaseWebParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            model.addAttribute("data", result.toString());
            return "redirect:/"; // TODO:错误页面
        }
        String userId = hostHolder.getPassportId();
        int clientId = Integer.parseInt(params.getClient_id());
        AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(userId);

        switch (domain) {
            case SOHU:
                return "redirect:" + SOHU_BINDMOBILE_URL;
            case THIRD:
                return "redirect:/";
            case PHONE:
                return "redirect:/web/security";
        }
        result = secureManager.queryAccountSecureInfo(userId, clientId, true);
        //绑定手机页面，密保邮箱、手机模糊处理
        if (result.isSuccess()) {
            processSecureMailMobile(result);
        }
        result.setDefaultModel("username", result.getModels().get("uniqname"));
//        result.setDefaultModel("username", accountInfoManager.getUniqName(userId, clientId));
        if (domain == AccountDomainEnum.PHONE) {
            result.setDefaultModel("actype", "phone");
        }
        ControllerHelper.process(result, clientId, null);
        model.addAttribute("data", result.toString());
        return "safe/tel";
    }

    /*
     * 显示绑定密保问题界面
     */
    @RequestMapping(value = "/question", method = RequestMethod.GET)
    @LoginRequired(resultType = ResponseResultType.redirect)
    public String bindQuestionView(BaseWebParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            model.addAttribute("data", result.toString());
            return "redirect:/"; // TODO:错误页面
        }
        String userId = hostHolder.getPassportId();
        int clientId = Integer.parseInt(params.getClient_id());
        AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(userId);

        switch (domain) {
            case SOHU:
                return "redirect:" + SOHU_BINDQUES_URL;
            case THIRD:
                return "redirect:/";
        }
        result = secureManager.queryAccountSecureInfo(userId, clientId, true);
        result.setDefaultModel("username", result.getModels().get("uniqname"));
//        result.setDefaultModel("username", accountInfoManager.getUniqName(userId, clientId));
        if (domain == AccountDomainEnum.PHONE) {
            result.setDefaultModel("actype", "phone");
        }
        ControllerHelper.process(result, clientId, null);
        model.addAttribute("data", result.toString());
        return "safe/question";
    }

    /*
     * 显示修改密码界面
     */
    @RequestMapping(value = "/password", method = RequestMethod.GET)
    @LoginRequired(resultType = ResponseResultType.redirect)
    public String modifyPasswordView(BaseWebParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            model.addAttribute("data", result.toString());
            return "redirect:/"; // TODO:错误页面
        }
        String userId = hostHolder.getPassportId();
        int clientId = Integer.parseInt(params.getClient_id());
        AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(userId);

        switch (domain) {
//            case SOHU:
//                return "redirect:" + SOHU_RESETPWD_URL;
            case THIRD:
                return "redirect:/";
        }
        result.setSuccess(true);
        result.setDefaultModel("username", accountInfoManager.getUniqName(userId, clientId, true));
        if (domain == AccountDomainEnum.PHONE) {
            result.setDefaultModel("actype", "phone");
        }
        ControllerHelper.process(result, clientId, null);
        model.addAttribute("data", result.toString());
        return "safe/password";
    }

    /*
     * 显示登录历史
     */
    @RequestMapping(value = "/history", method = RequestMethod.GET)
    @LoginRequired(resultType = ResponseResultType.redirect)
    public String showHistoryView(BaseWebParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            model.addAttribute("data", result.toString());
            return "redirect:/"; // TODO:错误页面
        }
        String userId = hostHolder.getPassportId();
        int clientId = Integer.parseInt(params.getClient_id());
        AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(userId);

        switch (domain) {
            case THIRD:
                return "redirect:/";
        }
        result = secureManager.queryActionRecords(userId, clientId, AccountModuleEnum.LOGIN);
        result.setSuccess(true);
        result.setDefaultModel("username", accountInfoManager.getUniqName(userId, clientId, true));
        if (domain == AccountDomainEnum.PHONE) {
            result.setDefaultModel("actype", "phone");
        }
        ControllerHelper.process(result, clientId, null);
        model.addAttribute("data", result.toString());
        return "safe/history";
    }

    /**
     * 官网修改密码
     *
     * @param updateParams 传入的参数
     */
    @RequestMapping(value = "/updatepwd", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public Object updatePwd(HttpServletRequest request, UpdatePwdParameters updateParams)
            throws Exception {
        Result result = new APIResultSupport(false);
        String passportId = hostHolder.getPassportId();
        updateParams.setPassport_id(passportId);
        String modifyIp = getIp(request);
        updateParams.setIp(modifyIp);
        try {
            String validateResult = ControllerHelper.validateParams(updateParams);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result;
            }
            switch (AccountDomainEnum.getAccountDomain(passportId)) {
//                case SOHU:
//                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTALLOWED);
//                    return result.toString();
                case THIRD:
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTALLOWED);
                    return result.toString();
            }
            result = secureManager.updateWebPwd(updateParams);
            return result.toString();
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog(passportId, request.getRequestURI(), updateParams.getClient_id(), result.getCode(), modifyIp);
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            UserOperationLogUtil.log(userOperationLog);
        }
    }

    /*
     * 绑定密保问题和答案
     */
    @RequestMapping(value = "/bindques", method = RequestMethod.POST)
    @LoginRequired
    @ResponseBody
    public Object bindQues(HttpServletRequest request, WebBindQuesParams params)
            throws Exception {
        Result result = new APIResultSupport(false);
        String userId = hostHolder.getPassportId();
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                int sep = validateResult.indexOf("|");
                result.setMessage(sep == -1 ? validateResult : validateResult.substring(0, sep));
                return result.toString();
            }
            int clientId = Integer.parseInt(params.getClient_id());
            String password = params.getPassword();
            String newQues = params.getNew_ques();
            String newAnswer = params.getNew_answer();
            String modifyIp = getIp(request);
            switch (AccountDomainEnum.getAccountDomain(userId)) {
                case SOHU:
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SOHU_NOTALLOWED);
                    return result.toString();
                case THIRD:
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_THIRD_NOTALLOWED);
                    return result.toString();
            }
            result = secureManager.modifyQuesByPassportId(userId, clientId, password, newQues, newAnswer, modifyIp);
            return result.toString();
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog(userId, request.getRequestURI(), params.getClient_id(), result.getCode(), getIp(request));
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            UserOperationLogUtil.log(userOperationLog);
        }
    }
}
