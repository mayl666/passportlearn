package com.sogou.upd.passport.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mayan
 * Date: 13-3-15
 * Time: 下午1:23
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class TestController {

    @RequestMapping(value = "/profile/getuser/tel")
    @ResponseBody
    public Object test(HttpServletRequest request, HttpServletResponse response,
                       @RequestParam(defaultValue = "0") int appid
                       )
            throws Exception {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!");

         return buildSuccess("",null);
    }
    private Map<String, Object> buildSuccess(String msg, Map<String, Object> data) {
        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("0", "0");
        return retMap;
    }

}
