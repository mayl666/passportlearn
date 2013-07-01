package com.sogou.upd.passport.web.account.action;

import com.google.common.collect.Lists;

import com.sogou.upd.passport.common.utils.KvUtils;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.service.account.dataobject.ActionStoreRecordDO;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-25 Time: 下午6:39 To change this template use
 * File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/web/test")
public class TestAction {
    @Autowired
    private KvUtils kvUtils;
    @Autowired
    private RedisUtils redisUtils;

    private static Logger logger = LoggerFactory.getLogger(TestAction.class);

    private static int count = 0;
    private static boolean flag = false;

    @RequestMapping(value = "reset", method = RequestMethod.GET)
    @ResponseBody
    public Object testReset() throws Exception {
        kvUtils.COUNT = 0;
        redisUtils.COUNT = 0;
        return "重置COUNT为："+kvUtils.COUNT + "\n " + redisUtils.COUNT;
    }

    @RequestMapping(value = "info", method = RequestMethod.GET)
    @ResponseBody
    public Object testInfo() throws Exception {
        return "count:" + kvUtils.COUNT + "\n " + redisUtils.COUNT;
    }



    @RequestMapping(value = "set", method = RequestMethod.GET)
    @ResponseBody
    public Object testSet() throws Exception {
        List<String> list = Lists.newLinkedList();
        ActionStoreRecordDO action = new ActionStoreRecordDO();
        for (int i=0; i<10; i++) {
            action.setClientId(1120);
            action.setDate(System.currentTimeMillis());
            action.setIp("202.101.112.212");
            list.add(new ObjectMapper().writeValueAsString(action));
        }
        kvUtils.setTest("TEST" + new Random().nextInt() % 100000,
                        new ObjectMapper().writeValueAsString(list));
        return "success";
    }

    @RequestMapping(value = "get", method = RequestMethod.GET)
    @ResponseBody
    public Object testGet() {
        kvUtils.getTest("TEST" + new Random().nextInt() % 100000);
        return "success";
    }

    @RequestMapping(value = "del", method = RequestMethod.GET)
    @ResponseBody
    public Object testDelete() {
        kvUtils.deleteTest("TEST" + new Random().nextInt() % 100000);
        return "success";
    }


    ////---------------------------------------------------------------
    @RequestMapping(value = "setr", method = RequestMethod.GET)
    @ResponseBody
    public Object testSetRedis() throws Exception {
        List<String> list = Lists.newLinkedList();
        ActionStoreRecordDO action = new ActionStoreRecordDO();
        for (int i=0; i<10; i++) {
            action.setClientId(1120);
            action.setDate(System.currentTimeMillis());
            action.setIp("202.101.112.212");
            list.add(new ObjectMapper().writeValueAsString(action));
        }
        redisUtils.setTest("TEST" + new Random().nextInt() % 100000,
                        new ObjectMapper().writeValueAsString(list));
        if (count % 10000 == 0) {
            logger.info("SET COUNT: " + count);
        }
        return "success";
    }

    @RequestMapping(value = "getr", method = RequestMethod.GET)
    @ResponseBody
    public Object testGetRedis() {
        redisUtils.getTest("TEST" + new Random().nextInt() % 100000);
        if (count % 10000 == 0) {
            logger.info("GET COUNT: " + count);
        }
        return "success";
    }
}
