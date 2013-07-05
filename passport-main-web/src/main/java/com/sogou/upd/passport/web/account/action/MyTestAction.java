package com.sogou.upd.passport.web.account.action;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.manager.account.LoginManager;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.OperateTimesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: chenjiameng
 * Date: 13-7-5
 * Time: 上午9:45
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/internal")
public class MyTestAction {
    @Autowired
    private OperateTimesService operateTimesService;
    @Autowired
    private LoginManager loginManager;

    private static final int MAX = 1000000;
//    private static Logger slf4jLlogger = LoggerFactory.getLogger(MyTestAction.class);
//    private static final org.apache.log4j.Logger oplogger = org.apache.log4j.Logger.getLogger("testOperationLogger");
//    private static final org.apache.log4j.Logger log4jLogger = org.apache.log4j.Logger.getLogger(MyTestAction.class);
    @Autowired
    private ThreadPoolTaskExecutor discardTaskExecutor;

    @RequestMapping(value = "/testLoginAfter", method = RequestMethod.GET)
    @ResponseBody
    public Object testLoginAfter() throws Exception {
        String username = "shipengzhi1986@sogou.com" + new Random().nextInt()%MAX;
        String ip = "127.0.0.1"+ new Random().nextInt()%MAX;
        loginManager.doAfterLoginSuccess(username,ip,username,1120);
        return "success";
    }

    @RequestMapping(value = "/testThreadPool", method = RequestMethod.GET)
    @ResponseBody
    public Object testThreadPool() throws Exception {
        String username = "shipengzhi1986@sogou.com" + new Random().nextInt()%MAX;
        String ip = "127.0.0.1"+ new Random().nextInt()%MAX;
        operateTimesService.incLoginSuccessTimes(username,ip);
        return "success";
    }

}
