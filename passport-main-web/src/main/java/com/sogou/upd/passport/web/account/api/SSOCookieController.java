package com.sogou.upd.passport.web.account.api;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.DateUtil;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.common.validation.constraints.RuValidator;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.CookieManager;
import com.sogou.upd.passport.manager.form.PPCookieParams;
import com.sogou.upd.passport.manager.form.SSOCookieParams;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.form.SSOClearCookieParams;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-12-31
 * Time: 下午4:05
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class SSOCookieController extends BaseController {
    @Autowired
    private CookieManager cookieManager;

    private static final String DEFAULT_URL = "https://account.sogou.com";

    @RequestMapping(value = "/sso/setcookie", method = RequestMethod.GET)
    public void setcookie(HttpServletRequest request, HttpServletResponse response, SSOCookieParams ssoCookieParams) throws Exception {
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(ssoCookieParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            returnErrMsg(response,ssoCookieParams.getRu(),result.getCode(),result.getMessage());
        }

        result = cookieManager.setSSOCookie(response,ssoCookieParams);

        String ru = ssoCookieParams.getRu();
        if(!result.isSuccess()){
            log(request,"sso_setcookie",ru,result.getCode());
            returnErrMsg(response,ru,result.getCode(),result.getMessage());
        }
        if (!StringUtils.isBlank(ru)) {
            response.sendRedirect(ru);
        }
        log(request,"sso_setcookie",ru,"0");
        return;
    }

    private void returnErrMsg(HttpServletResponse response, String ru,String errorCode,String errorMsg)throws Exception{
        RuValidator ruValidator=new RuValidator();
        boolean isValid = ruValidator.isValid(ru,null);
        if (Strings.isNullOrEmpty(ru) || !isValid){
            ru = DEFAULT_URL;
        }
        response.sendRedirect(ru + "?errorCode="+errorCode+"&errorMsg="+ Coder.encodeUTF8(errorMsg));
        return;
    }

    private void log(HttpServletRequest request,String passportId,String ru,String resultCode){
        //用户登录log
        UserOperationLog userOperationLog = new UserOperationLog(passportId, request.getRequestURI(), "", resultCode, getIp(request));
        userOperationLog.putOtherMessage("ref", request.getHeader("referer"));
        userOperationLog.putOtherMessage("ru", ru);
        UserOperationLogUtil.log(userOperationLog);
    }

    @RequestMapping(value = "/sso/logout_redirect", method = RequestMethod.GET)
    public void logoutWithRu(HttpServletRequest request, HttpServletResponse response, SSOClearCookieParams ssoClearCookieParams)
            throws Exception {
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(ssoClearCookieParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            returnErrMsg(response, ssoClearCookieParams.getRu(),result.getCode(), result.getMessage());
        }
        String domain = ssoClearCookieParams.getDomain();
        ServletUtil.clearCookie(response, LoginConstant.COOKIE_SGINF, domain);
        ServletUtil.clearCookie(response, LoginConstant.COOKIE_SGRDIG, domain);

        //记录log
        String ru = ssoClearCookieParams.getRu();
        UserOperationLog userOperationLog = new UserOperationLog("sso_logout", "", "0", getIp(request));
        userOperationLog.putOtherMessage("ref", request.getHeader("referer"));
        userOperationLog.putOtherMessage(CommonConstant.RESPONSE_RU, ru);
        UserOperationLogUtil.log(userOperationLog);

        if (!StringUtils.isBlank(ru)) {
            response.sendRedirect(ru);
        }
        return;
    }

    @RequestMapping(value = "/sso/setppcookie", method = RequestMethod.GET)
    public void setPPCookie(HttpServletRequest request, HttpServletResponse response, PPCookieParams ppCookieParams)
            throws Exception {
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(ppCookieParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            returnErrMsg(response, ppCookieParams.getRu(),result.getCode(), result.getMessage());
        }

        result = cookieManager.setPPCookie(response,ppCookieParams);

        String ru = ppCookieParams.getRu();
        if(!result.isSuccess()){
            log(request,"pp_setcookie",ru,result.getCode());
            returnErrMsg(response,ru,result.getCode(),result.getMessage());
        }
        if (!StringUtils.isBlank(ru)) {
            response.sendRedirect(ru);
        }
        log(request,"pp_setcookie",ru,"0");
        return;
    }


}
