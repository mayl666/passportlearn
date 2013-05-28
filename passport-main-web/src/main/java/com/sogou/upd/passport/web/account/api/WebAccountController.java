package com.sogou.upd.passport.web.account.api;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.AccountLoginManager;
import com.sogou.upd.passport.manager.account.AccountManager;
import com.sogou.upd.passport.manager.account.AccountRegManager;
import com.sogou.upd.passport.manager.account.AccountSecureManager;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.form.ActiveEmailParameters;
import com.sogou.upd.passport.manager.form.ResetPwdParameters;
import com.sogou.upd.passport.manager.form.WebLoginParameters;
import com.sogou.upd.passport.manager.form.WebRegisterParameters;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 移动用户注册登录 User: mayan Date: 13-3-22 Time: 下午3:29 To change this template use File | Settings |
 * File Templates.
 */
@Controller
public class WebAccountController extends BaseController {

  private static final Logger logger = LoggerFactory.getLogger(WebAccountController.class);

  @Autowired
  private AccountSecureManager accountSecureManager;
  @Autowired
  private AccountRegManager accountRegManager;
  @Autowired
  private AccountManager accountManager;
  @Autowired
  private ConfigureManager configureManager;
  @Autowired
  private AccountLoginManager accountLoginManager;

  /**
   * web页面登录
   *
   * @param loginParams 传入的参数
   */
  @RequestMapping(value = "/login", method = RequestMethod.POST)
  @ResponseBody
  public Object login(HttpServletRequest request, WebLoginParameters loginParams)
      throws Exception {
    //参数验证
    String validateResult = ControllerHelper.validateParams(loginParams);
    if (!Strings.isNullOrEmpty(validateResult)) {
      return Result.buildError(ErrorUtil.ERR_CODE_COM_REQURIE, validateResult);
    }

    //判断用户是否存在
    String username=loginParams.getUsername();
    if(!accountManager.isAccountExists(username)){
        return Result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_NOTHASACCOUNT);
    }
    Result result = accountLoginManager.accountLogin(loginParams);


    return null;
  }


  /**
   * web页面注册
   *
   * @param regParams 传入的参数
   */
  @RequestMapping(value = "/reguser", method = RequestMethod.POST)
  @ResponseBody
  public Object reguser(HttpServletRequest request, WebRegisterParameters regParams)
      throws Exception {

    //参数验证
    String validateResult = ControllerHelper.validateParams(regParams);
    if (!Strings.isNullOrEmpty(validateResult)) {
      return Result.buildError(ErrorUtil.ERR_CODE_COM_REQURIE, validateResult);
    }

    String username=regParams.getUsername();
    String ip = getIp(request);

    String captchaCode = regParams.getCaptcha();
    String token = regParams.getToken();
    //校验用户是否可注册
    Result result =accountRegManager.isAllowRegister(username,ip,token, captchaCode);

    if (!"0".equals(result.getStatus())) {
      return result;
    }
    //密码格式校验 todo

    //验证client_id
    int clientId=Integer.parseInt(regParams.getClient_id());

    //检查client_id格式以及client_id是否存在
    if (!configureManager.checkAppIsExist(clientId)) {
      return Result.buildError(ErrorUtil.INVALID_CLIENTID);
    }

    //验证用户是否注册过
    if (!accountManager.isAccountExists(username)) {
      result = accountRegManager.webRegister(regParams, ip);
    } else {
      result = result.buildError(ErrorUtil.ERR_CODE_ACCOUNT_REGED);
    }
    return result;
  }

  /**
   * 邮件激活
   *
   * @param activeParams 传入的参数
   */
  @RequestMapping(value = "/activemail", method = RequestMethod.GET)
  @ResponseBody
  public Object activeEmail(HttpServletRequest request, ActiveEmailParameters activeParams)
      throws Exception {

    //参数验证
    String validateResult = ControllerHelper.validateParams(activeParams);
    if (!Strings.isNullOrEmpty(validateResult)) {
      return Result.buildError(ErrorUtil.ERR_CODE_COM_REQURIE, validateResult);
    }
    //验证client_id
    int clientId;
    try {
      clientId = Integer.parseInt(activeParams.getClient_id());
    } catch (NumberFormatException e) {
      return Result.buildError(ErrorUtil.ERR_FORMAT_CLIENTID);
    }
    //检查client_id是否存在
    if (!configureManager.checkAppIsExist(clientId)) {
      return Result.buildError(ErrorUtil.INVALID_CLIENTID);
    }
    String ip = getIp(request);
    //邮件激活
    Result result = accountRegManager.activeEmail(activeParams,ip);

    return result;
  }


  /**
   * 修改密码
   *
   * @param resetParams 传入的参数
   */
  @RequestMapping(value = "/resetpwd", method = RequestMethod.POST)
  @ResponseBody
  public Object resetpwd(HttpServletRequest request, ResetPwdParameters resetParams)
      throws Exception {

    //todo 注解需要判断登录

    String validateResult = ControllerHelper.validateParams(resetParams);
    if (!Strings.isNullOrEmpty(validateResult)) {
      return Result.buildError(ErrorUtil.ERR_CODE_COM_REQURIE, validateResult);
    }
    Result result = accountSecureManager.resetWebPassword(resetParams);
    return result;
  }
}
