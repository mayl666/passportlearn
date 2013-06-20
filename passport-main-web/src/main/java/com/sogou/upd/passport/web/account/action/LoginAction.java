package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.parameter.AccountDomainEnum;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.CookieUtils;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.PhoneUtil;
import com.sogou.upd.passport.manager.account.LoginManager;
import com.sogou.upd.passport.manager.api.SHPPUrlConstant;
import com.sogou.upd.passport.manager.form.CheckUserNameExistParameters;
import com.sogou.upd.passport.manager.form.WebLoginParameters;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;

/**
 *  User: mayan
 *  Date: 13-6-7 Time: 下午5:48
 *  web登录
 */
@Controller
@RequestMapping("/web")
public class LoginAction extends BaseController {

  private static final Logger logger = LoggerFactory.getLogger(LoginAction.class);

  @Autowired
  private LoginManager loginManager;

    /**
     * 用户注册检查用户名是否存在
     *
     * @param checkParam
     */
    @RequestMapping(value = "/login/checkNeedCaptcha", method = RequestMethod.GET)
    @ResponseBody
    public String checkNeedCaptcha(HttpServletRequest request,CheckUserNameExistParameters checkParam)
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
        //校验是否需要验证码
        boolean needCaptcha = loginManager.needCaptchaCheck(checkParam.getClient_id(),username,getIp(request));
        result.setSuccess(true);
        result.setDefaultModel("needCaptcha",needCaptcha);
        return result.toString();
    }
  /**
   * web页面登录
   *
   * @param loginParams 传入的参数
   */
  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public String login(HttpServletRequest request,Model model, WebLoginParameters loginParams)
      throws Exception {
    Result result = new APIResultSupport(false);
    //参数验证
    String validateResult = ControllerHelper.validateParams(loginParams);
    if (!Strings.isNullOrEmpty(validateResult)) {
      result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
      result.setMessage(validateResult);
      result.setDefaultModel("xd",loginParams.getXd());
      model.addAttribute("data",result.toString());
      return "/login/api";
    }
    result = loginManager.accountLogin(loginParams, getIp(request), request.getScheme());
    result.setDefaultModel("xd",loginParams.getXd());
    model.addAttribute("data",result.toString());
    return "/login/api";
  }

    /**
     * web页面退出
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ModelAndView logout(HttpServletRequest request,HttpServletResponse response)
            throws Exception {
        String redirectUrl = "";
        CookieUtils.deleteCookie(response, LoginConstant.COOKIE_PPINF);
        CookieUtils.deleteCookie(response, LoginConstant.COOKIE_PPRDIG);
        return new ModelAndView(new RedirectView(SHPPUrlConstant.CLEAN_COOKIE));
    }

}
