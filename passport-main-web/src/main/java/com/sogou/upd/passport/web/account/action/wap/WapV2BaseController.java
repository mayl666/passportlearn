package com.sogou.upd.passport.web.account.action.wap;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.WapConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 14-10-15
 * Time: 上午12:20
 * To change this template use File | Settings | File Templates.
 */
public class WapV2BaseController extends BaseController {

    /**
     * 获取短信验证码校验通过后，需要跳转到一个接口，避免用户刷新导致页面不可用
     */
    protected String buildRedirectUrl(String redirectUri, boolean hasError, String ru, String errorMsg, String clientId,
                                      String skin, String v, int needCaptcha, String mobile, String scode) {
        ru = Coder.encodeUTF8(Strings.isNullOrEmpty(ru) ? CommonConstant.DEFAULT_WAP_URL : ru);
        clientId = Strings.isNullOrEmpty(clientId) ? String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID) : clientId;
        skin = Strings.isNullOrEmpty(skin) ? WapConstant.WAP_SKIN_GREEN : skin;
        v = Strings.isNullOrEmpty(v) ? WapConstant.WAP_COLOR : v;
        errorMsg = Strings.isNullOrEmpty(errorMsg) ? "" : Coder.encodeUTF8(errorMsg);
        boolean isNeedCaptcha = needCaptcha == 0 ? false : true;
        StringBuilder urlStr = new StringBuilder();
        urlStr.append(redirectUri).append("?");
        urlStr.append("client_id=").append(clientId);
        urlStr.append("&errorMsg=").append(errorMsg);
        urlStr.append("&hasError=").append(hasError);
        urlStr.append("&ru=").append(ru);
        urlStr.append("&skin=").append(skin);
        urlStr.append("&needCaptcha=").append(isNeedCaptcha);
        urlStr.append("&v=").append(v);
        urlStr.append("&mobile=").append(mobile);
        urlStr.append("&username=").append(mobile);
        urlStr.append("&scode=").append(scode);
        return urlStr.toString();
    }
    // hasError为false，errorMsg为空
    protected String buildSuccessRedirectUrl(String redirectUri, String ru,String clientId,
                                             String skin, String v, int needCaptcha, String mobile, String scode) {
        return buildRedirectUrl(redirectUri, false, ru, "", clientId, skin, v, needCaptcha, mobile, scode);
    }
    // hasError为true，errorMsg不能为空，needCaptcha为false
    protected String buildErrorRedirectUrl(String redirectUri, String ru, String errorMsg,String clientId,
                                             String skin, String v, String mobile, String scode) {
        return buildRedirectUrl(redirectUri, true, ru, errorMsg, clientId, skin, v, 0, mobile, scode);
    }

    /**
     * 在接口渲染VM模板的页面增加model的attribute
     * @param hasError
     * @param ru
     * @param errorMsg
     * @param clientId
     * @param skin
     * @param v
     * @param needCaptcha
     * @param model
     */
    protected void addReturnPageModel(Model model,boolean hasError, String ru, String errorMsg, String clientId, String skin, String v, boolean needCaptcha, String mobile) {
        model.addAttribute("errorMsg", errorMsg);
        model.addAttribute("hasError", hasError);
        model.addAttribute("ru", Coder.encodeUTF8(Strings.isNullOrEmpty(ru) ? CommonConstant.DEFAULT_WAP_URL : ru));
        model.addAttribute("skin", Strings.isNullOrEmpty(skin) ? WapConstant.WAP_SKIN_GREEN : skin);
        model.addAttribute("needCaptcha", needCaptcha);
        model.addAttribute("v", Strings.isNullOrEmpty(v) ? WapConstant.WAP_COLOR : v);
        model.addAttribute("client_id", clientId);
        model.addAttribute("mobile", mobile);
        model.addAttribute("username", mobile);
    }

    /**
     * 在重定向的中间页面增加model的attribute
     * @param model
     * @param scode
     * @param hasError
     */
    protected void addRedirectPageModule(Model model, boolean hasError, String ru, String errorMsg, String clientId,
                                         String skin, String v, boolean needCaptcha, String mobile, String scode) {
        addReturnPageModel(model, hasError, ru, errorMsg, clientId,skin,v, needCaptcha, mobile);
        model.addAttribute("scode", scode);
    }

    protected void log(HttpServletRequest request, String passportId, String clientIdStr, String resultCode) {
        //用户登录log
        UserOperationLog userOperationLog = new UserOperationLog(passportId, request.getRequestURI(), clientIdStr, resultCode, getIp(request));
        userOperationLog.putOtherMessage("ref", request.getHeader("referer"));
        UserOperationLogUtil.log(userOperationLog);
    }
}
