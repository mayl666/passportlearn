package com.sogou.upd.passport.web.account.screen;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.account.form.AccountWebParams;
import com.sogou.upd.passport.web.inteceptor.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

/**
 * User: mayan
 * Date: 13-6-7 Time: 下午7:58
 * 登录注册页面跳转页
 */
@Controller
@RequestMapping("/web")
public class AccountWeb extends BaseController {

    @Autowired
    private HostHolder hostHolder;

    /*
     web邮箱注册
   */
    @RequestMapping(value = "/reg/email", method = RequestMethod.GET)
    public String regEmail(HttpServletRequest request, HttpServletResponse response, AccountWebParams webParams, Model model)
            throws Exception {
        webCookieProcess(request, response);

        Result result = new APIResultSupport(false);
        //跳转ru client_id
        result = paramProcess(result, webParams);

        if (result.isSuccess()) {
            model.addAttribute("data", result.toString());
            return "/reg/email";
        }

        return "/404";
    }

    /*
     web手机注册
   */
    @RequestMapping(value = "/reg/mobile", method = RequestMethod.GET)
    public String regMobile(HttpServletRequest request, HttpServletResponse response, AccountWebParams webParams, Model model)
            throws Exception {
        webCookieProcess(request, response);

        Result result = new APIResultSupport(false);
        //跳转ru client_id
        result = paramProcess(result, webParams);

        if (result.isSuccess()) {
            model.addAttribute("data", result.toString());
            return "/reg/tel";
        }
        return "/404";
    }

    /*
     web个性账号注册
   */
    @RequestMapping(value = "/reg/nick", method = RequestMethod.GET)
    public String register(HttpServletRequest request, HttpServletResponse response, AccountWebParams webParams, Model model)
            throws Exception {
        webCookieProcess(request, response);

        Result result = new APIResultSupport(false);
        //跳转ru client_id
        result = paramProcess(result, webParams);

        if (result.isSuccess()) {
            model.addAttribute("data", result.toString());
            return "/reg/nick";
        }
        return "/404";
    }

    /*

    */
    @RequestMapping(value = "/remindActivate", method = RequestMethod.GET)
    private String remind_activate(AccountWebParams webParams, Model model) {
        Result result = new APIResultSupport(false);
        String validateResult = ControllerHelper.validateParams(webParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
        } else {
            result.setSuccess(true);
        }
        result.setDefaultModel("email", webParams.getEmail());
        if (result.isSuccess()) {
            model.addAttribute("data", result.toString());
            return "/reg/remind";
        }
        return "/404";
    }

    /*
    web登录页跳转
  */
    @RequestMapping(value = "/webLogin", method = RequestMethod.GET)
    public String login(HttpServletRequest request, AccountWebParams webParams, Model model)
            throws Exception {
        if (hostHolder.isLogin()) {
            return "forward:/";
        }
        Result result = new APIResultSupport(false);
        //跳转ru client_id
        result = paramProcess(result, webParams);

        if (result.isSuccess()) {
            model.addAttribute("data", result.toString());
            return "index";
        }
        return "/404";
    }

    /*
    ru跳转
     */
    private Result paramProcess(Result result, AccountWebParams webParams) {
        //参数验证
        String validateResult = ControllerHelper.validateParams(webParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result;
        }
        Map<String, String> map = Maps.newHashMap();
        String ru = webParams.getRu();
        String client_id = webParams.getClient_id();
        if (!Strings.isNullOrEmpty(ru)) {
            result.setSuccess(true);
            map.put(CommonConstant.RESPONSE_RU, ru);
        }
        if (!Strings.isNullOrEmpty(client_id)) {
            result.setSuccess(true);
            map.put(CommonConstant.CLIENT_ID, client_id);
        }
        result.setModels(map);
        return result;
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
