package com.sogou.upd.passport.web.account.action.wap;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.math.Coder;
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
import com.sogou.upd.passport.web.ControllerHelper;
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

/**
 * wap2.0注册
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-9-11
 * Time: 下午5:35
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class WapV2RegAction extends WapV2BaseController {

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

    private static String REG_REDIRECT_URL = CommonConstant.DEFAULT_WAP_INDEX_URL + "/wap2/r";

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
        String ru = reqParams.getRu();
        try {
            ru = Coder.decodeUTF8(ru);
            //参数验证
            String validateResult = ControllerHelper.validateParams(reqParams);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                addReturnPageModel(model, true, ru, validateResult, clientIdStr, skin, v, false, mobile);
                return "wap/regist_wap";
            }
            //检查client_id是否存在
            if (!configureManager.checkAppIsExist(clientId)) {
                result.setCode(ErrorUtil.INVALID_CLIENTID);
                addReturnPageModel(model, true, ru, result.getMessage(), clientIdStr, skin, v, false, mobile);
                return "wap/regist_wap";
            }
            //第二次弹出验证码
            result = commonManager.checkMobileSendSMSInBlackList(mobile, clientIdStr);
            if (!result.isSuccess()) {
                if (!Strings.isNullOrEmpty(reqParams.getToken()) && !Strings.isNullOrEmpty(reqParams.getCaptcha())) {
                    result = regManager.checkCaptchaToken(reqParams.getToken(), reqParams.getCaptcha());
                    //如果验证码校验失败，则提示
                    if (!result.isSuccess()) {
                        result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
                        addReturnPageModel(model, true, ru, result.getMessage(), clientIdStr, skin, v, true, mobile);
                        String token = RandomStringUtils.randomAlphanumeric(48);
                        model.addAttribute("token", token);
                        model.addAttribute("captchaUrl", CommonConstant.DEFAULT_WAP_INDEX_URL + "/captcha?token=" + token);
                        return "wap/regist_wap";
                    }
                } else {
                    //需要弹出验证码
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_NEED_CODE);
                    addReturnPageModel(model, true, ru, result.getMessage(), clientIdStr, skin, v, true, mobile);
                    String token = RandomStringUtils.randomAlphanumeric(48);
                    model.addAttribute("token", token);
                    model.addAttribute("captchaUrl", CommonConstant.DEFAULT_WAP_INDEX_URL + "/captcha?token=" + token);
                    return "wap/regist_wap";
                }
            }
            //校验用户ip是否中了黑名单
            result = commonManager.checkMobileSendSMSInBlackList(ip, clientIdStr);
            if (!result.isSuccess()) {
                finalCode = ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST;
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND);
                addReturnPageModel(model, true, ru, result.getMessage(), clientIdStr, skin, v, false, mobile);
                return "wap/regist_wap";
            }
            BaseMoblieApiParams baseMobileApiParams = buildProxyApiParams(clientId, mobile);
            result = sgRegisterApiManager.sendMobileRegCaptcha(baseMobileApiParams);
            addReturnPageModel(model, true, ru, result.getMessage(),clientIdStr, skin, v, false, mobile);
            if (!result.isSuccess()) {
                return "wap/regist_wap";
            }
        } catch (Exception e) {
            logger.error("wap2.0 reguser:User Register Is Failed,mobile is " + mobile, e);
        } finally {
            String logCode = !Strings.isNullOrEmpty(finalCode) ? finalCode : result.getCode();
            log(request, mobile, clientIdStr, logCode);
        }
        commonManager.incSendTimesForMobile(ip);
        commonManager.incSendTimesForMobile(mobile);
        String scode = commonManager.getSecureCode(mobile, clientId, CacheConstant.CACHE_PREFIX_PASSPORTID_PASSPORTID_SECURECODE);

        boolean needCaptcha = reqParams.getNeedCaptcha() == 0 ? false : true;
        String rediectUrl = buildSuccessRedirectUrl(REG_REDIRECT_URL, ru, clientIdStr, skin, v, needCaptcha, mobile, scode);
//        String rediectUrl = buildSuccessSendRedirectUrl(REG_REDIRECT_URL, reqParams, scode);
        response.sendRedirect(rediectUrl);
        return "empty";
    }


    @RequestMapping(value = "/wap2/reguser", method = RequestMethod.POST)
    public String regV2User(HttpServletRequest request, HttpServletResponse response, WapV2RegParams regParams, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String ip = null;
        String uuidName = null;
        String finalCode = null;
        int clientId = regParams.getClient_id();
        String clientIdStr = String.valueOf(clientId);
        String username = regParams.getUsername();
        String ru = regParams.getRu();
        String skin = regParams.getSkin();
        String v = regParams.getV();
        String scode = regParams.getScode();
        try {
            ru = Coder.decodeUTF8(ru);
            //参数验证
            String validateResult = ControllerHelper.validateParams(regParams);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
//                response.sendRedirect(buildRegErrorUrl(regParams));
                String redirectUrl = buildErrorRedirectUrl(REG_REDIRECT_URL, ru, validateResult,clientIdStr, skin, v, username, scode);
                response.sendRedirect(redirectUrl);
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
//                response.sendRedirect(buildRegErrorUrl(regParams));
                String redirectUrl = buildErrorRedirectUrl(REG_REDIRECT_URL, ru, result.getMessage(),clientIdStr, skin, v, username, scode);
                response.sendRedirect(redirectUrl);
                return "empty";
            }
            if (!PhoneUtil.verifyPhoneNumberFormat(username)) {
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PHONEERROR);
//                response.sendRedirect(buildRegErrorUrl(regParams));
                String redirectUrl = buildErrorRedirectUrl(REG_REDIRECT_URL, ru, result.getMessage(),clientIdStr, skin, v, username, scode);
                response.sendRedirect(redirectUrl);
                return "empty";
            }
            //校验安全码
            if (!commonManager.checkSecureCode(username, clientId, scode, CacheConstant.CACHE_PREFIX_PASSPORTID_PASSPORTID_SECURECODE)) {
                result.setCode(ErrorUtil.ERR_CODE_FINDPWD_SCODE_FAILED);
//                response.sendRedirect(buildRegErrorUrl(regParams));
                String redirectUrl = buildErrorRedirectUrl(REG_REDIRECT_URL, ru, result.getMessage(),clientIdStr, skin, v, username, scode);
                response.sendRedirect(redirectUrl);
                return "empty";
            }
            result = regManager.registerMobile(username, regParams.getPassword(), clientId, regParams.getCaptcha(), null);
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
                scode = commonManager.getSecureCode(username,
                        clientId, CacheConstant.CACHE_PREFIX_PASSPORTID_PASSPORTID_SECURECODE);
//                response.sendRedirect(buildRegErrorUrl(regParams));
                String redirectUrl = buildErrorRedirectUrl(REG_REDIRECT_URL, ru, result.getMessage(),clientIdStr, skin, v, username, scode);
                response.sendRedirect(redirectUrl);
                return "empty";
            }
        } catch (Exception e) {
            logger.error("wap2 reguser:User Register Is Failed,Username is " + username, e);
        } finally {

            regManager.incRegTimes(ip, uuidName);
            String userId = (String) result.getModels().get("userid");
            if (!Strings.isNullOrEmpty(userId) && AccountDomainEnum.getAccountDomain(userId) != AccountDomainEnum.OTHER) {
                if (result.isSuccess()) {
                    secureManager.logActionRecord(userId, clientId, AccountModuleEnum.LOGIN, ip, null);
                }
            }
            String logCode = !Strings.isNullOrEmpty(finalCode) ? finalCode : result.getCode();
            log(request, username, String.valueOf(clientId), logCode);
        }
        response.sendRedirect(getSuccessReturnStr(regParams.getRu(), String.valueOf(result.getModels().get("sgid"))));
        return "empty";
    }

//    private String buildRegErrorUrl(WapV2RegParams params) {
//        String ru = Coder.encodeUTF8(Strings.isNullOrEmpty(params.getRu()) ? CommonConstant.DEFAULT_WAP_URL : params.getRu());
//        String errorMsg = Strings.isNullOrEmpty(params.getErrorMsg()) ? "" : Coder.encodeUTF8(params.getErrorMsg());
//        StringBuilder urlStr = new StringBuilder();
//        urlStr.append(REG_REDIRECT_URL).append("?");
//        urlStr.append("client_id=").append(params.getClient_id());
//        urlStr.append("&errorMsg=").append(errorMsg);
//        urlStr.append("&hasError=").append(true);
//        urlStr.append("&ru=").append(ru);
//        urlStr.append("&skin=").append(params.getSkin());
//        urlStr.append("&needCaptcha=").append(params.getNeedCaptcha());
//        urlStr.append("&v=").append(params.getV());
//        urlStr.append("&mobile=").append(params.getUsername());
//        urlStr.append("&username=").append(params.getUsername());
//        urlStr.append("&scode=").append(params.getScode());
//        return urlStr.toString();
//    }

    private String getSuccessReturnStr(String ru, String token) {
        String deRu = ru;
        if (deRu.contains("?")) {
            return deRu + "&sgid=" + token;
        }
        return deRu + "?sgid=" + token;
    }

    /**
     * 通过接口跳转到填写验证码和密码页面 /wap2/r
     * 通过接口跳转到reset页面 /wap2/page/reg
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/wap2/r", method = RequestMethod.GET)
    public String regView(Model model, WapRegMobileCodeParams params, String scode, boolean hasError) throws Exception {
        addRedirectPageModule(model, params, scode, hasError);
        return "wap/regist_wap_setpwd";
    }

    private BaseMoblieApiParams buildProxyApiParams(int clientId, String mobile) {
        BaseMoblieApiParams baseMoblieApiParams = new BaseMoblieApiParams();
        baseMoblieApiParams.setMobile(mobile);
        baseMoblieApiParams.setClient_id(clientId);
        return baseMoblieApiParams;
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
