package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.form.UpdatePwdParameters;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.BaseWebParams;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.account.form.AccountScodeParams;
import com.sogou.upd.passport.web.account.form.security.WebBindEmailParams;
import com.sogou.upd.passport.web.account.form.security.WebBindMobileParams;
import com.sogou.upd.passport.web.account.form.security.WebBindQuesParams;
import com.sogou.upd.passport.web.account.form.security.WebMobileParams;
import com.sogou.upd.passport.web.account.form.security.WebModifyMobileParams;
import com.sogou.upd.passport.web.account.form.security.WebSmsParams;
import com.sogou.upd.passport.web.annotation.LoginRequired;
import com.sogou.upd.passport.web.annotation.ResponseResultType;
import com.sogou.upd.passport.web.inteceptor.HostHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(SecureAction.class);

    private static final String SOHU_RESETPWD_URL = SHPPUrlConstant.SOHU_RESETPWD_URL;
    private static final String SOHU_BINDEMAIL_URL = SHPPUrlConstant.SOHU_BINDEMAIL_URL;
    private static final String SOHU_BINDMOBILE_URL = SHPPUrlConstant.SOHU_BINDMOBILE_URL;
    private static final String SOHU_BINDQUES_URL = SHPPUrlConstant.SOHU_BINDQUES_URL;

    @Autowired
    private SecureManager secureManager;
    @Autowired
    private HostHolder hostHolder;


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
            result.setDefaultModel("disable", true);
            result.setSuccess(true);
        } else {
            result = secureManager.queryAccountSecureInfo(userId, clientId, true);
        }

        String nickName = hostHolder.getNickName();
        if (Strings.isNullOrEmpty(nickName)) {
            nickName = userId;
        }
        result.setDefaultModel("username", nickName);
        if (domain == AccountDomainEnum.PHONE) {
            result.setDefaultModel("actype", "phone");
        }

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
            return "safe/email"; // TODO:错误页面
        }
        String userId = hostHolder.getPassportId();
        int clientId = Integer.parseInt(params.getClient_id());
        AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(userId);

        switch (domain) {
            case SOHU:
                return "redirect:" + SOHU_BINDEMAIL_URL;
            case THIRD:
                return "redirect:/web/security";
        }

        result = secureManager.queryAccountSecureInfo(userId, clientId, true);

        result.setSuccess(true);
        String nickName = hostHolder.getNickName();
        if (Strings.isNullOrEmpty(nickName)) {
            nickName = userId;
        }
        result.setDefaultModel("username", nickName);
        if (domain == AccountDomainEnum.PHONE) {
            result.setDefaultModel("actype", "phone");
        }
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
            return "safe/tel"; // TODO:错误页面
        }
        String userId = hostHolder.getPassportId();
        int clientId = Integer.parseInt(params.getClient_id());
        AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(userId);

        switch (domain) {
            case SOHU:
                return "redirect:" + SOHU_BINDMOBILE_URL;
            case THIRD:
                return "redirect:/web/security";
        }

        result = secureManager.queryAccountSecureInfo(userId, clientId, true);

        result.setSuccess(true);
        String nickName = hostHolder.getNickName();
        if (Strings.isNullOrEmpty(nickName)) {
            nickName = userId;
        }
        result.setDefaultModel("username", nickName);
        if (domain == AccountDomainEnum.PHONE) {
            result.setDefaultModel("actype", "phone");
        }
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
            return "safe/question"; // TODO:错误页面
        }
        String userId = hostHolder.getPassportId();
        int clientId = Integer.parseInt(params.getClient_id());
        AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(userId);

        switch (domain) {
            case SOHU:
                return "redirect:" + SOHU_BINDQUES_URL;
            case THIRD:
                return "redirect:/web/security";
        }

        result = secureManager.queryAccountSecureInfo(userId, clientId, true);

        result.setSuccess(true);
        String nickName = hostHolder.getNickName();
        if (Strings.isNullOrEmpty(nickName)) {
            nickName = userId;
        }
        result.setDefaultModel("username", nickName);
        if (domain == AccountDomainEnum.PHONE) {
            result.setDefaultModel("actype", "phone");
        }
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
            return "safe/password"; // TODO:错误页面
        }
        String userId = hostHolder.getPassportId();
        int clientId = Integer.parseInt(params.getClient_id());
        AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(userId);

        switch (domain) {
            case SOHU:
                return "redirect:" + SOHU_RESETPWD_URL;
            case THIRD:
                return "redirect:/web/security";
        }

        result.setSuccess(true);
        String nickName = hostHolder.getNickName();
        if (Strings.isNullOrEmpty(nickName)) {
            nickName = userId;
        }
        result.setDefaultModel("username", nickName);
        if (domain == AccountDomainEnum.PHONE) {
            result.setDefaultModel("actype", "phone");
        }
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
            return "safe/history"; // TODO:错误页面
        }
        String userId = hostHolder.getPassportId();
        int clientId = Integer.parseInt(params.getClient_id());
        AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(userId);

        result = secureManager.queryActionRecords(userId, clientId, AccountModuleEnum.LOGIN);

        result.setSuccess(true);
        String nickName = hostHolder.getNickName();
        if (Strings.isNullOrEmpty(nickName)) {
            nickName = userId;
        }
        result.setDefaultModel("username", nickName);
        if (domain == AccountDomainEnum.PHONE) {
            result.setDefaultModel("actype", "phone");
        }
        model.addAttribute("data", result.toString());
        return "safe/history";
    }

    /**
     * 修改密码
     *
     * @param updateParams 传入的参数
     */
    @RequestMapping(value = "/updatepwd", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public Object updatePwd(HttpServletRequest request, UpdatePwdParameters updateParams)
            throws Exception {
        Result result = new APIResultSupport(false);

        String validateResult = ControllerHelper.validateParams(updateParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result;
        }

        String userId = hostHolder.getPassportId();

        switch (AccountDomainEnum.getAccountDomain(userId)) {
            case SOHU:
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SOHU_NOTALLOWED);
                return result.toString();
            case THIRD:
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_THIRD_NOTALLOWED);
                return result.toString();
        }

        updateParams.setPassport_id(userId);
        String modifyIp = getIp(request);
        updateParams.setIp(modifyIp);

        result = secureManager.resetWebPassword(updateParams);
        return result.toString();
    }

    /*
     * 发送绑定邮箱申请邮件
     */
    @RequestMapping(value = "/sendemail", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public Object sendEmailForBind(WebBindEmailParams params, Model model) throws Exception {
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

        switch (AccountDomainEnum.getAccountDomain(userId)) {
            case SOHU:
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SOHU_NOTALLOWED);
                return result.toString();
            case THIRD:
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_THIRD_NOTALLOWED);
                return result.toString();
        }

        result = secureManager.sendEmailForBinding(userId, clientId, password, newEmail, oldEmail);
        return result.toString();
    }

    // TODO:等数据迁移后需要修改和测试此方法
    /*
     * 验证绑定邮件
     */
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

        switch (AccountDomainEnum.getAccountDomain(userId)) {
            case SOHU:
                return "redirect:" + SOHU_BINDEMAIL_URL;
            case THIRD:
                return "redirect:/web/security";
        }

        result = secureManager.modifyEmailByPassportId(userId, clientId, scode);
        model.addAttribute("data", result.toString());
        if (result.isSuccess()) {
            return ""; // TODO:成功页面
        } else {
            return ""; // TODO:错误页面
        }
    }

    /*
     * 修改绑定手机，发送短信验证码至原绑定手机
     */
    @RequestMapping(value = "/sendsms", method = RequestMethod.GET)
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

        switch (AccountDomainEnum.getAccountDomain(userId)) {
            case SOHU:
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SOHU_NOTALLOWED);
                return result.toString();
            case THIRD:
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_THIRD_NOTALLOWED);
                return result.toString();
            case PHONE:
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_MOBILEUSER_NOTALLOWED);
                return result.toString();
        }

        // result = secureManager.sendMobileCodeByPassportId(userId, clientId);
        result = secureManager.sendMobileCodeOld(userId, clientId);
        return result.toString();
    }

    /*
     * 绑定和修改密保手机，发送短信验证码至新绑定手机
     */
    @RequestMapping(value = "/sendsmsnew", method = RequestMethod.GET)
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

        switch (AccountDomainEnum.getAccountDomain(userId)) {
            case SOHU:
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SOHU_NOTALLOWED);
                return result.toString();
            case THIRD:
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_THIRD_NOTALLOWED);
                return result.toString();
            case PHONE:
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_MOBILEUSER_NOTALLOWED);
                return result.toString();
        }

        // result = secureManager.sendMobileCode(newMobile, clientId);
        result = secureManager.sendMobileCodeNew(userId, clientId, newMobile);
        return result.toString();
    }

    /*
     * 绑定密保手机
     */
    @RequestMapping(value = "/bindmobile", method = RequestMethod.POST)
    @LoginRequired
    @ResponseBody
    public Object bindMobile(WebBindMobileParams params, HttpServletRequest request, Model model) throws Exception {
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
        String password = params.getPassword();
        String modifyIp = getIp(request);

        switch (AccountDomainEnum.getAccountDomain(userId)) {
            case PHONE:
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_MOBILEUSER_NOTALLOWED);
                return result.toString();
            case SOHU:
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SOHU_NOTALLOWED);
                return result.toString();
            case THIRD:
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_THIRD_NOTALLOWED);
                return result.toString();
        }

        result = secureManager.bindMobileByPassportId(userId, clientId, newMobile, smsCode, password, modifyIp);
        return result.toString();
    }

    /*
     * 验证原绑定手机短信验证码
     */
    @RequestMapping(value = "/checksms", method = RequestMethod.POST)
    @LoginRequired
    @ResponseBody
    public Object checkSmsSecMobile(WebSmsParams params, Model model) throws Exception {
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

        switch (AccountDomainEnum.getAccountDomain(userId)) {
            case PHONE:
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_MOBILEUSER_NOTALLOWED);
                return result.toString();
            case SOHU:
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SOHU_NOTALLOWED);
                return result.toString();
            case THIRD:
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_THIRD_NOTALLOWED);
                return result.toString();
        }

        result = secureManager.checkMobileCodeOldForBinding(userId, clientId, smsCode);
        return result.toString();
    }

    /*
     * 修改密保手机
     */
    @RequestMapping(value = "bindmobilenew", method = RequestMethod.POST)
    @LoginRequired
    @ResponseBody
    public Object modifyMobile(WebModifyMobileParams params, HttpServletRequest request, Model model) throws Exception {
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
        String modifyIp = getIp(request);

        switch (AccountDomainEnum.getAccountDomain(userId)) {
            case PHONE:
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_MOBILEUSER_NOTALLOWED);
                return result.toString();
            case SOHU:
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SOHU_NOTALLOWED);
                return result.toString();
            case THIRD:
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_THIRD_NOTALLOWED);
                return result.toString();
        }

        result = secureManager.modifyMobileByPassportId(userId, clientId, newMobile, smsCode, scode, modifyIp);
        return result.toString();
    }

    /*
     * 绑定密保问题和答案
     */
    @RequestMapping(value = "/bindques", method = RequestMethod.POST)
    @LoginRequired
    @ResponseBody
    public Object bindQues(WebBindQuesParams params, HttpServletRequest request, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            int sep = validateResult.indexOf("|");
            result.setMessage(sep == -1 ? validateResult : validateResult.substring(0, sep));
            return result.toString();
        }
        String userId = hostHolder.getPassportId();
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
    }

    /*
     * 绑定外域邮箱成功的页面
     */
    @RequestMapping(value = "/emailverify", method = RequestMethod.GET)
    public String emailVerifySuccess(HttpServletRequest request) throws Exception {
        //状态码参数
        return "safe/emailsuccess";
    }
}
