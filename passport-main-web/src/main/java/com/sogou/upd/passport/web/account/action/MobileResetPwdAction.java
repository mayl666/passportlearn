package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.RegManager;
import com.sogou.upd.passport.manager.account.ResetPwdManager;
import com.sogou.upd.passport.manager.account.WapResetPwdManager;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.form.AccountPwdParams;
import com.sogou.upd.passport.web.account.form.FindPwdCheckSmscodeParams;
import com.sogou.upd.passport.web.account.form.MoblieCodeParams;
import com.sogou.upd.passport.web.account.form.WapResetPwdParams;
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
 * 移动端找回密码
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-7-3
 * Time: 下午3:46
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/wap")
public class MobileResetPwdAction extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(MobileResetPwdAction.class);

    @Autowired
    private RegManager regManager;
    @Autowired
    private WapResetPwdManager wapRestPwdManager;
    @Autowired
    private ResetPwdManager resetPwdManager;

    /**
     * 找回密码默认跳转到手机找回
     *
     * @param ru
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findpwd/index", method = RequestMethod.GET)
    public String findPwdView(String ru, Model model, String client_id) throws Exception {
        Result result = new APIResultSupport(false);
        ru = Strings.isNullOrEmpty(ru) ? CommonConstant.DEFAULT_INDEX_URL : ru;
        client_id = Strings.isNullOrEmpty(client_id) ? String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID) : client_id;
        result.setDefaultModel("ru", ru);
        result.setDefaultModel("client_id", client_id);
        model.addAttribute("data", result.toString());
        return "/wap/index";
    }

    /**
     * 其它方式找回时跳转到其它页面
     *
     * @param ru
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findpwd/other", method = RequestMethod.GET)
    public String findPwdOtherView(String ru, Model model, String client_id) throws Exception {
        Result result = new APIResultSupport(false);
        ru = Strings.isNullOrEmpty(ru) ? CommonConstant.DEFAULT_INDEX_URL : ru;
        client_id = Strings.isNullOrEmpty(client_id) ? String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID) : client_id;
        result.setDefaultModel("ru", ru);
        result.setDefaultModel("client_id", client_id);
        model.addAttribute("data", result.toString());
        return "/wap/other";
    }

    /**
     * 移动端找回密码，发送短信验证码
     *
     * @param params
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"/findpwd/sendsms"}, method = RequestMethod.GET)
    @ResponseBody
    public Object sendMobileCode(MoblieCodeParams params, HttpServletRequest request)
            throws Exception {
        Result result = new APIResultSupport(false);
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            result = wapRestPwdManager.sendFindPwdMobileCode(params.getMobile(), params.getClient_id());
        } catch (Exception e) {
            logger.error("sendSmsSecMobile Is Failed,mobile is " + params.getMobile(), e);
        } finally {
            log(request, params.getMobile(), result.getCode());
        }
        return result.toString();
    }

    /**
     * 验证找回密码发送的手机验证码
     *
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findpwd/checksms", method = RequestMethod.POST)
    @ResponseBody
    public Object checkSmsSecMobile(HttpServletRequest request, FindPwdCheckSmscodeParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        int clientId = Integer.parseInt(params.getClient_id());
        result = resetPwdManager.checkMobileCodeResetPwd(params.getMobile(), clientId, params.getSmscode());
        result.setDefaultModel("userid", params.getMobile());
        log(request, params.getMobile(), result.getCode());
        return result.toString();
    }

    /**
     * 重置密码
     *
     * @param request
     * @param params
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findpwd/resetpwd", method = RequestMethod.POST)
    public Object updatePwd(HttpServletRequest request, AccountPwdParams params, Model model)
            throws Exception {
        Result result = new APIResultSupport(false);
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                model.addAttribute("data", result.toString());
                return "/wap/end";
            }
            String passportId = params.getUsername();
            int clientId = Integer.parseInt(params.getClient_id());
            String password = params.getPassword();
            result = resetPwdManager.resetPasswordByScode(passportId, clientId, password, params.getScode(), getIp(request));
            if (!result.isSuccess()) {
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                model.addAttribute("data", result.toString());
                return "/wap/end";
            }
        } catch (Exception e) {
            logger.error("resetPwd Is Failed,Username is " + params.getUsername(), e);
        } finally {
            log(request, params.getUsername(), result.getCode());
        }
        result.setCode(ErrorUtil.SUCCESS);
        result = setRuAndClientId(result, params.getRu(), params.getClient_id());
        model.addAttribute("data", result.toString());
        return "/wap/end";
    }

    /**
     * 用户选择其它方式找回时
     *
     * @param request
     * @param params
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findpwd/check", method = RequestMethod.POST)
    public Object findpwdother(HttpServletRequest request, WapResetPwdParams params, Model model)
            throws Exception {
        Result result = new APIResultSupport(false);
        try {
            //参数验证
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            String username = params.getUsername();
            AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(username);
            //搜狐域用户和第三方用户不支持此操作
            if (AccountDomainEnum.SOHU.equals(domain) || AccountDomainEnum.THIRD.equals(domain)) {
                result.setCode(ErrorUtil.ERR_CODE_NOTSUPPORT_SOHU_REGISTER);
                return result;
            }
            int client_id = Integer.parseInt(params.getClient_id());
            result = regManager.isAccountNotExists(username, client_id);
            if (result.isSuccess()) {  //用户不存在
                return result.toString();
            }
            if (AccountDomainEnum.PHONE.equals(domain)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_BINDED);
                result.setMessage("您是手机用户，请使用手机方式找回您的密码");
            }
            return result;
        } catch (Exception e) {

        } finally {

        }

        return result;
    }

    private Result setRuAndClientId(Result result, String ru, String client_id) {
        result.setDefaultModel("ru", Strings.isNullOrEmpty(ru) ? CommonConstant.DEFAULT_INDEX_URL : ru);
        result.setDefaultModel("client_id", client_id);
        return result;
    }

    private void log(HttpServletRequest request, String passportId, String resultCode) {
        //用户登录log
        UserOperationLog userOperationLog = new UserOperationLog(passportId, request.getRequestURI(), String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID), resultCode, getIp(request));
        userOperationLog.putOtherMessage("ref", request.getHeader("referer"));
        UserOperationLogUtil.log(userOperationLog);
    }

}
