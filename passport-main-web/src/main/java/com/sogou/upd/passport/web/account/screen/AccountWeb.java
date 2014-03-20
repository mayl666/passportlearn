package com.sogou.upd.passport.web.account.screen;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ServletUtil;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.inteceptor.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String regEmail(HttpServletRequest request, HttpServletResponse response,
                           @RequestParam(defaultValue = "") String ru,
                           @RequestParam(defaultValue = "") String client_id,
                           Model model)
            throws Exception {
        webCookieProcess(request, response);

        Result result = new APIResultSupport(false);
        //跳转ru client_id
        result = paramProcess(result, ru, client_id);

        if (result.isSuccess()) {
            model.addAttribute("data", result.toString());
        }

        return "/reg/email";
    }

    /*
     web手机注册
   */
    @RequestMapping(value = "/reg/mobile", method = RequestMethod.GET)
    public String regMobile(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam(defaultValue = "") String ru,
                            @RequestParam(defaultValue = "") String client_id,
                            Model model)
            throws Exception {
        webCookieProcess(request, response);

        Result result = new APIResultSupport(false);
        //跳转ru client_id
        result = paramProcess(result, ru, client_id);

        if (result.isSuccess()) {
            model.addAttribute("data", result.toString());
        }
        return "/reg/tel";
    }

    /*
     web个性账号注册
   */
    @RequestMapping(value = "/reg/nick", method = RequestMethod.GET)
    public String register(HttpServletRequest request, HttpServletResponse response,
                           @RequestParam(defaultValue = "") String ru,
                           @RequestParam(defaultValue = "") String client_id,
                           Model model)
            throws Exception {
        webCookieProcess(request, response);

        Result result = new APIResultSupport(false);
        //跳转ru client_id
        result = paramProcess(result, ru, client_id);

        if (result.isSuccess()) {
            model.addAttribute("data", result.toString());
        }
        return "/reg/nick";
    }

    /*

    */
    @RequestMapping(value = "/remindActivate", method = RequestMethod.GET)
    private String remind_activate(String email, Model model) {
        Result result = new APIResultSupport(true);
        result.setDefaultModel("email", email);
        model.addAttribute("data", result.toString());
        return "/reg/remind";
    }

    /*
    web登录页跳转
  */
    @RequestMapping(value = "/webLogin", method = RequestMethod.GET)
    public String login(HttpServletRequest request,
                        @RequestParam(defaultValue = "") String ru,
                        @RequestParam(defaultValue = "") String client_id,
                        Model model)
            throws Exception {
        if (hostHolder.isLogin()) {
            return "forward:/";
        }
        Result result = new APIResultSupport(false);
        //跳转ru client_id
        result = paramProcess(result, ru, client_id);

        if (result.isSuccess()) {
            model.addAttribute("data", result.toString());
        }
        return "index";
    }


    /*
     外域邮箱用户激活成功的页面
   */
    @RequestMapping(value = "/reg/emailverify", method = RequestMethod.GET)
    public String emailVerifySuccess(HttpServletRequest request, HttpServletResponse response, @RequestParam(defaultValue = "") String ru,
                                     @RequestParam(defaultValue = "") String client_id, Model model) throws Exception {
        Result result = new APIResultSupport(false);
        //跳转ru client_id
        result = paramProcess(result, ru, client_id);

        if (result.isSuccess()) {
            model.addAttribute("data", result.toString());
        }
        //状态码参数
        return "reg/emailsuccess";
    }


    /*
    ru跳转
     */
    private Result paramProcess(Result result, String ru, String client_id) {
        Map<String, String> map = Maps.newHashMap();
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
