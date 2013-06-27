package com.sogou.upd.passport.web.account.action;

import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.web.account.form.CheckUserNameExistParameters;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 用于前端相关的操作处理
 * User: ligang201716@sogou-inc.com
 * Date: 13-6-27
 * Time: 下午3:05
 */
@Controller
public class StaticAction {

    @RequestMapping(value = "/static/login.js", method = RequestMethod.GET)
    @ResponseBody
    public String checkNeedCaptcha(@RequestParam(value="callBack", required=false)  String callBack)
            throws Exception {
        if (!StringUtil.isBlank(callBack)) {

            return "/static/api/passport_cb.js";
        }
        return "/static/api/passport.js";
    }

}
