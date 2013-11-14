package com.sogou.upd.passport.web.account.api;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.LoginManager;
import com.sogou.upd.passport.manager.account.WapLoginManager;
import com.sogou.upd.passport.manager.form.WapAuthTokenParams;
import com.sogou.upd.passport.manager.form.WapLoginParams;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-11-12
 * Time: 下午12:17
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class WapAccountController extends BaseController {
    private static final String WAP_INDEX = "http://wap.sogou.com/";

    @Autowired
    private LoginManager loginManager;
    @Autowired
    private WapLoginManager wapLoginManager;


    @RequestMapping(value = "/wap/index", method = RequestMethod.GET)
    public String index(HttpServletRequest request,Model model,
                        @RequestParam(defaultValue = "") String ru,
                        @RequestParam(defaultValue = "") String client_id)
            throws Exception {

        model.addAttribute("ru",ru);
        model.addAttribute("client_id",client_id);
        return "wap/index";
    }



    @RequestMapping(value = "/wap/login", method = RequestMethod.POST)
    @ResponseBody
    public String login(HttpServletRequest request, HttpServletResponse response, Model model, WapLoginParams loginParams)
            throws Exception {
        Result result = new APIResultSupport(false);
        String ip = getIp(request);
        int clientId = Integer.parseInt(loginParams.getClient_id());
        //参数验证
        String validateResult = ControllerHelper.validateParams(loginParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            response.sendRedirect(getErrorReturnStr(loginParams, result.getMessage()));
            return "";
        }

        result = wapLoginManager.accountLogin(loginParams, ip);
        //用户登录log
        UserOperationLog userOperationLog = new UserOperationLog(loginParams.getUsername(), request.getRequestURI(), loginParams.getClient_id(), result.getCode(), getIp(request));
        String referer = request.getHeader("referer");
        userOperationLog.putOtherMessage("ref", referer);
        UserOperationLogUtil.log(userOperationLog);

        if (result.isSuccess()) {
            String userId = result.getModels().get("userid").toString();
            String accesstoken = result.getModels().get("token").toString();
            loginManager.doAfterLoginSuccess(loginParams.getUsername(), ip, userId, clientId);
            response.sendRedirect(loginParams.getRu() + "?token=" + accesstoken);
            return "";
        } else {
            loginManager.doAfterLoginFailed(loginParams.getUsername(), ip);
            //校验是否需要验证码
            boolean needCaptcha = loginManager.needCaptchaCheck(loginParams.getClient_id(), loginParams.getUsername(), getIp(request));
            if (needCaptcha) {
                result.setDefaultModel("needCaptcha", true);
            }
            if (result.getCode().equals(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_PWD_ERROR);
                result.setMessage("密码错误");
            }
            response.sendRedirect(getErrorReturnStr(loginParams, result.getMessage()));
            return "";
        }
    }

    private String getErrorReturnStr(WapLoginParams loginParams, String errorMsg) {
        if (!Strings.isNullOrEmpty(loginParams.getRu())) {
            return (loginParams.getRu() + "?errorMsg=" + errorMsg);
        }
        return WAP_INDEX + "?errorMsg=" + errorMsg;
    }


    /*@RequestMapping(value = "/wap/authtoken")
    @ResponseBody
    public Object authtoken(HttpServletRequest request, HttpServletResponse response, WapAuthTokenParams wapAuthTokenParams) {
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(wapAuthTokenParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return "1";//表示参数填写错误
        }

        String passportId = wapLoginManager.authtoken(wapAuthTokenParams.getAccesstoken());
        if (!Strings.isNullOrEmpty(passportId)) {
            return "0|" + passportId;
        } else {
            return "2";//token验证失败
        }
    }*/
}
