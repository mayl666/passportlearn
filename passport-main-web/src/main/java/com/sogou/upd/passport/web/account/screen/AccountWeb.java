package com.sogou.upd.passport.web.account.screen;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.CookieUtils;
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
                           @RequestParam(defaultValue = "") String ru, Model model)
            throws Exception {
        webCookieProcess(request, response);
        //跳转ru
        ruProcess(ru, model);

        return "/reg/email";
    }

    /*
     web手机注册
   */
    @RequestMapping(value = "/reg/mobile", method = RequestMethod.GET)
    public String regMobile(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam(defaultValue = "") String ru, Model model)
            throws Exception {
        webCookieProcess(request, response);
        //跳转ru
        ruProcess(ru, model);
        return "/reg/tel";
    }

    /*
     web个性账号注册
   */
    @RequestMapping(value = "/reg/nick", method = RequestMethod.GET)
    public String register(HttpServletRequest request, HttpServletResponse response,
                           @RequestParam(defaultValue = "") String ru, Model model)
            throws Exception {
        webCookieProcess(request, response);
        //跳转ru
        ruProcess(ru, model);
        return "/reg/nick";
    }

    /*
    web登录页跳转
  */
    @RequestMapping(value = "/webLogin", method = RequestMethod.GET)
    public String login(HttpServletRequest request, @RequestParam(defaultValue = "") String ru, Model model)
            throws Exception {
        if (hostHolder.isLogin()) {
            return "forward:/";
        }
        //连接来源
        ruProcess(ru, model);
        return "index";
    }

    /*
    ru跳转
     */
    private void ruProcess(String ru, Model model) {
        Result result = null;
        if (!Strings.isNullOrEmpty(ru)) {
            result = new APIResultSupport(false);
            result.setSuccess(true);
            result.setDefaultModel("ru", ru);
            model.addAttribute("data", result.toString());
        }
    }

    /*
    注册种cookie防止恶意注册，黑白名单
     */
    private void webCookieProcess(HttpServletRequest request, HttpServletResponse response) {
        String uuidName = CookieUtils.getCookie(request, "uuidName");
        if (Strings.isNullOrEmpty(uuidName)) {
            uuidName = UUID.randomUUID().toString().replaceAll("-", "");
            CookieUtils.setCookie(response, "uuidName", uuidName, (int) DateAndNumTimesConstant.TIME_ONEDAY);
        }
    }
}
