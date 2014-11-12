package com.sogou.upd.passport.zk;

import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.framework.recipes.cache.ChildData;
import com.netflix.curator.framework.recipes.cache.NodeCache;
import com.netflix.curator.framework.recipes.cache.NodeCacheListener;
import com.netflix.curator.retry.RetryNTimes;
import org.I0Itec.zkclient.ZkClient;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * zk node 节点变化测试
 * User: chengang
 * Date: 14-11-6
 * Time: 下午4:26
 */
@Ignore
public class ZkWatcherTest {

    private static final Logger logger = LoggerFactory.getLogger(ZkWatcherTest.class);

    public static final String zk = "10.136.24.136:2181";
    public static final String masterDataSourceZkPath = "/sogou_passport/datasource/master";

    public static NodeCache nodeCache(CuratorFramework curatorFramework, String path) {
        final NodeCache nodeCache = new NodeCache(curatorFramework, path, true);
        try {
            nodeCache.start();
            nodeCache.getListenable().addListener(new NodeCacheListener() {
                @Override
                public void nodeChanged() throws Exception {
                    ChildData current_data = nodeCache.getCurrentData();
                    logger.info("Data change watched, and current data = " + new String(current_data.getData()));
                }
            });

        } catch (Exception e) {
            logger.error("testWatchNode error.", e.getMessage());
        }
        return nodeCache;

    }


    public static void main(String args[]) {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(zk)
                .connectionTimeoutMs(5000)
                .retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 10000))
                .compressionProvider(new SGCompressionProvider())
                .build();

        curatorFramework.start();

        NodeCache nodeCache = nodeCache(curatorFramework, masterDataSourceZkPath);

        ChildData data = nodeCache.getCurrentData();
        if (data != null) {
            logger.info("Data change watched, and current data = " + new String(data.getData()));
        }

    }

}
