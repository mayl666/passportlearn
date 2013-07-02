package com.sogou.upd.passport.web.account.action;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.OperateTimesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-25 Time: 下午6:39 To change this template use
 * File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/web/redisTest")
public class MyTestRedisAction {
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private OperateTimesService operateTimesService;
    @Autowired
    private AccountService accountService;

    private static final int MAX = 1000000;
    private static Logger slf4jLlogger = LoggerFactory.getLogger(MyTestRedisAction.class);
    private static final org.apache.log4j.Logger oplogger = org.apache.log4j.Logger.getLogger("testOperationLogger");
    private static final org.apache.log4j.Logger log4jLogger = org.apache.log4j.Logger.getLogger(MyTestRedisAction.class);
//    @Autowired
//    private TaskExecutor loginAfterTaskExecutor;

    //redis set
    @RequestMapping(value = "/testThreadPool", method = RequestMethod.GET)
    @ResponseBody
    public Object testThreadPool() throws Exception {
        String username = "shipengzhi1986@sogou.com" + new Random().nextInt()%MAX;
        String ip = "127.0.0.1"+ new Random().nextInt()%MAX;
        operateTimesService.incLoginSuccessTimes(username,ip);
        return "success";
    }

    @RequestMapping(value = "/printMessages", method = RequestMethod.GET)
    @ResponseBody
    public Object printMessages() {
        oplogger.error("op_this is error");
        oplogger.info("op_this is info");
        oplogger.debug("op_this is debug");

        slf4jLlogger.error("this is error");
        slf4jLlogger.info("this is info");
        slf4jLlogger.debug("this is debug");

        log4jLogger.error("log4j_this is error");
        log4jLogger.info("log4j_this is info");
        log4jLogger.debug("log4j_this is debug");
        return "success";
    }

    //redis set
    @RequestMapping(value = "/getcount", method = RequestMethod.GET)
    @ResponseBody
    public Object getcount() throws Exception {
        return "success";
    }

    //redis set
    @RequestMapping(value = "/testSet", method = RequestMethod.GET)
    @ResponseBody
    public Object testSet() throws Exception {
        String username = "shipengzhi1986@sogou.com" + new Random().nextInt()%MAX;
        String userNameCacheKey = CacheConstant.CACHE_PREFIX_USERNAME_LOGINFAILEDNUM + username;
        redisUtils.setWithinSeconds(userNameCacheKey, 1, DateAndNumTimesConstant.TIME_ONEHOUR);

//        String ip = "127.0.0.1"+ new Random().nextInt()%MAX;
//        String ipFailedCacheKey = CacheConstant.CACHE_PREFIX_IP_LOGINFAILEDNUM + ip;
//        redisUtils.setWithinSeconds(ipFailedCacheKey, 1, DateAndNumTimesConstant.TIME_ONEHOUR);
//
//        String ipSuccessCacheKey = CacheConstant.CACHE_PREFIX_IP_LOGINSUCCESSNUM + ip;
//        redisUtils.setWithinSeconds(ipSuccessCacheKey, 1, DateAndNumTimesConstant.TIME_ONEHOUR);

        return "success";
    }

    @RequestMapping(value = "/testGet", method = RequestMethod.GET)
    @ResponseBody
    public Object testGet() throws Exception {
        String username = "shipengzhi1986@sogou.com" + new Random().nextInt()%MAX;
        String userNameCacheKey = CacheConstant.CACHE_PREFIX_USERNAME_LOGINFAILEDNUM + username;
        redisUtils.get(userNameCacheKey);

//        String ip = "shipengzhi1986@sogou.com" + new Random().nextInt()%MAX;
//        String ipFailedCacheKey = CacheConstant.CACHE_PREFIX_IP_LOGINFAILEDNUM + ip;
//        redisUtils.get(ipFailedCacheKey);
//
//        //一小时内ip登陆成功100次出验证码
//        String ipSuccessCacheKey = CacheConstant.CACHE_PREFIX_IP_LOGINSUCCESSNUM + ip;
//        redisUtils.get(ipSuccessCacheKey);

        return "success";
    }

