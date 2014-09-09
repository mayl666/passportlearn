package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.common.validation.constraints.RuValidator;
import com.sogou.upd.passport.manager.account.CookieManager;
import com.sogou.upd.passport.manager.account.LoginManager;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import com.sogou.upd.passport.manager.form.WebLoginParams;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.form.CheckUserNameExistParameters;
import com.sogou.upd.passport.web.inteceptor.HostHolder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;

/**
 * User: mayan
 * Date: 13-6-7 Time: 下午5:48
 * web登录
 */
@Controller
@RequestMapping("/web")
public class LoginAction extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(LoginAction.class);

    @Autowired
    private LoginManager loginManager;
    @Autowired
    private CookieManager cookieManager;
    @Autowired
    private HostHolder hostHolder;

    /**
     * 用户登录检查是否显示验证码
     *
     * @param checkParam
     */
    @RequestMapping(value = "/login/checkNeedCaptcha", method = RequestMethod.GET)
    @ResponseBody
    public String checkNeedCaptcha(HttpServletRequest request, CheckUserNameExistParameters checkParam)
            throws Exception {
        Result result = new APIResultSupport(false);
        try {
            //参数验证
            String validateResult = ControllerHelper.validateParams(checkParam);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            String username = URLDecoder.decode(checkParam.getUsername(), "utf-8");
            int clientId = Integer.valueOf(checkParam.getClient_id());
            //判断账号是否存在,存在返回0，否则返回相应错误码
            result = loginManager.checkUser(username, clientId);
            if (result.isSuccess()) {
                //校验是否需要验证码
                boolean needCaptcha = loginManager.needCaptchaCheck(checkParam.getClient_id(), username, getIp(request));
                result.setDefaultModel("needCaptcha", needCaptcha);
            }
        } catch (Exception e) {
            logger.error("checkNeedCaptcha:check user need captcha or not is failed,username is " + checkParam.getUsername(), e);
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog(checkParam.getUsername(), request.getRequestURI(), checkParam.getClient_id(), result.getCode(), getIp(request));
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            UserOperationLogUtil.log(userOperationLog);
        }
        return result.toString();
    }

    /**
     * web页面登录
     *
     * @param loginParams 传入的参数
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(HttpServletRequest request, HttpServletResponse response, Model model, WebLoginParams loginParams)
            throws Exception {
        Result result = new APIResultSupport(false);
        String ip = getIp(request);
        //参数验证
        String validateResult = ControllerHelper.validateParams(loginParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            result.setDefaultModel("xd", loginParams.getXd());
            model.addAttribute("data", result.toString());
            return "/login/api";
        }
        String userId = loginParams.getUsername();
        result = loginManager.accountLogin(loginParams, ip, request.getScheme());
        //用户登录log
        UserOperationLog userOperationLog = new UserOperationLog(userId, request.getRequestURI(), loginParams.getClient_id(), result.getCode(), getIp(request));
        userOperationLog.putOtherMessage("ref", request.getHeader("referer"));
        userOperationLog.putOtherMessage("yyid", ServletUtil.getCookie(request, "YYID"));
        UserOperationLogUtil.log(userOperationLog);
        if (result.isSuccess()) {
            userId = result.getModels().get("userid").toString();
            String uniqName = StringUtils.EMPTY;
            if (result.getModels().get("uniqname") != null) {
                uniqName = result.getModels().get("uniqname").toString();
            }

            int clientId = Integer.parseInt(loginParams.getClient_id());
            int autoLogin = loginParams.getAutoLogin();
            int sogouMaxAge = autoLogin == 0 ? -1 : (int) DateAndNumTimesConstant.TWO_WEEKS;
            String sogouRu = loginParams.getRu();
            if (Strings.isNullOrEmpty(sogouRu)) {
                sogouRu = CommonConstant.DEFAULT_INDEX_URL;
            }

            //最初版本
//            result = cookieManager.setCookie(response, userId, clientId, ip, sogouRu, sogouMaxAge);
            //新重载的方法、增加昵称参数、以及判断种老cookie还是新cookie  module 替换
//            result = cookieManager.setCookie(response, userId, clientId, ip, sogouRu, sogouMaxAge, uniqName);

            CookieApiParams cookieApiParams = new CookieApiParams();
            cookieApiParams.setUserid(userId);
            cookieApiParams.setClient_id(clientId);
            cookieApiParams.setRu(sogouRu);
            cookieApiParams.setTrust(CookieApiParams.IS_ACTIVE);
            cookieApiParams.setPersistentcookie(String.valueOf(1));
            cookieApiParams.setIp(ip);
            cookieApiParams.setUniqname(uniqName);
            cookieApiParams.setMaxAge(sogouMaxAge);
            cookieApiParams.setCreateAndSet(CommonConstant.CREATE_COOKIE_AND_SET);

//            CookieApiParams cookieApiParams = buildCookieApiParams(userId, clientId, sogouRu, ip, sogouMaxAge);
//            cookieApiParams.setTrust(CookieApiParams.IS_ACTIVE);
//            cookieApiParams.setPersistentcookie(String.valueOf(1));

            result = cookieManager.createCookie(response, cookieApiParams);
            if (result.isSuccess()) {
                result.setDefaultModel(CommonConstant.RESPONSE_RU, sogouRu);
                result.setDefaultModel("userid", userId);
                loginManager.doAfterLoginSuccess(loginParams.getUsername(), ip, userId, clientId);
            }
        } else {
            loginManager.doAfterLoginFailed(loginParams.getUsername(), ip, result.getCode());
            //校验是否需要验证码
            boolean needCaptcha = loginManager.needCaptchaCheck(loginParams.getClient_id(), loginParams.getUsername(), getIp(request));
            if (needCaptcha) {
                result.setDefaultModel("needCaptcha", true);
            }
            if (result.getCode().equals(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_PWD_ERROR);
                result.setMessage("您登陆过于频繁，请稍后再试。");
            }
        }
        result.setDefaultModel("xd", loginParams.getXd());
        model.addAttribute("data", result.toString());
        return "/login/api";
    }

    /**
     * web页面退出
     * 通过js调用，返回结果
     */
    @RequestMapping(value = "/logout_js", method = RequestMethod.GET)
    public void logoutInjs(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "client_id", required = false) String client_id)
            throws Exception {
        ServletUtil.clearCookie(response, LoginConstant.COOKIE_PPINF);
        ServletUtil.clearCookie(response, LoginConstant.COOKIE_PPRDIG);
        ServletUtil.clearCookie(response, LoginConstant.COOKIE_PASSPORT);
        ServletUtil.clearCookie(response, LoginConstant.COOKIE_PPINFO);

        String userId = hostHolder.getPassportId();

        //用于记录log
        UserOperationLog userOperationLog = new UserOperationLog(userId, client_id, "0", getIp(request));
        String referer = request.getHeader("referer");
        userOperationLog.putOtherMessage("ref", referer);
        UserOperationLogUtil.log(userOperationLog);
    }

    /**
     * web页面退出
     * 页面直接跳转，回跳到之前的地址
     */
    @RequestMapping(value = "/logout_redirect", method = RequestMethod.GET)
    public ModelAndView logoutWithRu(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "ru", required = false) String ru, @RequestParam(value = "client_id", required = false) String client_id)
            throws Exception {
        ServletUtil.clearCookie(response, LoginConstant.COOKIE_PPINF);
        ServletUtil.clearCookie(response, LoginConstant.COOKIE_PPRDIG);
        ServletUtil.clearCookie(response, LoginConstant.COOKIE_PASSPORT);
        ServletUtil.clearCookie(response, LoginConstant.COOKIE_PPINFO);

        String userId = hostHolder.getPassportId();

        //用于记录log
        UserOperationLog userOperationLog = new UserOperationLog(userId, client_id, "0", getIp(request));
        String referer = request.getHeader("referer");
        userOperationLog.putOtherMessage("ref", referer);
        userOperationLog.putOtherMessage(CommonConstant.RESPONSE_RU, ru);
        UserOperationLogUtil.log(userOperationLog);
        RuValidator ruValidator = new RuValidator();
        if (StringUtil.isBlank(ru) || !ruValidator.isValid(ru, null)) {
            if (StringUtil.isBlank(referer)) {
                referer = CommonConstant.DEFAULT_CONNECT_REDIRECT_URL;
            }
            ru = referer;
        }
        return new ModelAndView(new RedirectView(ru));
    }
}
