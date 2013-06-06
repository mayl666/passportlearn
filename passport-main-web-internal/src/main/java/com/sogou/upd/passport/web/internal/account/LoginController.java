package com.sogou.upd.passport.web.internal.account;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-5
 * Time: 下午12:05
 */
@Controller
@RequestMapping("/account")
public class LoginController {

    @RequestMapping(value = "/test",params = "internal_method=proxy")
    @ResponseBody
    public String register(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        return "method=proxy";
    }
}
