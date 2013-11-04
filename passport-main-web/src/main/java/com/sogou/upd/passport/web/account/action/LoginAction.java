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
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieUrlApiParams;
import com.sogou.upd.passport.manager.form.WebLoginParams;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.manager.account.LoginManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.account.form.CheckUserNameExistParameters;
import com.sogou.upd.passport.web.annotation.InterfaceLimited;
import com.sogou.upd.passport.web.inteceptor.HostHolder;
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
import java.net.URLEncoder;

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
    private HostHolder hostHolder;
    @Autowired
    private LoginApiManager proxyLoginApiManager;
    private static final String LOGIN_INDEX_URLSTR = "https://account.sogou.com";

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
        //参数验证
        String validateResult = ControllerHelper.validateParams(checkParam);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }

        String username = URLDecoder.decode(checkParam.getUsername(), "utf-8");
        //校验是否需要验证码
        boolean needCaptcha = loginManager.needCaptchaCheck(checkParam.getClient_id(), username, getIp(request));

        result.setSuccess(true);
        result.setDefaultModel("needCaptcha", needCaptcha);
        return result.toString();
    }

    /**
     * web页面登录
     *
     * @param loginParams 传入的参数
     */
//    @InterfaceLimited
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

        result = loginManager.accountLogin(loginParams, ip, request.getScheme());

        String userId = loginParams.getUsername();
        //用户登录log
        UserOperationLog userOperationLog = new UserOperationLog(userId, request.getRequestURI(), loginParams.getClient_id(), result.getCode(), getIp(request));
        String referer = request.getHeader("referer");
        userOperationLog.putOtherMessage("ref", referer);
        UserOperationLogUtil.log(userOperationLog);

        if (result.isSuccess()) {

            userId = result.getModels().get("userid").toString();

//            CreateCookieUrlApiParams createCookieUrlApiParams = new CreateCookieUrlApiParams();
//            createCookieUrlApiParams.setUserid(userId);
//
//            //设置来源
            String ru = loginParams.getRu();
            if (Strings.isNullOrEmpty(ru)) {
                ru = LOGIN_INDEX_URLSTR;
            }
//            createCookieUrlApiParams.setRu(ru);
//            createCookieUrlApiParams.setDomain("sogou.com");
            CookieApiParams cookieApiParams = new CookieApiParams();
            cookieApiParams.setUserid(userId);
            cookieApiParams.setClient_id(Integer.parseInt(loginParams.getClient_id()));
            cookieApiParams.setRu(ru);
            cookieApiParams.setTrust(CookieApiParams.IS_ACTIVE);
            int authLogin = loginParams.getAutoLogin();
            //0-否  1-真
            int validTime = authLogin == 0 ? 0 : 1;
            cookieApiParams.setPersistentcookie(String.valueOf(validTime));

            //TODO sogou域账号迁移后cookie生成问题
            Result getCookieValueResult = proxyLoginApiManager.getSHCookieValue(cookieApiParams);
            if (getCookieValueResult.isSuccess()) {
                String ppinf = (String) getCookieValueResult.getModels().get("ppinf");
                String pprdig = (String) getCookieValueResult.getModels().get("pprdig");

                //0-否  1-真
                validTime=authLogin==0?-1:(int)DateAndNumTimesConstant.TWO_WEEKS;

                ServletUtil.setCookie(response, "ppinf", ppinf, validTime, CommonConstant.SOGOU_ROOT_DOMAIN);
                ServletUtil.setCookie(response, "pprdig", pprdig, validTime, CommonConstant.SOGOU_ROOT_DOMAIN);

                userId = result.getModels().get("userid").toString();
                int clientId = Integer.parseInt(loginParams.getClient_id());
                loginManager.doAfterLoginSuccess(loginParams.getUsername(), ip, userId, clientId);
            }


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

        result.setDefaultModel("xd", loginParams.getXd());
        model.addAttribute("data", result.toString());
        return "/login/api";
    }

    /**
     * web页面退出
     * 通过js调用，返回结果
     */
    @RequestMapping(value = "/logout_js", method = RequestMethod.GET)
    public ModelAndView logoutInjs(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "client_id", required = false) String client_id)
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

        return new ModelAndView(new RedirectView(SHPPUrlConstant.CLEAN_COOKIE));
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

        if (StringUtil.isBlank(ru)) {
            if (StringUtil.isBlank(referer)) {
                referer = CommonConstant.DEFAULT_CONNECT_REDIRECT_URL;
            }
            ru = URLEncoder.encode(referer, CommonConstant.DEFAULT_CONTENT_CHARSET);
        }
        StringBuilder url = new StringBuilder(SHPPUrlConstant.CLEAN_COOKIE_REDIRECT);
        url.append(ru);

        return new ModelAndView(new RedirectView(url.toString()));
    }
}
