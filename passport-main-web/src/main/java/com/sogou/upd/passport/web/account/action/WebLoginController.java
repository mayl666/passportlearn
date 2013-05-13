package com.sogou.upd.passport.web.account.action;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.form.WebRegisterParameters;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: lg
 * Date: 13-5-12
 * Time: 下午9:57
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/web")
public class WebLoginController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(WebLoginController.class);


    /**
     * web端的登陆接口
     * @url /web/login
     * @param request
     * @param regParams 登陆需要的参数
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Object login(HttpServletRequest request, WebRegisterParameters regParams){
        //参数验证
        String validateResult = ControllerHelper.validateParams(regParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            return Result.buildError(ErrorUtil.ERR_CODE_COM_REQURIE, validateResult);
        }



        return null;
    }
}
