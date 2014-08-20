package com.sogou.upd.passport.web.internal.debug;

import com.sogou.upd.passport.common.utils.JsonUtil;
import com.sogou.upd.passport.web.BaseController;
import com.sogou.upd.passport.web.inteceptor.HostHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 调试module 获取请求头信息
 * User: chengang
 * Date: 14-8-20
 * Time: 下午2:03
 */
@Controller
@RequestMapping(value = "/internal/debug/module")
public class DebugNginxModule extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DebugNginxModule.class);

    @Autowired
    private HostHolder hostHolder;


    @RequestMapping(value = "/getRH", method = RequestMethod.GET)
    @ResponseBody
    public String getRequestHeaders(HttpServletRequest request) {


        String sLoginPassportId;
        if (hostHolder.isLogin()) {
            sLoginPassportId = hostHolder.getPassportId();
        } else {
            return "";
        }

        Map<String, String> map = new HashMap<>();

        String requestIp = getIp(request);
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);

            if (key.equals("HTTP_COOKIE")) {
                LOGGER.info("HTTP_COOKIE:{},requestIp:{}", value, requestIp);
            }

            if (key.equals("HTTP_X_SOHUPASSPORT_USERID")) {
                LOGGER.info("HTTP_X_SOHUPASSPORT_USERID:{},requestIp;{}", value, requestIp);
            }
            map.put(key, value);
        }
        return JsonUtil.obj2Json(map);
    }


}
