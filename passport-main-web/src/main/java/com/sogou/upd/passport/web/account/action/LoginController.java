package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.LoginConstant;
import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.CookieUtils;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.manager.account.AccountLoginManager;
import com.sogou.upd.passport.manager.form.WebLoginParameters;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.annotation.LoginRequired;
import com.sogou.upd.passport.web.annotation.LoginRequiredResultType;
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
import java.net.URLDecoder;

/**
 * web端登陆相关的接口
 * User: lg
 * Date: 13-5-12
 * Time: 下午9:57
 */
@Controller
@RequestMapping("/web")
public class LoginController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private AccountLoginManager accountLoginManager;


    @RequestMapping(value = "/testLoginRequired", method = RequestMethod.GET)
    @LoginRequired(resultType= LoginRequiredResultType.redirect)
    @ResponseBody
    public String testLoginRequired(){
        return "目前处于登录状态";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ResponseBody
    public String logout(HttpServletRequest request,HttpServletResponse response){
        CookieUtils.deleteCookie(response,LoginConstant.PASSPORTID_COOKIE_ID);
        return "退出登录成功";
    }




    /**
     * web端的登陆接口
     *
     * @param request
     * @param loginParams 登陆需要的参数
     * @return
     * @url /web/login
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @ResponseBody
    public Object login(HttpServletRequest request,HttpServletResponse response, WebLoginParameters loginParams) {
//        //参数验证
//        String validateResult = ControllerHelper.validateParams(loginParams);
//        if (!Strings.isNullOrEmpty(validateResult)) {
//            return Result.buildError(ErrorUtil.ERR_CODE_COM_REQURIE, validateResult);
//        }
//
//        Result result = accountLoginManager.accountLogin(loginParams);
//
//        if (result.isSuccess()) {
//
//        }
//
//        return null;
        if(!StringUtil.isBlank(loginParams.getUsername())){
            CookieUtils.setCookie(response, LoginConstant.PASSPORTID_COOKIE_ID,loginParams.getUsername(),0);
            return "登录成功："+loginParams.getUsername();
        }
        return "请求 /web/login?account=18600000000@sohu.com  来登录";
    }


    @RequestMapping(value = "/testhttpclient", method = RequestMethod.GET)
    @ResponseBody
    public String testHttpClient(@RequestParam("url") String url) {
        url= URLDecoder.decode(url);
        RequestModel requestModel=new RequestModel(url);
        String html= SGHttpClient.executeStr(requestModel);
        return html;
    }

}
