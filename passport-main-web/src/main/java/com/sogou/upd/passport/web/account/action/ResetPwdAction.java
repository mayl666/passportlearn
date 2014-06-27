package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
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
import org.apache.commons.codec.digest.DigestUtils;
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
 * User: mayan
 * Date: 13-6-7 Time: 下午5:49
 * 重置密码 （通过注册邮箱，密保邮箱，密保手机，密保问题）
 */
@Controller
@RequestMapping(value = "/web")
public class ResetPwdAction extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ResetPwdAction.class);

    private static final String SOHU_FINDPWD_URL = SHPPUrlConstant.SOHU_FINDPWD_URL;

    @Autowired
    private RegManager regManager;
    @Autowired
    private SecureManager secureManager;
    @Autowired
    private CheckManager checkManager;
    @Autowired
    private ResetPwdManager resetPwdManager;
    @Autowired
    private CommonManager commonManager;

    /**
     * 找回密码主页跳转
     *
     * @param ru
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"/findpwd", "/findpwd/index", "/recover/index"}, method = RequestMethod.GET)
    public String findPwdView(String ru) throws Exception {
        return "/recover/index";
    }

    /**
     * 通过注册/密保邮箱；注册/密保手机；找回密码页面跳转
     *
     * @param params
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"/recover/email", "/recover/mobile"}, method = RequestMethod.GET)
    public String email(HttpServletRequest request, FindPwdParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            model.addAttribute("data", result.toString());
            return "/recover/index";
        }

        switch (params.getType()) {
            case "bind_email":
                result = secureManager.queryAccountSecureInfo(params.getUserid(), Integer.parseInt(params.getClient_id()), true);
                String email = (String) (result.getModels().get("sec_email"));
                result = new APIResultSupport(true);
                result.setDefaultModel("bind_email", email);
                result.setDefaultModel("userid", params.getUserid());
                model.addAttribute("data", result.toString());
                log(request, params.getUserid(), result.getCode());
                return "/recover/email";
            case "reg_email":
                result.setDefaultModel("reg_email", params.getUserid());
                result.setDefaultModel("userid", params.getUserid());
                model.addAttribute("data", result.toString());
                return "/recover/email";
            case "bind_mobile":
                result = secureManager.queryAccountSecureInfo(params.getUserid(), Integer.parseInt(params.getClient_id()), true);
                String mobile = (String) (result.getModels().get("sec_mobile"));
                result = new APIResultSupport(true);
                result.setDefaultModel("bind_mobile", mobile);
                result.setDefaultModel("userid", params.getUserid());
                model.addAttribute("data", result.toString());
                log(request, params.getUserid(), result.getCode());
                return "/recover/mobile";
            case "reg_mobile":
                result.setDefaultModel("reg_mobile", getMobile(params.getUserid()));
                result.setDefaultModel("userid", params.getUserid());
                model.addAttribute("data", result.toString());
                log(request, params.getUserid(), result.getCode());
                return "/recover/mobile";
        }

        result.setCode(ErrorUtil.ERR_CODE_FINDPWD_TYPE_FAILED);
        result.setMessage(validateResult);
        model.addAttribute("data", result.toString());
        return "/recover/index";
    }

    private void log(HttpServletRequest request, String passportId, String resultCode) {
        //用户登录log
        UserOperationLog userOperationLog = new UserOperationLog(passportId, request.getRequestURI(), String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID), resultCode, getIp(request));
        userOperationLog.putOtherMessage("ref", request.getHeader("referer"));
        UserOperationLogUtil.log(userOperationLog);
    }

    /**
     * 验证注册或密保邮箱页面跳转
     *
     * @param email
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/recover/sended", method = RequestMethod.GET)
    public String sended(@RequestParam(value = "email", defaultValue = "") String email, Model model) throws Exception {
        Result result = new APIResultSupport(true);
        result.setDefaultModel("email", email);
        model.addAttribute("data", result.toString());
        return "/recover/sended";
    }

    /**
     * 重置密码页面跳转
     *
     * @param userid
     * @param scode
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/recover/reset", method = RequestMethod.GET)
    public String reset(@RequestParam(value = "userid") String userid, @RequestParam(value = "scode") String scode,
                        Model model) throws Exception {
        Result result = new APIResultSupport(true);
        result.setDefaultModel("userid", userid);
        result.setDefaultModel("scode", scode);
        model.addAttribute("data", result.toString());
        return "/recover/reset";
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
                passportId = passportId + "@sogou.com";
            }
            //查询主账号：@sogou.com/外域/第三方账号返回原样，手机账号返回绑定的主账号，若无主账号则返回手机号+@sohu.com
            passportId = commonManager.getPassportIdByUsername(passportId);
            AccountDomainEnum domain = AccountDomainEnum.getAccountDomain(passportId);
            switch (domain) {
                //主账号是sohu域/外域/手机号的去sohu找回密码
                case SOHU:
                case OTHER:
                case PHONE:
                    return "redirect:" + SOHU_FINDPWD_URL + "?ru=" + CommonConstant.DEFAULT_CONNECT_REDIRECT_URL;
                case THIRD:
                    return "redirect:/web/findpwd";
            }
            result.setDefaultModel("userid", username);   //页面上需要显示的用户填入的账号

            //校验验证码
            String captcha = params.getCaptcha();
            String token = params.getToken();
            if (!checkManager.checkCaptcha(captcha, token)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
                model.addAttribute("data", result.toString());
                return "/recover/index";
            }

            boolean checkTimes = resetPwdManager.checkFindPwdTimes(username).isSuccess();
            if (!checkTimes) {
                result.setCode(ErrorUtil.ERR_CODE_FINDPWD_LIMITED);
                model.addAttribute("data", result.toString());
                return "/recover/index";
            }

            result = regManager.isAccountNotExists(passportId, Integer.parseInt(params.getClient_id()));
            if (!result.isSuccess()) {
                result.setMessage("账号不存在");
                model.addAttribute("data", result.toString());
                return "/recover/index";
            }
//            if (PhoneUtil.verifyPhoneNumberFormat(passportId)) {
//                //如果是手机号，取主账号
//                passportId = (String) result.getModels().get("userid");
//            }
            int clientId = Integer.parseInt(params.getClient_id());
            result = secureManager.queryAccountSecureInfo(passportId, clientId, true);
            if (!result.isSuccess()) {
                model.addAttribute("data", result.toString());
                return "/recover/index";
            }
            //记录找回密码次数
            resetPwdManager.incFindPwdTimes(username);
            //如果所填账号为手机账号，则返回模糊处理的手机号及完整手机号加密后的md5串
            if (AccountDomainEnum.getAccountDomain(username).equals(AccountDomainEnum.PHONE)) {
                AccountSecureInfoVO accountSecureInfoVO = (AccountSecureInfoVO) result.getDefaultModel();
                String sec_mobile = (String) result.getModels().get("sec_mobile");
                result.setDefaultModel("sec_process_mobile", accountSecureInfoVO.getSec_mobile());
                result.setDefaultModel("sec_mobile_md5", DigestUtils.md5Hex(sec_mobile.getBytes()));
            }
            if (AccountDomainEnum.getAccountDomain(username).equals(AccountDomainEnum.OTHER)) {
                result.setDefaultModel("reg_email", username);
            }
//        result.setDefaultModel("userid", passportId);    //用户输入账号的主账号
            model.addAttribute("data", result.toString());   //返回的信息包含密保手机、密保邮箱、及密保问题（找回密码不会用到此返回结果）
            passportIdLog = passportId;
        } catch (Exception e) {
            logger.error("querySecureInfo Is Failed,Username is " + params.getUsername(), e);
        } finally {
            log(request, passportIdLog, result.getCode());
        }
        return "/recover/type";
    }

    private String getMobile(String passportId) {
        if (passportId.contains("@")) {
            return passportId.substring(0, passportId.indexOf("@"));
        }
        return passportId;
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
    public String sendEmailRegResetPwd(HttpServletRequest request, BaseAccountParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String passportId = params.getUserid();
        int clientId = Integer.parseInt(params.getClient_id());
        result = resetPwdManager.sendEmailResetPwdByPassportId(passportId, clientId, true);
        result.setDefaultModel("userid", passportId);
        log(request, passportId, result.getCode());
        return result.toString();
    }

    /**
     * 发送重置密码申请验证邮件至绑定邮箱
     *
     * @param params
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findpwd/sendbemail", method = RequestMethod.POST)
    @ResponseBody
    public String sendEmailBindResetPwd(HttpServletRequest request, BaseAccountParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String passportId = params.getUserid();
        int clientId = Integer.parseInt(params.getClient_id());
        result = resetPwdManager.sendEmailResetPwdByPassportId(passportId, clientId, false);
        result.setDefaultModel("userid", passportId);
        log(request, passportId, result.getCode());
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
    public String checkEmailResetPwd(HttpServletRequest request, FindPwdCheckMailParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String passportId = params.getUserid();
        int clientId = Integer.parseInt(params.getClient_id());
        result = resetPwdManager.checkEmailResetPwd(passportId, clientId, params.getScode());
        if (result.isSuccess()) {
            //邮箱连接校验成功跳转到修改密码页面
            result.setDefaultModel("userid", passportId);
            model.addAttribute("data", result.toString());
            return "/recover/reset";
        }
        result.setCode(ErrorUtil.ERR_CODE_FINDPWD_EMAIL_FAILED);
        model.addAttribute("data", result.toString());
        log(request, passportId, result.getCode());
        return "/recover/index";
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
    public String resetPwd(HttpServletRequest request, ResetPwdParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String passportId = params.getUserid();
        int clientId = Integer.parseInt(params.getClient_id());
        String password = params.getPassword();
        result = resetPwdManager.resetPasswordByScode(passportId, clientId, password, params.getScode(), getIp(request));
        log(request, passportId, result.getCode());
        return result.toString();
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
    public Object sendSmsSecMobile(HttpServletRequest request, BaseAccountParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        int clientId = Integer.parseInt(params.getClient_id());
        result = resetPwdManager.sendFindPwdMobileCode(params.getUserid(), clientId);
        log(request, params.getUserid(), result.getCode());
        return result.toString();
    }

    /**
     * 验证找回密码发送的手机验证码
     *
     * @param params
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findpwd/checksms", method = RequestMethod.POST)
    @ResponseBody
    public Object checkSmsSecMobile(HttpServletRequest request, FindPwdCheckSmscodeParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        int clientId = Integer.parseInt(params.getClient_id());
        result = resetPwdManager.checkMobileCodeResetPwd(params.getUserid(), clientId, params.getSmscode());
        result.setDefaultModel("userid", params.getUserid());
        log(request, params.getUserid(), result.getCode());
        return result.toString();
    }


    /**
     * todo 验证手机和验证码后跳转到重置密码页
     *
     * @param params
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findpwd/resetview", method = RequestMethod.POST)
    @ResponseBody
    public Object resetPwdView(HttpServletRequest request, FindPwdCheckSmscodeParams params, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        //需要根据用户名生成token，返回至reset页面
        return result;
    }
}