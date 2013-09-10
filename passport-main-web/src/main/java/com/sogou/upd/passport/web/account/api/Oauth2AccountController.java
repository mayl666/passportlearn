package com.sogou.upd.passport.web.account.api;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.result.APIResultSupport;
import com.sogou.upd.passport.common.result.Result;
import com.sogou.upd.passport.manager.api.account.UserInfoApiManager;
import com.sogou.upd.passport.manager.api.account.form.GetUserInfoApiparams;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.ControllerHelper;
import com.sogou.upd.passport.web.account.form.Oauth2PcIndexParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * sohu+浏览器相关接口替换
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-9-9
 * Time: 下午7:37
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class Oauth2AccountController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(Oauth2AccountController.class);
    @Autowired
    private UserInfoApiManager proxyUserInfoApiManagerImpl;

    @RequestMapping(value = "/oauth2/pclogin", method = RequestMethod.GET)
    public String pcLogin(Model model) throws Exception {
        return "";
    }

    @RequestMapping(value = "/oauth2/pcindex", method = RequestMethod.GET)
    public String pcindex(HttpServletRequest request, Oauth2PcIndexParams oauth2PcIndexParams, Model model) throws Exception {
        //参数验证
        String validateResult = ControllerHelper.validateParams(oauth2PcIndexParams);
        if (!Strings.isNullOrEmpty(validateResult)) {
            return "forward:/oauth2/errorMsg?msg=" + validateResult;
        }
        //TODO 校验token,获取userid
        String passportId = "tinkame700@sogou.com";

        //获取昵称
        GetUserInfoApiparams getUserInfoApiparams = new GetUserInfoApiparams(passportId, "uniqname");
        Result getUserInfoResult = proxyUserInfoApiManagerImpl.getUserInfo(getUserInfoApiparams);
        String uniqname;
        if (getUserInfoResult.isSuccess()) {
            uniqname = (String) getUserInfoResult.getModels().get("uniqname");
            uniqname = Strings.isNullOrEmpty(uniqname) ? defaultUniqname(passportId) : uniqname;
        } else {
            uniqname = defaultUniqname(passportId);
        }
        model.addAttribute("userid", passportId);
        model.addAttribute("uniqname", uniqname);



        return "/oauth2pc/pcindex";
    }

    @RequestMapping(value = "/oauth2/errorMsg")
    @ResponseBody
    public Object errorMsg(@RequestParam("msg") String msg) throws Exception {
        return msg;
    }
    private String defaultUniqname(String passportId) {
        return passportId.substring(0, passportId.indexOf("@"));
    }
}
