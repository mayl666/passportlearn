package com.sogou.upd.passport.web.threadPool.action;

import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.CacheConstant;
import com.sogou.upd.passport.common.DateAndNumTimesConstant;
import com.sogou.upd.passport.common.utils.RedisUtils;
import com.sogou.upd.passport.service.account.AccountService;
import com.sogou.upd.passport.service.account.OperateTimesService;
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
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-25 Time: 下午6:39 To change this template use
 * File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/thread/poolSize")
public class ThreadPoolSizeAction {
    @Autowired
    private ThreadPoolTaskExecutor loginAfterTaskExecutor;
    @Autowired
    private ThreadPoolTaskExecutor regAfterTaskExecutor;
    @Autowired
    private ThreadPoolTaskExecutor batchOperateExecutor;

    @RequestMapping(value = "/loginAfterTaskExecutor", method = RequestMethod.GET)
    @ResponseBody
    public Object loginAfterTaskExecutor(ThreadPoolSizeParameters threadPoolSizeParameters) {
        if (threadPoolSizeParameters.getCorePoolSize() > threadPoolSizeParameters.getMaxPoolSize()) {
            return "failed,corePoolSize cannot greater than  maxPoolSize";
        }
        try {
            if (threadPoolSizeParameters.getCorePoolSize() > 0) {
                loginAfterTaskExecutor.setCorePoolSize(threadPoolSizeParameters.getCorePoolSize());
            }
            if (threadPoolSizeParameters.getMaxPoolSize() > 0) {
                loginAfterTaskExecutor.setMaxPoolSize(threadPoolSizeParameters.getMaxPoolSize());
            }
            if (threadPoolSizeParameters.getKeepAliveSeconds() > 0) {
                loginAfterTaskExecutor.setKeepAliveSeconds(threadPoolSizeParameters.getKeepAliveSeconds());
            }
            if (threadPoolSizeParameters.getQueueCapacity() > 0) {
                loginAfterTaskExecutor.setKeepAliveSeconds(threadPoolSizeParameters.getQueueCapacity());
            }
        } catch (Exception e) {
            return "failed,exception occur" + e.getMessage();
        }
        return "success";
    }

    //redis set
    @RequestMapping(value = "/regAfterTaskExecutor", method = RequestMethod.GET)
    @ResponseBody
    public Object regAfterTaskExecutor(ThreadPoolSizeParameters threadPoolSizeParameters) throws Exception {
        if (threadPoolSizeParameters.getCorePoolSize() > threadPoolSizeParameters.getMaxPoolSize()) {
            return "failed,corePoolSize cannot greater than  maxPoolSize";
        }
        try {
            if (threadPoolSizeParameters.getCorePoolSize() > 0) {
                regAfterTaskExecutor.setCorePoolSize(threadPoolSizeParameters.getCorePoolSize());
            }
            if (threadPoolSizeParameters.getMaxPoolSize() > 0) {
                regAfterTaskExecutor.setMaxPoolSize(threadPoolSizeParameters.getMaxPoolSize());
            }
            if (threadPoolSizeParameters.getKeepAliveSeconds() > 0) {
                regAfterTaskExecutor.setKeepAliveSeconds(threadPoolSizeParameters.getKeepAliveSeconds());
            }
            if (threadPoolSizeParameters.getQueueCapacity() > 0) {
                regAfterTaskExecutor.setKeepAliveSeconds(threadPoolSizeParameters.getQueueCapacity());
            }
        } catch (Exception e) {
            return "failed,exception occur" + e.getMessage();
        }
        return "success";
    }

    @RequestMapping(value = "/batchOperateExecutor", method = RequestMethod.GET)
    @ResponseBody
    public Object batchOperateExecutor(ThreadPoolSizeParameters threadPoolSizeParameters) throws Exception {
        if (threadPoolSizeParameters.getCorePoolSize() > threadPoolSizeParameters.getMaxPoolSize()) {
            return "failed,corePoolSize cannot greater than  maxPoolSize";
        }
        try {
            if (threadPoolSizeParameters.getCorePoolSize() > 0) {
                batchOperateExecutor.setCorePoolSize(threadPoolSizeParameters.getCorePoolSize());
            }
            if (threadPoolSizeParameters.getMaxPoolSize() > 0) {
                batchOperateExecutor.setMaxPoolSize(threadPoolSizeParameters.getMaxPoolSize());
            }
            if (threadPoolSizeParameters.getKeepAliveSeconds() > 0) {
                batchOperateExecutor.setKeepAliveSeconds(threadPoolSizeParameters.getKeepAliveSeconds());
            }
            if (threadPoolSizeParameters.getQueueCapacity() > 0) {
                batchOperateExecutor.setKeepAliveSeconds(threadPoolSizeParameters.getQueueCapacity());
            }
        } catch (Exception e) {
            return "failed,exception occur" + e.getMessage();
        }
        return "success";
    }
}
