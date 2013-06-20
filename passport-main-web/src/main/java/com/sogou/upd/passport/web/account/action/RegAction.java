package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;

import com.sogou.upd.passport.common.CommonHelper;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.CookieUtils;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.manager.account.RegManager;
import com.sogou.upd.passport.manager.app.ConfigureManager;
import com.sogou.upd.passport.manager.form.ActiveEmailParameters;
import com.sogou.upd.passport.manager.form.CheckUserNameExistParameters;
import com.sogou.upd.passport.manager.form.WebRegisterParameters;
import com.sogou.upd.passport.service.account.OperateTimesService;
import com.sogou.upd.passport.service.account.generator.PassportIDGenerator;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

/**
 * web注册
 * User: mayan
 * Date: 13-6-7 Time: 下午5:48
 */
@Controller
@RequestMapping("/web")
public class RegAction extends BaseController {

  private static final Logger logger = LoggerFactory.getLogger(RegAction.class);
  private static final String LOGIN_INDEX_URL = "https://account.sogou.com";

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
   * @param checkParam
   */
  @RequestMapping(value = "/account/checkusername", method = RequestMethod.GET)
  @ResponseBody
  public String checkusername(CheckUserNameExistParameters checkParam)
      throws Exception {

    Result result = new APIResultSupport(false);
    //参数验证
    String validateResult = ControllerHelper.validateParams(checkParam);
    if (!Strings.isNullOrEmpty(validateResult)) {
      result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
      result.setMessage(validateResult);
      return result.toString();
    }

    String username= URLDecoder.decode(checkParam.getUsername(), "utf-8");
    //校验是否是搜狐域内用户

    if(AccountDomainEnum.SOHU.equals(AccountDomainEnum.getAccountDomain(username))){
      result.setCode(ErrorUtil.ERR_CODE_NOTSUPPORT_SOHU_REGISTER);
      return result.toString();
    }

    //判断是否是个性账号
    if(username.indexOf("@")==-1){
      //判断是否是手机号注册
      if(PhoneUtil.verifyPhoneNumberFormat(username)){
        result= regManager.isAccountNotExists(username, true);
      } else {
        username=username+"@sogou.com";
        result= regManager.isAccountNotExists(username, false);
      }
    }else {
      result= regManager.isAccountNotExists(username, false);
    }
    return result.toString();
  }

  /**
   * web页面注册
   *
   * @param regParams 传入的参数
   */
  @RequestMapping(value = "/reguser", method = RequestMethod.POST)
  @ResponseBody
  public Object reguser(HttpServletRequest request, WebRegisterParameters regParams,Model model)
      throws Exception {
    Result result = new APIResultSupport(false);
    //参数验证
    String validateResult = ControllerHelper.validateParams(regParams);
    if (!Strings.isNullOrEmpty(validateResult)) {
      result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
      result.setMessage(validateResult);
      return result.toString();
    }

    String ip = getIp(request);
    String passportId=regParams.getUsername();
    //黑白名单
    //校验是否在账户黑名单或者IP黑名单之中
    String uuidName= CookieUtils.getCookie(request, "uuidName");
    if (operateTimesService.checkRegInBlackList(ip, uuidName)){
      result.setCode(ErrorUtil.ERR_CODE_ACCOUNT_USERNAME_IP_INBLACKLIST);
      return result;
    }

    //验证client_id
    int clientId = Integer.parseInt(regParams.getClient_id());

    //检查client_id格式以及client_id是否存在
    if (!configureManager.checkAppIsExist(clientId)) {
      result.setCode(ErrorUtil.INVALID_CLIENTID);
      return result.toString();
    }

    result = regManager.webRegister(regParams, ip, request.getScheme());

    if(result.isSuccess()){
      //设置来源
      String ru =  regParams.getRu();
      if(Strings.isNullOrEmpty(ru)){
        ru=LOGIN_INDEX_URL;
      }
      result.setDefaultModel("ru",ru);
    }
    operateTimesService.incRegTimes(ip, null);
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
    if(result.isSuccess()){
      // 种sohu域cookie
      result=commonManager.createCookieUrl(result,activeParams.getPassport_id(),request.getScheme(),1) ;
    }
    return result;
  }
  /*
   外域邮箱用户激活成功的页面
 */
  @RequestMapping(value = "/reg/emailverify", method = RequestMethod.GET)
  @ResponseBody
  public Object emailVerifySuccess(HttpServletRequest request) throws Exception {
    //状态码参数
    return "/reg/emailsuccess";
  }
}
