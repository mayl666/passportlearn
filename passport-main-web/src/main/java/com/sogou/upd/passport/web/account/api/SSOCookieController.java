package com.sogou.upd.passport.web.account.api;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.account.CommonManager;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.account.form.SSOCookieParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-12-31
 * Time: 下午4:05
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class SSOCookieController {
    @Autowired
    private CommonManager commonManager;

    @RequestMapping(value = "/sso/setcookie", method = RequestMethod.GET)
    @ResponseBody
    public Object setcookie(HttpServletRequest request, HttpServletResponse response,SSOCookieParams ssoCookieParams) throws Exception {
        Result result = new APIResultSupport(false);
        //参数验证
        String validateResult = ControllerHelper.validateParams(ssoCookieParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            result.setCode(ErrorUtil.ERR_CODE_COM_REQURIE);
            result.setMessage(validateResult);
            return result.toString();
        }
        //todo 验证code
        String domain="";
        request.getServerName();
        commonManager.setSSOCookie(response,ssoCookieParams.getSginf(),ssoCookieParams.getSgrdig(),domain,ssoCookieParams.getMaxAge());
        return "empty";
    }
}
