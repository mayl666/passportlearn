package com.sogou.upd.passport.web.account.screen;

import org.springframework.stereotype.Controller;
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
public class AccountLogin {
  /*
  web登录页跳转
*/
  @RequestMapping(value = "/login", method = RequestMethod.GET)
  public String login(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    return "login";
  }
}
