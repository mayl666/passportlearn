package com.sogou.upd.passport.web.account.action.wap;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.WapConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.ResetPwdManager;
import com.sogou.upd.passport.manager.account.WapResetPwdManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.UserOperationLogUtil;
import com.sogou.upd.passport.web.account.form.BaseWebRuParams;
import com.sogou.upd.passport.web.account.form.FindPwdCheckSmscodeParams;
import com.sogou.upd.passport.web.account.form.MoblieCodeParams;
import com.sogou.upd.passport.web.account.form.WapIndexParams;
import com.sogou.upd.passport.web.account.form.wap.WapCheckEmailParams;
import com.sogou.upd.passport.web.account.form.wap.WapPwdParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

/**
 * wap2.0找回密码
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-9-12
 * Time: 上午10:50
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class WapV2ResetPwdAction extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(WapV2ResetPwdAction.class);

    @Autowired
    private WapResetPwdManager wapRestPwdManager;
    @Autowired
    private ResetPwdManager resetPwdManager;

    /**
     * 找回密码
     *
     * @param model
     * @param redirectAttributes
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/wap2/findpwd", method = RequestMethod.GET)
    public String findPwdView(Model model, RedirectAttributes redirectAttributes, WapIndexParams wapIndexParams) throws Exception {
        String ru = Strings.isNullOrEmpty(wapIndexParams.getRu()) ? CommonConstant.DEFAULT_WAP_URL : wapIndexParams.getRu();
        Result result = new APIResultSupport(false);
        String client_id = Strings.isNullOrEmpty(wapIndexParams.getClient_id()) ? String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID) : wapIndexParams.getClient_id();
        result.setDefaultModel("ru", ru);
        result.setDefaultModel("client_id", client_id);
        if (WapConstant.WAP_COLOR.equals(wapIndexParams.getV())) {
            model.addAttribute("client_id", client_id);
            model.addAttribute("ru", ru);
            return "wap/findpwd_wap";
        } else if (WapConstant.WAP_TOUCH.equals(wapIndexParams.getV())) {
            model.addAttribute("data", result.toString());
            return "wap/findpwd_touch";
        }
        redirectAttributes.addAttribute("ru", ru);
        return "redirect:" + SHPPUrlConstant.SOHU_WAP_FINDPWD_URL + "?ru={ru}";
    }


    /**
     * 其它方式找回时跳转到其它页面
     *
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/wap2/findpwd/customer", method = RequestMethod.GET)
    public String findPwdKefuView(Model model, BaseWebRuParams params) throws Exception {
        String ru = Strings.isNullOrEmpty(params.getRu()) ? CommonConstant.DEFAULT_WAP_URL : params.getRu();
        String client_id = Strings.isNullOrEmpty(params.getClient_id()) ? String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID) : params.getClient_id();
        model.addAttribute("ru", ru);
        model.addAttribute("client_id", client_id);
        return "/wap/findpwd_contact_touch";
    }

    /**
     * 找回密码，发送短信验证码至原绑定手机
     *
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/wap2/findpwd/sendsms", method = RequestMethod.POST)
    @ResponseBody
    public Object sendSmsSecMobile(HttpServletRequest request, MoblieCodeParams params) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                return result.toString();
            }
            result = wapRestPwdManager.sendMobileCaptcha(params.getMobile(), params.getClient_id(), params.getToken(), params.getCaptcha());
        } catch (Exception e) {
            logger.error("sendSmsSecMobile Is Failed,mobile is " + params.getMobile(), e);
        } finally {
            log(request, params.getMobile(), result.getCode());
        }
        return result.toString();
    }

    /**
     * 验证找回密码发送的手机验证码
     *
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/wap2/findpwd/checksms", method = RequestMethod.POST)
    @ResponseBody
    public Object checkSmsSecMobile(HttpServletRequest request, FindPwdCheckSmscodeParams params) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                return result.toString();
            }
            int clientId = Integer.parseInt(params.getClient_id());
            result = wapRestPwdManager.checkMobileCodeResetPwd(params.getMobile(), clientId, params.getSmscode());
            if (result.isSuccess()) {
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                result.setDefaultModel("skin", params.getSkin());
                String param = buildRedirectUrl(result);
                String url = CommonConstant.DEFAULT_WAP_INDEX_URL + param;
                result.setDefaultModel("url", url);
                return result.toString();
            }
        } catch (Exception e) {
            logger.error("checksms is failed,mobile is " + params.getMobile(), e);
        } finally {
            log(request, params.getMobile(), result.getCode());
        }
        result = setRuAndClientId(result, params.getRu(), params.getClient_id());
        return result.toString();
    }

    //手机与短信验证码验证成功后，给前端生成下一步跳转的url
    private String buildRedirectUrl(Result result) {
        StringBuilder urlStr = new StringBuilder();
        urlStr.append("/wap/findpwd/page/reset?");
        String userid = (String) result.getModels().get("userid");
        urlStr.append("username=" + userid);
        String scode = (String) result.getModels().get("scode");
        urlStr.append("&scode=" + scode);
        String client_id = (String) result.getModels().get("client_id");
        urlStr.append("&client_id=" + client_id);
        String ru = (String) result.getModels().get("ru");
        urlStr.append("&ru=" + Coder.encodeUTF8(ru));
        urlStr.append("&code=" + result.getCode());
        urlStr.append("&message=" + result.getMessage());
        urlStr.append("&v=" + WapConstant.WAP_TOUCH);
        String skin = (String) result.getModels().get("skin");
        urlStr.append("&skin=" + skin);
        return urlStr.toString();
    }

    //验证完邮件跳转至页面提示重置密码页
    private String buildSendRedirectUrl(WapCheckEmailParams params) throws UnsupportedEncodingException {
        String ru = Strings.isNullOrEmpty(params.getRu()) ? Coder.encodeUTF8(CommonConstant.DEFAULT_WAP_URL) : Coder.encodeUTF8(params.getRu());
        String client_id = Strings.isNullOrEmpty(params.getClient_id()) ? String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID) : params.getClient_id();
        StringBuilder urlStr = new StringBuilder();
        urlStr.append("/wap/findpwd/page/reset?");
        urlStr.append("username=" + params.getUsername());
        urlStr.append("&client_id=" + client_id);
        urlStr.append("&ru=" + ru);
        urlStr.append("&skin=" + params.getSkin());
        urlStr.append("&v=" + params.getV());
        return urlStr.toString();
    }

    /**
     * 通过接口跳转到reset页面
     *
     * @param ru
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/wap2/findpwd/page/reset", method = RequestMethod.GET)
    public String findResetView(String ru, Model model, String client_id, String scode, String username, String
            code, String skin) throws Exception {
        Result result = new APIResultSupport(false);
        ru = Strings.isNullOrEmpty(ru) ? Coder.encodeUTF8(CommonConstant.DEFAULT_WAP_URL) : Coder.encodeUTF8(ru);
        client_id = Strings.isNullOrEmpty(client_id) ? String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID) : client_id;
        result.setCode(code);
        result.setMessage(ErrorUtil.getERR_CODE_MSG(code));
        result.setDefaultModel("ru", ru);
        result.setDefaultModel("client_id", client_id);
        result.setDefaultModel("userid", username);
        result.setDefaultModel("scode", scode);
        result.setDefaultModel("v", WapConstant.WAP_TOUCH);
        result.setDefaultModel("skin", skin);
        model.addAttribute("data", result.toString());
        return "/wap/resetpwd_touch";
    }

    /**
     * 重设密码
     *
     * @param request
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/wap2/findpwd/reset", method = RequestMethod.POST)
    @ResponseBody
    public String resetPwd(HttpServletRequest request, WapPwdParams params) throws Exception {
        Result result = new APIResultSupport(false);
        try {
            String validateResult = ControllerHelper.validateParams(params);
            if (!Strings.isNullOrEmpty(validateResult)) {
                result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
                result.setMessage(validateResult);
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                return result.toString();
            }
            String passportId = params.getUsername();
            int clientId = Integer.parseInt(params.getClient_id());
            String password = params.getPassword();
            result = resetPwdManager.resetPasswordByScode(passportId, clientId, password, params.getScode(), getIp(request));
            if (!result.isSuccess()) {
                result = setRuAndClientId(result, params.getRu(), params.getClient_id());
                result.setDefaultModel("skin", params.getSkin());
                return result.toString();
            }
        } catch (Exception e) {
            logger.error("resetPwd Is Failed,Username is " + params.getUsername(), e);
        } finally {
            log(request, params.getUsername(), result.getCode());
        }
        result.setCode(ErrorUtil.SUCCESS);
        result = setRuAndClientId(result, params.getRu(), params.getClient_id());
        result.setDefaultModel("skin", params.getSkin());
        return result.toString();
    }


    private Result setRuAndClientId(Result result, String ru, String client_id) {
        result.setDefaultModel("ru", Strings.isNullOrEmpty(ru) ? Coder.encodeUTF8(CommonConstant.DEFAULT_WAP_URL) : Coder.encodeUTF8(ru));
        result.setDefaultModel("client_id", Strings.isNullOrEmpty(client_id) ? CommonConstant.SGPP_DEFAULT_CLIENTID : client_id);
        result.setDefaultModel("v", WapConstant.WAP_TOUCH);
        return result;
    }

    private void log(HttpServletRequest request, String passportId, String resultCode) {
        //用户登录log
        UserOperationLog userOperationLog = new UserOperationLog(passportId, request.getRequestURI(), String.valueOf(CommonConstant.SGPP_DEFAULT_CLIENTID), resultCode, getIp(request));
        userOperationLog.putOtherMessage("ref", request.getHeader("referer"));
        UserOperationLogUtil.log(userOperationLog);
    }
}
