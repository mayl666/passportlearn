package com.sogou.upd.passport.web.internal.debug;

import com.sogou.upd.passport.web.UserOperationLogUtil;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-8-6 Time: 上午10:47 To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/internal/logback")
public class LogBackSetAction {

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    @ResponseBody
    public Object userLogAppenderSet(String appender) {
            if ("scribe".equalsIgnoreCase(appender)) {
                UserOperationLogUtil.setUserLogger("scribe");
                return "set to scribe";
            } else if ("local".equalsIgnoreCase(appender)) {
                UserOperationLogUtil.setUserLogger("local");
                return "set to local";
            } else if ("base".equalsIgnoreCase(appender)) {
                UserOperationLogUtil.setUserLogger("base");
                return "set to fail";
            } else {
                return "Wrong setting, should set 'appender' to scribe or local";
            }
    }
}
