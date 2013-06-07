package com.sogou.upd.passport.web.account.action;

import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.AccountManager;
import com.sogou.upd.passport.manager.account.AccountRegManager;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.web.BaseController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: mayan Date: 13-4-28 Time: 下午4:07 To change this template use File | Settings | File
 * Templates.
 */
@Controller
@RequestMapping("/web")
public class AccountAction extends BaseController {

  private static final Logger logger = LoggerFactory.getLogger(AccountAction.class);

  @Autowired
  private AccountRegManager accountRegManager;
  @Autowired
  private AccountManager accountManager;
  @Autowired
  private ConfigureManager configureManager;

  /*
     web注册页跳转
   */
  @RequestMapping(value = "/register", method = RequestMethod.GET)
  public String register(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    return "reg";
  }

//  /*
//     web登录页跳转
//   */
//  @RequestMapping(value = "/login", method = RequestMethod.GET)
//  public String login(HttpServletRequest request, HttpServletResponse response)
//      throws Exception {
//
//    return "login";
//  }
//  /*
//   web登录页跳转
// */
//  @RequestMapping(value = "/resetpwd", method = RequestMethod.GET)
//  public String resetpwd(HttpServletRequest request, HttpServletResponse response)
//      throws Exception {
//
//    return "resetpwd";
//  }
  /**
   * 用户注册检查用户名是否存在
   *
   * @param username
   */
  @RequestMapping(value = "/checkusername", method = RequestMethod.GET)
  @ResponseBody
  public String checkusername(@RequestParam(defaultValue = "") String username)
      throws Exception {
    //校验username格式 todo

    Result result = new APIResultSupport(false);
    boolean isExists=accountManager.isAccountExists(username);
    if(isExists){
      result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGED);
    }else{
      result.setSuccess(true);
      result.setMessage("账户未被占用，可以注册");
    }
    return result.toString();
  }
}
