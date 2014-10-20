package com.sogou.upd.passport.web.account.api;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.CommonHelper;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.manager.account.CookieManager;
import com.sogou.upd.passport.manager.account.LoginManager;
import com.sogou.upd.passport.manager.account.OAuth2ResourceManager;
import com.sogou.upd.passport.manager.account.PCAccountManager;
import com.sogou.upd.passport.manager.api.account.form.CookieApiParams;
import com.sogou.upd.passport.manager.form.PcAuthTokenParams;
import com.sogou.upd.passport.manager.form.PcGetTokenParams;
import com.sogou.upd.passport.manager.form.PcPairTokenParams;
import com.sogou.upd.passport.manager.form.PcRefreshTokenParams;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.form.PcAccountWebParams;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
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
import java.util.Calendar;

/**
 * 桌面端登录流程Controller
 * User: shipengzhi
 * Date: 13-7-26
 * Time: 下午7:07
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class PCAccountController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(PCAccountController.class);

    @Autowired
    private PCAccountManager pcAccountManager;
    @Autowired
    private LoginManager loginManager;
    @Autowired
    private OAuth2ResourceManager oAuth2ResourceManager;
    @Autowired
    private CookieManager cookieManager;

    @RequestMapping(value = "/act/pclogin", method = RequestMethod.GET)
    public String pcLogin(HttpServletRequest request, PcAccountWebParams pcAccountWebParams, Model model)
            throws Exception {
        String refreshToken = pcAccountWebParams.getRefresh_token();
        String userId = pcAccountWebParams.getUserid();
        String appId = pcAccountWebParams.getAppid();
        String ts = pcAccountWebParams.getTs();
        //校验非法appid
        if (!appId.matches("[0-9]{4}")) {
            pcAccountWebParams.setAppid("9998");
        }
        //计算isAuthedUser，用于是否可以自动登录
        boolean isAuthedUser = false;
        if (!Strings.isNullOrEmpty(refreshToken) && !Strings.isNullOrEmpty(userId)) {
            PcRefreshTokenParams pcRefreshTokenParams = new PcRefreshTokenParams();
            pcRefreshTokenParams.setRefresh_token(refreshToken);
            pcRefreshTokenParams.setUserid(userId);
            pcRefreshTokenParams.setAppid(appId);
            pcRefreshTokenParams.setTs(ts);
            int clientId = Integer.parseInt(pcRefreshTokenParams.getAppid());
            if (pcAccountManager.verifyRefreshToken(pcRefreshTokenParams.getUserid(), clientId, pcRefreshTokenParams.getTs(), pcRefreshTokenParams.getRefresh_token())) {
                isAuthedUser = true;
            }
        }
        model.addAttribute("isAuthedUser", isAuthedUser);
        if (isAuthedUser) {
            String timestamp = new Long(Calendar.getInstance().getTimeInMillis()).toString();
            String sig = pcAccountManager.getSig(userId, Integer.parseInt(appId), refreshToken, timestamp);
            model.addAttribute("timestamp", timestamp);
            model.addAttribute("sig", sig);
        }

        //用户log
        UserOperationLog userOperationLog = new UserOperationLog(userId, request.getRequestURI(), appId, "0", getIp(request));
        String referer = request.getHeader("referer");
        userOperationLog.putOtherMessage("ref", referer);
        userOperationLog.putOtherMessage("yyid", ServletUtil.getCookie(request, "YYID"));
        UserOperationLogUtil.log(userOperationLog);

        //此处是帮浏览器打的个补丁，根据版本号判断
        String version = pcAccountWebParams.getV();
        boolean supportLocalHash = version.compareTo("3.1.0.0000") > 0;
        model.addAttribute("version", version);
        model.addAttribute("supportLocalHash", supportLocalHash);

        //赋给页面值
        model.addAttribute("userid", userId);
        model.addAttribute("appid", appId);
        model.addAttribute("ts", ts);
        model.addAttribute("openAppType", pcAccountWebParams.getOpenapptype());
        return "/pcaccount/pclogin";
    }

    //输入法的客户端登录使用
    @RequestMapping(value = "/act/gettoken")
    @ResponseBody
    public Object getToken(HttpServletRequest request, PcGetTokenParams pcGetTokenParams) throws Exception {
        //参数验证
        String validateResult = ControllerHelper.validateParams(pcGetTokenParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            return "1";
        }
        String userId = pcGetTokenParams.getUserid();
        //不允许第三方格式的账号登录
        if (AccountDomainEnum.THIRD.equals(AccountDomainEnum.getAccountDomain(userId))) {
            return "1";
        }
        String ip = getIp(request);
        String appId = pcGetTokenParams.getAppid();
        String ts = pcGetTokenParams.getTs();
        PcPairTokenParams pcPairTokenParams = new PcPairTokenParams();
        //仅限输入法使用，用户只能输入userid；客户端使用用户输入的userid作为唯一标识
        pcPairTokenParams.setUserid(userId);
        pcPairTokenParams.setAppid(appId);
        pcPairTokenParams.setTs(ts);
        pcPairTokenParams.setPassword(pcGetTokenParams.getPassword());

        Result result = pcAccountManager.createPairToken(pcPairTokenParams, ip);
        String resStr = "";
        if (result.isSuccess()) {
            AccountToken accountToken = (AccountToken) result.getDefaultModel();
            resStr = "0|" + accountToken.getAccessToken();   //0|token|refreshToken
        } else {
            resStr = handleGetPairTokenErr(result.getCode());
        }

        //用户log
        String resultCode = StringUtil.defaultIfEmpty(result.getCode(), "0");
        UserOperationLog userOperationLog = new UserOperationLog(userId, request.getRequestURI(), appId, resultCode, ip);
        UserOperationLogUtil.log(userOperationLog);

        return resStr;
    }

    //除输入法外的浏览器，游戏大厅等其它客户端的登录使用
    @RequestMapping(value = "/act/getpairtoken")
    @ResponseBody
    public Object getPairToken(HttpServletRequest request, PcPairTokenParams reqParams, @RequestParam(value = "cb", defaultValue = "") String cb) throws Exception {
        //参数验证
        if (!isCleanString(cb)) {
            return getReturnStr(cb, "1");
        }
        String validateResult = ControllerHelper.validateParams(reqParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            return getReturnStr(cb, "1");
        }
        String userId = reqParams.getUserid();
        //不允许第三方格式的账号登录
        if (AccountDomainEnum.THIRD.equals(AccountDomainEnum.getAccountDomain(userId))) {
            return "1";
        }
        //getpairtoken允许个性账号、手机号登陆；gettoken不允许
        userId = loginManager.getIndividPassportIdByUsername(userId);
        reqParams.setUserid(userId);

        String ip = getIp(request);
        int appid = Integer.parseInt(reqParams.getAppid());

        Result result = new APIResultSupport(false);
        // 手机移动端，取不到用户的真实ip，所以不做安全限制
        if (!CommonHelper.isIePinyinToken(appid) && loginManager.isLoginUserInBlackList(userId, ip)) {
            result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST);
        } else {
            result = pcAccountManager.createPairToken(reqParams, ip);
        }
        String resStr;
        if (result.isSuccess()) {
            AccountToken accountToken = (AccountToken) result.getDefaultModel();
            // 浏览器sohu接口昵称使用userid，@前半部分作为昵称, 壁纸、游戏用自己存的
            String passportId = accountToken.getPassportId();
            String uniqname = passportId.substring(0, passportId.indexOf("@"));
            //客户端使用getPairToken返回的userid作为唯一标识
            resStr = "0|" + accountToken.getAccessToken() + "|" + accountToken.getRefreshToken() + "|" + accountToken.getPassportId() + "|" + uniqname;   //0|token|refreshToken|userid|nick
            if (!CommonHelper.isIePinyinToken(appid)) {
                loginManager.doAfterLoginSuccess(userId, ip, userId, appid);
            }
        } else {
            resStr = handleGetPairTokenErr(result.getCode());
            if (!CommonHelper.isIePinyinToken(appid)) {
                loginManager.doAfterLoginFailed(reqParams.getUserid(), ip, result.getCode());
            }
        }

        //用户log
        String resultCode = StringUtil.defaultIfEmpty(result.getCode(), "0");
        UserOperationLog userOperationLog = new UserOperationLog(userId, request.getRequestURI(), reqParams.getAppid(), resultCode, ip);
        UserOperationLogUtil.log(userOperationLog);

        return getReturnStr(cb, resStr);
    }

    @RequestMapping(value = "/act/refreshtoken")
    @ResponseBody
    public Object refreshToken(HttpServletRequest request, PcRefreshTokenParams reqParams, @RequestParam(value = "cb", defaultValue = "") String cb) throws Exception {
        //参数验证
        if (!isCleanString(cb)) {
            return getReturnStr(cb, "1");
        }

        String validateResult = ControllerHelper.validateParams(reqParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            return getReturnStr(cb, "1|invalid|required_params"); //参数错误
        }

        Result result = pcAccountManager.authRefreshToken(reqParams);
        String resStr = "";
        if (result.isSuccess()) {
            AccountToken accountToken = (AccountToken) result.getDefaultModel();
            resStr = "0|" + accountToken.getAccessToken() + "|" + accountToken.getRefreshToken();
        } else {
            resStr = handleRefreshTokenErr(result.getCode());
        }

        //用户log
        String resultCode = StringUtil.defaultIfEmpty(result.getCode(), "0");
        UserOperationLog userOperationLog = new UserOperationLog(reqParams.getUserid(), request.getRequestURI(), reqParams.getAppid(), resultCode, getIp(request));
        userOperationLog.putOtherMessage("refreshtoken", reqParams.getRefresh_token());
        UserOperationLogUtil.log(userOperationLog);

        return getReturnStr(cb, resStr);
    }

    @RequestMapping(value = "/act/authtoken")
    public void authToken(HttpServletRequest request, HttpServletResponse response, PcAuthTokenParams authPcTokenParams) throws Exception {
        //参数验证
        String validateResult = ControllerHelper.validateParams(authPcTokenParams);
        String ru = authPcTokenParams.getRu();
        String ua = request.getHeader(CommonConstant.USER_AGENT);
        ua = ua.contains(CommonConstant.SOGOU_IME_UA) ? ua : ""; //输入法的标识
        if (!Strings.isNullOrEmpty(validateResult)) {
            if (!Strings.isNullOrEmpty(ru)) {
                response.sendRedirect(buildRedirectUrl(ru, 1)); //status=1表示参数错误
                return;
            }
            response.getWriter().print("Error: parameter error!");
            return;
        }
        //线上存在userid=null的校验情况，此时，通过accesstoken反解出passportid
        String userId = authPcTokenParams.getUserid();
        if ("null".equals(userId) || StringUtil.isBlank(userId)) {
            Result getUserIdResult = oAuth2ResourceManager.getPassportIdByToken(authPcTokenParams.getToken(), Integer.parseInt(authPcTokenParams.getAppid()));
            if (getUserIdResult.isSuccess()) {
                userId = (String) getUserIdResult.getDefaultModel();
            } else {
                if (!Strings.isNullOrEmpty(ru)) {
                    response.sendRedirect(buildRedirectUrl(ru, 1)); //status=1表示参数错误
                    return;
                }
                response.getWriter().print("Error: parameter error!");
                return;
            }
        }

        userId = AccountDomainEnum.getAuthtokenCase(userId);
        authPcTokenParams.setUserid(userId);
        Result authTokenResult = pcAccountManager.authToken(authPcTokenParams);

        //用户log
        String resultCode = StringUtil.defaultIfEmpty(authTokenResult.getCode(), "0");
        UserOperationLog userOperationLog = new UserOperationLog(userId, request.getRequestURI(), authPcTokenParams.getAppid(), resultCode, getIp(request));
        userOperationLog.putOtherMessage("accesstoken", authPcTokenParams.getToken());
        userOperationLog.putOtherMessage("ua", ua);
        UserOperationLogUtil.log(userOperationLog);

        //重定向生成cookie
        if (authTokenResult.isSuccess()) {

//            CreateCookieUrlApiParams createCookieUrlApiParams = new CreateCookieUrlApiParams();
//            createCookieUrlApiParams.setUserid(userId);
//            createCookieUrlApiParams.setRu(ru);
//            createCookieUrlApiParams.setClientId(authPcTokenParams.getAppid());
//            if (!"0".equals(authPcTokenParams.getLivetime())) {
//                createCookieUrlApiParams.setPersistentcookie(1);
//            }
//            createCookieUrlApiParams.setDomain("sogou.com");
            //TODO sogou域账号迁移后cookie生成问题 最初版本
//            Result getCookieValueResult = proxyLoginApiManager.getCookieInfoWithRedirectUrl(createCookieUrlApiParams);

            CookieApiParams cookieApiParams = new CookieApiParams();
            cookieApiParams.setUserid(userId);
            cookieApiParams.setClient_id(Integer.parseInt(authPcTokenParams.getAppid()));
            cookieApiParams.setRu(ru);
            cookieApiParams.setTrust(CookieApiParams.IS_ACTIVE);
            cookieApiParams.setIp(getIp(request));

            if (!Strings.isNullOrEmpty(userId)) {
                if (StringUtils.contains(userId, "@")) {
                    cookieApiParams.setUniqname(StringUtils.substring(userId, 0, userId.indexOf("@")));
                } else {
                    cookieApiParams.setUniqname(userId);
                }
            }

            cookieApiParams.setCreateAndSet(CommonConstant.CREATE_COOKIE_NOT_SET);

            if (!"0".equals(authPcTokenParams.getLivetime())) {
                cookieApiParams.setPersistentcookie("1");
            }
            cookieApiParams.setDomain("sogou.com");
            Result getCookieValueResult = cookieManager.createCookie(response, cookieApiParams);


            if (getCookieValueResult.isSuccess()) {
                String ppinf = (String) getCookieValueResult.getModels().get("ppinf");
                String pprdig = (String) getCookieValueResult.getModels().get("pprdig");
                String passport = (String) getCookieValueResult.getModels().get("passport");   // 手机浏览器需要此cookie
                ServletUtil.setHttpOnlyCookie(response, "ppinf", ppinf, CommonConstant.SOGOU_ROOT_DOMAIN);   //浏览器移动端要求返回cookie以HttpOnly结尾
                ServletUtil.setHttpOnlyCookie(response, "pprdig", pprdig, CommonConstant.SOGOU_ROOT_DOMAIN);
                ServletUtil.setHttpOnlyCookie(response, "passport", passport, CommonConstant.SOGOU_ROOT_DOMAIN);
                response.addHeader("Sohupp-Cookie", "ppinf,pprdig");     // 输入法Mac版需要此字段

                String redirectUrl = (String) getCookieValueResult.getModels().get("redirectUrl");
                response.sendRedirect(redirectUrl);
                return;  //如果重定向url不是固定的，不可使用springmvc的RedirectView，因为会缓存url
            }
        }
        //token验证失败
        response.sendRedirect(buildRedirectUrl(ru, 6));  //status=6表示验证失败
        return;
    }

    @RequestMapping(value = "/act/errorMsg")
    @ResponseBody
    public Object errorMsg(@RequestParam("msg") String msg) throws Exception {
        return msg;
    }

    private boolean isCleanString(String cb) {
        if (Strings.isNullOrEmpty(cb)) {
            return true;
        }
        String cleanValue = Jsoup.clean(cb, Whitelist.none());
        return cleanValue.equals(cb);
    }

    private String getReturnStr(String cb, String resStr) {
        if (!Strings.isNullOrEmpty(cb)) {
            return cb + "('" + resStr + "')";
        }
        return resStr;
    }

    private String handleGetPairTokenErr(String errCode) {
        String errStr;
        switch (errCode) {
            case ErrorUtil.INVALID_CLIENTID:
                errStr = "1"; //参数错误
                break;
            case ErrorUtil.INVALID_ACCOUNT:
                errStr = "2";  //用户名不存在
                break;
            case ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST:
                errStr = "3";  // 账号被封禁
                break;
            case ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_PWD_ERROR:
                errStr = "3";  //用户名密码错误
                break;
            case ErrorUtil.ERR_SIGNATURE_OR_TOKEN:
                errStr = "7|invalid sig"; //生成token失败
                break;
            default:
                errStr = "6"; //失败
                break;
        }
        return errStr;
    }

    private String handleRefreshTokenErr(String errCode) {
        String errStr;
        switch (errCode) {
            case ErrorUtil.INVALID_CLIENTID:
                errStr = "1|invalid|required_params";
                break;
            case ErrorUtil.ERR_REFRESH_TOKEN:
                errStr = "2|invalid|refreshtoken";
                break;
            case ErrorUtil.CREATE_TOKEN_FAIL:
                errStr = "3|failed|createtoken"; //生成token失败
                break;
            default:
                errStr = "6|error|syste_error"; //系统错误
                break;
        }
        return errStr;
    }

    private String buildRedirectUrl(String ru, int status) {
        if (Strings.isNullOrEmpty(ru)) {
            ru = CommonConstant.DEFAULT_CONNECT_REDIRECT_URL;
        }
        if (ru.contains("?")) {
            return ru + "&status=" + status;
        } else {
            return ru + "?status=" + status;
        }
    }
}