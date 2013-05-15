package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.CookieUtils;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.StringUtil;
import com.sogou.upd.passport.manager.account.AccountLoginManager;
import com.sogou.upd.passport.manager.form.WebLoginParameters;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.annotation.LoginRequired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * web端登陆相关的接口
 * User: lg
 * Date: 13-5-12
 * Time: 下午9:57
 */
@Controller
@RequestMapping("/web")
public class LoginController extends BaseController {
//
//    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
//
//    @Autowired
//    private AccountLoginManager accountLoginManager;
//
//
//    @RequestMapping(value = "/testLoginRequired", method = RequestMethod.GET)
//    @LoginRequired
//    @ResponseBody
//    public String testLoginRequired(){
//        return "ok";
//    }
//
//
//
//    /**
//     * web端的登陆接口
//     *
//     * @param request
//     * @param loginParams 登陆需要的参数
//     * @return
//     * @url /web/login
//     */
//    @RequestMapping(value = "/login", method = RequestMethod.GET)
//    @ResponseBody
//    public Object login(HttpServletRequest request,HttpServletResponse response, WebLoginParameters loginParams) {
////        //参数验证
////        String validateResult = ControllerHelper.validateParams(loginParams);
////        if (!Strings.isNullOrEmpty(validateResult)) {
////            return Result.buildError(ErrorUtil.ERR_CODE_COM_REQURIE, validateResult);
////        }
////
////        Result result = accountLoginManager.accountLogin(loginParams);
////
////        if (result.isSuccess()) {
////
////        }
////
////        return null;
//        if(!StringUtil.isBlank(loginParams.getAccount())){
//            CookieUtils.setCookie(response, LoginConstant.PASSPORTID_COOKIE_ID,loginParams.getAccount(),0);
//        }
//        return "ok";
//    }
}
