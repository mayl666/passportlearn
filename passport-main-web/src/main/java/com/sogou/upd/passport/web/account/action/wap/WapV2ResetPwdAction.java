package com.sogou.upd.passport.web.account.action.wap;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.ResetPwdManager;
import com.sogou.upd.passport.manager.account.WapResetPwdManager;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.account.form.wap.WapPwdParams;
import com.sogou.upd.passport.web.account.form.wap.WapRegMobileCodeParams;
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
 * wap2.0找回密码
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-9-12
 * Time: 上午10:50
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class WapV2ResetPwdAction extends WapV2BaseController {

    private static final Logger logger = LoggerFactory.getLogger(WapV2ResetPwdAction.class);

    @Autowired
    private WapResetPwdManager wapRestPwdManager;
    @Autowired
    private ResetPwdManager resetPwdManager;
    @Autowired
    private ConfigureManager configureManager;
    @Autowired
    private CommonManager commonManager;

    private static String FINDPWD_REDIRECT_URL = CommonConstant.DEFAULT_WAP_INDEX_URL + "/wap2/f";

    /**
     * 找回密码，发送短信验证码至绑定手机
     *
     * @param reqParams@return
     * @throws Exception
     */
    @RequestMapping(value = "/wap2/findpwd/sendsms", method = RequestMethod.POST)
    public String sendSmsSecMobile(HttpServletRequest request, HttpServletResponse response, WapRegMobileCodeParams reqParams, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String ip = getIp(request);
        String finalCode = null;
        String clientIdStr = reqParams.getClient_id();
        int clientId = Integer.parseInt(clientIdStr);
        String mobile = reqParams.getMobile();
        String skin = reqParams.getSkin();
        String v = reqParams.getV();
        try {
            reqParams.setRu(Coder.decodeUTF8(reqParams.getRu()));
            String ru = reqParams.getRu();
            //参数验证
            String validateResult = ControllerHelper.validateParams(reqParams);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                addReturnPageModel(model, true, ru, validateResult, clientIdStr, skin, v, 0, mobile);
                return "wap/findpwd_wap";
            }
            if (!configureManager.checkAppIsExist(clientId)) {
                result.setCode(ErrorUtil.INVALID_CLIENTID);
                addReturnPageModel(model, true, ru, result.getMessage(), clientIdStr, skin, v, 0, mobile);
                return "wap/findpwd_wap";
            }
            //校验用户ip是否中了黑名单
            result = commonManager.checkMobileSendSMSInBlackList(ip, clientIdStr);
            if (!result.isSuccess()) {
                finalCode = ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST;
                result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_SMSCODE_SEND);
                addReturnPageModel(model, true, ru, result.getMessage(), clientIdStr, skin, v, 0, mobile);
                return "wap/findpwd_wap";
            }
            result = wapRestPwdManager.sendMobileCaptcha(mobile, clientIdStr, reqParams.getToken(), reqParams.getCaptcha());
            if (!result.isSuccess()) {
                if (ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED.equals(result.getCode())
                        || ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_NEED_CODE.equals(result.getCode())) {
                    String token = String.valueOf(result.getModels().get("token"));
                    result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_CAPTCHA_CODE_FAILED);
                    addReturnPageModel(model, true, ru, result.getMessage(), clientIdStr, skin, v, 1, mobile);
                    model.addAttribute("token", token);
                    model.addAttribute("captchaUrl", CommonConstant.DEFAULT_WAP_INDEX_URL + "/captcha?token=" + token);
                    return "wap/findpwd_wap";
                } else {
                    addReturnPageModel(model, true, ru, result.getMessage(), clientIdStr, skin, v, 0, mobile);
                    return "wap/findpwd_wap";
                }
            }
            addReturnPageModel(model, true, ru, result.getMessage(), clientIdStr, skin, v, 0, mobile);
