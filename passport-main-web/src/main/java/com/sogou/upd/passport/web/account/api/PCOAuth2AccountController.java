package com.sogou.upd.passport.web.account.api;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.OAuthResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.manager.account.*;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieUrlApiParams;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.form.*;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.oauth2.authzserver.request.OAuthTokenASRequest;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.form.CheckUserNameExistParameters;
import com.sogou.upd.passport.web.account.form.PCOAuth2BaseParams;
import com.sogou.upd.passport.web.account.form.PCOAuth2IndexParams;
import com.sogou.upd.passport.web.account.form.PCOAuth2LoginParams;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Map;
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
    private UserInfoApiManager proxyUserInfoApiManager;
    @Autowired
    private LoginApiManager proxyLoginApiManager;
    @Autowired
    private OAuth2AuthorizeManager oAuth2AuthorizeManager;
    @Autowired
    private OAuth2ResourceManager oAuth2ResourceManager;
    @Autowired
    private ConfigureManager configureManager;
    @Autowired
    private PCOAuth2RegManager pcoAuth2RegManager;
    @Autowired
    private LoginManager loginManager;
    @Autowired
    private PCAccountManager pcAccountManager;
    @Autowired
    private RegManager regManager;
    @Autowired
    private CommonManager commonManager;

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

        return result.toString();
    }

    @RequestMapping(value = "/oauth2/resource/")
    @ResponseBody
    public Object resource(PCOAuth2ResourceParams params) throws Exception {
        Result result = new OAuthResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(params);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }

        result = oAuth2ResourceManager.resource(params);
        return result.toString();
    }

    /**
     * 浏览器桌面端：用户注册检查用户名是否可用
     */
    @RequestMapping(value = "/oauth2/checkregname", method = RequestMethod.POST)
    @ResponseBody
    public String checkRegisterName(CheckUserNameExistParameters checkParam)
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
        result = checkPCAccountNotExists(username);
        if (PhoneUtil.verifyPhoneNumberFormat(username) && ErrorUtil.ERR_CODE_ACCOUNT_PHONE_BINDED.equals(result.getCode())) {
            result.setMessage("该手机号已注册或已绑定，请直接登录");
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
                    result.setCode(ErrorUtil.ERR_CODE_REGISTER_USER_UNUSUAL);
                    return result;
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
                    String passportId = accountToken.getPassportId();
                    setDefaultModelForResult(result, passportId, accountToken);
                }
            }
        } catch (Exception e) {
            logger.error("Sohu+ Register Failed,UserName Is " + pcoAuth2RegisterParams.getUsername(), e);
        } finally {
            String logCode = null;
            if (!Strings.isNullOrEmpty(finalCode)) {
                logCode = finalCode;
            } else {
                logCode = result.getCode();
            }
            //用户注册log
            UserOperationLog userOperationLog = new UserOperationLog(pcoAuth2RegisterParams.getUsername(), request.getRequestURI(), pcoAuth2RegisterParams.getClient_id(), logCode, getIp(request));
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            UserOperationLogUtil.log(userOperationLog);
        }
        commonManager.incRegTimes(ip, uuidName);
        return result.toString();
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

    private Result setDefaultModelForResult(Result result, String passportId, AccountToken accountToken) throws Exception {
        result.setDefaultModel("accesstoken", accountToken.getAccessToken());
        result.setDefaultModel("refreshtoken", accountToken.getRefreshToken());
        result.setDefaultModel("nick", Coder.enBase64(getUniqname(passportId)));
        result.setDefaultModel("sname", Coder.enBase64(passportId));
        result.setDefaultModel("passport", Coder.enBase64(passportId));
        result.setDefaultModel("result", 0);
        result.setDefaultModel("sid", 0);
        result.setDefaultModel("logintype", "sogou");
        return result;
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
        //参数验证
        String validateResult = ControllerHelper.validateParams(loginParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        //默认是sogou.com
        String passportId = loginParams.getLoginname();
        AccountDomainEnum accountDomainEnum = AccountDomainEnum.getAccountDomain(loginParams.getLoginname());
        if (AccountDomainEnum.INDIVID.equals(accountDomainEnum)) {
            //TODO 去sohu+取该个性账号的@sohu账号
        }

        WebLoginParams webLoginParams = new WebLoginParams();
        webLoginParams.setUsername(passportId);
        webLoginParams.setPassword(loginParams.getPwd());
        webLoginParams.setCaptcha(loginParams.getCaptcha());
        webLoginParams.setToken(loginParams.getToken());
        webLoginParams.setClient_id(String.valueOf(loginParams.getClient_id()));
        result = loginManager.accountLogin(webLoginParams, ip, request.getScheme());

        //用户登录log
        UserOperationLog userOperationLog = new UserOperationLog(passportId, request.getRequestURI(), String.valueOf(loginParams.getClient_id()), result.getCode(), ip);
        String referer = request.getHeader("referer");
        userOperationLog.putOtherMessage("ref", referer);
        UserOperationLogUtil.log(userOperationLog);

        if (result.isSuccess()) {
            String userId = result.getModels().get("userid").toString();
            int clientId = loginParams.getClient_id();
            //构造成功返回结果
            result = new APIResultSupport(true);
            //获取token
            Result tokenResult = pcAccountManager.createAccountToken(userId, loginParams.getInstanceid(), clientId);
            AccountToken accountToken = (AccountToken) tokenResult.getDefaultModel();
            result.setDefaultModel("accesstoken", accountToken.getAccessToken());
            result.setDefaultModel("refreshtoken", accountToken.getRefreshToken());

            result.setDefaultModel("autologin", loginParams.getRememberMe());
            result.setDefaultModel("passport", Coder.enBase64(userId));
            result.setDefaultModel("sname", Coder.enBase64(userId));
            result.setDefaultModel("nick", Coder.enBase64(getUniqname(passportId)));
            result.setDefaultModel("result", 0);
            result.setDefaultModel("sid", 0);
            result.setDefaultModel("logintype", "sogou");
            loginManager.doAfterLoginSuccess(passportId, ip, userId, clientId);
        } else {
            loginManager.doAfterLoginFailed(passportId, ip);
            //校验是否需要验证码
            boolean needCaptcha = loginManager.needCaptchaCheck(String.valueOf(loginParams.getClient_id()), passportId, ip);
            if (needCaptcha) {
                result.setDefaultModel("needCaptcha", true);
            }
            if (result.getCode().equals(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_PWD_ERROR);
                result.setMessage("密码错误");
            }
        }
        return result.toString();
    }


    //检查用户是否存在
    private Result checkPCAccountNotExists(String username) throws Exception {
        Result result = new APIResultSupport(false);
        //不允许邮箱注册
        if (username.indexOf("@") != -1) {
            result.setCode(ErrorUtil.ERR_CODE_REGISTER_EMAIL_NOT_ALLOWED);
            return result;
        }
        //判断是否是手机号注册
        if (PhoneUtil.verifyPhoneNumberFormat(username)) {
            result = pcoAuth2RegManager.isPcAccountNotExists(username, true);
        } else {
            result = pcoAuth2RegManager.isPcAccountNotExists(username, false);
        }
        return result;
    }

    //个人中心页面
    @RequestMapping(value = "/sogou/profile/basic/edit", method = RequestMethod.GET)
    public String pcindex(HttpServletRequest request, HttpServletResponse response, PCOAuth2IndexParams oauth2PcIndexParams, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(oauth2PcIndexParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return "forward:/oauth2/errorMsg?msg=" + result.toString();
        }
        Result queryPassportIdResult = pcAccountManager.queryPassportIdByAccessToken(oauth2PcIndexParams.getAccesstoken(), oauth2PcIndexParams.getClient_id());
        if (!queryPassportIdResult.isSuccess()) {
            return "forward:/oauth2/errorMsg?msg=" + queryPassportIdResult.toString();
        }
        String passportId = (String) queryPassportIdResult.getDefaultModel();
        //校验token有效期
        PcAuthTokenParams authPcTokenParams = new PcAuthTokenParams();
        authPcTokenParams.setToken(oauth2PcIndexParams.getAccesstoken());
        authPcTokenParams.setAppid(String.valueOf(oauth2PcIndexParams.getClient_id()));
        authPcTokenParams.setTs(oauth2PcIndexParams.getInstanceid());
        authPcTokenParams.setUserid(passportId);
        Result authTokenResult = pcAccountManager.authToken(authPcTokenParams);
        if(!authTokenResult.isSuccess()){
            result.setCode(ErrorUtil.ERR_ACCESS_TOKEN);
            return "forward:/oauth2/errorMsg?msg=" + result.toString();
        }
        //获取用户信息
        GetUserInfoApiparams getUserInfoApiparams =  new GetUserInfoApiparams(passportId, "uniqname,avatarurl,sec_mobile,sec_email");
        getUserInfoApiparams.setImagesize("180");
        Result getUserInfoResult=proxyUserInfoApiManager.getUserInfo(getUserInfoApiparams);
        String uniqname="",imageUrl ="",bindMobile="",bindEmail="";
        if (getUserInfoResult.isSuccess()) {
            uniqname = (String) getUserInfoResult.getModels().get("uniqname");
            uniqname = Strings.isNullOrEmpty(uniqname) ? defaultUniqname(passportId) : uniqname;
            bindMobile = (String) getUserInfoResult.getModels().get("sec_mobile");
            bindMobile = Strings.isNullOrEmpty(bindMobile)?"":bindMobile;
            bindEmail =(String)getUserInfoResult.getModels().get("sec_email");
            bindEmail = Strings.isNullOrEmpty(bindEmail)? "":bindEmail;
            String avatarStr =  getUserInfoResult.getModels().get("avatarurl").toString();
            if(!StringUtils.isEmpty(avatarStr)){
                Map map = (Map)getUserInfoResult.getModels().get("avatarurl");
                imageUrl =(String)map.get("img_180");
            }
        } else {
            uniqname = defaultUniqname(passportId);
        }
        model.addAttribute("uniqname",uniqname);
        model.addAttribute("imageUrl", imageUrl);
        model.addAttribute("bindMobile", bindMobile);
        model.addAttribute("bindEmail", bindEmail);
        model.addAttribute("userid", passportId);
        model.addAttribute("instanceid",oauth2PcIndexParams.getInstanceid());
        model.addAttribute("client_id",oauth2PcIndexParams.getClient_id());
        //判断绑定手机或者绑定邮箱是否可用;获取绑定手机，绑定邮箱
        handleBindAndPwd(passportId,bindMobile,bindEmail,model);
        //生成cookie
        CreateCookieUrlApiParams createCookieUrlApiParams = new CreateCookieUrlApiParams();
        createCookieUrlApiParams.setUserid(passportId);
        createCookieUrlApiParams.setRu("https://account.sogou.com");
        //TODO sogou域账号迁移后cookie生成问题
        Result getCookieValueResult = proxyLoginApiManager.getCookieValue(createCookieUrlApiParams);
        if (getCookieValueResult.isSuccess()) {
            String ppinf = (String) getCookieValueResult.getModels().get("ppinf");
            String pprdig = (String) getCookieValueResult.getModels().get("pprdig");
            ServletUtil.setCookie(response, "ppinf", ppinf, -1, CommonConstant.SOGOU_ROOT_DOMAIN);
            ServletUtil.setCookie(response, "pprdig", pprdig, -1, CommonConstant.SOGOU_ROOT_DOMAIN);
            response.addHeader("Sohupp-Cookie", "ppinf,pprdig");
        }
        return "/oauth2pc/pcindex";
    }


    @RequestMapping(value = "/oauth2/errorMsg")
    @ResponseBody
    public Object errorMsg(@RequestParam("msg") String msg) throws Exception {
        return msg;
    }

    private void handleBindAndPwd(String passportId, String bindMobile, String bindEmail, Model model) throws Exception {
        AccountDomainEnum accountDomain = AccountDomainEnum.getAccountDomain(passportId);
        switch (accountDomain) {
            case SOHU:
//                model.addAttribute("isBindEmailUsable", 0);
//                model.addAttribute("isBindMobileUsable", 0);
//                model.addAttribute("isUpdatepwdUsable", 0);
                model.addAttribute("isSohuAccount", 1);
                model.addAttribute("sohuBindUrl","https://passport.sohu.com/web/requestBindMobileAction.action");
                model.addAttribute("sohuUpdatepwdUrl","https://passport.sohu.com/web/updateInfo.action?modifyType=password");
                break;
            case THIRD:
                model.addAttribute("isBindEmailUsable", 0);
                model.addAttribute("isBindMobileUsable", 0);
                model.addAttribute("isUpdatepwdUsable", 0);
                break;
            case PHONE:
                if (StringUtils.isEmpty(bindEmail)) {
                    model.addAttribute("isBindEmailUsable", 1);
                } else {
                    model.addAttribute("isBindEmailUsable", 0);
                }
                model.addAttribute("isBindMobileUsable", 0);
                model.addAttribute("isUpdatepwdUsable", 1);
                break;
            default:
                if (StringUtils.isEmpty(bindEmail)) {
                    model.addAttribute("isBindEmailUsable", 1);
                } else {
                    model.addAttribute("isBindEmailUsable", 0);
                }
                if (StringUtils.isEmpty(bindMobile)) {
                    model.addAttribute("isBindMobileUsable", 1);
                } else {
                    model.addAttribute("isBindMobileUsable", 0);
                }
                model.addAttribute("isUpdatepwdUsable", 1);
                break;
        }
    }

    private String getUniqname(String passportId) {
        GetUserInfoApiparams getUserInfoApiparams = new GetUserInfoApiparams(passportId, "uniqname");
        Result getUserInfoResult = proxyUserInfoApiManager.getUserInfo(getUserInfoApiparams);
        String uniqname;
        if (getUserInfoResult.isSuccess()) {
            uniqname = (String) getUserInfoResult.getModels().get("uniqname");
            uniqname = Strings.isNullOrEmpty(uniqname) ? defaultUniqname(passportId) : uniqname;
        } else {
            uniqname = defaultUniqname(passportId);
        }
        return uniqname;
    }

    private String defaultUniqname(String passportId) {
        return passportId.substring(0, passportId.indexOf("@"));
    }

    /*
    注册种cookie防止恶意注册，黑白名单
     */
    private void webCookieProcess(HttpServletRequest request, HttpServletResponse response) {
        String uuidName = ServletUtil.getCookie(request, "uuidName");
        if (Strings.isNullOrEmpty(uuidName)) {
            uuidName = UUID.randomUUID().toString().replaceAll("-", "");
            ServletUtil.setCookie(response, "uuidName", uuidName, (int) DateAndNumTimesConstant.TIME_ONEDAY);
        }
    }

}