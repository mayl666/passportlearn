package com.sogou.upd.passport.web.account.action;

import com.sogou.upd.passport.common.model.useroperationlog.UserOperationLog;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.UserOperationLogUtil;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-8-1 Time: 下午5:40 To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/test")
public class TestAction extends BaseController {


    // TODO:删除
    @RequestMapping(value = "/scribe", method = RequestMethod.POST)
    public Object testScribe(HttpServletRequest request) {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
        }

        UserOperationLog
                userOperationLog = new UserOperationLog("test_sogou@sogou.com", "/internal/account/reguser", "1120", "0", getIp(request));
        String referer = request.getHeader("referer");
        userOperationLog.putOtherMessage("ref", referer);
        UserOperationLogUtil.log(userOperationLog);

        return "testscribe";
    }
}
