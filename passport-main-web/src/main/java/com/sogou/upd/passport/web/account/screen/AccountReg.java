package com.sogou.upd.passport.web.account.screen;

import com.sogou.upd.passport.web.BaseController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: mayan
 * Date: 13-6-7 Time: 下午7:23
 * 注册页面跳转页
 */
@Controller
@RequestMapping("/web")
public class AccountReg  extends BaseController {
  /*
    web注册页跳转
  */
  @RequestMapping(value = "/account/register", method = RequestMethod.GET)
  public String register(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    return "reg";
  }
}
