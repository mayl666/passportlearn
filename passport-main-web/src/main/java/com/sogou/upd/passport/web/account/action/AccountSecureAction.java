package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.StringUtil;
import com.sogou.upd.passport.manager.account.AccountManager;
import com.sogou.upd.passport.manager.account.AccountSecureManager;
import com.sogou.upd.passport.manager.form.AccountSecureParams;
import com.sogou.upd.passport.manager.form.MobileModifyPwdParams;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-4-28 Time: 下午1:51 To change this template use
 * File | Settings | File Templates.
 */
@Controller
@RequestMapping("/web/findpwd")
public class AccountSecureAction extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AccountSecureAction.class);

    @Autowired
    private AccountManager accountManager;
    @Autowired
    private AccountSecureManager accountSecureManager;

    /**
     * 显示找回密码界面
     */
    @RequestMapping
    public String findPwd(Model model) throws Exception {
        model.addAttribute("clientId", "999");
        return "findpwd";
    }
    /**
     * 查询账号所拥有的密码找回方式
     *
     * @param username 传入的参数
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public String queryPassportId(@RequestParam("username") String username, @RequestParam("client_id") String client_id,
            Model model) throws Exception {
        if(!StringUtil.checkIsDigit(client_id)){
            model.addAttribute("error", Result.buildError(ErrorUtil.ERR_CODE_COM_REQURIE));
            return "forward:";
        }
        int clientId = Integer.parseInt(client_id);
        String passportId = accountManager.getPassportIdByUsername(username);
        if (Strings.isNullOrEmpty(passportId)) {
            // model.addAttribute("exist", false);
            return "forward:";
        }
        // model.addAttribute("exist", true);
        Result result = accountSecureManager.queryAccountSecureInfo(
                passportId, clientId);
        if (result.getStatus().equals("0")) {
            model.addAttribute("secInfo",result.getData());
        } else {
            model.addAttribute("error", result);
        }
        model.addAttribute("passportId", passportId);
        model.addAttribute("clientId", clientId);
        return "forward:";
    }

    /**
     * 发送手机验证码
     *
     * @param passportId 传入的参数;
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sendsms", method = RequestMethod.POST)
    public String sendSms(@RequestParam("username") String passportId, @RequestParam("client_id") String client_id,
            Model model) throws Exception {
        if(Strings.isNullOrEmpty(client_id) || !StringUtil.checkIsDigit(client_id)){
            model.addAttribute("error", Result.buildError(ErrorUtil.ERR_CODE_COM_REQURIE));
            return "forward:";
        }
        int clientId = Integer.parseInt(client_id);
        Result result = accountSecureManager.sendMobileCodeByPassportId(passportId, clientId);
        if (!result.getStatus().equals("0")) {
            model.addAttribute("error", result);
            return "forward:";
        }
        model.addAttribute("send", true);
        return "forward:";
    }

    @RequestMapping(value = "/mobile", method = RequestMethod.POST)
    public String resetPasswordByMobile(@RequestParam("username") String passportId, @RequestParam("client_id") int clientId,
                @RequestParam("password") String password, @RequestParam("smscode") String smsCode, Model model) throws Exception {
        if (Strings.isNullOrEmpty(passportId)) {
            model.addAttribute("error", Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT));
            return "forward:";
        }
        if (Strings.isNullOrEmpty(password) || Strings.isNullOrEmpty(smsCode)) {
            model.addAttribute("error", Result.buildError(ErrorUtil.ERR_CODE_COM_REQURIE));
            return "forward:";
        }
        Result result = accountSecureManager.resetPasswordByMobile(passportId, clientId, password, smsCode);
        model.addAttribute("error", result);
        if (result.getStatus().equals("0")) {
            // 重置密码成功
            // TODO
            return "success";
        } else {
            return "forward:";
        }
    }

    @RequestMapping(value = "/sendemail", method = RequestMethod.POST)
    public String sendEmail(@RequestParam("username") String passportId, @RequestParam("client_id") String client_id,
            Model model) throws Exception {
        Result result;
        if(!StringUtil.checkIsDigit(client_id)){
            result = Result.buildError(ErrorUtil.ERR_CODE_COM_REQURIE);
            model.addAttribute("error", result);
            return "forward:";
        }
        int clientId = Integer.parseInt(client_id);
        result  = accountSecureManager.sendEmailResetPwdByPassportId(passportId, clientId, 2);
        model.addAttribute("error", result);
        if (result.getStatus().equals("0")) {
            return "forward:";
        }
        return "forward:";
    }

    @RequestMapping(value = "/checkemail", method = RequestMethod.GET)
    public String checkEmail(@RequestParam("uid") String uid, @RequestParam("cid") String client_id, @RequestParam("token") String token,
            Model model) throws Exception {
        Result result;
        if(!StringUtil.checkIsDigit(client_id)){
            result = Result.buildError(ErrorUtil.ERR_CODE_COM_REQURIE);
            model.addAttribute("error", result);
            return "forward:";
        }
        int clientId = Integer.parseInt(client_id);
        result = accountSecureManager.checkEmailResetPwd(uid, clientId,token);
        model.addAttribute("error", result);
        if (result.getStatus().equals("0")) {
            model.addAttribute("username", uid);
            model.addAttribute("clientId", clientId);
            model.addAttribute("token", token);
            return "resetpwd";
        }
        return "findpwd";
    }

    @RequestMapping(value = "/email", method = RequestMethod.POST)
    public String resetPasswordByEmail(@RequestParam("username") String passportId, @RequestParam("client_id") String client_id,
            @RequestParam("password") String password, @RequestParam("token") String token, Model model) throws Exception {
        int clientId = Integer.parseInt(client_id);
        Result result = accountSecureManager.resetPasswordByEmail(passportId, clientId, password, token);
        model.addAttribute("error", result);
        return "success";
    }

    @RequestMapping(value = "/ques", method = RequestMethod.POST)
    public String resetPasswordByQues(@RequestParam("username") String passportId, @RequestParam("client_id") String client_id,
            @RequestParam("password") String password, String answer, Model model) throws Exception {
        if (Strings.isNullOrEmpty(passportId) || Strings.isNullOrEmpty(password) || Strings.isNullOrEmpty(answer)) {
            model.addAttribute("error", Result.buildError(ErrorUtil.ERR_CODE_COM_REQURIE));
            return "forward:";
        }
        int clientId = Integer.parseInt(client_id);
        Result result = accountSecureManager.resetPasswordByQues(passportId, clientId, password, answer);
        model.addAttribute("error", result);
        if (result.getStatus().equals("0")) {
            return "success";
        } else {
            return "forward:";
        }
    }

}
