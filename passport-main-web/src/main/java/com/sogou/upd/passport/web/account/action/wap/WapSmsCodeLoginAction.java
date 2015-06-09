package com.sogou.upd.passport.web.account.action.wap;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.WapConstant;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.SmsCodeLoginManager;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.account.form.WapIndexParams;
import com.sogou.upd.passport.web.account.form.wap.WapSmsCodeLoginParams;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 短信登录
 * User: chengang
 * Date: 15-6-8
 * Time: 下午3:36
 */
@Controller
@RequestMapping(value = "/wap")
public class WapSmsCodeLoginAction extends WapV2BaseController {

    @Autowired
    private ConfigureManager configureManager;
    @Autowired
    private CommonManager commonManager;
    @Autowired
    private SmsCodeLoginManager smsCodeLoginManager;

    @RequestMapping(value = "/smsCodeLogin/index")
    public String smsCodeLoginIndex(HttpServletRequest request, HttpServletResponse response, Model model, WapIndexParams wapIndexParams) throws Exception {
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(wapIndexParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            String ru = wapIndexParams.getRu();
            if (validateResult.contains("域名不正确")) {  // TODO 最好是在RuValidator统一修改
                ru = CommonConstant.DEFAULT_WAP_URL;
            }
            response.sendRedirect(getIndexErrorReturnStr(ru, result.getMessage()));
            return "empty";
        }

        model.addAttribute("v", wapIndexParams.getV());
        model.addAttribute("ru", wapIndexParams.getRu());
        model.addAttribute("client_id", wapIndexParams.getClient_id());
        model.addAttribute("errorMsg", wapIndexParams.getErrorMsg());
        model.addAttribute("isNeedCaptcha", wapIndexParams.getNeedCaptcha());
        model.addAttribute("skin", wapIndexParams.getSkin());
        //生成token
        String token = RandomStringUtils.randomAlphanumeric(48);
        model.addAttribute("token", token);

        if (WapConstant.WAP_SIMPLE.equals(wapIndexParams.getV())) {
            response.setHeader("Content-Type", "text/vnd.wap.wml;charset=utf-8");
            return "wap/index_simple";
        } else if (WapConstant.WAP_TOUCH.equals(wapIndexParams.getV())) {
            return "wap/index_smscode_login_wap";
        } else {
            if (!Strings.isNullOrEmpty(wapIndexParams.getErrorMsg())) {
                model.addAttribute("hasError", true);
            } else {
                model.addAttribute("hasError", false);
            }
            model.addAttribute("ru", Strings.isNullOrEmpty(wapIndexParams.getRu()) ? Coder.encodeUTF8(CommonConstant.DEFAULT_WAP_URL) : Coder.encodeUTF8(wapIndexParams.getRu()));
            if (wapIndexParams.getNeedCaptcha() == 1) {
                model.addAttribute("isNeedCaptcha", 1);
                model.addAttribute("captchaUrl", CommonConstant.DEFAULT_WAP_INDEX_URL + "/captcha?token=" + token);
            }
            model.addAttribute("username", wapIndexParams.getUsername());
            return "wap/login_wap";
        }

    }

    @RequestMapping(value = "/smsCode/login", method = RequestMethod.POST)
    public String smsCodeLogin() throws Exception {
        Result result = new APIResultSupport(false);

        return "";
    }


    @RequestMapping(value = "/smsCodeLogin/sendSmsCode", method = RequestMethod.POST)
    public String sendSmsCode(HttpServletRequest request, HttpServletResponse response, WapSmsCodeLoginParams reqParams, Model model) throws Exception {
        Result result = new APIResultSupport(false);

        return "empty";


    }

    private String getIndexErrorReturnStr(String ru, String errorMsg) {
        if (!Strings.isNullOrEmpty(ru)) {
            return (ru + "?errorMsg=" + Coder.encodeUTF8(errorMsg));
        }
        return WapConstant.WAP_INDEX + "?errorMsg=" + Coder.encodeUTF8(errorMsg);
    }


}
