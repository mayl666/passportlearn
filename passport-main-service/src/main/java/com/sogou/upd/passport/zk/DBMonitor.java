package com.sogou.upd.passport.zk;

import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.retry.RetryNTimes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 数据库动态切换monitor
 * User: chengang
 * Date: 14-11-12
 * Time: 上午9:06
 */
@Component
public class DBMonitor {

    private static final Logger log = LoggerFactory.getLogger(DBMonitor.class);

    private CuratorFramework curatorFramework;


    public DBMonitor(String zks) {
        SGCompressionProvider compressionProvider = new SGCompressionProvider();
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(zks)
                .connectionTimeoutMs(5000)
                .retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 10000))
                .compressionProvider(compressionProvider)
                .build();
        curatorFramework.start();
        log.info("zookeeper db monitor inti success");
    }


    public CuratorFramework getCuratorFramework() {
        return curatorFramework;
    }


    public void destroy() {
        if (curatorFramework != null) {
            curatorFramework.close();
        }
    }
}