//            if (!result.isSuccess()) {
//                return "wap/findpwd_wap";
//            }
        } catch (Exception e) {
            logger.error("wap2.0 reguser:User Register Is Failed,mobile is " + mobile, e);
        } finally {
            String logCode = !Strings.isNullOrEmpty(finalCode) ? finalCode : result.getCode();
            log(request, mobile, clientIdStr, logCode);
        }
        commonManager.incSendTimesForMobile(ip);
        commonManager.incSendTimesForMobile(mobile);
        String scode = commonManager.getSecureCode(mobile, clientId, CacheConstant.CACHE_PREFIX_PASSPORTID_PASSPORTID_SECURECODE);
//        String rediectUrl = buildSuccessSendRedirectUrl(FINDPWD_REDIRECT_URL, reqParams, scode);
        String rediectUrl = buildSuccessRedirectUrl(FINDPWD_REDIRECT_URL, reqParams.getRu(), clientIdStr, skin, v, reqParams.getNeedCaptcha(), mobile, scode);
        response.sendRedirect(rediectUrl);
        return "empty";
    }

    /**
     * 重设密码
     *
     * @param request
     * @param reqParams
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/wap2/findpwd/reset", method = RequestMethod.POST)
    public String resetPwd(HttpServletRequest request, HttpServletResponse response, WapPwdParams reqParams, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        String clientIdStr = reqParams.getClient_id();
        String username = reqParams.getUsername();
        String skin = reqParams.getSkin();
        String v = reqParams.getV();
        String scode = reqParams.getScode();
        try {
            reqParams.setRu(Coder.decodeUTF8(reqParams.getRu()));
            String ru = reqParams.getRu();
            String validateResult = ControllerHelper.validateParams(reqParams);
            if (!Strings.isNullOrEmpty(validateResult) || Strings.isNullOrEmpty(reqParams.getCaptcha())) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
//                buildErrorUrl(true, ru, Strings.isNullOrEmpty(validateResult) ? "短信验证码不能为空" : validateResult,
//                        clientIdStr, skin, v, false,username, scode);
//                response.sendRedirect(CommonConstant.DEFAULT_WAP_INDEX_URL + "/wap2/findpwd/page/reset");

                String errorMsg = Strings.isNullOrEmpty(validateResult) ? "短信验证码不能为空" : validateResult;
                String redirectUrl = buildErrorRedirectUrl(FINDPWD_REDIRECT_URL, ru, errorMsg, clientIdStr, skin, v, username, scode);
                response.sendRedirect(redirectUrl);
                return "empty";
            }
            if (!PhoneUtil.verifyPhoneNumberFormat(reqParams.getUsername())) {
//                buildErrorUrl(true, ru, ErrorUtil.getERR_CODE_MSG(ErrorUtil.ERR_CODE_ACCOUNT_PHONEERROR),
//                        clientIdStr, skin, v, false,username, scode);
//                response.sendRedirect(CommonConstant.DEFAULT_WAP_INDEX_URL + "/wap2/findpwd/page/reset");

                String errorMsg = ErrorUtil.getERR_CODE_MSG(ErrorUtil.ERR_CODE_ACCOUNT_PHONEERROR);
                String redirectUrl = buildErrorRedirectUrl(FINDPWD_REDIRECT_URL, ru, errorMsg, clientIdStr, skin, v, username, scode);
                response.sendRedirect(redirectUrl);
                return "empty";
            }
            int clientId = Integer.parseInt(clientIdStr);
            String password = reqParams.getPassword();
            result = wapRestPwdManager.checkMobileCodeResetPwd(reqParams.getUsername(), clientId, reqParams.getCaptcha(), false);
            if (!result.isSuccess()) {
//                buildErrorUrl(true, ru, ErrorUtil.getERR_CODE_MSG(result.getCode()),
//                        clientIdStr, skin, v, false,username, scode);
//                response.sendRedirect(CommonConstant.DEFAULT_WAP_INDEX_URL + "/wap2/findpwd/page/reset");

                String redirectUrl = buildErrorRedirectUrl(FINDPWD_REDIRECT_URL, ru, result.getMessage(), clientIdStr, skin, v, username, scode);
                response.sendRedirect(redirectUrl);
                return "empty";
            }
            String passportId = String.valueOf(result.getModels().get("userid"));
            result = resetPwdManager.resetPasswordByScode(passportId, clientId, password, scode, getIp(request));
            if (!result.isSuccess()) {
                scode = commonManager.getSecureCode(String.valueOf(result.getModels().get("userid")), clientId, CacheConstant.CACHE_PREFIX_PASSPORTID_RESETPWDSECURECODE);
//                buildErrorUrl(true, ru, ErrorUtil.getERR_CODE_MSG(result.getCode()),
//                        clientIdStr, skin, v, false,username, scode);
//                response.sendRedirect(CommonConstant.DEFAULT_WAP_INDEX_URL + "/wap2/findpwd/page/reset");

                String redirectUrl = buildErrorRedirectUrl(FINDPWD_REDIRECT_URL, ru, result.getMessage(), clientIdStr, skin, v, username, scode);
                response.sendRedirect(redirectUrl);
                return "empty";
            }
        } catch (Exception e) {
            logger.error("resetPwd Is Failed,Mobile is " + username, e);
        } finally {
            log(request, username, clientIdStr, result.getCode());
        }
        return "redirect:" + reqParams.getRu();
    }

    /**
     * 通过接口跳转到填写验证码和密码页面
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/wap2/f", method = RequestMethod.GET)
    public String regView(Model model, boolean hasError, String ru, String errorMsg, String clientId,
                          String skin, String v, int needCaptcha, String mobile, String scode) throws Exception {
        addRedirectPageModule(model, hasError, ru, errorMsg, clientId, skin, v, needCaptcha, mobile, scode);
        return "wap/findpwd_wap_setpwd";
    }

//    /**
//     * 通过接口跳转到reset页面
//     *
//     * @param model
//     * @return
//     * @throws Exception
//     */
//    @RequestMapping(value = "/wap2/findpwd/page/reset", method = RequestMethod.GET)
//    public String findResetView(Model model) throws Exception {
//        model.addAttribute("hasError", resetParamsMap.get("hasError"));
//        model.addAttribute("ru", resetParamsMap.get("ru"));
//        model.addAttribute("errorMsg", resetParamsMap.get("errorMsg"));
//        model.addAttribute("client_id", resetParamsMap.get("client_id"));
//        model.addAttribute("scode", resetParamsMap.get("scode"));
//        model.addAttribute("v", WapConstant.WAP_TOUCH);
//        model.addAttribute("skin", resetParamsMap.get("skin"));
//        model.addAttribute("needCaptcha", resetParamsMap.get("needCaptcha"));
//        model.addAttribute("mobile", resetParamsMap.get("mobile"));
//        model.addAttribute("username", resetParamsMap.get("mobile"));
//        return "/wap/findpwd_wap_setpwd";
//    }

//    /**
//     * 构造返回错误里的跳转链接
//     */
//    private Map<String, Object> buildErrorUrl(boolean hasError, String ru, String errorMsg, String client_id,
//                                              String skin, String v, boolean needCaptcha, String mobile, String scode) {
//        resetParamsMap.put("client_id", client_id);
//        resetParamsMap.put("errorMsg", errorMsg);
//        resetParamsMap.put("hasError", hasError);
//        resetParamsMap.put("ru", Coder.encodeUTF8(ru));
//        resetParamsMap.put("skin", skin);
//        resetParamsMap.put("needCaptcha", needCaptcha);
//        resetParamsMap.put("v", v);
//        resetParamsMap.put("mobile", mobile);
//        resetParamsMap.put("scode", scode);
//        resetParamsMap.put("username", mobile);
//        return resetParamsMap;
//    }
}
