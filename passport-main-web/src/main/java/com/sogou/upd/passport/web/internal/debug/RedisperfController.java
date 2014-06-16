package com.sogou.upd.passport.web.internal.debug;

import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.common.utils.TokenRedisUtils;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-8-27
 * Time: 上午1:16
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
@Controller
@RequestMapping(value = "/internal/debug")
public class RedisperfController {

    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private TokenRedisUtils tokenRedisUtils;
    @Autowired
    private RedisUtils cacheRedisUtils;

    @RequestMapping(value = "/tokenRedisSwitcher", method = RequestMethod.GET)
    @ResponseBody
    public String testTokenRedisSet() throws Exception {
        String passportId = "tokenRedisTest" + new Random().nextInt(1000000) + "@sogou.com";
        tokenRedisUtils.set(passportId, "1");
        String value = tokenRedisUtils.get(passportId);
        if ("1".equals(value)) {
            return "OK";
        } else {
            return "NO";
        }
    }

    @RequestMapping(value = "/cacheRedisSwitcher", method = RequestMethod.GET)
    @ResponseBody
    public String testCacheRedisSet() throws Exception {
        String passportId = "cacheRedisTest" + new Random().nextInt(1000000) + "@sogou.com";
        cacheRedisUtils.set(passportId, "1");
        String value = cacheRedisUtils.get(passportId);
        if ("1".equals(value)) {
            return "OK";
        } else {
            return "NO";
        }
    }

}
