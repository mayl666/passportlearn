package com.sogou.upd.passport.web.account.api;

import com.sogou.upd.passport.manager.form.PcAuthTokenParams;
import com.sogou.upd.passport.web.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-11-12
 * Time: 下午12:17
 * To change this template use File | Settings | File Templates.
 */
public class WapAccountController extends BaseController {
    @RequestMapping(value = "/wap/login")
    @ResponseBody
    public String login(HttpServletRequest request, HttpServletResponse response, PcAuthTokenParams authPcTokenParams) throws Exception {
    return "";
    }
}