    @RequestMapping(value = "/testMultiSet", method = RequestMethod.GET)
    @ResponseBody
    public Object testMultiSet() throws Exception {
        Map<String, Object> objectMap = Maps.newHashMap();
        String username = "shipengzhi1986@sogou.com" + new Random().nextInt()%MAX;
        String userNameCacheKey = CacheConstant.CACHE_PREFIX_USERNAME_LOGINFAILEDNUM + username;
        objectMap.put(userNameCacheKey,1);

//        String ip = "127.0.0.1"+ new Random().nextInt()%MAX;
//        String ipFailedCacheKey = CacheConstant.CACHE_PREFIX_IP_LOGINFAILEDNUM + ip;
//        objectMap.put(ipFailedCacheKey,1);
//
//        String ipSuccessCacheKey = CacheConstant.CACHE_PREFIX_IP_LOGINSUCCESSNUM + ip;
//        objectMap.put(ipSuccessCacheKey,1);

        redisUtils.multiSet(objectMap);
        return "success";
    }

    @RequestMapping(value = "/testMultiGet", method = RequestMethod.GET)
    @ResponseBody
    public Object testMultiGet() throws Exception {
        List<String> keyList = new ArrayList<String>();

        String username = "shipengzhi1986@sogou.com" + new Random().nextInt()%MAX;
        String userNameCacheKey = CacheConstant.CACHE_PREFIX_USERNAME_LOGINFAILEDNUM + username;
        keyList.add(userNameCacheKey);

//        String ip = "127.0.0.1"+ new Random().nextInt()%MAX;
//        String ipFailedCacheKey = CacheConstant.CACHE_PREFIX_IP_LOGINFAILEDNUM + ip;
//        keyList.add(ipFailedCacheKey);
//
//        String ipSuccessCacheKey = CacheConstant.CACHE_PREFIX_IP_LOGINSUCCESSNUM + ip;
//        keyList.add(ipSuccessCacheKey);

        redisUtils.multiGet(keyList);
        return "success";
    }


    //
    @RequestMapping(value = "/testIncLoginSuccessTimes", method = RequestMethod.GET)
    @ResponseBody
    public Object testIncLoginSuccessTimes() throws Exception {
        String username = "shipengzhi1986@sogou.com" + new Random().nextInt()%MAX;
        String ip = "127.0.0.1"+ new Random().nextInt()%MAX;
        operateTimesService.incLoginSuccessTimes(username,ip);
        return "success";
    }

    @RequestMapping(value = "/testLoginFailedTimesNeedCaptcha", method = RequestMethod.GET)
    @ResponseBody
    public Object testLoginFailedTimesNeedCaptcha() {
        String username = "shipengzhi1986@sogou.com"+ new Random().nextInt()%MAX;
        String ip = "127.0.0.1"+ new Random().nextInt()%MAX;
        operateTimesService.loginFailedTimesNeedCaptcha(username,ip);
        return "success";
    }

    @RequestMapping(value = "/testCheckLoginUserInBlackList", method = RequestMethod.GET)
    @ResponseBody
    public Object testCheckLoginUserInBlackList() {
        String username = "shipengzhi1986@sogou.com"+ new Random().nextInt()%MAX;
        operateTimesService.checkLoginUserInBlackList(username);
        return "success";
    }

    @RequestMapping(value = "/testCheckCaptchaCodeIsVaild", method = RequestMethod.GET)
    @ResponseBody
    public Object testCheckCaptchaCodeIsVaild() {
        String token ="fc5709c27f80aa3efdd04b4919fd9bf2&t=1372227596101";
        String captchaCode="LEW7A";
        accountService.checkCaptchaCodeIsVaild(token,captchaCode);
        return "success";
    }

    @RequestMapping(value = "/del", method = RequestMethod.GET)
    @ResponseBody
    public Object testCheckTimesByKey() {
        String username = "shipengzhi1986@sogou.com"+ new Random().nextInt()%MAX;
        operateTimesService.checkTimesByKey(username,20);
        return "success";
    }
}
