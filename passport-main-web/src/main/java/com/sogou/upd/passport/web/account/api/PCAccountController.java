package com.sogou.upd.passport.web.account.api;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.PCAccountManager;
import com.sogou.upd.passport.manager.api.account.LoginApiManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.CreateCookieUrlApiParams;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.form.PcAuthTokenParams;
import com.sogou.upd.passport.manager.form.PcGetTokenParams;
import com.sogou.upd.passport.manager.form.PcPairTokenParams;
import com.sogou.upd.passport.manager.form.PcRefreshTokenParams;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.form.PcAccountWebParams;
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
    private UserInfoApiManager proxyUserInfoApiManagerImpl;
    @Autowired
    private LoginApiManager proxyLoginApiManager;

    @RequestMapping(value = "/act/pclogin", method = RequestMethod.GET)
    public String pcLogin(HttpServletRequest request, PcAccountWebParams pcAccountWebParams, Model model)
            throws Exception {
        //校验非法appid
        if (!pcAccountWebParams.getAppid().matches("[0-9]{4}")) {
            pcAccountWebParams.setAppid("9998");
        }

        //计算isAuthedUser，用于是否可以自动登录
        boolean isAuthedUser = false;
        if (!Strings.isNullOrEmpty(pcAccountWebParams.getRefresh_token()) && !Strings.isNullOrEmpty(pcAccountWebParams.getUserid())) {
            PcRefreshTokenParams pcRefreshTokenParams = new PcRefreshTokenParams();
            pcRefreshTokenParams.setRefresh_token(pcAccountWebParams.getRefresh_token());
            pcRefreshTokenParams.setUserid(pcAccountWebParams.getUserid());
            pcRefreshTokenParams.setAppid(pcAccountWebParams.getAppid());
            pcRefreshTokenParams.setTs(pcAccountWebParams.getTs());
            if (pcAccountManager.verifyRefreshToken(pcRefreshTokenParams)) {
                isAuthedUser = true;
            }
        }
        model.addAttribute("isAuthedUser", isAuthedUser);
        if (isAuthedUser) {
            String timestamp = new Long(Calendar.getInstance().getTimeInMillis()).toString();
            String sig = pcAccountManager.getSig(pcAccountWebParams.getUserid(), Integer.parseInt(pcAccountWebParams.getAppid()), pcAccountWebParams.getRefresh_token(), timestamp);
            model.addAttribute("timestamp", timestamp);
            model.addAttribute("sig", sig);
        }

        //用户log
        UserOperationLog userOperationLog = new UserOperationLog(pcAccountWebParams.getUserid(), request.getRequestURI(), pcAccountWebParams.getAppid(), "0", getIp(request));
        String referer = request.getHeader("referer");
        userOperationLog.putOtherMessage("ref", referer);
        UserOperationLogUtil.log(userOperationLog);

        //此处是帮浏览器打的个补丁，根据版本号判断
        String version = pcAccountWebParams.getV();
        boolean supportLocalHash = version.compareTo("3.1.0.0000") > 0;
        model.addAttribute("version", version);
        model.addAttribute("supportLocalHash", supportLocalHash);

        //赋给页面值
        model.addAttribute("userid", pcAccountWebParams.getUserid());
        model.addAttribute("appid", pcAccountWebParams.getAppid());
        model.addAttribute("ts", pcAccountWebParams.getTs());
        model.addAttribute("openAppType", pcAccountWebParams.getOpenapptype());
        return "/pcaccount/pclogin";
    }

    @RequestMapping(value = "/act/gettoken")
    @ResponseBody
    public Object getToken(HttpServletRequest request, PcGetTokenParams pcGetTokenParams) throws Exception {
        //参数验证
        String validateResult = ControllerHelper.validateParams(pcGetTokenParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            return "1";
        }

        PcPairTokenParams pcPairTokenParams = new PcPairTokenParams();
        pcPairTokenParams.setUserid(pcGetTokenParams.getUserid());
        pcPairTokenParams.setAppid(pcGetTokenParams.getAppid());
        pcPairTokenParams.setTs(pcGetTokenParams.getTs());
        pcPairTokenParams.setPassword(pcGetTokenParams.getPassword());
        Result result = pcAccountManager.createPairToken(pcPairTokenParams);
        String resStr = "";
        if (result.isSuccess()) {
            AccountToken accountToken = (AccountToken) result.getDefaultModel();
            resStr = "0|" + accountToken.getAccessToken();   //0|token|refreshToken
        } else {
            resStr = handleGetPairTokenErr(result.getCode());
        }

        //用户log
        String resultCode = StringUtil.defaultIfEmpty(result.getCode(), "0");
        UserOperationLog userOperationLog = new UserOperationLog(pcGetTokenParams.getUserid(), request.getRequestURI(), pcGetTokenParams.getAppid(), resultCode, getIp(request));
        userOperationLog.putOtherMessage("ts", pcGetTokenParams.getTs());
        UserOperationLogUtil.log(userOperationLog);

        return resStr;
    }

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

        Result result = pcAccountManager.createPairToken(reqParams);
        String resStr;
        if (result.isSuccess()) {
            AccountToken accountToken = (AccountToken) result.getDefaultModel();
            // 获取昵称，返回格式
            String passportId = accountToken.getPassportId();
            GetUserInfoApiparams getUserInfoApiparams = new GetUserInfoApiparams(passportId, "uniqname");
            Result getUserInfoResult = proxyUserInfoApiManagerImpl.getUserInfo(getUserInfoApiparams);
            String uniqname;
            if (getUserInfoResult.isSuccess()) {
                uniqname = (String) getUserInfoResult.getModels().get("uniqname");
                uniqname = Strings.isNullOrEmpty(uniqname) ? defaultUniqname(passportId) : uniqname;
            } else {
                uniqname = defaultUniqname(passportId);
            }
            resStr = "0|" + accountToken.getAccessToken() + "|" + accountToken.getRefreshToken() + "|" + accountToken.getPassportId() + "|" + uniqname;   //0|token|refreshToken|userid|nick
        } else {
            resStr = handleGetPairTokenErr(result.getCode());
        }

        //用户log
        String resultCode = StringUtil.defaultIfEmpty(result.getCode(), "0");
        UserOperationLog userOperationLog = new UserOperationLog(reqParams.getUserid(), request.getRequestURI(), reqParams.getAppid(), resultCode, getIp(request));
        userOperationLog.putOtherMessage("ts", reqParams.getTs());
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
        userOperationLog.putOtherMessage("ts", reqParams.getTs());
        UserOperationLogUtil.log(userOperationLog);

        return getReturnStr(cb, resStr);
    }

    @RequestMapping(value = "/act/authtoken")
    public String authToken(HttpServletRequest request, HttpServletResponse response, PcAuthTokenParams authPcTokenParams) throws Exception {
        //参数验证
        String validateResult = ControllerHelper.validateParams(authPcTokenParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            if (!Strings.isNullOrEmpty(authPcTokenParams.getRu())) {
                return "redirect:" + authPcTokenParams.getRu() + "?status=1";   //status=1表示参数错误
            }
            return "forward:/act/errorMsg?msg=Error: parameter error!";
        }
        Result result = pcAccountManager.authToken(authPcTokenParams);

        //用户log
        String resultCode = StringUtil.defaultIfEmpty(result.getCode(), "0");
        UserOperationLog userOperationLog = new UserOperationLog(authPcTokenParams.getUserid(), request.getRequestURI(), authPcTokenParams.getAppid(), resultCode, getIp(request));
        userOperationLog.putOtherMessage("ts", authPcTokenParams.getTs());
        UserOperationLogUtil.log(userOperationLog);

        //重定向生成cookie
        if (result.isSuccess()) {
            CreateCookieUrlApiParams createCookieUrlApiParams = new CreateCookieUrlApiParams();
            createCookieUrlApiParams.setUserid(authPcTokenParams.getUserid());
            createCookieUrlApiParams.setRu(authPcTokenParams.getRu());
            if (authPcTokenParams.getLivetime() > 0) {
                createCookieUrlApiParams.setPersistentcookie(1);
            }
            //TODO sogou域账号迁移后cookie生成问题
            Result createCookieResult = proxyLoginApiManager.buildCreateCookieUrl(createCookieUrlApiParams);
            if (createCookieResult.isSuccess()) {
                String setcookieUrl = createCookieResult.getModels().get("url").toString();
                response.addHeader("Sohupp-Cookie", "ppinf,pprdig"); // 输入法mac版从这里取到要读哪个cookie
                return "redirect:" + setcookieUrl;
            } else {
                result.setCode(ErrorUtil.ERR_CODE_CREATE_COOKIE_FAILED);
                logger.error("authToken:createCookieUrl error");
            }
        }
        //token验证失败
        return "redirect:" + authPcTokenParams.getRu() + "?status=6";//status=6表示验证失败
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

    private String defaultUniqname(String passportId) {
        return passportId.substring(0, passportId.indexOf("@"));
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
            case ErrorUtil.ERR_CODE_ACCOUNT_PHONE_NOBIND:
                errStr = "2";  //用户名不存在
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
}