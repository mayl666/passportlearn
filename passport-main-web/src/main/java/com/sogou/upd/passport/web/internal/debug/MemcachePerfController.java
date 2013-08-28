package com.sogou.upd.passport.web.internal.debug;

import com.sogou.upd.passport.common.utils.MemcacheUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-8-27
 * Time: 上午1:16
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class MemcachePerfController {
    private final int MEM_TIMEOUT = 60*60;

    @Autowired
    private MemcacheUtils rTokenMemUtils;

    @RequestMapping(value = "/internal/debug/memset", method = RequestMethod.GET)
    @ResponseBody
    public String memset() throws Exception {
        String passportId = RandomStringUtils.randomAlphanumeric(8) + "@sogou.com";
        String instanceId = RandomStringUtils.randomAlphanumeric(8);
        int clientId = 1044;
        String key = buildTsKeyStr(passportId,clientId,instanceId);
        rTokenMemUtils.set(key,MEM_TIMEOUT, "OpPP841SOEL4C5cJlf4r0D4Fj74c5l");
        return "OK";
    }

    @RequestMapping(value = "/internal/debug/memget", method = RequestMethod.GET)
    @ResponseBody
    public String memget() throws Exception {
        String passportId = RandomStringUtils.randomAlphanumeric(8) + "@sogou.com";
        String instanceId = RandomStringUtils.randomAlphanumeric(8);
        int clientId = 1044;
        String key = buildTsKeyStr(passportId,clientId,instanceId);
        rTokenMemUtils.get(key);
        return "OK";
    }

    private String buildTsKeyStr(String passportId, int clientId, String instanceId) {
        return passportId + "|" + clientId + "|" + instanceId;
    }

}
