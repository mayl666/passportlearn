package com.sogou.upd.passport.web.account;

import com.sogou.upd.passport.common.web.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * User: mayan
 * Date: 13-3-22
 * Time: 下午3:29
 * 用户注册登录
 */
@Controller
public class AccountController extends BaseController{
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @RequestMapping(value = "/account/reguser", method = RequestMethod.GET)
    @ResponseBody
    public Object reguser(HttpServletRequest request, HttpServletResponse response,
                          @RequestParam(defaultValue = "0") int appid, @RequestParam(defaultValue = "") String account,
                          @RequestParam(defaultValue = "") String client_signature, @RequestParam(defaultValue = "") String signature)
            throws Exception {

//        Map<String, Object> ret = checkAccount(account);
//        if (ret != null) return ret;
//
//        boolean isReg = accountService.isRegExcludeExpuser(account);
//        if (isReg) { return ErrorUtil.buildError(ErrorUtil.ERR_CODE_ACCOUNT_REGED); }

        return buildSuccess(null,null);
    }
}
