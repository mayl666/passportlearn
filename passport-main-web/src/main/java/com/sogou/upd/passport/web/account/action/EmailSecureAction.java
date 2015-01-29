package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.AccountInfoManager;
import com.sogou.upd.passport.manager.account.CheckManager;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.form.AccountScodeParams;
import com.sogou.upd.passport.web.account.form.security.WebBindEmailParams;
import com.sogou.upd.passport.web.account.form.security.WebBindEmailVerifyParams;
import com.sogou.upd.passport.web.annotation.LoginRequired;
import com.sogou.upd.passport.web.inteceptor.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 密保手机相关的安全操作
 * User: shipengzhi
 * Date: 14-7-1
 * Time: 上午3:05
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/web/security")
public class EmailSecureAction extends BaseController {

    private static final String SOHU_BINDEMAIL_URL = SHPPUrlConstant.SOHU_BINDEMAIL_URL;

    @Autowired
    private SecureManager secureManager;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private CheckManager checkManager;
    @Autowired
    private AccountInfoManager accountInfoManager;

    /*
   * 发送修改绑定邮箱申请邮件
   */
    @RequestMapping(value = "/sendemail", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public Object sendEmailForBind(HttpServletRequest request, WebBindEmailParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String passportId = hostHolder.getPassportId();
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            int clientId = Integer.parseInt(params.getClient_id());
            String password = params.getPassword();
            String newEmail = params.getNew_email();
            String oldEmail = params.getOld_email();
            String modifyIp = getIp(request);
            String ru = params.getRu();
            if (Strings.isNullOrEmpty(ru)) {
                ru = CommonConstant.DEFAULT_INDEX_URL;
            }
            switch (AccountDomainEnum.getAccountDomain(passportId)) {
                case SOHU:
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SOHU_NOTALLOWED);
                    return result.toString();
                case THIRD:
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_THIRD_NOTALLOWED);
                    return result.toString();
            }
            result = secureManager.sendEmailForBinding(passportId, clientId, password, newEmail, oldEmail, modifyIp, ru);
            return result.toString();
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog(passportId, request.getRequestURI(), params.getClient_id(), result.getCode(), getIp(request));
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            UserOperationLogUtil.log(userOperationLog);
        }
    }

    /*
     * 验证绑定邮件
     */
    @RequestMapping(value = "/checkemail", method = RequestMethod.GET)
    public String checkEmailForBind(HttpServletRequest request, AccountScodeParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                model.addAttribute("data", result.toString());
                return "/404";
            }
            String passportId = params.getUsername();
            int clientId = Integer.parseInt(params.getClient_id());
            String scode = params.getScode();
            switch (AccountDomainEnum.getAccountDomain(passportId)) {
                case SOHU:
                    return "redirect:" + SOHU_BINDEMAIL_URL;
                case THIRD:
                    return "redirect:/web/security";
            }
            result = secureManager.modifyEmailByPassportId(passportId, clientId, scode);
            model.addAttribute("data", result.toString());
            return "redirect:" + params.getRu();
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog(params.getUsername(), request.getRequestURI(), params.getClient_id(), result.getCode(), getIp(request));
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            UserOperationLogUtil.log(userOperationLog);
        }
    }

    /*
    * 绑定外域邮箱成功的页面
    */
    @RequestMapping(value = "/emailverify", method = RequestMethod.GET)
    public String emailVerifySuccess(HttpServletRequest request, Model model, WebBindEmailVerifyParams params) throws Exception {
        // TODO:状态码参数或token
        Result result = new APIResultSupport(false);
        String username = params.getUsername();
        String token = params.getToken();
        String id = params.getId();
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return "/404";
            } else {
                AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(username);
                if (domain == AccountDomainEnum.PHONE) {
                    result.setDefaultModel("actype", "phone");
                }
                if (StringUtil.checkExistNullOrEmpty(token, id) || !checkManager.checkScode(token, id)) {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_BINDEMAIL_URL_FAILED);
                    result.setMessage("绑定密保邮箱申请链接失效，请尝试重新绑定！");
                } else {
                    result.setSuccess(true);
                    result.setCode(ErrorUtil.SUCCESS);
                    result.setMessage("绑定密保邮箱成功！");
                }
            }
            result.setDefaultModel("username", accountInfoManager.getUniqName(username, CommonConstant.SGPP_DEFAULT_CLIENTID, true));
            result.setDefaultModel("status", result.getCode());
            result.setDefaultModel("statusText", result.getMessage());
            model.addAttribute("data", result.toString());
            return "safe/emailsuccess";
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog(token, request.getRequestURI(), "1120", result.getCode(), getIp(request));
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            UserOperationLogUtil.log(userOperationLog);
        }
    }

}
