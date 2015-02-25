package com.sogou.upd.passport.web.account.api;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.ServletUtil;
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

    /*
     * 非搜狗域下种跨域cookie接口，目前使用产品：导航daohang.qq.com/hao.qq.com、输入法pinyin.qq.com、teemo.cn
     * 该接口域名为非搜狗域的域名
     */
    @RequestMapping(value = "/sso/setcookie", method = RequestMethod.GET)
    @ResponseBody
    public String setcookie(HttpServletRequest request, HttpServletResponse response, SSOCookieParams ssoCookieParams) throws Exception {
        Result result = new APIResultSupport(false);
        String cb = ssoCookieParams.getCb();
        String ru = ssoCookieParams.getRu();
        try {
            //参数验证
            String validateResult = ControllerHelper.validateParams(ssoCookieParams);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                //参数验证
                if (!isCleanString(cb)) {
                    return buildJsonpResult(cb, result);
                }
                returnErrMsg(response, ssoCookieParams.getRu(), result.getCode(), result.getMessage());
                return "empty";
            }
            result = cookieManager.setSSOCookie(response, ssoCookieParams);
            // 如果cb参数不为空，则返回jsonp函数，不需要重定向
            if (!Strings.isNullOrEmpty(cb)) {
                return buildJsonpResult(cb, result);
            }
            if (!result.isSuccess()) {
                returnErrMsg(response, ru, result.getCode(), result.getMessage());
                return "empty";
            }
            if (!StringUtils.isBlank(ru)) {
                response.sendRedirect(ru);
            }
            return "empty";
        } finally {
            log(request, "sso_setcookie", ru, result.getCode());
        }
    }

    private String buildJsonpResult(String cb, Result result) {
        String status = result.getCode();
        String statusText = result.getMessage();
        StringBuilder sb = new StringBuilder();
        sb.append("status=").append(status).append("|");
        sb.append("statusText=").append(Coder.encodeUTF8(statusText));
        return cb + "('" + sb.toString() + "')";
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
            returnErrMsg(response, ssoClearCookieParams.getRu(), result.getCode(), result.getMessage());
            return;
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

    /*
     * 桌面端产品种搜狗域cookie接口，由于客户端已写死，所以需要一直保持种ppinf、pprdig
     * 该接口域名为https;//account.sogou.com
     */
    @RequestMapping(value = "/act/setppcookie", method = RequestMethod.GET)
    public void setPPCookie(HttpServletRequest request, HttpServletResponse response, PPCookieParams ppCookieParams)
            throws Exception {
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(ppCookieParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            returnErrMsg(response, ppCookieParams.getRu(), result.getCode(), result.getMessage());
            return;
        }
        result = cookieManager.setPPCookie(response, ppCookieParams);

        String ru = ppCookieParams.getRu();
        if (!result.isSuccess()) {
            log(request, "pp_setcookie", ru, result.getCode());
            returnErrMsg(response, ru, result.getCode(), result.getMessage());
            return;
        }
        if (!StringUtils.isBlank(ru)) {
            response.sendRedirect(ru);
        }
        log(request, "pp_setcookie", ru, "0");
        return;
    }

    private void log(HttpServletRequest request, String passportId, String ru, String resultCode) {
        //用户登录log
        UserOperationLog userOperationLog = new UserOperationLog(passportId, request.getRequestURI(), "", resultCode, getIp(request));
        UserOperationLogUtil.log(userOperationLog);
    }

}
