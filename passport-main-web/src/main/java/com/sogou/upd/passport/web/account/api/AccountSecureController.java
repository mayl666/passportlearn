package com.sogou.upd.passport.web.account.api;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.manager.account.CheckManager;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.ResetPwdManager;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.annotation.LoginRequired;
import com.sogou.upd.passport.web.account.form.*;
import com.sogou.upd.passport.web.inteceptor.HostHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-5-9 Time: 上午11:50 To change this template use
 * File | Settings | File Templates.
 */
@Controller
@RequestMapping("/api")
public class AccountSecureController {
    private static final Logger logger = LoggerFactory.getLogger(AccountSecureController.class);

    @Autowired
    private CommonManager commonManager;
    @Autowired
    private SecureManager secureManager;
    @Autowired
    private ResetPwdManager resetPwdManager;
    @Autowired
    private CheckManager checkManager;
    @Autowired
    private HostHolder hostHolder;

    // TODO:method是POST或GET，或者POST的话，GET怎么处理

    /**
     * 查询密保方式，用于重置密码/修改密保内容
     *
     * @param params 传入的参数
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/secure/query", method = RequestMethod.POST)
    @ResponseBody
    public Object querySecureInfo(UserCaptchaParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String username = params.getUsername();
        int clientId = Integer.parseInt(params.getClient_id());
        String captcha = params.getCaptcha();
        String token = params.getToken();
        if (!checkManager.checkCaptcha(captcha, token)) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
            return result.toString();
        }
        String passportId = commonManager.getPassportIdByUsername(username);
        if (passportId == null) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
            return result.toString();
        }

        return secureManager.queryAccountSecureInfo(passportId, clientId, true).toString();
    }

    /**
     * 重置密码（邮件方式）——1.发送重置密码申请验证邮件至注册邮箱
     *
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findpwd/sendremail", method = RequestMethod.POST)
    @ResponseBody
    public Object sendEmailRegForResetPwd(BaseAccountParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String passportId = params.getPassport_id();
        int clientId = Integer.parseInt(params.getClient_id());
        return resetPwdManager.sendEmailResetPwdByPassportId(passportId, clientId, true).toString();
    }

    /**
     * 重置密码（邮件方式）——1.发送重置密码申请验证邮件至绑定邮箱
     *
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findpwd/sendbemail", method = RequestMethod.POST)
    @ResponseBody
    public Object sendEmailBindForResetPwd(BaseAccountParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String passportId = params.getPassport_id();
        int clientId = Integer.parseInt(params.getClient_id());
        return resetPwdManager.sendEmailResetPwdByPassportId(passportId, clientId, false);
    }

    /*
     * 验证邮件链接的方法在Action里，需要指向某页面，不能作为接口？TODO:Action完成之后删除此注释
     */

