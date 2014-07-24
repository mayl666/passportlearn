package com.sogou.upd.passport.web.account.action.wap;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.WapConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountClientEnum;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.*;
import com.sogou.upd.passport.manager.account.vo.AccountSecureInfoVO;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.form.*;
import com.sogou.upd.passport.web.account.form.wap.WapCheckEmailParams;
import com.sogou.upd.passport.web.account.form.wap.WapPwdParams;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
     * 找回密码
     *
     * @param ru
     * @param model
     * @param redirectAttributes
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findpwd", method = RequestMethod.GET)
    public String findPwdView(String ru, Model model, RedirectAttributes redirectAttributes, WapIndexParams wapIndexParams) throws Exception {
        ru = Strings.isNullOrEmpty(ru) ? CommonConstant.DEFAULT_WAP_URL : ru;
        if (WapConstant.WAP_TOUCH.equals(wapIndexParams.getV())) {
            Result result = new APIResultSupport(false);
            String client_id = Strings.isNullOrEmpty(wapIndexParams.getClient_id()) ? String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID) : wapIndexParams.getClient_id();
            result.setDefaultModel("ru", ru);
            result.setDefaultModel("client_id", client_id);
            model.addAttribute("data", result.toString());
            return "wap/findpwd_touch";
        }
        redirectAttributes.addAttribute("ru", ru);
        return "redirect:" + SHPPUrlConstant.SOHU_WAP_FINDPWD_URL + "?ru={ru}";
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
        ru = Strings.isNullOrEmpty(ru) ? CommonConstant.DEFAULT_WAP_URL : ru;
        client_id = Strings.isNullOrEmpty(client_id) ? String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID) : client_id;
        result.setDefaultModel("ru", ru);
        result.setDefaultModel("client_id", client_id);
        model.addAttribute("token", RandomStringUtils.randomAlphanumeric(48));
        model.addAttribute("data", result.toString());
        return "/wap/findpwd_other_touch";
    }


    /**
     * 其它方式找回时跳转到其它页面
     *
     * @param ru
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findpwd/kefu", method = RequestMethod.GET)
    public String findPwdKefuView(String ru, Model model, String client_id) throws Exception {
        Result result = new APIResultSupport(false);
        ru = Strings.isNullOrEmpty(ru) ? CommonConstant.DEFAULT_WAP_URL : ru;
        client_id = Strings.isNullOrEmpty(client_id) ? String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID) : client_id;
        result.setDefaultModel("ru", ru);
        result.setDefaultModel("client_id", client_id);
        model.addAttribute("data", result.toString());
        return "/wap/findpwd_contact_touch";
    }

    /**
     * 找回密码，发送短信验证码至原绑定手机
     *
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findpwd/sendsms", method = RequestMethod.GET)
    @ResponseBody
    public Object sendSmsSecMobile(HttpServletRequest request, MoblieCodeParams params) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            result = wapRestPwdManager.sendMobileCaptcha(params.getMobile(), params.getClient_id());
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
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                return result.toString();
            }
            int clientId = Integer.parseInt(params.getClient_id());
            result = wapRestPwdManager.checkMobileCodeResetPwd(params.getMobile(), clientId, params.getSmscode());
            if (result.isSuccess()) {
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                result.setDefaultModel("skin", params.getSkin());
                String param = buildRedirectUrl(result);
                String url = CommonConstant.DEFAULT_WAP_INDEX_URL + param;
                result.setDefaultModel("url", url);
                return result.toString();
            }
        } catch (Exception e) {
            logger.error("checksms is failed,mobile is " + params.getMobile(), e);
        } finally {
            log(request, params.getMobile(), result.getCode());
        }
        result = setRuAndClientId(result, params.getRu(), params.getClient_id());
        return result.toString();
    }

    //手机与短信验证码验证成功后，给前端生成下一步跳转的url
    private String buildRedirectUrl(Result result) {
        StringBuilder urlStr = new StringBuilder();
        urlStr.append("/wap/findpwd/vm/reset?");
        String userid = (String) result.getModels().get("userid");
        urlStr.append("username=" + userid);
        String scode = (String) result.getModels().get("scode");
        urlStr.append("&scode=" + scode);
        String client_id = (String) result.getModels().get("client_id");
        urlStr.append("&client_id=" + client_id);
        String ru = (String) result.getModels().get("ru");
        urlStr.append("&ru=" + Coder.encodeUTF8(ru));
        urlStr.append("&code=" + result.getCode());
        urlStr.append("&message=" + result.getMessage());
        urlStr.append("&v=" + WapConstant.WAP_TOUCH);
        String skin = (String) result.getModels().get("skin");
        urlStr.append("&skin=" + skin);
        return urlStr.toString();
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
    @ResponseBody
    public Object findpwdother(HttpServletRequest request, RedirectAttributes redirectAttributes, OtherResetPwdParams params, Model model)
            throws Exception {
        Result result = new APIResultSupport(false);
        try {
            //参数验证
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                return result.toString();
            }
            String username = params.getUsername();
            String passportId = commonManager.getPassportIdByUsername(username);
            AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(passportId);
            //第三方用户不允许此操作
            if (AccountDomainEnum.THIRD.equals(domain)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_THIRD_NOTALLOWED);
                return result.toString();
            }
            //主账号是搜狐域用户跳转到sohu的wap页面
            if (AccountDomainEnum.SOHU.equals(domain)) {
                result.setSuccess(true);
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                String url = SHPPUrlConstant.SOHU_WAP_FINDPWD_URL + "?client_id=" + result.getModels().get("client_id") + "&ru=" + result.getModels().get("ru");
                result.setDefaultModel("url", url);
                return result.toString();
            }
            //校验验证码
            if (!checkManager.checkCaptcha(params.getCaptcha(), params.getToken())) {
                result.setDefaultModel("userid", passportId);
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                return result.toString();
            }
            //校验找回密码次数是否超限
            boolean checkTimes = resetPwdManager.checkFindPwdTimes(passportId).isSuccess();
            if (!checkTimes) {
                result.setDefaultModel("userid", passportId);
                result.setCode(ErrorUtil.ERR_CODE_FINDPWD_LIMITED);
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                return result.toString();
            }
            int client_id = Integer.parseInt(params.getClient_id());
            result = regManager.isAccountNotExists(passportId, client_id);
            if (result.isSuccess()) {  //用户不存在
                result = new APIResultSupport(false);
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                return result.toString();
            }
            switch (domain) {
                case PHONE:  //主账号是手机账号，提示手机方式找回
                    result = new APIResultSupport(false);
                    result.setCode(ErrorUtil.ERR_CODE_USER_HAVA_BIND_MOBILE);
                    result.setDefaultModel("userid", passportId);
                    result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                    return result.toString();    //跳转至其它方式找回首页
                case OTHER:
                case SOGOU:
                case INDIVID: //主账号是外域/搜狗域/个性账号，则查询它的密保邮箱/手机返回，有则返回；无则返回通过客服找回
                    result = getSecureInfo(passportId, client_id); //返回密保邮箱或手机及模糊处理过的
                    result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                    result.setDefaultModel("userid", passportId);
                    if (!result.isSuccess()) {
                        result.setCode(ErrorUtil.ERR_CODE_FIND_KEFU);
                        return result.toString();//跳转至其它方式找回首页
                    } else {
                        String sec_mobile = (String) result.getModels().get("sec_mobile");
                        String sec_email = (String) result.getModels().get("sec_email");
                        //用户有绑定关系，但只有密保手机无密保邮箱，提示用户使用密保手机找回
                        if (!Strings.isNullOrEmpty(sec_mobile) && Strings.isNullOrEmpty(sec_email)) {
                            result.setSuccess(false);
                            result.setCode(ErrorUtil.ERR_CODE_USER_HAVA_BIND_MOBILE);
                            return result.toString(); //跳转至其它方式找回首页
                        } else {
                            //主账号是外域，则返回注册邮箱 ;主账号非外域，则返回密保邮箱
                            result.setDefaultModel("sec_email", AccountDomainEnum.OTHER.equals(domain) ? passportId : sec_email);
                            result.setDefaultModel("sec_process_email", AccountDomainEnum.OTHER.equals(domain) ? StringUtil.processEmail(passportId) : StringUtil.processEmail(sec_email));
                            result.setDefaultModel("scode", commonManager.getSecureCodeResetPwd(passportId, client_id));      //安全验证码
                        }
                    }
            }
            //记录找回密码次数
            resetPwdManager.incFindPwdTimes(passportId);
        } catch (Exception e) {
            logger.error("checksms is failed,mobile is " + params.getUsername(), e);
        } finally {
            log(request, params.getUsername(), result.getCode());
        }
        return result.toString();//跳转至邮箱确认页面
    }

    /**
     * 发送重置密码申请验证邮件至注册/绑定邮箱
     *
     * @param params
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findpwd/sendemail", method = RequestMethod.POST)
    @ResponseBody
    public String sendEmailResetPwd(HttpServletRequest request, WapSendEmailParams params, Model model) throws Exception {
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
            String ru = Strings.isNullOrEmpty(params.getRu()) ? CommonConstant.DEFAULT_WAP_URL : params.getRu();
            result = resetPwdManager.sendEmailResetPwd(passportId, clientId, AccountClientEnum.wap, AccountModuleEnum.RESETPWD, params.getEmail(), ru, params.getScode());
            result.setDefaultModel("scode", commonManager.getSecureCodeResetPwd(passportId, clientId));
            result.setDefaultModel("userid", passportId);
            result = setRuAndClientId(result, params.getRu(), params.getClient_id());
        } catch (Exception e) {
            logger.error("sendEmailResetPwd Is Failed,Username is " + params.getUsername(), e);
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
    public String checkEmailResetPwd(HttpServletRequest request, WapCheckEmailParams params, Model model, RedirectAttributes redirectAttributes) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                redirectAttributes.addAttribute("code", ErrorUtil.ERR_CODE_COM_REQURIE);
                redirectAttributes.addAttribute("message", Coder.encodeUTF8(validateResult));
                return "redirect:" + CommonConstant.DEFAULT_WAP_INDEX_URL + "/wap/findpwd/vm/reset?code={code}&message={message}";
            }
            String passportId = params.getUsername();
            int clientId = Integer.parseInt(params.getClient_id());
            result = resetPwdManager.checkEmailResetPwd(passportId, clientId, params.getScode());
            //邮箱连接校验成功跳转到修改密码页面
//            redirectAttributes.addAttribute("code", ErrorUtil.ERR_CODE_COM_REQURIE);
//            redirectAttributes.addAttribute("message", Coder.encodeUTF8(validateResult));
//            return "redirect:" + CommonConstant.DEFAULT_WAP_INDEX_URL + "/wap/findpwd/vm/reset?code={code}&message={message}";
        } catch (Exception e) {
            logger.error("checkEmailResetPwd Is Failed,Username is " + params.getUsername(), e);
        } finally {
            log(request, params.getUsername(), result.getCode());
        }
        String ru = Strings.isNullOrEmpty(params.getRu()) ? Coder.encodeUTF8(CommonConstant.DEFAULT_WAP_URL) : Coder.encodeUTF8(params.getRu());
        String client_id = Strings.isNullOrEmpty(params.getClient_id()) ? String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID) : params.getClient_id();
        redirectAttributes.addAttribute("ru", ru);
        redirectAttributes.addAttribute("client_id", client_id);
        redirectAttributes.addAttribute("message", result.getMessage());
        redirectAttributes.addAttribute("username", params.getUsername());
        redirectAttributes.addAttribute("skin", params.getSkin());
        if (result.isSuccess()) {
            String scode = (String) result.getModels().get("scode");
            redirectAttributes.addAttribute("scode", scode);
            redirectAttributes.addAttribute("code", "0");
            return "redirect:" + CommonConstant.DEFAULT_WAP_INDEX_URL + "/wap/findpwd/vm/reset?username={username}&scode={scode}&client_id={client_id}&ru={ru}&code={code}&message={message}&skin={skin}";
        }
        redirectAttributes.addAttribute("code", result.getCode());
        return "redirect:" + CommonConstant.DEFAULT_WAP_INDEX_URL + "/wap/findpwd/vm/reset?username={username}&client_id={client_id}&ru={ru}&code={code}&message={message}&skin={skin}";

    }

    /**
     * 通过接口跳转到reset页面
     *
     * @param ru
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findpwd/vm/reset", method = RequestMethod.GET)
    public String findResetView(String ru, Model model, String client_id, String scode, String username, String code, String message, String skin) throws Exception {
        Result result = new APIResultSupport(false);
        ru = Strings.isNullOrEmpty(ru) ? Coder.encodeUTF8(CommonConstant.DEFAULT_WAP_URL) : Coder.encodeUTF8(ru);
        client_id = Strings.isNullOrEmpty(client_id) ? String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID) : client_id;
        result.setCode(code);
        result.setMessage(message);
        result.setDefaultModel("ru", ru);
        result.setDefaultModel("client_id", client_id);
        result.setDefaultModel("userid", username);
        result.setDefaultModel("scode", scode);
        result.setDefaultModel("v", WapConstant.WAP_TOUCH);
        result.setDefaultModel("skin", skin);
        model.addAttribute("data", result.toString());
        return "/wap/resetpwd_touch";
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
    @ResponseBody
    public String resetPwd(HttpServletRequest request, WapPwdParams params) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                return result.toString();
            }
            String passportId = params.getUsername();
            int clientId = Integer.parseInt(params.getClient_id());
            String password = params.getPassword();
            result = resetPwdManager.resetPasswordByScode(passportId, clientId, password, params.getScode(), getIp(request));
            if (!result.isSuccess()) {
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                result.setDefaultModel("skin", params.getSkin());
                return result.toString();
            }
        } catch (Exception e) {
            logger.error("resetPwd Is Failed,Username is " + params.getUsername(), e);
        } finally {
            log(request, params.getUsername(), result.getCode());
        }
        result.setCode(ErrorUtil.SUCCESS);
        result = setRuAndClientId(result, params.getRu(), params.getClient_id());
        result.setDefaultModel("skin", params.getSkin());
        return result.toString();
    }

    private Result getSecureInfo(String passportId, int clientId) throws Exception {
        Result result = new APIResultSupport(false);
        Result queryResult = secureManager.queryAccountSecureInfo(passportId, clientId, true);
        if (queryResult.isSuccess()) {
            AccountSecureInfoVO accountSecureInfoVO = (AccountSecureInfoVO) queryResult.getDefaultModel();
            //如果用户的密保手机和密保邮箱存在，则返回模糊处理的手机号/密保邮箱及完整手机号/邮箱加密后的md5串
            if (accountSecureInfoVO != null) {
                String sec_mobile = (String) queryResult.getModels().get("sec_mobile");
                String sec_email = (String) queryResult.getModels().get("sec_email");
                if (!Strings.isNullOrEmpty(sec_mobile)) {
                    result = new APIResultSupport(true);
                    result.setDefaultModel("sec_mobile", sec_mobile);
                    result.setDefaultModel("sec_process_mobile", accountSecureInfoVO.getSec_mobile());
                }
                if (!Strings.isNullOrEmpty(sec_email)) {
                    result = new APIResultSupport(true);
                    result.setDefaultModel("sec_email", sec_email);
                    result.setDefaultModel("sec_process_email", accountSecureInfoVO.getSec_email());
                }
            }
        } else {
            result.setCode(queryResult.getCode());
            result.setMessage(queryResult.getMessage());
        }
        return result;
    }

    private Result setRuAndClientId(Result result, String ru, String client_id) {
        result.setDefaultModel("ru", Strings.isNullOrEmpty(ru) ? Coder.encodeUTF8(CommonConstant.DEFAULT_WAP_URL) : Coder.encodeUTF8(ru));
        result.setDefaultModel("client_id", Strings.isNullOrEmpty(client_id) ? CommonConstant.SGPP_DEFAULT_CLIENTID : client_id);
        result.setDefaultModel("v", WapConstant.WAP_TOUCH);
        return result;
    }

    private void log(HttpServletRequest request, String passportId, String resultCode) {
        //用户登录log
        UserOperationLog userOperationLog = new UserOperationLog(passportId, request.getRequestURI(), String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID), resultCode, getIp(request));
        userOperationLog.putOtherMessage("ref", request.getHeader("referer"));
        UserOperationLogUtil.log(userOperationLog);
    }

}
