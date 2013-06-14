package com.sogou.upd.passport.web.account.screen;

import com.google.common.base.Strings;
import com.sogou.upd.passport.web.BaseController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
  public String regEmail(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    return "/reg/email";
  }
  /*
   web手机注册
 */
  @RequestMapping(value = "/reg/mobile", method = RequestMethod.GET)
  public String regMobile(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    return "/reg/tel";
  }
  /*
   web个性账号注册
 */
  @RequestMapping(value = "/reg/nick", method = RequestMethod.GET)
  public String register(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    return "/reg/nick";
  }
  /*
  web登录页跳转
*/
  @RequestMapping(value = "/index", method = RequestMethod.GET)
  public String login(HttpServletRequest request, HttpServletResponse response,Model model)
      throws Exception {
      //连接来源
      String ru = request.getParameter("ru");
      if (!Strings.isNullOrEmpty(ru)){
          model.addAttribute("ru",ru);
      }
    return "index";
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