    /**
     * 重置密码（邮件方式）——3.再一次验证token，并修改密码
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "/findpwd/email", method = RequestMethod.POST)
    @ResponseBody
    public Object resetPasswordByEmail(AccountPwdScodeParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String passportId = params.getPassport_id();
        int clientId = Integer.parseInt(params.getClient_id());
        String password = params.getPassword();
        String scode = params.getScode();

        if (!checkManager.checkLimitResetPwd(passportId, clientId)) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_LIMITED);
            return result.toString();
        }

        return resetPwdManager.resetPasswordByEmail(passportId, clientId, password, scode);
    }

    /**
     * 发送手机验证码——不区分业务，统一接口
     *
     * @param params 传入的参数:
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sendsms", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired(value = false)
    public Object sendSms(UserModuleTypeParams params) throws Exception {
        // TODO:module按业务模块划分
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String username = params.getUsername();
        int clientId = Integer.parseInt(params.getClient_id());
        String mode = params.getMode();
        String module = params.getModule();
        if (mode.equals("1")) {
            // 发送至已绑定手机（验证登录，不传递手机号——适用于修改绑定手机）
            if (!hostHolder.isLogin()) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CHECKLOGIN_FAILED);
                return result.toString();
            }
            String passportIdLogin = hostHolder.getPassportId();
            if (!passportIdLogin.equals(username)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_LOGIN_OPERACCOUNT_MISMATCH);
                return result.toString();
            }
            return secureManager.sendMobileCodeByPassportId(username, clientId);
            /*if (PhoneUtil.verifyPhoneNumberFormat(username)) {
                if (!accountManager.isAccountExists(username)) {
                    return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_OBTAIN_FIELDS);
                }
                return accountSecureManager.sendSmsCodeToMobile(username, clientId);
            } else {
                return accountSecureManager.sendMobileCodeByPassportId(username, clientId);
            }*/
        } else if (mode.equals("2")) {
            // 发送至已绑定手机（不验证登录，不传递手机号——适用于找回密码）
            return secureManager.sendMobileCodeByPassportId(username, clientId);
        } else if (mode.equals("3")) {
            // 发送至未绑定手机（验证登录，传递手机号——适用于修改绑定手机或注册）
            if (!hostHolder.isLogin()) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CHECKLOGIN_FAILED);
                return result.toString();
            }
            String passportIdLogin = hostHolder.getPassportId();
            if (!passportIdLogin.equals(params.getPassport_id())) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_LOGIN_OPERACCOUNT_MISMATCH);
                return result.toString();
            }
            if (PhoneUtil.verifyPhoneNumberFormat(username)) {
                if (commonManager.isAccountExists(username)) {
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONE_BINDED);
                    return result.toString();
                }
                return secureManager.sendMobileCode(username, clientId);
            } else {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONEERROR);
                return result.toString();
            }
        } else {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            return result.toString();
        }
    }

    @RequestMapping(value = "/findpwd/sendsms", method = RequestMethod.POST)
    @ResponseBody
    public Object sendSmsResetPwd(BaseAccountParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String passportId = params.getPassport_id();
        int clientId = Integer.parseInt(params.getClient_id());
        return secureManager.sendMobileCodeByPassportId(passportId, clientId);
    }

    /**
     * 重置密码（手机方式）——2.检查手机短信码 TODO:可否与其他验证的合并在一起？
     *
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findpwd/checksms", method = RequestMethod.POST)
    @ResponseBody
    public Object checkSmsResetPwd(AccountSmsScodeParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String passportId = params.getPassport_id();
        int clientId = Integer.parseInt(params.getClient_id());
        String smsCode = params.getSmscode();
        return resetPwdManager.checkMobileCodeResetPwd(passportId, clientId, smsCode);
    }

    /**
     * 重置密码（手机和密保方式）——2.根据scode修改密码
     *
     * @param params
     * @return
     * @throws Exception
     */
