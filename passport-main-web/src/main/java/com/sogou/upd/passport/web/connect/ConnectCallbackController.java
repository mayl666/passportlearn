package com.sogou.upd.passport.web.connect;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-4-26
 * Time: 下午5:52
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/connect")
public class ConnectCallbackController {

    @RequestMapping("/callback/{providerStr}")
    public ModelAndView handleCallbackRedirect(HttpServletRequest req, HttpServletResponse res,
                                               @PathVariable("providerStr") String providerStr) throws Exception {

        return new ModelAndView("");
    }
}
