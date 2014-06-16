package com.sogou.upd.passport.web.internal.debug;

import com.sogou.upd.passport.common.utils.KvUtils;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.PCAccountTokenService;
import com.sogou.upd.passport.service.account.impl.PCAccountServiceImpl;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-8-27
 * Time: 上午1:16
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
@Controller
public class KvperfController {

    @Autowired
    private PCAccountTokenService pcAccountTokenService;
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private KvUtils kvUtils;

    @RequestMapping(value = "/internal/debug/kvset", method = RequestMethod.GET)
    @ResponseBody
    public String testKvSet() throws Exception {
        String passportId = RandomStringUtils.randomAlphanumeric(8) + "@sogou.com";
        String instanceId = RandomStringUtils.randomAlphanumeric(10);
        int clientId = 1044;

        AppConfig appconfig = appConfigService.queryAppConfigByClientId(1044);
        AccountToken accountToken = PCAccountServiceImpl.newAccountToken(passportId, instanceId, appconfig);
        String key = PCAccountServiceImpl.buildKeyStr(passportId, clientId, instanceId);
        kvUtils.set(key, accountToken);
        return "OK";
    }

    @RequestMapping(value = "/internal/debug/kvsetput", method = RequestMethod.GET)
    @ResponseBody
    public String testKvSet_Put() throws Exception {
        String passportId = RandomStringUtils.randomAlphanumeric(8) + "@sogou.com";
        String instanceId = RandomStringUtils.randomAlphanumeric(10);

        AppConfig appconfig = appConfigService.queryAppConfigByClientId(1044);
        pcAccountTokenService.updateAccountToken(passportId, instanceId, appconfig);
        return "OK";
    }

    @RequestMapping(value = "/internal/debug/kvget", method = RequestMethod.GET)
    @ResponseBody
    public String testKvGet() throws Exception {
        String passportId = RandomStringUtils.randomAlphanumeric(8) + "@sogou.com";
        String instanceId = RandomStringUtils.randomAlphanumeric(10);

        AccountToken accountToken = pcAccountTokenService.queryAccountToken(passportId, 1044, instanceId);
        return "OK";
    }

    @RequestMapping(value = "/internal/debug/kvsetputget", method = RequestMethod.GET)
    @ResponseBody
    public String testKvSet_Get() throws Exception {
        String passportId = RandomStringUtils.randomAlphanumeric(8) + "@sogou.com";
        String instanceId = RandomStringUtils.randomAlphanumeric(10);
        AppConfig appconfig = appConfigService.queryAppConfigByClientId(1044);
        pcAccountTokenService.updateAccountToken(passportId, instanceId, appconfig);
        AccountToken accountToken = pcAccountTokenService.queryAccountToken(passportId, 1044, instanceId);
        return "OK";
    }
}
