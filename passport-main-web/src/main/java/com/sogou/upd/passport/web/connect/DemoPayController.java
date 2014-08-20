package com.sogou.upd.passport.web.connect;

import com.sogou.upd.passport.common.CommonConstant;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.web.BaseConnectController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-8-19
 * Time: 下午3:48
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/connect/pay")
public class DemoPayController extends BaseConnectController {

    //Passport模拟第三方，返回回调地址
    @RequestMapping("/demo")
    @ResponseBody
    public String pay_demo(HttpServletRequest req, HttpServletResponse res) {
        return CommonConstant.DEFAULT_INDEX_URL;
    }
}
