package com.sogou.upd.passport.web.internal.account;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.common.utils.ErrorUtil;
import com.sogou.upd.passport.manager.form.AccountSecureParams;
import com.sogou.upd.passport.service.account.impl.OperateTimesServiceImpl;
import com.sogou.upd.passport.web.ControllerHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 13-8-15
 * Time: 下午6:03
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/internal")
public class AccountSecureController {

    @RequestMapping(value = "/getuseripfromset", method = RequestMethod.GET)
    @ResponseBody
    public Object getUserIpFromSet(HttpServletRequest request, AccountSecureParams params) {
        Result result = new APIResultSupport(false);
        OperateTimesServiceImpl.ipListSet.add(params.getSubIp());
        result.setSuccess(true);
        return result.toString();
    }
}
