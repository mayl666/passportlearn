package com.sogou.upd.passport.web.internal.debug;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created with IntelliJ IDEA. User: hujunfei Date: 13-6-25 Time: 下午6:39 To change this template use
 * File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/internal/debug/threadPoolSize")
public class ThreadPoolSizeAction {
    @Autowired
    private ThreadPoolTaskExecutor discardTaskExecutor;
    @Autowired
    private ThreadPoolTaskExecutor batchOperateExecutor;

    @RequestMapping()
    public String indexPage(Model model) throws Exception {
        model.addAttribute("batchOperateExecutor",batchOperateExecutor);
        model.addAttribute("discardTaskExecutor",discardTaskExecutor);
        return "threadpool";
    }

    @RequestMapping(value = "/discardTaskExecutor", method = RequestMethod.POST)
    @ResponseBody
    public Object loginAfterTaskExecutor(ThreadPoolSizeParameters threadPoolSizeParameters) {
        if (threadPoolSizeParameters.getCorePoolSize() > threadPoolSizeParameters.getMaxPoolSize()) {
            return "failed,corePoolSize cannot greater than  maxPoolSize";
        }
        try {
            if (threadPoolSizeParameters.getCorePoolSize() > 0) {
                discardTaskExecutor.setCorePoolSize(threadPoolSizeParameters.getCorePoolSize());
            }
            if (threadPoolSizeParameters.getMaxPoolSize() > 0) {
                discardTaskExecutor.setMaxPoolSize(threadPoolSizeParameters.getMaxPoolSize());
            }
            if (threadPoolSizeParameters.getKeepAliveSeconds() > 0) {
                discardTaskExecutor.setKeepAliveSeconds(threadPoolSizeParameters.getKeepAliveSeconds());
            }
            if (threadPoolSizeParameters.getQueueCapacity() > 0) {
                discardTaskExecutor.setQueueCapacity(threadPoolSizeParameters.getQueueCapacity());
            }
        } catch (Exception e) {
            return "failed,exception occur" + e.getMessage();
        }
        return "success";
    }

    @RequestMapping(value = "/batchOperateExecutor", method = RequestMethod.POST)
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
                batchOperateExecutor.setQueueCapacity(threadPoolSizeParameters.getQueueCapacity());
            }
        } catch (Exception e) {
            return "failed,exception occur" + e.getMessage();
        }
        return "success";
    }
}
