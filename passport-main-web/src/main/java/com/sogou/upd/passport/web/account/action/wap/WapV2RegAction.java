package com.sogou.upd.passport.web.account.action.wap;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.*;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.parameter.AccountModuleEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.RegManager;
import com.sogou.upd.passport.manager.account.SecureManager;
import com.sogou.upd.passport.manager.api.account.RegisterApiManager;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.BaseMoblieApiParams;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.manager.api.connect.SessionServerManager;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.form.wap.WapRegMobileCodeParams;
import com.sogou.upd.passport.web.account.form.wap.WapV2RegParams;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * wap2.0注册
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-9-11
 * Time: 下午5:35
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class WapV2RegAction extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(WapV2RegAction.class);

    @Autowired
    private ConfigureManager configureManager;
    @Autowired
    private CommonManager commonManager;
    @Autowired
    private RegisterApiManager sgRegisterApiManager;
    @Autowired
    private RegManager regManager;
    @Autowired
    private SecureManager secureManager;
    @Autowired
    private SessionServerManager sessionServerManager;
    @Autowired
    private UserInfoApiManager sgUserInfoApiManager;

    @RequestMapping(value = "/wap2/sendsms", method = RequestMethod.POST)
    public String sendsms(HttpServletRequest request, HttpServletResponse response, WapRegMobileCodeParams reqParams, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String ip = getIp(request);
        String finalCode = null;
        String mobile = reqParams.getMobile();
        String clientIdStr = reqParams.getClient_id();
        int clientId = Integer.parseInt(clientIdStr);
        String v = reqParams.getV();
        String skin = reqParams.getSkin();
        try {
            reqParams.setRu(Coder.decodeUTF8(reqParams.getRu()));
            //参数验证
            String validateResult = ControllerHelper.validateParams(reqParams);
            if (!Strings.isNullOrEmpty(validateResult)) {
                buildModuleReturnStr(true, reqParams.getRu(), validateResult,
                        clientIdStr, skin, v, false, model);
                model.addAttribute("mobile", mobile);
                model.addAttribute("username", mobile);
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                return "wap/regist_wap";
            }
            //检查client_id是否存在
            if (!configureManager.checkAppIsExist(clientId)) {
                buildModuleReturnStr(true, reqParams.getRu(), ErrorUtil.getERR_CODE_MSG(ErrorUtil.INVALID_CLIENTID),
                        clientIdStr, skin, v, false, model);
                model.addAttribute("mobile", mobile);
                model.addAttribute("username", mobile);
                result.setCode(ErrorUtil.INVALID_CLIENTID);
                return "wap/regist_wap";
            }
            //第二次弹出验证码
            result = commonManager.checkMobileSendSMSInBlackList(mobile, clientIdStr);
            if (!result.isSuccess()) {
                if (!Strings.isNullOrEmpty(reqParams.getToken()) && !Strings.isNullOrEmpty(reqParams.getCaptcha())) {
                    result = regManager.checkCaptchaToken(reqParams.getToken(), reqParams.getCaptcha());
                    //如果验证码校验失败，则提示
                    if (!result.isSuccess()) {
                        buildModuleReturnStr(true, reqParams.getRu(), ErrorUtil.getERR_CODE_MSG(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED),
                                clientIdStr, skin, v, true, model);
                        String token = RandomStringUtils.randomAlphanumeric(48);
                        model.addAttribute("mobile", mobile);
                        model.addAttribute("username", mobile);
                        model.addAttribute("token", token);
                        model.addAttribute("captchaUrl", CommonConstant.DEFAULT_WAP_INDEX_URL + "/captcha?token=" + token);
                        result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
                        return "wap/regist_wap";
                    }
                } else {
                    //需要弹出验证码
                    buildModuleReturnStr(true, reqParams.getRu(), ErrorUtil.getERR_CODE_MSG(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_NEED_CODE),
                            clientIdStr, skin, v, true, model);
                    String token = RandomStringUtils.randomAlphanumeric(48);
                    model.addAttribute("mobile", mobile);
                    model.addAttribute("username", mobile);
                    model.addAttribute("token", token);
                    model.addAttribute("captchaUrl", CommonConstant.DEFAULT_WAP_INDEX_URL + "/captcha?token=" + token);
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_NEED_CODE);
                    return "wap/regist_wap";
                }
            }
            //校验用户ip是否中了黑名单
            result = commonManager.checkMobileSendSMSInBlackList(ip, clientIdStr);
            if (!result.isSuccess()) {
                finalCode = ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST;
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND);
                buildModuleReturnStr(true, reqParams.getRu(), ErrorUtil.getERR_CODE_MSG(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND),
                        clientIdStr, skin, v, false, model);
                model.addAttribute("mobile", mobile);
                model.addAttribute("username", mobile);
                return "wap/regist_wap";
            }
            BaseMoblieApiParams baseMobileApiParams = buildProxyApiParams(clientId, mobile);
            result = sgRegisterApiManager.sendMobileRegCaptcha(baseMobileApiParams);
            buildModuleReturnStr(true, reqParams.getRu(), ErrorUtil.getERR_CODE_MSG(result.getCode()),
                    clientIdStr, skin, v, false, model);
            model.addAttribute("mobile", mobile);
            if (!result.isSuccess()) {
                model.addAttribute("username", mobile);
                return "wap/regist_wap";
            }
        } catch (Exception e) {
            logger.error("wap2.0 reguser:User Register Is Failed,mobile is " + mobile, e);
        } finally {
            String logCode;
            if (!Strings.isNullOrEmpty(finalCode)) {
                logCode = finalCode;
            } else {
                logCode = result.getCode();
            }
            UserOperationLog userOperationLog = new UserOperationLog(mobile, request.getRequestURI(), clientIdStr, logCode, ip);
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            UserOperationLogUtil.log(userOperationLog);
        }
        commonManager.incSendTimesForMobile(ip);
        commonManager.incSendTimesForMobile(mobile);
        String scode = commonManager.getSecureCode(mobile, clientId, CacheConstant.CACHE_PREFIX_PASSPORTID_PASSPORTID_SECURECODE);
        String rediectUrl = buildSendRedirectUrl(reqParams, scode, false);
        response.sendRedirect(rediectUrl);
        return "empty";
    }

    /**
     * 获取短信验证码校验通过后，需要跳转到一个接口，避免用户刷新导致页面不可用
     */
    private String buildSendRedirectUrl(WapRegMobileCodeParams params, String scode, boolean hasError) throws UnsupportedEncodingException {
        String ru = Coder.encodeUTF8(Strings.isNullOrEmpty(params.getRu()) ? CommonConstant.DEFAULT_WAP_URL : params.getRu());
        String client_id = Strings.isNullOrEmpty(params.getClient_id()) ? String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID) : params.getClient_id();
        String skin = Strings.isNullOrEmpty(params.getSkin()) ? WapConstant.WAP_SKIN_GREEN : params.getSkin();
        String v = Strings.isNullOrEmpty(params.getV()) ? WapConstant.WAP_COLOR : params.getV();
        StringBuilder urlStr = new StringBuilder();
        urlStr.append(CommonConstant.DEFAULT_WAP_INDEX_URL + "/wap2/r?");
        urlStr.append("&client_id=").append(client_id);
        urlStr.append("&ru=").append(ru);
        urlStr.append("&mobile=").append(params.getMobile());
        urlStr.append("&username=").append(params.getMobile());
        urlStr.append("&skin=").append(skin);
        urlStr.append("&v=").append(v);
        urlStr.append("&scode=").append(scode);
        urlStr.append("&needCaptcha=").append(params.getNeedCaptcha());
        urlStr.append("&hasError=").append(hasError);
        urlStr.append("&errorMsg=").append(params.getErrorMsg());
        return urlStr.toString();
    }

    @RequestMapping(value = "/wap2/reguser", method = RequestMethod.POST)
    public String regV2User(HttpServletRequest request, HttpServletResponse response, WapV2RegParams regParams, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String ip = null;
        String uuidName = null;
        String finalCode = null;
        int clientId = regParams.getClient_id();
        try {
            regParams.setRu(Coder.decodeUTF8(regParams.getRu()));
            //参数验证
            String validateResult = ControllerHelper.validateParams(regParams);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                regParams.setErrorMsg(validateResult);
                response.sendRedirect(buildRegErrorUrl(regParams,true));
                return "empty";
            }
            ip = getIp(request);
            //校验用户是否允许注册
            uuidName = ServletUtil.getCookie(request, "uuidName");
            result = regManager.checkRegInBlackList(ip, uuidName);
            if (!result.isSuccess()) {
                if (result.getCode().equals(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST)) {
                    finalCode = ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST;
                    result.setCode(ErrorUtil.ERR_CODE_REGISTER_UNUSUAL);
                }
                regParams.setErrorMsg(ErrorUtil.getERR_CODE_MSG(result.getCode()));
                response.sendRedirect(buildRegErrorUrl(regParams,true));
                return "empty";
            }
            if (!PhoneUtil.verifyPhoneNumberFormat(regParams.getUsername())) {
                regParams.setErrorMsg(ErrorUtil.getERR_CODE_MSG(ErrorUtil.ERR_CODE_ACCOUNT_PHONEERROR));
                response.sendRedirect(buildRegErrorUrl(regParams,true));
                return "empty";
            }
            //校验安全码
            if (!commonManager.checkSecureCode(regParams.getUsername(), clientId, regParams.getScode(), CacheConstant.CACHE_PREFIX_PASSPORTID_PASSPORTID_SECURECODE)) {
                result.setCode(ErrorUtil.ERR_CODE_FINDPWD_SCODE_FAILED);
                regParams.setErrorMsg( ErrorUtil.getERR_CODE_MSG(ErrorUtil.ERR_CODE_FINDPWD_SCODE_FAILED));
                response.sendRedirect(buildRegErrorUrl(regParams,true));
                return "empty";
            }
            result = regManager.registerMobile(regParams.getUsername(), regParams.getPassword(), clientId, regParams.getCaptcha(), null);
            if (result.isSuccess()) {
                //第三方获取个人资料
                String userid = result.getModels().get("userid").toString();
                // 调用内部接口
                GetUserInfoApiparams userInfoApiparams = new GetUserInfoApiparams(userid, "uniqname,avatarurl,gender");
                result = sgUserInfoApiManager.getUserInfo(userInfoApiparams);
                logger.info("wap reg userinfo result:" + result);
                Result sessionResult = sessionServerManager.createSession(userid);
                String sgid;
                if (sessionResult.isSuccess()) {
                    sgid = (String) sessionResult.getModels().get(LoginConstant.COOKIE_SGID);
                    result.getModels().put("userid", userid);
                    if (!Strings.isNullOrEmpty(sgid)) {
                        result.getModels().put(LoginConstant.COOKIE_SGID, sgid);
                        setSgidCookie(response, sgid);
                    }
                } else {
                    logger.warn("can't get session result, userid:" + result.getModels().get("userid"));
                }
            } else {
                String scode = commonManager.getSecureCode(regParams.getUsername(),
                        clientId, CacheConstant.CACHE_PREFIX_PASSPORTID_PASSPORTID_SECURECODE);
                regParams.setErrorMsg(ErrorUtil.getERR_CODE_MSG(result.getCode()));
                regParams.setScode(scode);
                response.sendRedirect(buildRegErrorUrl(regParams,true));
                return "empty";
            }
        } catch (Exception e) {
            logger.error("wap2 reguser:User Register Is Failed,Username is " + regParams.getUsername(), e);
        } finally {
            String logCode = !Strings.isNullOrEmpty(finalCode) ? finalCode : result.getCode();
            regManager.incRegTimes(ip, uuidName);
            String userId = (String) result.getModels().get("userid");
            if (!Strings.isNullOrEmpty(userId) && AccountDomainEnum.getAccountDomain(userId) != AccountDomainEnum.OTHER) {
                if (result.isSuccess()) {
                    secureManager.logActionRecord(userId, clientId, AccountModuleEnum.LOGIN, ip, null);
                }
            }
            //用户注册log
            UserOperationLog userOperationLog = new UserOperationLog(regParams.getUsername(), request.getRequestURI(), clientId + "", logCode, getIp(request));
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            UserOperationLogUtil.log(userOperationLog);
        }
        response.sendRedirect(getSuccessReturnStr(regParams.getRu(), String.valueOf(result.getModels().get("sgid"))));
        return "empty";
    }

    private String buildRegErrorUrl(WapV2RegParams params, boolean hasError){
        String ru = Coder.encodeUTF8(Strings.isNullOrEmpty(params.getRu()) ? CommonConstant.DEFAULT_WAP_URL : params.getRu());
        StringBuilder urlStr = new StringBuilder();
        urlStr.append(CommonConstant.DEFAULT_WAP_INDEX_URL + "/wap2/page/reg?");
        urlStr.append("&client_id=").append(params.getClient_id());
        urlStr.append("&errorMsg=").append(params.getErrorMsg());
        urlStr.append("&hasError=").append(hasError);
        urlStr.append("&ru=").append(ru);
        urlStr.append("&skin=").append(params.getSkin());
        urlStr.append("&needCaptcha=").append(params.getNeedCaptcha());
        urlStr.append("&v=").append(params.getV());
        urlStr.append("&mobile=").append(params.getUsername());
        urlStr.append("&username=").append(params.getUsername());
        urlStr.append("&scode=").append(params.getScode());
        return urlStr.toString();
    }

    private String getSuccessReturnStr(String ru, String token) {
        String deRu = ru;
        if (deRu.contains("?")) {
            return deRu + "&sgid=" + token;
        }
        return deRu + "?sgid=" + token;
    }

    /**
     * 通过接口跳转到填写验证码和密码页面
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/wap2/r", method = RequestMethod.GET)
    public String regView(Model model, WapRegMobileCodeParams params, String scode, boolean hasError) throws Exception {
        model.addAttribute("errorMsg", params.getErrorMsg());
        model.addAttribute("hasError", hasError);
        model.addAttribute("ru", params.getRu());
        model.addAttribute("skin", params.getSkin());
        model.addAttribute("needCaptcha", params.getNeedCaptcha());
        model.addAttribute("v", params.getV());
        model.addAttribute("client_id", params.getClient_id());
        model.addAttribute("mobile", params.getMobile());
        model.addAttribute("username", params.getMobile());
        model.addAttribute("scode", scode);
        return "wap/regist_wap_setpwd";
    }

    /**
     * 通过接口跳转到reset页面
     *
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/wap2/page/reg", method = RequestMethod.GET)
    public String findResetView(Model model,WapV2RegParams params, boolean hasError) throws Exception {
        model.addAttribute("hasError", hasError);
        model.addAttribute("ru", params.getRu());
        model.addAttribute("errorMsg", params.getErrorMsg());
        model.addAttribute("client_id", params.getClient_id());
        model.addAttribute("scode", params.getScode());
        model.addAttribute("v", params.getV());
        model.addAttribute("skin", params.getSkin());
        model.addAttribute("needCaptcha", params.getNeedCaptcha());
        model.addAttribute("mobile", params.getUsername());
        model.addAttribute("username", params.getUsername());
        return "/wap/regist_wap_setpwd";
    }

    private BaseMoblieApiParams buildProxyApiParams(int clientId, String mobile) {
        BaseMoblieApiParams baseMoblieApiParams = new BaseMoblieApiParams();
        baseMoblieApiParams.setMobile(mobile);
        baseMoblieApiParams.setClient_id(clientId);
        return baseMoblieApiParams;
    }

    private void buildModuleReturnStr(boolean hasError, String ru, String errorMsg, String client_id, String skin, String v, boolean needCaptcha, Model model) {
        model.addAttribute("errorMsg", errorMsg);
        model.addAttribute("hasError", hasError);
        model.addAttribute("ru", Strings.isNullOrEmpty(ru) ? Coder.encodeUTF8(CommonConstant.DEFAULT_WAP_URL) : Coder.encodeUTF8(ru));
        model.addAttribute("skin", Strings.isNullOrEmpty(skin) ? WapConstant.WAP_SKIN_GREEN : skin);
        model.addAttribute("needCaptcha", needCaptcha);
        model.addAttribute("v", Strings.isNullOrEmpty(v) ? WapConstant.WAP_COLOR : v);
        model.addAttribute("client_id", client_id);
    }

    public static void setSgidCookie(HttpServletResponse response, String sgid) {
        //种cookie
        ServletUtil.setCookie(response, LoginConstant.COOKIE_SGID, sgid, (int) DateAndNumTimesConstant.SIX_MONTH, CommonConstant.SOGOU_ROOT_DOMAIN);
        //防止wap登录时，同时有ppinf存在的时候，会导致双重登录问题。 所以生成sgid的时候，就把ppinf去掉
        ServletUtil.clearCookie(response, LoginConstant.COOKIE_PPINF);
        ServletUtil.clearCookie(response, LoginConstant.COOKIE_PPRDIG);
        ServletUtil.clearCookie(response, LoginConstant.COOKIE_PASSPORT);
        ServletUtil.clearCookie(response, LoginConstant.COOKIE_PPINFO);
    }
}
