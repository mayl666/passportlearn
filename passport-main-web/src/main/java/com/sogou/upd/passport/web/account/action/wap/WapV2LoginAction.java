package com.sogou.upd.passport.web.account.action.wap;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.WapConstant;
import com.sogou.upd.passport.common.math.Coder;
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
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 14-10-13
 * Time: 下午12:26
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class WapV2LoginAction extends BaseController {

    @Autowired
    private LoginManager loginManager;
    @Autowired
    private WapLoginManager wapLoginManager;

    /**
     * wap2.0页面登录，V=2(v值暂时没用到)
     * @param request
     * @param response
     * @param loginParams
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/wap2/login", method = RequestMethod.POST)
    public String login(HttpServletRequest request, HttpServletResponse response, WapLoginParams loginParams, Model model)
            throws Exception {
        Result result = new APIResultSupport(false);
        String ip = getIp(request);
        loginParams.setRu(Coder.decodeUTF8(loginParams.getRu()));
        //参数验证
        String validateResult = ControllerHelper.validateParams(loginParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return getErrorReturnStr(loginParams, validateResult, 0);
        }
        result = wapLoginManager.accountLogin(loginParams, ip);  //wap端ip做安全限制
        //用户登录log
        UserOperationLog userOperationLog = new UserOperationLog(loginParams.getUsername(), request.getRequestURI(), loginParams.getClient_id(), result.getCode(), ip);
        String referer = request.getHeader("referer");
        userOperationLog.putOtherMessage("ref", referer);
        UserOperationLogUtil.log(userOperationLog);
        //如果校验用户名和密码成功，则生成登录态sgid
        if (result.isSuccess()) {
            String userId = (String) result.getModels().get("userid");
            String sgid = (String) result.getModels().get(LoginConstant.COOKIE_SGID);
            WapRegAction.setSgidCookie(response, sgid);
            loginManager.doAfterLoginSuccess(loginParams.getUsername(), ip, userId, Integer.parseInt(loginParams.getClient_id()));
            response.sendRedirect(getSuccessReturnStr(loginParams.getRu(), sgid));
            return "empty";
        } else {
            //如果校验用户名和密码失败，且是因为需要验证码，则置验证码为1，即需要验证码
            int isNeedCaptcha = 0;
            loginManager.doAfterLoginFailed(loginParams.getUsername(), ip, result.getCode());
            //校验是否需要验证码
            if (result.getCode() == ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_NEED_CODE) {
                String token = RandomStringUtils.randomAlphanumeric(48);
                buildModuleReturnStr(true, loginParams.getRu(), ErrorUtil.getERR_CODE_MSG(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_NEED_CODE),
                        loginParams.getClient_id(), null, loginParams.getV(), true, model);
                model.addAttribute("token", token);
                model.addAttribute("isNeedCaptcha", 1);
                model.addAttribute("username", loginParams.getUsername());
                model.addAttribute("captchaUrl", CommonConstant.DEFAULT_WAP_INDEX_URL + "/captcha?token=" + token);
                return "wap/login_wap";
            }
            //否则，还需要校验是否需要弹出验证码
            boolean needCaptcha = wapLoginManager.needCaptchaCheck(loginParams.getClient_id(), loginParams.getUsername(), getIp(request));
            if (needCaptcha) {
                buildModuleReturnStr(true, loginParams.getRu(), ErrorUtil.getERR_CODE_MSG(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_NEED_CODE),
                        loginParams.getClient_id(), null, loginParams.getV(), true, model);
                String token = RandomStringUtils.randomAlphanumeric(48);
                model.addAttribute("token", token);
                model.addAttribute("isNeedCaptcha", 1);
                model.addAttribute("username", loginParams.getUsername());
                model.addAttribute("captchaUrl", CommonConstant.DEFAULT_WAP_INDEX_URL + "/captcha?token=" + token);
                return "wap/login_wap";
            }
            String defaultMessage = "用户名或者密码错误";
            //不直接返回直接的文案告诉用户中了安全限制
            if (result.getCode().equals(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_PWD_ERROR);
                result.setMessage("您登陆过于频繁，请稍后再试。");
                defaultMessage = "您登陆过于频繁，请稍后再试。";
            }
            return getErrorReturnStr(loginParams, defaultMessage, isNeedCaptcha);
        }
    }

    private String getSuccessReturnStr(String ru, String token) {
        String deRu = Coder.decodeUTF8(ru);
        if (deRu.contains("?")) {
            return deRu + "&sgid=" + token;
        }
        return deRu + "?sgid=" + token;
    }

    private String getErrorReturnStr(WapLoginParams loginParams, String errorMsg, int isNeedCaptcha) {
        StringBuilder returnStr = new StringBuilder();
        returnStr.append("redirect:/wap/index?");
        if (!Strings.isNullOrEmpty(loginParams.getV())) {
            returnStr.append("v=" + loginParams.getV());
        }
        if (!Strings.isNullOrEmpty(loginParams.getRu())) {
            if (WapConstant.WAP_COLOR.equals(loginParams.getV())) {
                returnStr.append("&ru=" + Coder.encodeUTF8(loginParams.getRu()));
            } else {
                returnStr.append("&ru=" + loginParams.getRu());
            }
        }
        if (!Strings.isNullOrEmpty(loginParams.getClient_id())) {
            returnStr.append("&client_id=" + loginParams.getClient_id());
        }
        if (!Strings.isNullOrEmpty(errorMsg)) {
            returnStr.append("&errorMsg=" + Coder.encodeUTF8(errorMsg));
        }
        returnStr.append("&needCaptcha=" + isNeedCaptcha);
        if (WapConstant.WAP_COLOR.equals(loginParams.getV())) {
            returnStr.append("&username=" + loginParams.getUsername());
        }
        return returnStr.toString();
    }

    private void buildModuleReturnStr(boolean hasError, String ru, String errorMsg, String client_id, String skin, String v, boolean needCaptcha, Model model) {
        model.addAttribute("errorMsg", errorMsg);
        model.addAttribute("hasError", hasError);
        model.addAttribute("ru", Strings.isNullOrEmpty(ru) ? Coder.encodeUTF8(CommonConstant.DEFAULT_WAP_INDEX_URL) : Coder.encodeUTF8(ru));
        model.addAttribute("skin", Strings.isNullOrEmpty(skin) ? WapConstant.WAP_SKIN_GREEN : skin);
        model.addAttribute("needCaptcha", needCaptcha);
        model.addAttribute("v", Strings.isNullOrEmpty(v) ? WapConstant.WAP_COLOR : v);
        model.addAttribute("client_id", client_id);
    }
}
