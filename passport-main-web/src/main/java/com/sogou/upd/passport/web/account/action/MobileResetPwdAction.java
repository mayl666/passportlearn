package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.ResetPwdManager;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.form.FindPwdCheckSmscodeParams;
import com.sogou.upd.passport.web.account.form.MoblieCodeParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
    private ResetPwdManager resetPwdManager;

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
            int clientId = Integer.parseInt(params.getClient_id());
            result = resetPwdManager.sendFindPwdMobileCode(params.getMobile(), clientId);
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

    private void log(HttpServletRequest request, String passportId, String resultCode) {
        //用户登录log
        UserOperationLog userOperationLog = new UserOperationLog(passportId, request.getRequestURI(), String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID), resultCode, getIp(request));
        userOperationLog.putOtherMessage("ref", request.getHeader("referer"));
        UserOperationLogUtil.log(userOperationLog);
    }

}
