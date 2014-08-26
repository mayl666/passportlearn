package com.sogou.upd.passport.web.account.api;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.OAuthResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.manager.ManagerHelper;
import com.sogou.upd.passport.manager.account.*;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.form.PCOAuth2LoginParams;
import com.sogou.upd.passport.manager.form.PCOAuth2RegisterParams;
import com.sogou.upd.passport.manager.form.PCOAuth2ResourceParams;
import com.sogou.upd.passport.manager.form.WebRegisterParams;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.oauth2.authzserver.request.OAuthTokenASRequest;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.form.PCOAuth2BaseParams;
import com.sogou.upd.passport.web.account.form.PCOAuth2IndexParams;
import com.sogou.upd.passport.web.account.form.RegUserNameParams;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.UUID;


/**
 * sohu+浏览器相关接口替换
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-9-9
 * Time: 下午7:37
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class PCOAuth2AccountController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(PCOAuth2AccountController.class);
    @Autowired
    private OAuth2AuthorizeManager oAuth2AuthorizeManager;
    @Autowired
    private OAuth2ResourceManager oAuth2ResourceManager;
    @Autowired
    private ConfigureManager configureManager;
    @Autowired
    private LoginManager loginManager;
    @Autowired
    private PCAccountManager pcAccountManager;
    @Autowired
    private RegManager regManager;
    @Autowired
    private CommonManager commonManager;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private PCOAuth2LoginManager pcOAuth2LoginManager;
    @Autowired
    private CookieManager cookieManager;
    @Autowired
    private AccountInfoManager accountInfoManager;


    @RequestMapping(value = "/sogou/fastreg", method = RequestMethod.GET)
    public String fastreg(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "instanceid", defaultValue = "") String instanceid, Model model) throws Exception {
        model.addAttribute("instanceid", instanceid);
        model.addAttribute("client_id", CommonConstant.PC_CLIENTID);
        return "/oauth2pc/fastreg";
    }

    @RequestMapping(value = "/sogou/mobilereg", method = RequestMethod.GET)
    public String mobilereg(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "instanceid", defaultValue = "") String instanceid, Model model) throws Exception {
        model.addAttribute("instanceid", instanceid);
        model.addAttribute("client_id", CommonConstant.PC_CLIENTID);
        return "/oauth2pc/mobilereg";
    }

    /**
     * sohu+登录注册主窗口
     *
     * @param request
     * @param response
     * @param pcOAuth2BaseParams
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sogou/flogon", method = RequestMethod.GET)
    public String pcLogin(HttpServletRequest request, HttpServletResponse response, PCOAuth2BaseParams pcOAuth2BaseParams, Model model) throws Exception {
        webCookieProcess(request, response);
        model.addAttribute("instanceid", pcOAuth2BaseParams.getInstanceid());
        model.addAttribute("client_id", pcOAuth2BaseParams.getClient_id());
        return "/oauth2pc/pclogin";
    }

    @RequestMapping(value = "/oauth2/token/")
    @ResponseBody
    public Object authorize(HttpServletRequest request) throws Exception {
        OAuthTokenASRequest oauthRequest;
        Result result = new OAuthResultSupport(false);
        try {
            oauthRequest = new OAuthTokenASRequest(request);
        } catch (OAuthProblemException e) {
            result.setCode(e.getError());
            result.setMessage(e.getDescription());
            return result.toString();
        }

        result = oAuth2AuthorizeManager.oauth2Authorize(oauthRequest);
        if (StringUtils.isBlank(result.getCode()) && result.isSuccess()) {
            result.setCode("0");
        }
        UserOperationLog userOperationLog = new UserOperationLog(oauthRequest.getUsername(), "/oauth2/token/?grant_type=" + oauthRequest.getGrantType(), String.valueOf(oauthRequest.getClientId()), result.getCode(), getIp(request));
        userOperationLog.putOtherMessage("refresh_token", oauthRequest.getRefreshToken());
        userOperationLog.putOtherMessage("instance_id", oauthRequest.getInstanceId());
        UserOperationLogUtil.log(userOperationLog);
        return result.toString();
    }

    @RequestMapping(value = "/oauth2/resource/")
    @ResponseBody
    public Object resource(HttpServletRequest request, PCOAuth2ResourceParams params) throws Exception {
        Result result = new OAuthResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }

        result = oAuth2ResourceManager.resource(params);

        if (StringUtils.isBlank(result.getCode()) && result.isSuccess()) {
            result.setCode("0");
        }
        UserOperationLog userOperationLog = new UserOperationLog(params.getUsername(), "/oauth2/resource/?resource_type=" + params.getResource_type(), String.valueOf(params.getClient_id()), result.getCode(), getIp(request));
        userOperationLog.putOtherMessage("access_token", params.getAccess_token());
        userOperationLog.putOtherMessage("instance_id", params.getInstance_id());
        UserOperationLogUtil.log(userOperationLog);
        return result.toString();
    }

    /**
     * 浏览器桌面端：用户注册检查用户名是否可用
     *
     * @param checkParam
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/oauth2/checkregname")
    @ResponseBody
    public String checkRegisterName(HttpServletRequest request, RegUserNameParams checkParam)
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
            result = checkPCAccountNotExists(username);
            if (PhoneUtil.verifyPhoneNumberFormat(username) && ErrorUtil.ERR_CODE_ACCOUNT_PHONE_BINDED.equals(result.getCode())) {
                result.setMessage("该手机号已注册或已绑定，请直接登录");
            }
        } catch (Exception e) {
            logger.error("oauth2:checkRegisterName is failed,username is " + checkParam.getUsername(), e);
        } finally {
            UserOperationLog userOperationLog = new UserOperationLog(checkParam.getUsername(), request.getRequestURI(), checkParam.getClient_id(), result.getCode(), getIp(request));
            UserOperationLogUtil.log(userOperationLog);
        }
        return result.toString();
    }

    /**
     * 浏览器桌面端sohu+注册接口
     *
     * @param request
     * @param pcoAuth2RegisterParams
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/oauth2/register", method = RequestMethod.POST)
    @ResponseBody
    public Object register(HttpServletRequest request, PCOAuth2RegisterParams pcoAuth2RegisterParams) throws Exception {
        Result result = new APIResultSupport(false);
        String ip = null;
        String uuidName = null;
        String finalCode = null;
        try {
            //参数验证
            String validateResult = ControllerHelper.validateParams(pcoAuth2RegisterParams);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            ip = getIp(request);
            uuidName = ServletUtil.getCookie(request, "uuidName");

            result = regManager.checkRegInBlackList(ip, uuidName);
            if (!result.isSuccess()) {
                if (result.getCode().equals(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST)) {
                    finalCode = ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST;
                    result.setCode(ErrorUtil.ERR_CODE_REGISTER_UNUSUAL);
                    return result.toString();
                }
            }
            //验证client_id
            int clientId = Integer.parseInt(pcoAuth2RegisterParams.getClient_id());
            if (!configureManager.checkAppIsExist(clientId)) {
                result.setCode(ErrorUtil.INVALID_CLIENTID);
                return result.toString();
            }
            result = checkPCAccountNotExists(pcoAuth2RegisterParams.getUsername());
            if (!result.isSuccess()) {
                return result.toString();
            }
            result = regManager.webRegister(transferToWebParams(pcoAuth2RegisterParams), ip);
            //注册成功后获取token
            if (result.isSuccess()) {
                String userId = result.getModels().get("userid").toString();
                String instanceId = pcoAuth2RegisterParams.getInstance_id();
                result = pcAccountManager.createAccountToken(userId, instanceId, clientId);
                if (result.isSuccess()) {
                    AccountToken accountToken = (AccountToken) result.getDefaultModel();
                    result = new APIResultSupport(true);
                    result.setCode("0");
                    String passportId = accountToken.getPassportId();
                    ManagerHelper.setModelForOAuthResult(result, defaultUniqname(passportId), accountToken, "sogou");
                }
            }
        } catch (Exception e) {
            logger.error("Sohu+ Register Failed,UserName Is " + pcoAuth2RegisterParams.getUsername(), e);
        } finally {
            writeUserLogForRegister(finalCode, result, request, pcoAuth2RegisterParams);
        }
        commonManager.incRegTimes(ip, uuidName);

        //注册添加log
        if (StringUtils.isBlank(result.getCode()) && result.isSuccess()) {
            result.setCode("0");
        }
        UserOperationLog userOperationLog = new UserOperationLog(pcoAuth2RegisterParams.getUsername(), request.getRequestURI(), pcoAuth2RegisterParams.getClient_id(), result.getCode(), ip);
        UserOperationLogUtil.log(userOperationLog);
        return result.toString();
    }

    private void writeUserLogForRegister(String finalCode, Result result, HttpServletRequest request, PCOAuth2RegisterParams pcoAuth2RegisterParams) {
        String logCode;
        if (!Strings.isNullOrEmpty(finalCode)) {
            logCode = finalCode;
        } else {
            logCode = result.getCode();
        }
        UserOperationLog userOperationLog = new UserOperationLog(pcoAuth2RegisterParams.getUsername(), request.getRequestURI(), pcoAuth2RegisterParams.getClient_id(), logCode, getIp(request));
        String referer = request.getHeader("referer");
        userOperationLog.putOtherMessage("ref", referer);
        UserOperationLogUtil.log(userOperationLog);
    }

    private WebRegisterParams transferToWebParams(PCOAuth2RegisterParams pcParams) {
        WebRegisterParams webParams = new WebRegisterParams();
        webParams.setCaptcha(pcParams.getCaptcha());
        webParams.setClient_id(pcParams.getClient_id());
        webParams.setPassword(pcParams.getPassword());
        webParams.setRu(pcParams.getRu());
        webParams.setToken(pcParams.getToken());
        webParams.setUsername(pcParams.getUsername());
        return webParams;
    }

    /**
     * 浏览器登陆流程
     */
    @RequestMapping(value = "/oauth2/login", method = RequestMethod.POST)
    @ResponseBody
    public Object login(HttpServletRequest request, PCOAuth2LoginParams loginParams)
            throws Exception {
        Result result = new APIResultSupport(false);
        String ip = getIp(request);
        int clientId = pcOAuth2LoginManager.getClientId(loginParams.getClient_id());
        //参数验证
        String validateResult = ControllerHelper.validateParams(loginParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        String username = loginParams.getUsername();
        result = pcOAuth2LoginManager.accountLogin(loginParams, getIp(request), request.getScheme());

        //用户登录log
        if (result.isSuccess()) {
            String userId = result.getModels().get("userid").toString();
            //构造成功返回结果
            result = new APIResultSupport(true);
            Result tokenResult = pcAccountManager.createAccountToken(userId, loginParams.getInstanceid(), clientId);
            result.setDefaultModel("autologin", loginParams.getRememberMe());
            AccountToken accountToken = (AccountToken) tokenResult.getDefaultModel();
            ManagerHelper.setModelForOAuthResult(result, accountInfoManager.getUniqName(userId, clientId, false), accountToken, "sogou");
            loginManager.doAfterLoginSuccess(username, ip, userId, clientId);
        } else {
            loginManager.doAfterLoginFailed(username, ip, result.getCode());
            //校验是否需要验证码
            boolean needCaptcha = loginManager.needCaptchaCheck(String.valueOf(clientId), username, ip);
            if (needCaptcha) {
                result.setDefaultModel("needCaptcha", true);
            }
            if (result.getCode().equals(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_PWD_ERROR);
                result.setMessage("您登陆过于频繁，请稍后再试。");
            }
        }

        if (StringUtils.isBlank(result.getCode()) && result.isSuccess()) {
            result.setCode("0");
        }
        UserOperationLog userOperationLog = new UserOperationLog(username, request.getRequestURI(), String.valueOf(clientId), result.getCode(), ip);
        UserOperationLogUtil.log(userOperationLog);
        return result.toString();
    }

    /**
     * 检查用户是否存在
     *
     * @param username
     * @return
     * @throws Exception
     */
    private Result checkPCAccountNotExists(String username) throws Exception {
        Result result = new APIResultSupport(false);
        //不允许邮箱注册
        if (username.indexOf("@") != -1) {
            result.setCode(ErrorUtil.ERR_CODE_REGISTER_EMAIL_NOT_ALLOWED);
            return result;
        }
        //检查用户是否存在
        result = regManager.isAccountNotExists(username, CommonConstant.PC_CLIENTID);
        return result;
    }

    //个人中心页面
    @RequestMapping(value = "/sogou/profile/basic/edit", method = RequestMethod.GET)
    public void pcindex(HttpServletRequest request, HttpServletResponse response, PCOAuth2IndexParams oauth2PcIndexParams, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(oauth2PcIndexParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            response.sendRedirect("/tokenerror");
            return;
        }

        //当前页面cookie
        String cookieUserId = "";
        if (hostHolder.isLogin()) {
            cookieUserId = hostHolder.getPassportId();
        }
        int clientId = pcOAuth2LoginManager.getClientId(oauth2PcIndexParams.getClient_id());
        Result queryPassportIdResult = oAuth2ResourceManager.queryPassportIdByAccessToken(oauth2PcIndexParams.getAccesstoken(), clientId, oauth2PcIndexParams.getInstanceid(), "");

        if (StringUtils.isBlank(queryPassportIdResult.getCode()) && queryPassportIdResult.isSuccess()) {
            queryPassportIdResult.setCode("0");
        }

        UserOperationLog userOperationLog = new UserOperationLog("", request.getRequestURI(), String.valueOf(oauth2PcIndexParams.getClient_id()), queryPassportIdResult.getCode(), getIp(request));
        UserOperationLogUtil.log(userOperationLog);

        if (!queryPassportIdResult.isSuccess()) {
            //token 验证出错，跳出到登录页
            response.sendRedirect("/tokenerror");
            return;
        }
        String passportId = (String) queryPassportIdResult.getDefaultModel();
        String redirectUrl;
        if (oauth2PcIndexParams.getType().equals(CommonConstant.PC_REDIRECT_AVATARURL)) {
            redirectUrl = "/web/userinfo/avatarurl?client_id=" + oauth2PcIndexParams.getClient_id();
        } else if (oauth2PcIndexParams.getType().equals(CommonConstant.PC_REDIRECT_PASSWORD)) {
            redirectUrl = "/web/security/password?client_id=" + oauth2PcIndexParams.getClient_id();
        } else {
            redirectUrl = "/web/userinfo/getuserinfo?client_id=" + oauth2PcIndexParams.getClient_id();
        }

        //判断cookie中的passportId与token解密出来的passportId是否相等
        if (!Strings.isNullOrEmpty(cookieUserId)) {
            if (!cookieUserId.equals(passportId)) {
                response.sendRedirect("/web/logout_redirect");
                return;
            }
            response.sendRedirect(redirectUrl);
            return;
        }

        //TODO module 替换 种ver=5 cookie
        cookieManager.setCookie(response, passportId, oauth2PcIndexParams.getClient_id(), getIp(request), CommonConstant.DEFAULT_INDEX_URL, -1);
        response.sendRedirect(redirectUrl);
        return;
    }

    @RequestMapping(value = "/oauth2/errorMsg")
    @ResponseBody
    public Object errorMsg(@RequestParam("msg") String msg) throws Exception {
        return msg;
    }

    @RequestMapping(value = "/tokenerror")
    public String tokenerror() throws Exception {
        return "tokenerror";
    }

    /**
     * 注册种cookie防止恶意注册，黑白名单
     *
     * @param request
     * @param response
     */
    private void webCookieProcess(HttpServletRequest request, HttpServletResponse response) {
        String uuidName = ServletUtil.getCookie(request, "uuidName");
        if (Strings.isNullOrEmpty(uuidName)) {
            uuidName = UUID.randomUUID().toString().replaceAll("-", "");
            ServletUtil.setCookie(response, "uuidName", uuidName, (int) DateAndNumTimesConstant.TIME_ONEDAY);
        }
    }

    private String defaultUniqname(String passportId) {
        return passportId.substring(0, passportId.indexOf("@"));
    }

}