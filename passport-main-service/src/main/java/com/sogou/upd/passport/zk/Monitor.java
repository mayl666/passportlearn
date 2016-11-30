package com.sogou.upd.passport.zk;


import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.retry.RetryNTimes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 临时redis切换方案，写的代码较搓
 * 以后需要改为   Monitor中包含一个list，在配置文件中添加这个list
 * list中元素实现对path的Listener
 * User: ligang201716@sogou-inc.com
 * Date: 13-10-31
 * Time: 下午9:56
 */
//@Component
public class Monitor {

    private static final Logger log = LoggerFactory.getLogger(Monitor.class);

    private CuratorFramework curatorFramework;


    public Monitor(String zks) {
        SGCompressionProvider compressionProvider=new SGCompressionProvider();
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(zks)
                .connectionTimeoutMs(5000)
                .retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 10000))
                .compressionProvider(compressionProvider)
                .build();
        curatorFramework.start();
        log.info("zookeeper monitor inti success");
    }

    public CuratorFramework getCuratorFramework(){
        return curatorFramework;
    }


    public void destory(){
        curatorFramework.close();
    }
}


