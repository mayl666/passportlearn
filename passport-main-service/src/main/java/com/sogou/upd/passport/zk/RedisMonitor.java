package com.sogou.upd.passport.zk;

import com.netflix.curator.framework.recipes.cache.NodeCache;
import com.netflix.curator.framework.recipes.cache.NodeCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * User: ligang201716@sogou-inc.com
 * Date: 13-10-31
 * Time: 下午11:14
 */
@Component
public class RedisMonitor {

    private static final Logger log = LoggerFactory.getLogger(RedisMonitor.class);

    private NodeCache nodeCache;

    private String path;

    public RedisMonitor(Monitor monitor, String path) {
        this.path = path;
        nodeCache = new NodeCache(monitor.getCuratorFramework(), path, true);
        try {
            nodeCache.start();
            nodeCache.getListenable().addListener(new NodeCacheListenerImpl());
        } catch (Exception e) {
            log.error("RedisMonitor start error");
        }
    }


    private class NodeCacheListenerImpl implements NodeCacheListener {

        @Override
        public void nodeChanged() throws Exception {
            log.warn("redis node changed ");
            if (nodeCache.getCurrentData() != null && nodeCache.getCurrentData().getData() != null) {
                String data = new String(nodeCache.getCurrentData().getData());
                log.warn("redis node changed data:" + data);
            } else {
                log.warn("redis node changed data: is null");
            }
        }
    }

    public void destroy() {
        try {
            nodeCache.close();
        } catch (Exception e) {
            log.error("error when destroy PathChildrenCache in Observer", e);
        }
    }
}
