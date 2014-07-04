package com.sogou.upd.passport.web.account.action.wap;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.*;
import com.sogou.upd.passport.manager.account.vo.AccountSecureInfoVO;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.form.AccountPwdParams;
import com.sogou.upd.passport.web.account.form.FindPwdCheckSmscodeParams;
import com.sogou.upd.passport.web.account.form.OtherResetPwdParams;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
public class WapResetPwdAction extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(WapResetPwdAction.class);

    @Autowired
    private RegManager regManager;
    @Autowired
    private SecureManager secureManager;
    @Autowired
    private WapResetPwdManager wapRestPwdManager;
    @Autowired
    private ResetPwdManager resetPwdManager;
    @Autowired
    private CommonManager commonManager;
    @Autowired
    private CheckManager checkManager;


    /**
     * 找回密码默认跳转到手机找回
     *
     * @param ru
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findpwd", method = RequestMethod.GET)
    public String findPwdView(String ru, Model model, String client_id) throws Exception {
        Result result = new APIResultSupport(false);
        ru = Strings.isNullOrEmpty(ru) ? CommonConstant.DEFAULT_INDEX_URL : ru;
        client_id = Strings.isNullOrEmpty(client_id) ? String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID) : client_id;
        result.setDefaultModel("ru", ru);
        result.setDefaultModel("client_id", client_id);
        model.addAttribute("data", result.toString());
        return "/wap/findpwd_touch";
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
        return "/wap/findpwd_other_touch";
    }

    /**
     * 验证找回密码发送的手机验证码
     *
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findpwd/checksms", method = RequestMethod.POST)
    public String checkSmsSecMobile(HttpServletRequest request, FindPwdCheckSmscodeParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                model.addAttribute("data", result.toString());
                return "/wap/findpwd_touch";
            }
            int clientId = Integer.parseInt(params.getClient_id());
            result = wapRestPwdManager.checkMobileCodeResetPwd(params.getMobile(), clientId, params.getSmscode());
            if (result.isSuccess()) {
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                model.addAttribute("data", result.toString());
                return "/wap/resetpwd_touch";
            }
        } catch (Exception e) {
            logger.error("checksms is failed,mobile is " + params.getMobile(), e);
        } finally {
            log(request, params.getMobile(), result.getCode());
        }
        result = setRuAndClientId(result, params.getRu(), params.getClient_id());
        model.addAttribute("data", result.toString());
        return "/wap/findpwd_touch";
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
    public String findpwdother(HttpServletRequest request, OtherResetPwdParams params, Model model)
            throws Exception {
        Result result = new APIResultSupport(false);
        try {
            //参数验证
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                model.addAttribute("data", result.toString());
                return "/wap/findpwd_other_touch";
            }
            String username = params.getUsername();
            String passportId = commonManager.getPassportIdByUsername(username);
            AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(passportId);
            //主账号是搜狐域用户和第三方用户时不支持此操作
            if (AccountDomainEnum.SOHU.equals(domain) || AccountDomainEnum.THIRD.equals(domain)) {
                result.setCode(ErrorUtil.ERR_CODE_NOTSUPPORT_SOHU_REGISTER);
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                model.addAttribute("data", result.toString());
                return "/wap/findpwd_other_touch";
            }
            //校验验证码
            if (!checkManager.checkCaptcha(params.getCaptcha(), params.getToken())) {
                result.setDefaultModel("userid", passportId);
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                model.addAttribute("data", result.toString());
                return "/wap/findpwd_other_touch";
            }
            //校验找回密码次数是否超限
            boolean checkTimes = resetPwdManager.checkFindPwdTimes(passportId).isSuccess();
            if (!checkTimes) {
                result.setDefaultModel("userid", passportId);
                result.setCode(ErrorUtil.ERR_CODE_FINDPWD_LIMITED);
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                model.addAttribute("data", result.toString());
                return "/wap/findpwd_other_touch";
            }
            int client_id = Integer.parseInt(params.getClient_id());
            result = regManager.isAccountNotExists(passportId, client_id);
            if (result.isSuccess()) {  //用户不存在
                result = new APIResultSupport(false);
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                model.addAttribute("data", result.toString());
                return "/wap/findpwd_other_touch";
            }
            if (AccountDomainEnum.PHONE.equals(domain)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_BINDED);
                result.setMessage("您是手机用户，请使用手机方式找回您的密码");
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                model.addAttribute("data", result.toString());
                return "/wap/findpwd_other_touch";
            }

        } catch (Exception e) {
            logger.error("checksms is failed,mobile is " + params.getUsername(), e);
        } finally {
            log(request, params.getUsername(), result.getCode());
        }

        return "";
    }

    private Result getSecureInfo(String passportId, int clientId) throws Exception {
        Result result = new APIResultSupport(false);
        Result queryResult = secureManager.queryAccountSecureInfo(passportId, clientId, true);
        if (queryResult.isSuccess()) {
            AccountSecureInfoVO accountSecureInfoVO = (AccountSecureInfoVO) queryResult.getDefaultModel();
            //如果用户的密保手机和密保邮箱存在，则返回模糊处理的手机号/密保邮箱及完整手机号/邮箱加密后的md5串
            if (accountSecureInfoVO != null) {
                result = new APIResultSupport(true);
                String sec_mobile = (String) queryResult.getModels().get("sec_mobile");
                String sec_email = (String) queryResult.getModels().get("sec_email");
                if (!Strings.isNullOrEmpty(sec_mobile)) {
                    result.setDefaultModel("sec_process_mobile", accountSecureInfoVO.getSec_mobile());
                    result.setDefaultModel("sec_mobile_md5", DigestUtils.md5Hex(sec_mobile.getBytes()));
                }
                if (!Strings.isNullOrEmpty(sec_email)) {
                    result.setDefaultModel("sec_process_email", accountSecureInfoVO.getSec_email());
                    result.setDefaultModel("sec_email_md5", DigestUtils.md5Hex(sec_email.getBytes()));
                }
            }
        } else {
            result.setCode(queryResult.getCode());
            result.setMessage(queryResult.getMessage());
        }
        return result;
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

//    private Result buildModel(Model model, Result result, String ru, String client_id, String code, String message) {
//
//    }


    private Result setRuAndClientId(Result result, String ru, String client_id) {
        result.setDefaultModel("ru", Strings.isNullOrEmpty(ru) ? CommonConstant.DEFAULT_WAP_URL : ru);
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
