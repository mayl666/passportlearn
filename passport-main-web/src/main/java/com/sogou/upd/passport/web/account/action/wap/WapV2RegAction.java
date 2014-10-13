package com.sogou.upd.passport.web.account.action.wap;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
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
import java.util.Map;

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

    private static Map<String, Object> params = Maps.newHashMap();

    private static Map<String, Object> regParams = Maps.newHashMap();


    @RequestMapping(value = "/wap2/sendsms", method = RequestMethod.POST)
    public String sendsms(HttpServletRequest request, HttpServletResponse response, WapRegMobileCodeParams reqParams, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String ip = getIp(request);
        String finalCode = null;
        try {
            reqParams.setRu(Coder.decodeUTF8(reqParams.getRu()));
            //参数验证
            String validateResult = ControllerHelper.validateParams(reqParams);
            if (!Strings.isNullOrEmpty(validateResult)) {
                buildModuleReturnStr(true, reqParams.getRu(), validateResult,
                        reqParams.getClient_id(), reqParams.getSkin(), reqParams.getV(), false, model);
                model.addAttribute("mobile", reqParams.getMobile());
                model.addAttribute("username", reqParams.getMobile());
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                return "wap/regist_wap";
            }
            //验证client_id
            int clientId = Integer.parseInt(reqParams.getClient_id());
            //检查client_id是否存在
            if (!configureManager.checkAppIsExist(clientId)) {
                buildModuleReturnStr(true, reqParams.getRu(), ErrorUtil.getERR_CODE_MSG(ErrorUtil.INVALID_CLIENTID),
                        reqParams.getClient_id(), reqParams.getSkin(), reqParams.getV(), false, model);
                model.addAttribute("mobile", reqParams.getMobile());
                model.addAttribute("username", reqParams.getMobile());
                result.setCode(ErrorUtil.INVALID_CLIENTID);
                return "wap/regist_wap";
            }
            String mobile = reqParams.getMobile();
            //第二次弹出验证码
            result = commonManager.checkMobileSendSMSInBlackList(mobile, reqParams.getClient_id());
            if (!result.isSuccess()) {
                if (!Strings.isNullOrEmpty(reqParams.getToken()) && !Strings.isNullOrEmpty(reqParams.getCaptcha())) {
                    result = regManager.checkCaptchaToken(reqParams.getToken(), reqParams.getCaptcha());
                    //如果验证码校验失败，则提示
                    if (!result.isSuccess()) {
                        buildModuleReturnStr(true, reqParams.getRu(), ErrorUtil.getERR_CODE_MSG(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED),
                                reqParams.getClient_id(), reqParams.getSkin(), reqParams.getV(), true, model);
                        String token = RandomStringUtils.randomAlphanumeric(48);
                        model.addAttribute("mobile", reqParams.getMobile());
                        model.addAttribute("username", reqParams.getMobile());
                        model.addAttribute("token", token);
                        model.addAttribute("captchaUrl", CommonConstant.DEFAULT_WAP_INDEX_URL + "/captcha?token=" + token);
                        result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
                        return "wap/regist_wap";
                    }
                } else {
                    //需要弹出验证码
                    buildModuleReturnStr(true, reqParams.getRu(), ErrorUtil.getERR_CODE_MSG(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_NEED_CODE),
                            reqParams.getClient_id(), reqParams.getSkin(), reqParams.getV(), true, model);
                    String token = RandomStringUtils.randomAlphanumeric(48);
                    model.addAttribute("mobile", reqParams.getMobile());
                    model.addAttribute("username", reqParams.getMobile());
                    model.addAttribute("token", token);
                    model.addAttribute("captchaUrl", CommonConstant.DEFAULT_WAP_INDEX_URL + "/captcha?token=" + token);
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_NEED_CODE);
                    return "wap/regist_wap";
                }
            }
            //校验用户ip是否中了黑名单
            result = commonManager.checkMobileSendSMSInBlackList(ip, reqParams.getClient_id());
            if (!result.isSuccess()) {
                finalCode = ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST;
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND);
                buildModuleReturnStr(true, reqParams.getRu(), ErrorUtil.getERR_CODE_MSG(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND),
                        reqParams.getClient_id(), reqParams.getSkin(), reqParams.getV(), false, model);
                model.addAttribute("mobile", reqParams.getMobile());
                model.addAttribute("username", reqParams.getMobile());
                return "wap/regist_wap";
            }
            BaseMoblieApiParams baseMobileApiParams = buildProxyApiParams(clientId, mobile);
            result = sgRegisterApiManager.sendMobileRegCaptcha(baseMobileApiParams);
            buildModuleReturnStr(true, reqParams.getRu(), ErrorUtil.getERR_CODE_MSG(result.getCode()),
                    reqParams.getClient_id(), reqParams.getSkin(), reqParams.getV(), false, model);
            model.addAttribute("mobile", reqParams.getMobile());
            if (!result.isSuccess()) {
                model.addAttribute("username", reqParams.getMobile());
                return "wap/regist_wap";
            }
        } catch (Exception e) {
            logger.error("wap2.0 reguser:User Register Is Failed,mobile is " + reqParams.getMobile(), e);
        } finally {
            String logCode;
            if (!Strings.isNullOrEmpty(finalCode)) {
                logCode = finalCode;
            } else {
                logCode = result.getCode();
            }
            UserOperationLog userOperationLog = new UserOperationLog(reqParams.getMobile(), request.getRequestURI(), reqParams.getClient_id(), logCode, ip);
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            UserOperationLogUtil.log(userOperationLog);
        }
        commonManager.incSendTimesForMobile(ip);
        commonManager.incSendTimesForMobile(reqParams.getMobile());
        buildSendRedirectUrl(Strings.isNullOrEmpty(reqParams.getRu()) ? Coder.encodeUTF8(CommonConstant.DEFAULT_WAP_URL) : Coder.encodeUTF8(reqParams.getRu()),
                reqParams.getClient_id(), false, reqParams.getMobile(), Strings.isNullOrEmpty(reqParams.getSkin()) ? WapConstant.WAP_SKIN_GREEN : reqParams.getSkin(),
                false, Strings.isNullOrEmpty(reqParams.getV()) ? WapConstant.WAP_COLOR : reqParams.getV(), null);
        params.put("scode", commonManager.getSecureCode(reqParams.getMobile(), Integer.parseInt(reqParams.getClient_id()), CacheConstant.CACHE_PREFIX_PASSPORTID_PASSPORTID_SECURECODE));
        response.sendRedirect(CommonConstant.DEFAULT_WAP_INDEX_URL + "/wap2/r");
        return "empty";
    }

    @RequestMapping(value = "/wap2/reguser", method = RequestMethod.POST)
    public String regV2User(HttpServletRequest request, HttpServletResponse response, WapV2RegParams regParams, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String ip = null;
        String uuidName = null;
        String finalCode = null;
        try {
            regParams.setRu(Coder.decodeUTF8(regParams.getRu()));
            //参数验证
            String validateResult = ControllerHelper.validateParams(regParams);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                buildErrorUrl(true, regParams.getRu(), validateResult,
                        String.valueOf(regParams.getClient_id()), regParams.getSkin(), regParams.getV(), false, regParams.getUsername(), regParams.getScode());
                response.sendRedirect(CommonConstant.DEFAULT_WAP_INDEX_URL + "/wap2/page/reg");
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
                buildErrorUrl(true, regParams.getRu(), ErrorUtil.getERR_CODE_MSG(result.getCode()),
                        String.valueOf(regParams.getClient_id()), regParams.getSkin(), regParams.getV(), false, regParams.getUsername(), regParams.getScode());
                response.sendRedirect(CommonConstant.DEFAULT_WAP_INDEX_URL + "/wap2/page/reg");
                return "empty";
            }
            if (!PhoneUtil.verifyPhoneNumberFormat(regParams.getUsername())) {
                buildErrorUrl(true, regParams.getRu(), ErrorUtil.getERR_CODE_MSG(ErrorUtil.ERR_CODE_ACCOUNT_PHONEERROR),
                        String.valueOf(regParams.getClient_id()), regParams.getSkin(), regParams.getV(), false, regParams.getUsername(), regParams.getScode());
                response.sendRedirect(CommonConstant.DEFAULT_WAP_INDEX_URL + "/wap2/page/reg");
                return "empty";
            }
            //校验安全码
            if (!commonManager.checkSecureCode(regParams.getUsername(), regParams.getClient_id(), regParams.getScode(), CacheConstant.CACHE_PREFIX_PASSPORTID_PASSPORTID_SECURECODE)) {
                result.setCode(ErrorUtil.ERR_CODE_FINDPWD_SCODE_FAILED);
                buildErrorUrl(true, regParams.getRu(), ErrorUtil.getERR_CODE_MSG(ErrorUtil.ERR_CODE_FINDPWD_SCODE_FAILED),
                        String.valueOf(regParams.getClient_id()), regParams.getSkin(), regParams.getV(), false, regParams.getUsername(), regParams.getScode());
                response.sendRedirect(CommonConstant.DEFAULT_WAP_INDEX_URL + "/wap2/page/reg");
                return "empty";
            }
            result = regManager.registerMobile(regParams.getUsername(), regParams.getPassword(), regParams.getClient_id(), regParams.getCaptcha(), null);
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
                        regParams.getClient_id(), CacheConstant.CACHE_PREFIX_PASSPORTID_PASSPORTID_SECURECODE);
                buildErrorUrl(true, regParams.getRu(), ErrorUtil.getERR_CODE_MSG(result.getCode()),
                        String.valueOf(regParams.getClient_id()), regParams.getSkin(), regParams.getV(), false, regParams.getUsername(), scode);
                response.sendRedirect(CommonConstant.DEFAULT_WAP_INDEX_URL + "/wap2/page/reg");
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
                    int clientId = regParams.getClient_id();
                    secureManager.logActionRecord(userId, clientId, AccountModuleEnum.LOGIN, ip, null);
                }
            }
            //用户注册log
            UserOperationLog userOperationLog = new UserOperationLog(regParams.getUsername(), request.getRequestURI(), regParams.getClient_id() + "", logCode, getIp(request));
            String referer = request.getHeader("referer");
            userOperationLog.putOtherMessage("ref", referer);
            UserOperationLogUtil.log(userOperationLog);
        }
        response.sendRedirect(getSuccessReturnStr(regParams.getRu(), String.valueOf(result.getModels().get("sgid"))));
        return "empty";
    }

    private String getSuccessReturnStr(String ru, String token) {
        String deRu = ru;
        if (deRu.contains("?")) {
            return deRu + "&sgid=" + token;
        }
        return deRu + "?sgid=" + token;
    }

    /**
     * 获取短信验证码校验通过后，需要跳转到一个接口，避免用户刷新导致页面不可用
     */
    private Map<String, Object> buildSendRedirectUrl(String ru, String client_id, boolean hasError, String mobile, String
            skin, boolean needCaptcha, String v, String errorMsg) {
        params.put("client_id", client_id);
        params.put("errorMsg", errorMsg);
        params.put("hasError", hasError);
        params.put("ru", ru);
        params.put("skin", skin);
        params.put("needCaptcha", needCaptcha);
        params.put("v", v);
        params.put("mobile", mobile);
        params.put("username", mobile);
        return params;
    }

    /**
     * 通过接口跳转到填写验证码和密码页面
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/wap2/r", method = RequestMethod.GET)
    public String regView(Model model) throws Exception {
        model.addAttribute("errorMsg", params.get("errorMsg"));
        model.addAttribute("hasError", params.get("hasError"));
        model.addAttribute("ru", params.get("ru"));
        model.addAttribute("skin", params.get("skin"));
        model.addAttribute("needCaptcha", params.get("needCaptcha"));
        model.addAttribute("v", params.get("v"));
        model.addAttribute("client_id", params.get("client_id"));
        model.addAttribute("mobile", params.get("mobile"));
        model.addAttribute("username", params.get("username"));
        model.addAttribute("scode", params.get("scode"));
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
    public String findResetView(Model model) throws Exception {
        model.addAttribute("hasError", regParams.get("hasError"));
        model.addAttribute("ru", regParams.get("ru"));
        model.addAttribute("errorMsg", regParams.get("errorMsg"));
        model.addAttribute("client_id", regParams.get("client_id"));
        model.addAttribute("scode", regParams.get("scode"));
        model.addAttribute("v", regParams.get("v"));
        model.addAttribute("skin", regParams.get("skin"));
        model.addAttribute("needCaptcha", regParams.get("needCaptcha"));
        model.addAttribute("mobile", regParams.get("mobile"));
        model.addAttribute("username", regParams.get("mobile"));
        return "/wap/regist_wap_setpwd";
    }

    /**
     * 构造返回错误里的跳转链接
     */
    private Map<String, Object> buildErrorUrl(boolean hasError, String ru, String errorMsg, String client_id,
                                              String skin, String v, boolean needCaptcha, String mobile, String scode) {
        regParams.put("client_id", client_id);
        regParams.put("errorMsg", errorMsg);
        regParams.put("hasError", hasError);
        regParams.put("ru", Coder.encodeUTF8(ru));
        regParams.put("skin", skin);
        regParams.put("needCaptcha", needCaptcha);
        regParams.put("v", v);
        regParams.put("mobile", mobile);
        regParams.put("scode", scode);
        regParams.put("username", mobile);
        return regParams;
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
