package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.CheckManager;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.ResetPwdManager;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.account.vo.AccountSecureInfoVO;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.service.account.dataobject.ActiveEmailDO;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.form.AccountPwdParams;
import com.sogou.upd.passport.web.account.form.BaseWebResetPwdParams;
import com.sogou.upd.passport.web.account.form.BaseWebRuParams;
import com.sogou.upd.passport.web.account.form.CheckSecMobileParams;
import com.sogou.upd.passport.web.account.form.CheckSmsCodeAndGetSecInfoParams;
import com.sogou.upd.passport.web.account.form.ResendEmailResetPwdParams;
import com.sogou.upd.passport.web.account.form.UserCaptchaParams;

import org.apache.commons.codec.digest.DigestUtils;
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
 * User: mayan
 * Date: 13-6-7 Time: 下午5:49
 * 重置密码 （通过注册邮箱，密保邮箱，密保手机，密保问题）
 */
@Controller
@RequestMapping(value = "/web")
public class ResetPwdAction extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ResetPwdAction.class);

//    private static final String SOHU_FINDPWD_URL = SHPPUrlConstant.SOHU_FINDPWD_URL;

    @Autowired
    private SecureManager secureManager;
    @Autowired
    private CheckManager checkManager;
    @Autowired
    private ResetPwdManager resetPwdManager;
    @Autowired
    private CommonManager commonManager;
    @Autowired
    private RegisterApiManager registerApiManager;

    /**
     * 找回密码主页跳转
     *
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findpwd", method = RequestMethod.GET)
    public String findPwdView(BaseWebRuParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String ru = Strings.isNullOrEmpty(params.getRu()) ? CommonConstant.DEFAULT_INDEX_URL : params.getRu();
        String client_id = Strings.isNullOrEmpty(params.getClient_id()) ? String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID) : params.getClient_id();
        result.setDefaultModel("ru", ru);
        result.setDefaultModel("client_id", client_id);
        model.addAttribute("data", result.toString());
        return "/recover/index";
    }

    /**
     * 找回密码时获取用户安全信息,非搜狗账号跳转至sohu找回密码，搜狗账号跳转至搜狗找回密码页
     *
     * @param params
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findpwd/getsecinfo", method = RequestMethod.POST)
    public String querySecureInfo(HttpServletRequest request, UserCaptchaParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        result = setRuAndClientId(result, params.getRu(), params.getClient_id());
        String passportIdLog = null;
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                model.addAttribute("data", result.toString());
                return "/recover/index";
            }
            //用户输入的账号
            String username = params.getUsername();
            //默认是sogou.com
            AccountDomainEnum accountDomainEnum = AccountDomainEnum.getAccountDomain(username);
            String passportId = username;
            if (AccountDomainEnum.INDIVID.equals(accountDomainEnum)) {
                passportId = passportId + CommonConstant.SOGOU_SUFFIX;
            }
            //查询主账号：@sogou.com/外域/第三方账号返回原样，手机账号返回绑定的主账号，若无主账号则返回手机号+@sohu.com
            passportId = commonManager.getPassportIdByUsername(passportId);
            AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(passportId);
            switch (domain) {
                //主账号是sohu域/外域/手机号的去sohu找回密码
//                case SOHU:
//                    return "redirect:" + SOHU_FINDPWD_URL + "?ru=" + CommonConstant.DEFAULT_INDEX_URL;
                case THIRD:
                    return "redirect:/web/findpwd";
                case UNKNOWN:
                    result.setCode(ErrorUtil.INVALID_ACCOUNT);
                    model.addAttribute("data", result.toString());
                    return "/recover/index";
            }
            result.setDefaultModel("userid", passportId);
            //校验验证码
            if (!checkManager.checkCaptcha(params.getCaptcha(), params.getToken())) {
                result.setDefaultModel("userid", username);
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
                model.addAttribute("data", result.toString());
                return "/recover/index";
            }
            boolean checkTimes = resetPwdManager.checkFindPwdTimes(passportId).isSuccess();
            if (!checkTimes) {
                result.setDefaultModel("userid", username);
                result.setCode(ErrorUtil.ERR_CODE_FINDPWD_LIMITED);
                model.addAttribute("data", result.toString());
                return "/recover/index";
            }
            result = registerApiManager.checkUser(passportId, Integer.parseInt(params.getClient_id()),true);//允许搜狐账号
            if (result.isSuccess()) {
                result.setSuccess(false);
                result.setDefaultModel("userid", username);
                result.setCode(ErrorUtil.INVALID_ACCOUNT);
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                model.addAttribute("data", result.toString());
                return "/recover/index";
            }
            int clientId = Integer.parseInt(params.getClient_id());
            result = secureManager.queryAccountSecureInfo(passportId, clientId, true);
            if (!result.isSuccess()) {
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                model.addAttribute("data", result.toString());
                return "/recover/index";
            }
            AccountSecureInfoVO accountSecureInfoVO = (AccountSecureInfoVO) result.getDefaultModel();
            //记录找回密码次数
            resetPwdManager.incFindPwdTimes(passportId);
            //如果用户的密保手机和密保邮箱存在，则返回模糊处理的手机号/密保邮箱及完整手机号/邮箱加密后的md5串
            if (accountSecureInfoVO != null) {
                String sec_mobile = (String) result.getModels().get("sec_mobile");
                String sec_email = (String) result.getModels().get("sec_email");
                if (AccountDomainEnum.OTHER.equals(domain)) {
                    if (!passportId.equals(sec_email)) { //如果passportId是外域，则注册邮箱是它本身,当注册邮箱和密保邮箱不一样时，才返回注册邮箱
                        result.setDefaultModel("reg_process_email", accountSecureInfoVO.getReg_email());
                        result.setDefaultModel("reg_email_md5", DigestUtils.md5Hex(passportId));
                    }
                }
                if (!Strings.isNullOrEmpty(sec_mobile)) {
                    result.setDefaultModel("sec_process_mobile", accountSecureInfoVO.getSec_mobile());
                    result.setDefaultModel("sec_mobile_md5", DigestUtils.md5Hex(sec_mobile.getBytes()));
                    result.getModels().remove("sec_mobile"); //为了账号安全，不返回完整的手机号
                }
                if (!Strings.isNullOrEmpty(sec_email)) {
                    result.setDefaultModel("sec_process_email", accountSecureInfoVO.getSec_email());
                    result.setDefaultModel("sec_email_md5", DigestUtils.md5Hex(sec_email.getBytes()));
                    result.getModels().remove("sec_email"); //为了账号安全，不返回完整的密保邮箱
                }
            }
//            result.setDefaultModel("userid", passportId);    //用户输入账号的主账号
            result = setRuAndClientId(result, params.getRu(), params.getClient_id());
            result.setDefaultModel("scode", commonManager.getSecureCode(passportId, clientId,CacheConstant.CACHE_PREFIX_PASSPORTID_RESETPWDSECURECODE));
            model.addAttribute("data", result.toString());   //返回的信息包含密保手机、密保邮箱、及密保问题（找回密码不会用到此返回结果）
            passportIdLog = passportId;
        } catch (Exception e) {
            logger.error("querySecureInfo Is Failed,Username is " + params.getUsername(), e);
        } finally {
            log(request, passportIdLog, result.getCode());
        }
        return "/recover/type";
    }

    private Result setRuAndClientId(Result result, String ru, String client_id) {
        result.setDefaultModel("ru", Strings.isNullOrEmpty(ru) ? CommonConstant.DEFAULT_INDEX_URL : ru);
        result.setDefaultModel("client_id", Strings.isNullOrEmpty(client_id) ? CommonConstant.SGPP_DEFAULT_CLIENTID : client_id);
        return result;
    }

    /**
     * 发送重置密码申请验证邮件至绑定邮箱
     *
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findpwd/sendbemail", method = RequestMethod.POST)
    @ResponseBody
    public String sendEmailBindResetPwd(HttpServletRequest request, BaseWebResetPwdParams params) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            String passportId = params.getUsername();
            int clientId = Integer.parseInt(params.getClient_id());
            result = resetPwdManager.sendEmailResetPwdByPassportId(passportId, clientId, false, params.getRu(), params.getScode(), params.isRtp(), params.getLang());
            result.setDefaultModel("scode", commonManager.getSecureCode(passportId, clientId,CacheConstant.CACHE_PREFIX_PASSPORTID_RESETPWDSECURECODE));
            result.setDefaultModel("userid", passportId);
            result = setRuAndClientId(result, params.getRu(), params.getClient_id());
        } catch (Exception e) {
            logger.error("sendEmailBindResetPwd Is Failed,Username is " + params.getUsername(), e);
        } finally {
            log(request, params.getUsername(), result.getCode());
        }
        return result.toString();
    }


    /**
     * 发送重置密码申请验证邮件至注册邮箱
     *
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findpwd/sendremail", method = RequestMethod.POST)
    @ResponseBody
    public String sendEmailRegResetPwd(HttpServletRequest request, BaseWebResetPwdParams params) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            String passportId = params.getUsername();
            int clientId = Integer.parseInt(params.getClient_id());
            result = resetPwdManager.sendEmailResetPwdByPassportId(passportId, clientId, true, params.getRu(), params.getScode());
            result.setDefaultModel("scode", commonManager.getSecureCode(passportId, clientId,CacheConstant.CACHE_PREFIX_PASSPORTID_RESETPWDSECURECODE));
            result.setDefaultModel("userid", passportId);
        } catch (Exception e) {
            logger.error("sendEmailRegResetPwd Is Failed,Username is " + params.getUsername(), e);
        } finally {
            log(request, params.getUsername(), result.getCode());
        }
        return result.toString();
    }


    /**
     * 重新发送激活邮件
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/findpwd/resendmail", method = RequestMethod.POST)
    @ResponseBody
    public Object resendActiveMail(HttpServletRequest request, ResendEmailResetPwdParams params) throws Exception {
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
            String toEmail = params.getTo_email();
            result = resetPwdManager.checkEmailCorrect(username, toEmail);
            if (!result.isSuccess()) {
                return result.toString();
            }
            ActiveEmailDO activeEmailDO = new ActiveEmailDO(username, Integer.parseInt(params.getClient_id()), params.getRu(), AccountModuleEnum.RESETPWD, toEmail, false);
            result = resetPwdManager.sendEmailResetPwd(activeEmailDO, params.getScode());
            result.setDefaultModel("scode", commonManager.getSecureCode(params.getUsername(), Integer.parseInt(params.getClient_id()), CacheConstant.CACHE_PREFIX_PASSPORTID_RESETPWDSECURECODE));
            result.setDefaultModel("userid", params.getUsername());
            result = setRuAndClientId(result, params.getRu(), params.getClient_id());
        } catch (Exception e) {
            logger.error("method[resendActiveMail] send mobile sms error.{}", e);
        } finally {
            log(request, params.getUsername(), result.getCode());
        }
        return result.toString();
    }

    /**
     * 校验找回密码邮箱连接
     *
     * @param params
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findpwd/checkemail", method = RequestMethod.GET)
    public String checkEmailResetPwd(HttpServletRequest request, BaseWebResetPwdParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                model.addAttribute("data", result.toString());
                if(params.isRtp()) { // 跳转到 passport 页面
                    return "/recover/index";
                } else {
                    return "redirect:" + params.getRu();
                }
            }
            String passportId = params.getUsername();
            int clientId = Integer.parseInt(params.getClient_id());
            result = resetPwdManager.checkEmailResetPwd(passportId, clientId, params.getScode());
            if (result.isSuccess()) {
                //邮箱连接校验成功跳转到修改密码页面
                result.setDefaultModel("userid", passportId);
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                model.addAttribute("data", result.toString());
                if(params.isRtp()) { // 跳转到 passport 页面
                    return "/recover/reset";
                } else {
                    return "redirect:" + params.getRu();
                }
            }
            result.setCode(ErrorUtil.ERR_CODE_FINDPWD_EMAIL_FAILED);
            result = setRuAndClientId(result, params.getRu(), params.getClient_id());
            model.addAttribute("data", result.toString());
        } catch (Exception e) {
            logger.error("checkEmailResetPwd Is Failed,Username is " + params.getUsername(), e);
        } finally {
            log(request, params.getUsername(), result.getCode());
        }
        if(params.isRtp()) { // 跳转到 passport 页面
            return "/recover/index";
        } else {
            return "redirect:" + params.getRu();
        }
    }

    /**
     * 重设密码
     *
     * @param request
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findpwd/reset", method = RequestMethod.POST)
    public String resetPwd(HttpServletRequest request, AccountPwdParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                model.addAttribute("data", result.toString());
                if(params.isRtp()) { // 跳转到 passport 页面
                  return "/recover/end";
                } else {
                  return "redirect:" + params.getRu();
                }
            }
            String passportId = params.getUsername();
            int clientId = Integer.parseInt(params.getClient_id());
            String password = params.getPassword();
            result = resetPwdManager.resetPasswordByScode(passportId, clientId, password, params.getScode(), getIp(request));
            if (!result.isSuccess()) {
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                model.addAttribute("data", result.toString());
                if(params.isRtp()) { // 跳转到 passport 页面
                  return "/recover/end";
                } else {
                  return "redirect:" + params.getRu();
                }
            }
        } catch (Exception e) {
            logger.error("resetPwd Is Failed,Username is " + params.getUsername(), e);
        } finally {
            log(request, params.getUsername(), result.getCode());
        }
        result.setCode(ErrorUtil.SUCCESS);
        result = setRuAndClientId(result, params.getRu(), params.getClient_id());
        model.addAttribute("data", result.toString());
        if(params.isRtp()) { // 跳转到 passport 页面
          return "/recover/end";
        } else {
          return "redirect:" + params.getRu();
        }
    }

    /**
     * 找回密码，发送短信验证码至原绑定手机
     *
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findpwd/sendsms", method = RequestMethod.POST)
    @ResponseBody
    public Object sendSmsSecMobile(HttpServletRequest request, CheckSecMobileParams params) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            int clientId = Integer.parseInt(params.getClient_id());
            result = resetPwdManager.sendFindPwdMobileCode(params.getUsername(), clientId, params.getSec_mobile(), params.getToken(), params.getCaptcha());
        } catch (Exception e) {
            logger.error("sendSmsSecMobile Is Failed,Username is " + params.getUsername(), e);
        } finally {
            log(request, params.getUsername(), result.getCode());
        }
        return result.toString();
    }

    /**
     * 验证手机号与验证码是否匹配
     *
     * @param params
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findpwd/check", method = RequestMethod.POST)
    public String checkPwdView(HttpServletRequest request, CheckSmsCodeAndGetSecInfoParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                return "/404";
            }
            String username = params.getUsername();
            String passportId = username;
            if (AccountDomainEnum.INDIVID.equals(AccountDomainEnum.getAccountDomain(username))) {
                passportId += CommonConstant.SOGOU_SUFFIX;
            }
            passportId = commonManager.getPassportIdByUsername(username);
            if (Strings.isNullOrEmpty(passportId)) {
                result = buildErrorResult(result, params, ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOBIND, ErrorUtil.getERR_CODE_MSG(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOBIND));
                model.addAttribute("data", result.toString());
                return "/recover/type";
            }
            result = registerApiManager.checkUser(passportId, Integer.parseInt(params.getClient_id()),true);
            if (result.isSuccess()) {  //账号不存在
                result = buildErrorResult(result, params, null, ErrorUtil.INVALID_ACCOUNT);
                model.addAttribute("data", result.toString());
                return "/recover/type";
            }
            int clientId = Integer.parseInt(params.getClient_id());
            result = resetPwdManager.checkMobileCodeResetPwd(username, clientId, params.getSmscode());
            if (result.isSuccess()) {
                result.setDefaultModel("userid", params.getUsername());
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                model.addAttribute("data", result.toString());
            } else {
                String message = result.getMessage();
                result = buildErrorResult(result, params, null, message);
                model.addAttribute("data", result.toString());
                return "/recover/type";
            }
        } catch (Exception e) {
            logger.error("checkPwdView Is Failed,Username is " + params.getUsername(), e);
        } finally {
            log(request, params.getUsername(), result.getCode());
        }
        return "/recover/reset";
    }

    //构建错误的返回结果
    private Result buildErrorResult(Result result, CheckSmsCodeAndGetSecInfoParams params, String code, String message) throws Exception {
        code = Strings.isNullOrEmpty(code) ? result.getCode() : code;
        result = getSecureInfo(params.getUsername(), Integer.parseInt(params.getClient_id()));
        result.setSuccess(false);
        result.setCode(code);
        result.setMessage(message);
        result.setDefaultModel("userid", params.getUsername());
        result = setRuAndClientId(result, params.getRu(), params.getClient_id());
        result.setDefaultModel("scode", commonManager.getSecureCode(params.getUsername(), Integer.parseInt(params.getClient_id()), CacheConstant.CACHE_PREFIX_PASSPORTID_RESETPWDSECURECODE));
        return result;
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
//                    result.getModels().remove("sec_mobile"); //为了账号安全，不返回完整的手机号
                }
                if (!Strings.isNullOrEmpty(sec_email)) {
                    result.setDefaultModel("sec_process_email", accountSecureInfoVO.getSec_email());
                    result.setDefaultModel("sec_email_md5", DigestUtils.md5Hex(sec_email.getBytes()));
//                    result.getModels().remove("sec_email"); //为了账号安全，不返回完整的密保邮箱
                }
            }
        } else {
            result.setCode(queryResult.getCode());
            result.setMessage(queryResult.getMessage());
        }
        return result;
    }

    private void log(HttpServletRequest request, String passportId, String resultCode) {
        //用户登录log
        UserOperationLog userOperationLog = new UserOperationLog(passportId, request.getRequestURI(), String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID), resultCode, getIp(request));
        userOperationLog.putOtherMessage("ref", request.getHeader("referer"));
        UserOperationLogUtil.log(userOperationLog);
    }

//    /**
//     * 通过注册/密保邮箱；注册/密保手机；找回密码页面跳转
//     *
//     * @param params
//     * @param model
//     * @return
//     * @throws Exception
//     */
//    @RequestMapping(value = {"/recover/email", "/recover/mobile"}, method = RequestMethod.GET)
//    public String email(HttpServletRequest request, FindPwdParams params, Model model) throws Exception {
//        Result result = new APIResultSupport(false);
//        String validateResult = ControllerHelper.validateParams(params);
//        if (!Strings.isNullOrEmpty(validateResult)) {
//            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
//            result.setMessage(validateResult);
//            model.addAttribute("data", result.toString());
//            return "/recover/index";
//        }
//
//        switch (params.getType()) {
//            case "bind_email":
//                result = secureManager.queryAccountSecureInfo(params.getUsername(), Integer.parseInt(params.getClient_id()), true);
//                String email = (String) (result.getModels().get("sec_email"));
//                result = new APIResultSupport(true);
//                result.setDefaultModel("bind_email", email);
//                result.setDefaultModel("userid", params.getUsername());
//                model.addAttribute("data", result.toString());
//                log(request, params.getUsername(), result.getCode());
//                return "/recover/email";
//            case "reg_email":
//                result.setDefaultModel("reg_email", params.getUsername());
//                result.setDefaultModel("userid", params.getUsername());
//                model.addAttribute("data", result.toString());
//                return "/recover/email";
//            case "bind_mobile":
//                result = secureManager.queryAccountSecureInfo(params.getUsername(), Integer.parseInt(params.getClient_id()), true);
//                String mobile = (String) (result.getModels().get("sec_mobile"));
//                result = new APIResultSupport(true);
//                result.setDefaultModel("bind_mobile", mobile);
//                result.setDefaultModel("userid", params.getUsername());
//                model.addAttribute("data", result.toString());
//                log(request, params.getUsername(), result.getCode());
//                return "/recover/mobile";
//            case "reg_mobile":
//                result.setDefaultModel("reg_mobile", getMobile(params.getUsername()));
//                result.setDefaultModel("userid", params.getUsername());
//                model.addAttribute("data", result.toString());
//                log(request, params.getUsername(), result.getCode());
//                return "/recover/mobile";
//        }
//
//        result.setCode(ErrorUtil.ERR_CODE_FINDPWD_TYPE_FAILED);
//        result.setMessage(validateResult);
//        model.addAttribute("data", result.toString());
//        return "/recover/index";
//    }
//
//
//    /**
//     * 验证注册或密保邮箱页面跳转
//     *
//     * @param email
//     * @param model
//     * @return
//     * @throws Exception
//     */
//    @RequestMapping(value = "/recover/sended", method = RequestMethod.GET)
//    public String sended(@RequestParam(value = "email", defaultValue = "") String email, Model model) throws Exception {
//        Result result = new APIResultSupport(true);
//        result.setDefaultModel("email", email);
//        model.addAttribute("data", result.toString());
//        return "/recover/sended";
//    }
//
//    /**
//     * 重置密码页面跳转
//     *
//     * @param userid
//     * @param scode
//     * @param model
//     * @return
//     * @throws Exception
//     */
//    @RequestMapping(value = "/recover/reset", method = RequestMethod.GET)
//    public String reset(@RequestParam(value = "userid") String userid, @RequestParam(value = "scode") String scode,
//                        Model model) throws Exception {
//        Result result = new APIResultSupport(true);
//        result.setDefaultModel("userid", userid);
//        result.setDefaultModel("scode", scode);
//        model.addAttribute("data", result.toString());
//        return "/recover/reset";
//    }


//    private String getMobile(String passportId) {
//        if (passportId.contains("@")) {
//            return passportId.substring(0, passportId.indexOf("@"));
//        }
//        return passportId;
//    }

}