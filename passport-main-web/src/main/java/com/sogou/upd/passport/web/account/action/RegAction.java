package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.CommonHelper;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.RegManager;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.form.ActiveEmailParameters;
import com.sogou.upd.passport.manager.form.WebRegisterParameters;
import com.sogou.upd.passport.service.account.OperateTimesService;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

/**
 * User: mayan Date: 13-6-7 Time: 下午5:48
 * web注册
 */
@Controller
@RequestMapping("/web")
public class RegAction extends BaseController {

  private static final Logger logger = LoggerFactory.getLogger(RegAction.class);

  @Autowired
  private RegManager regManager;
  @Autowired
  private CommonManager commonManager;
  @Autowired
  private ConfigureManager configureManager;

  @Autowired
  private OperateTimesService operateTimesService;

  /**
   * 用户注册检查用户名是否存在
   *
   * @param username
   */
  @RequestMapping(value = "/account/checkusername", method = RequestMethod.GET)
  @ResponseBody
  public String checkusername(@RequestParam(defaultValue = "") String username)
      throws Exception {

    //校验username格式 todo
    username= URLDecoder.decode(username, "utf-8");
    Result result = new APIResultSupport(false);
    //校验是否是@sohu.com
    if(!isSohuUserName(username)){
      result.setCode(ErrorUtil.ERR_CODE_NOTSUPPORT_SOHU_REGISTER);
      return result.toString();
    }

    //判断是否是个性账号
    if(username.indexOf("@")==-1){
      //判断是否是手机号注册
      if(PhoneUtil.verifyPhoneNumberFormat(username)){
        username=username+"@sohu.com";
      } else {
        username=username+"@sogou.com";
      }
    }

    boolean isExists= commonManager.isAccountExists(username);
    if(isExists){
      result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_REGED);
    }else{
      result.setSuccess(true);
      result.setMessage("账户未被占用，可以注册");
    }
    return result.toString();
  }

  private boolean isSohuUserName(String username) {
    if (Strings.isNullOrEmpty(username)) {   // NotBlank已经校验过了，无需再校验
      return true;
    }
    if(username.endsWith("@sohu.com")){
      return false;
    }
    return true;
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
    Result result = new APIResultSupport(false);
    //参数验证
    String validateResult = ControllerHelper.validateParams(regParams);
    if (!Strings.isNullOrEmpty(validateResult)) {
      result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
      result.setMessage(validateResult);
      return result.toString();
    }

    String password = regParams.getPassword();

    if (!CommonHelper.checkPasswd(password) && StringUtil.checkPwdFormat(password)) {
      result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_PWDERROR);
      return result.toString();
    }
    String ip = getIp(request);
    //todo 黑白名单

    //验证client_id
    int clientId = Integer.parseInt(regParams.getClient_id());

    //检查client_id格式以及client_id是否存在
    if (!configureManager.checkAppIsExist(clientId)) {
      result.setCode(ErrorUtil.INVALID_CLIENTID);
      return result.toString();
    }

    result = regManager.webRegister(regParams, ip);
    return result.toString();
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
    Result result = new APIResultSupport(false);
    //参数验证
    String validateResult = ControllerHelper.validateParams(activeParams);
    if (!Strings.isNullOrEmpty(validateResult)) {
      result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
      result.setMessage(validateResult);
      return result;
    }
    //验证client_id
    int clientId = Integer.parseInt(activeParams.getClient_id());

    //检查client_id是否存在
    if (!configureManager.checkAppIsExist(clientId)) {
      result.setCode(ErrorUtil.INVALID_CLIENTID);
      return result;
    }
    String ip = getIp(request);
    //邮件激活
    result = regManager.activeEmail(activeParams, ip);
    return result;
  }
}
