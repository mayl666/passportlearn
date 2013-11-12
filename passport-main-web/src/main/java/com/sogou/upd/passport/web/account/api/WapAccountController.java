package com.sogou.upd.passport.web.account.api;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.LoginManager;
import com.sogou.upd.passport.manager.account.WapLoginManager;
import com.sogou.upd.passport.manager.form.WapLoginParams;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
public class WapAccountController extends BaseController {
    @Autowired
    private LoginManager loginManager;
    @Autowired
    private WapLoginManager wapLoginManager;

    @RequestMapping(value = "/wap/login", method = RequestMethod.POST)
    public String login(HttpServletRequest request, HttpServletResponse response, Model model, WapLoginParams loginParams)
            throws Exception {
        Result result = new APIResultSupport(false);
        String ip = getIp(request);
        //参数验证
        String validateResult = ControllerHelper.validateParams(loginParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return "forward:/wap/errorMsg?msg="+result.toString();
        }

        result = wapLoginManager.accountLogin(loginParams, ip);
        String userId = loginParams.getUsername();
        //用户登录log
        UserOperationLog userOperationLog = new UserOperationLog(userId, request.getRequestURI(), loginParams.getClient_id(), result.getCode(), getIp(request));
        String referer = request.getHeader("referer");
        userOperationLog.putOtherMessage("ref", referer);
        UserOperationLogUtil.log(userOperationLog);

        if (result.isSuccess()) {
            userId = result.getModels().get("userid").toString();
            int clientId = Integer.parseInt(loginParams.getClient_id());
            loginManager.doAfterLoginSuccess(loginParams.getUsername(), ip, userId, clientId);
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
        }
        return loginParams.getRu();
    }

    @RequestMapping(value = "/wap/errorMsg")
    @ResponseBody
    public Object errorMsg(@RequestParam("msg") String msg) throws Exception {
        return msg;
    }

}