/*    @RequestMapping(value = "/findpwd/mobile", method = RequestMethod.POST)
    @ResponseBody
    public Object resetPasswordByMobile(AccountPwdScodeParams params) throws Exception {
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            return Result.buildError(ErrorUtil.ERR_CODE_COM_REQURIE, validateResult);
        }
        String passportId = params.getPassport_id();
        int clientId = Integer.parseInt(params.getClient_id());
        String password = params.getPassword();
        String scode = params.getScode();
        return accountSecureManager.resetPasswordBySecureCode(passportId, clientId, password, scode);
    }*/

    /**
     * 重置密码（密保方式）——1.验证密保答案及captcha
     *
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/findpwd/checkanswer", method = RequestMethod.POST)
    @ResponseBody
    public Object checkAnswerResetPwd(AccountAnswerCaptParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String passportId = params.getPassport_id();
        int clientId = Integer.parseInt(params.getClient_id());
        String answer = params.getAnswer();
        String captcha = params.getCaptcha();
        String token = params.getToken();
        if (!checkManager.checkCaptcha(captcha, token)) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
            return result.toString();
        }
        return resetPwdManager.checkAnswerByPassportId(passportId, clientId, answer, token, captcha);
    }

    /**
     * 重置密码（手机和密保方式）——2.根据scode修改密码 TODO:与手机方式合并，目前代码相同
     *
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = {"/findpwd/mobile", "/findpwd/ques"}, method = RequestMethod.POST)
    @ResponseBody
    public Object resetPasswordByScode(AccountPwdScodeParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String passportId = params.getPassport_id();
        int clientId = Integer.parseInt(params.getClient_id());
        String password = params.getPassword();
        String scode = params.getScode();

        // 第一步，检测scode
        if (!checkManager.checkScodeResetPwd(passportId, clientId, scode)) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNTSECURE_RESETPWD_URL_FAILED);
            return result.toString();
        }

        if (!checkManager.checkLimitResetPwd(passportId, clientId)) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_RESETPASSWORD_LIMITED);
            return result.toString();
        }

        // 第二步，修改密码
        return resetPwdManager.resetPassword(passportId, clientId, password);
    }

    /**
     * 修改密保邮箱——1.验证原绑定邮箱及发送邮件至待绑定邮箱
     *
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/bind/sendemail", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public Object sendEmailForBind(AccountBindEmailParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String passportId = params.getPassport_id();
        if (!passportId.equals(hostHolder.getPassportId())) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_LOGIN_OPERACCOUNT_MISMATCH);
            return result.toString();
        }
        int clientId = Integer.parseInt(params.getClient_id());
        String password = params.getPassword();
        String newEmail = params.getNew_email();
        String oldEmail = params.getOld_email();
        return secureManager.sendEmailForBinding(passportId, clientId, password, newEmail, oldEmail);
    }

    /*
     * 验证邮件链接的方法在Action里，需要指向某页面，不能作为接口？TODO:Action完成之后删除此注释
     */

    /**
     * 修改密保手机——1.检查原绑定手机短信码
     *
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/bind/checksms", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public Object checkSmsBindMobile(AccountSmsScodeParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String passportId = params.getPassport_id();
        if (!passportId.equals(hostHolder.getPassportId())) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_LOGIN_OPERACCOUNT_MISMATCH);
            return result.toString();
        }
        int clientId = Integer.parseInt(params.getClient_id());
        String smsCode = params.getSmscode();
        return secureManager.checkMobileCodeOldForBinding(passportId, clientId, smsCode);
    }

    /**
     * 修改密保手机——2.验证密码、新绑定手机短信码，绑定新手机号
     *
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/bind/", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @LoginRequired
    public Object modifyBindMobile(AccountSmsNewScodeParams params) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String passportId = params.getPassport_id();
        if (!passportId.equals(hostHolder.getPassportId())) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_LOGIN_OPERACCOUNT_MISMATCH);
            return result.toString();
        }
        int clientId = Integer.parseInt(params.getClient_id());
        String smsCode = params.getSmscode();
        String newMobile = params.getNew_mobile();
        String scode = params.getScode();
        return secureManager.modifyMobileByPassportId(passportId, clientId, newMobile, smsCode, scode, false);
    }

    /**
     * 修改密保手机——2.验证密码、新绑定手机短信码，绑定新手机号
     *
     * @param params
     * @param password
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/bind/bindmobile", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public Object bindNewMobile(AccountSmsNewScodeParams params, @RequestParam("password") String password) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult) || Strings.isNullOrEmpty(password)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String passportId = params.getPassport_id();
        if (!passportId.equals(hostHolder.getPassportId())) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_LOGIN_OPERACCOUNT_MISMATCH);
            return result.toString();
        }
        int clientId = Integer.parseInt(params.getClient_id());
        String smsCode = params.getSmscode();
        String newMobile = params.getNew_mobile();
        return secureManager.modifyMobileByPassportId(passportId, clientId, newMobile, smsCode, password, true);
    }

    /**
     * 修改密保问题
     *
     * @param params
     * @param newQues
     * @param newAnswer
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/bind/ques", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public Object bindQues(AccountPwdParams params, @RequestParam("new_ques") String newQues,
                           @RequestParam("new_answer") String newAnswer) throws Exception {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult) || StringUtil.checkExistNullOrEmpty(newQues, newAnswer)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(StringUtil.defaultIfEmpty(validateResult, "必选参数未填"));
            return result.toString();
        }
        String passportId = params.getPassport_id();
        if (!passportId.equals(hostHolder.getPassportId())) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_LOGIN_OPERACCOUNT_MISMATCH);
            return result.toString();
        }
        int clientId = Integer.parseInt(params.getClient_id());
        String password = params.getPassword();
        return secureManager.modifyQuesByPassportId(passportId, clientId, password, newQues, newAnswer);
    }
}