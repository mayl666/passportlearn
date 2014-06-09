package com.sogou.upd.passport.web.internal.debug;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.utils.TokenRedisUtils;
import com.sogou.upd.passport.model.account.AccountToken;
import com.sogou.upd.passport.model.app.AppConfig;
import com.sogou.upd.passport.service.account.impl.PCAccountServiceImpl;
import com.sogou.upd.passport.service.app.AppConfigService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-8-27
 * Time: 上午1:16
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
@Controller
public class TokenRedisperfController {
    private static final int clientId = 1044;
    private static AppConfig appConfig = null;

    @Autowired
    private TokenRedisUtils tokenRedisUtils;
    @Autowired
    private AppConfigService appConfigService;

    static {
        if(appConfig ==null){
            appConfig=new AppConfig();
            appConfig.setId(8);
            appConfig.setClientId(1044);
            appConfig.setSmsText("您的“搜狗通行证”验证码为：%s，30分钟内有效哦");
            appConfig.setAccessTokenExpiresin(604800);
            appConfig.setRefreshTokenExpiresin(7776000);
            appConfig.setClientSecret("=#dW$h%q)6xZB#m#lu'x]]wP=\\FUO7");
            appConfig.setServerSecret("c1756a351db27d817225e2a4fd7b3f7d");
            appConfig.setCreateTime(new Date());
            appConfig.setClientName("浏览器输入法桌面");
        }
    }

    @RequestMapping(value = "/internal/tokenredis/get", method = RequestMethod.GET)
    @ResponseBody
    public Object get() {
        try {
            _get();
            return "ok";
        } catch (Exception ex) {
            return "error!";
        }
    }

    public void _get() {
        String passportId = getPassportId();
        String instanceId = getInstanceId();
        String oldRTokenKey = buildOldRTokenKeyStr(passportId, clientId, instanceId);
        String oldRTokenValue = tokenRedisUtils.get(oldRTokenKey);
//        return "OK";
    }

    @RequestMapping(value = "/internal/tokenredis/set", method = RequestMethod.GET)
    @ResponseBody
    public Object set() {
        try {
            _set();
            return "ok";
        } catch (Exception ex) {
            return "error!";
        }
    }

    public void _set() throws Exception{
        String passportId = getPassportId();
        String instanceId = getInstanceId();
        String oldRTokenKey = buildOldRTokenKeyStr(passportId, clientId, instanceId);

        AccountToken accountToken = PCAccountServiceImpl.newAccountToken(passportId, instanceId, appConfig);
        String refreshToken = accountToken.getRefreshToken();
        tokenRedisUtils.setWithinSeconds(oldRTokenKey, refreshToken, DateAndNumTimesConstant.TIME_ONEDAY);
    }

    @RequestMapping(value = "/internal/tokenredis/getObject", method = RequestMethod.GET)
    @ResponseBody
    public Object getObject() {
        try {
            _getObject();
            return "ok";
        } catch (Exception ex) {
            return "error!";
        }
    }

    public void _getObject() {
        String passportId = getPassportId();
        String instanceId = getInstanceId();
        String redisKey = buildTokenRedisKeyStr(passportId, clientId, instanceId);
        AccountToken accountToken = tokenRedisUtils.getObject(redisKey, AccountToken.class);
//        System.out.println("accountToken:"+accountToken);
    }

    @RequestMapping(value = "/internal/tokenredis/setObject", method = RequestMethod.GET)
    @ResponseBody
    public Object setObject() {
        try {
            _setObject();
            return "ok";
        } catch (Exception ex) {
            return "error!";
        }
    }

    public void _setObject() throws Exception {
        String passportId = getPassportId();
        String instanceId = getInstanceId();
        String redisKey = buildTokenRedisKeyStr(passportId, clientId, instanceId);
        AccountToken accountToken = PCAccountServiceImpl.newAccountToken(passportId, instanceId, appConfig);
        tokenRedisUtils.set(redisKey, accountToken);
    }

    @RequestMapping(value = "/internal/tokenredis/queryAppConfigByClientId", method = RequestMethod.GET)
    @ResponseBody
    public Object queryAppConfigByClientId() {
        try {
            _queryAppConfigByClientId();
            return "ok";
        } catch (Exception ex) {
            return "error!";
        }
    }


    private void _queryAppConfigByClientId() {
        AppConfig obj = appConfigService.queryAppConfigByClientId(clientId);
    }


    public String getPassportId() {
        String passportId = "tinkame" + RandomStringUtils.randomNumeric(7) + "@sogou.com";
        return passportId;
    }

    public String getInstanceId() {
        String instanceId = "2147483647";
        return instanceId;
    }

    public static String buildTokenRedisKeyStr(String passportId, int clientId, String instanceId) {
        if (StringUtils.isEmpty(instanceId)) {
            return passportId + "_" + clientId;
        }
        return passportId + "_" + clientId + "_" + instanceId;
    }

    private String buildOldRTokenKeyStr(String passportId, int clientId, String instanceId) {
        String key;
        if (Strings.isNullOrEmpty(instanceId)) {
            key = "old_" + passportId + "_" + clientId;
        } else {
            key = "old_" + passportId + "_" + clientId + "_" + instanceId;
        }
        return key;
    }
}