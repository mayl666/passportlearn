package com.sogou.upd.passport.web.account.action.wap;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.WapConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.manager.account.LoginManager;
import com.sogou.upd.passport.manager.account.WapLoginManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.form.WapLoginParams;
import com.sogou.upd.passport.manager.form.WapLogoutParams;
import com.sogou.upd.passport.oauth2.common.parameters.QueryParameterApplier;
import com.sogou.upd.passport.oauth2.common.types.ConnectTypeEnum;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.form.WapIndexParams;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-11-12
 * Time: 下午12:17
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class WapLoginAction extends BaseController {

    @Autowired
    private LoginManager loginManager;
    @Autowired
    private WapLoginManager wapLoginManager;

    private static final Logger logger = LoggerFactory.getLogger(WapLoginAction.class);

    @RequestMapping(value = {"/wap/index"})
    public String index(HttpServletRequest request, HttpServletResponse response, Model model, WapIndexParams wapIndexParams)
            throws Exception {
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(wapIndexParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            response.sendRedirect(getIndexErrorReturnStr(wapIndexParams.getRu(), result.getMessage()));
            return "empty";
        }

        model.addAttribute("v", wapIndexParams.getV());
        model.addAttribute("ru", wapIndexParams.getRu());
        model.addAttribute("client_id", wapIndexParams.getClient_id());
        model.addAttribute("errorMsg", wapIndexParams.getErrorMsg());
        model.addAttribute("isNeedCaptcha", wapIndexParams.getNeedCaptcha());
        //生成token
        String token = RandomStringUtils.randomAlphanumeric(48);
        model.addAttribute("token", token);

        if (WapConstant.WAP_SIMPLE.equals(wapIndexParams.getV())) {
            response.setHeader("Content-Type","text/vnd.wap.wml;charset=utf-8");
            return "wap/index_simple";
        } else if (WapConstant.WAP_TOUCH.equals(wapIndexParams.getV())) {
            return "wap/index_touch";
        } else {
            return "wap/index_color";
        }
    }

    private String getIndexErrorReturnStr(String ru, String errorMsg) {
        if (!Strings.isNullOrEmpty(ru)) {
            return (ru + "?errorMsg=" + errorMsg);
        }
        return WapConstant.WAP_INDEX + "?errorMsg=" + errorMsg;
    }


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
            return getErrorReturnStr(loginParams,validateResult, 0);
        }

        result = wapLoginManager.accountLogin(loginParams, ip);
        //用户登录log
        UserOperationLog userOperationLog = new UserOperationLog(loginParams.getUsername(), request.getRequestURI(), loginParams.getClient_id(), result.getCode(), getIp(request));
        String referer = request.getHeader("referer");
        userOperationLog.putOtherMessage("ref", referer);
        UserOperationLogUtil.log(userOperationLog);

        if (result.isSuccess()) {
            String userId = result.getModels().get("userid").toString();
            String token = result.getModels().get("token").toString();
            wapLoginManager.doAfterLoginSuccess(loginParams.getUsername(), ip, userId, Integer.parseInt(loginParams.getClient_id()));
            response.sendRedirect(getSuccessReturnStr(loginParams.getRu(),token));
            return "empty";
        } else {
            int isNeedCaptcha = 0;
            loginManager.doAfterLoginFailed(loginParams.getUsername(), ip);
            //校验是否需要验证码
            boolean needCaptcha = wapLoginManager.needCaptchaCheck(loginParams.getClient_id(), loginParams.getUsername(), getIp(request));
            if (needCaptcha) {
                isNeedCaptcha = 1;
            }
            if (result.getCode().equals(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_PWD_ERROR);
                result.setMessage("密码错误");
            }
            return getErrorReturnStr(loginParams,"用户名或者密码错误", isNeedCaptcha);

        }
    }

    /**
     * wap页面退出
     * 页面直接跳转，回跳到之前的地址
     */
    @RequestMapping(value = "/logout_redirect", method = RequestMethod.GET)
    public String logoutWithRu(HttpServletRequest request,
                                     HttpServletResponse response,
                                     WapLogoutParams params) {
        // 校验参数
        String viewUrl= null;
        String sgid = null;
        String client_id= null;
        String ru= null;
        try {
            ru=params.getRu();
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                viewUrl = buildErrorRu(ru, ErrorUtil.ERR_CODE_COM_REQURIE, validateResult);
                response.sendRedirect(viewUrl);
                return "";
            }
            sgid=params.getSgid();
            client_id=params.getClient_id();

            //处理ru
            if (Strings.isNullOrEmpty(ru)) {
                ru= CommonConstant.DEFAULT_WAP_URL;
            } else {
                ru = URLEncoder.encode(ru, CommonConstant.DEFAULT_CONTENT_CHARSET);
            }
            //session server中清除cookie
            Result result=wapLoginManager.removeSession(sgid);
            if(result.isSuccess()){
                //清除cookie
                ServletUtil.clearCookie(response, LoginConstant.COOKIE_SGID);
                response.sendRedirect(ru);
                return "";
            }
        }catch (Exception e){
            if (logger.isDebugEnabled()) {
                logger.debug("logout_redirect " + "sgid:" + sgid +",client_id:"+client_id);
            }
        } finally {
            //用于记录log
            UserOperationLog userOperationLog = new UserOperationLog(sgid, client_id, "0", getIp(request));
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            userOperationLog.putOtherMessage(CommonConstant.RESPONSE_RU, ru);
            UserOperationLogUtil.log(userOperationLog);
        }
        ru = buildErrorRu(ru,ErrorUtil.ERR_CODE_REMOVE_COOKIE_FAILED,"error");
        try {
            response.sendRedirect(ru);
        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("logout_redirect " + "sgid:" + sgid +",client_id:"+client_id);
            }
        }
        return "";
    }

    /**
     * 第三方登录接口错误返回结果的跳转url
     * @param ru        回调url
     * @param errorCode 错误码
     * @param errorText 错误文案
     * @return
     */
    protected String buildErrorRu(String ru, String errorCode, String errorText) {
        try{
            if (Strings.isNullOrEmpty(ru)) {
                ru = CommonConstant.DEFAULT_WAP_CONNECT_REDIRECT_URL;
            }
            if (!Strings.isNullOrEmpty(errorCode) && !Strings.isNullOrEmpty(errorText)) {
                Map params = Maps.newHashMap();
                params.put(CommonConstant.RESPONSE_STATUS, errorCode);
                if (Strings.isNullOrEmpty(errorText)) {
                    errorText = ErrorUtil.ERR_CODE_MSG_MAP.get(errorCode);
                }
                params.put(CommonConstant.RESPONSE_STATUS_TEXT, errorText);
                ru = QueryParameterApplier.applyOAuthParametersString(ru, params);
            }
        }catch (Exception e){
            logger.error("buildErrorRu! ru:" + ru);
        }
        return ru;
    }

    private String getSuccessReturnStr(String ru, String token) {
        String deRu = Coder.decodeUTF8(ru);
        if (deRu.contains("?")) {
            return deRu + "&token=" + token;
        }
        return deRu + "?token=" + token;
    }

    private String getErrorReturnStr(WapLoginParams loginParams,String errorMsg, int isNeedCaptcha) {
        StringBuilder returnStr = new StringBuilder();
        returnStr.append("redirect:/wap/index?");
        if (!Strings.isNullOrEmpty(loginParams.getV())) {
            returnStr.append("v=" + loginParams.getV());
        }
        if (!Strings.isNullOrEmpty(loginParams.getRu())) {
            returnStr.append("&ru=" + loginParams.getRu());
        }
        if (!Strings.isNullOrEmpty(loginParams.getClient_id())) {
            returnStr.append("&client_id=" + loginParams.getClient_id());
        }
        if (!Strings.isNullOrEmpty(errorMsg)) {
            returnStr.append("&errorMsg=" + Coder.encodeUTF8(errorMsg));
        }
        returnStr.append("&needCaptcha=" + isNeedCaptcha);
        return returnStr.toString();
    }
}
