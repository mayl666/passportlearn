package com.sogou.upd.passport.web.account.screen;

import com.google.common.base.Strings;
import com.sogou.upd.passport.web.BaseController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

/**
 * User: mayan
 * Date: 13-6-7 Time: 下午7:58
 * 登录注册页面跳转页
 */
@Controller
@RequestMapping("/web")
public class AccountWeb extends BaseController {

  /*
   web邮箱注册
 */
  @RequestMapping(value = "/reg/email", method = RequestMethod.GET)
  public String regEmail(HttpServletRequest request, Model model)
      throws Exception {
    //连接来源
    String ru = request.getParameter("ru");
    if (!Strings.isNullOrEmpty(ru)){
      ru = URLEncoder.encode(ru, "UTF-8");
      model.addAttribute("ru",ru);
    }
    return "/reg/email";
  }
  /*
   web手机注册
 */
  @RequestMapping(value = "/reg/mobile", method = RequestMethod.GET)
  public String regMobile(HttpServletRequest request, Model model)
      throws Exception {
    //连接来源
    String ru = request.getParameter("ru");
    if (!Strings.isNullOrEmpty(ru)){
      ru = URLEncoder.encode(ru, "UTF-8");
      model.addAttribute("ru",ru);
    }
    return "/reg/tel";
  }
  /*
   web个性账号注册
 */
  @RequestMapping(value = "/reg/nick", method = RequestMethod.GET)
  public String register(HttpServletRequest request, Model model)
      throws Exception {
    //连接来源
    String ru = request.getParameter("ru");
    if (!Strings.isNullOrEmpty(ru)){
      ru = URLEncoder.encode(ru, "UTF-8");
      model.addAttribute("ru",ru);
    }
    return "/reg/nick";
  }
  /*
  web登录页跳转
*/
  @RequestMapping(value = "/webLogin", method = RequestMethod.GET)
  public String login(HttpServletRequest request, Model model)
      throws Exception {
      //连接来源
     String ru = request.getParameter("ru");
      if (!Strings.isNullOrEmpty(ru)){
          ru = URLEncoder.encode(ru, "UTF-8");
          model.addAttribute("ru",ru);
     }
    return "login";
  }
    /*
   web修改密码页跳转
*/
  @RequestMapping(value = "/resetpwd", method = RequestMethod.GET)
  public String resetpwd(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    return "resetpwd";
  }
}
