package com.sogou.upd.passport.zk;

import com.google.common.base.Strings;
import com.netflix.curator.framework.recipes.cache.NodeCache;
import com.netflix.curator.framework.recipes.cache.NodeCacheListener;
import com.sogou.upd.passport.common.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisShardInfo;

import java.util.Map;

/**
 * 用于监控zookeeper上的redis连接地址
 * 如果地址变化则动态切换redis链接
 * User: ligang201716@sogou-inc.com
 * Date: 13-10-31
 * Time: 下午11:14
 */
@Component
public class RedisMonitor {

    private static final Logger log = LoggerFactory.getLogger(RedisMonitor.class);

    private NodeCache cacheNodeCache;

    private NodeCache tokenNodeCache;

    private NodeCache dbCacheNodeCache;

    private String cachePath;

    private String tokenPath;

    private String dbCachePath;

    private Monitor monitor;

    private JedisConnectionFactory tokenConnectionFactory; //PC端token存储缓存

    private JedisConnectionFactory cacheConnectionFactory;  //web接口临时信息存储缓存

    private JedisConnectionFactory dbCacheConnectionFactory;  //web接口临时信息存储缓存


    public RedisMonitor(Monitor monitor, String cachePath, String tokenPath,String dbCachePath,JedisConnectionFactory cacheConnectionFactory,
                        JedisConnectionFactory tokenConnectionFactory, JedisConnectionFactory dbCacheConnectionFactory) {
        this.monitor = monitor;
        this.cachePath = cachePath;
        this.tokenPath = tokenPath;
        this.dbCachePath = dbCachePath;
        this.cacheConnectionFactory = cacheConnectionFactory;
        this.tokenConnectionFactory = tokenConnectionFactory;
        this.dbCacheConnectionFactory = dbCacheConnectionFactory;
        cacheNodeCache = this.addListener(cachePath, new CacheListenerImpl());
        tokenNodeCache = this.addListener(tokenPath, new TokenListenerImpl());
        dbCacheNodeCache = this.addListener(dbCachePath,new DbCacheListenerImpl());
    }


    private NodeCache addListener(String path, NodeCacheListener nodeCacheListener) {
        NodeCache nodeCache = new NodeCache(monitor.getCuratorFramework(), path, true);
        try {
            nodeCache.start();
            nodeCache.getListenable().addListener(nodeCacheListener);
        } catch (Exception e) {
            log.error("RedisMonitor start error", e);
        }
        return nodeCache;
    }


    private class CacheListenerImpl implements NodeCacheListener {
        @Override
        public void nodeChanged() throws Exception {
            log.warn("cache redis node changed ");
            refresh(cacheNodeCache, cacheConnectionFactory);
        }
    }

    private class TokenListenerImpl implements NodeCacheListener {

        @Override
        public void nodeChanged() throws Exception {
            log.warn("redis node changed ");
            refresh(tokenNodeCache, tokenConnectionFactory);
        }
    }

    private class DbCacheListenerImpl implements NodeCacheListener {

        @Override
        public void nodeChanged() throws Exception {
            log.warn("redis node changed ");
            refresh(dbCacheNodeCache, dbCacheConnectionFactory);
        }
    }

    /**
     * 动态刷新redis连接
     *
     * @param nodeCache
     * @param factory
     */
    private void refresh(NodeCache nodeCache, JedisConnectionFactory factory) {
        try {
            if (nodeCache.getCurrentData() != null && nodeCache.getCurrentData().getData() != null) {
                String data = new String(nodeCache.getCurrentData().getData());
                log.warn("cache redis node changed data:" + data);

                Map jsonMap = JsonUtil.jsonToBean(data, Map.class);
                String host = (String) jsonMap.get("host");
                int port = (Integer) jsonMap.get("port");
                if (!Strings.isNullOrEmpty(host) && port >= 0) {
                    if (host.equals(factory.getHostName()) && port == factory.getPort()) {
                        log.warn("redis not need refresh  host:" + host + " ,port:" + port);
                        return;
                    }

                    if (factory != null) {
                        factory.destroy();
                    }

                    JedisShardInfo shardInfo = new JedisShardInfo(host, port);
                    factory.setHostName(host);
                    factory.setPort(port);
                    factory.setShardInfo(shardInfo);
                    factory.afterPropertiesSet();
                }

            } else {
                log.warn(" redis node changed data: is null ,path:" + nodeCache.getCurrentData().getPath());
            }
        } catch (Exception e) {
            log.error("refresh redis error", e);
        }
    }

    /**
     * spring销毁Monitor时，关闭对zookeeper的监听
     */
    public void destroy() {
        try {
            if (cacheNodeCache != null) {
                cacheNodeCache.close();
            }
            if (tokenNodeCache != null) {
                tokenNodeCache.close();
            }

            if (dbCacheNodeCache != null) {
                dbCacheNodeCache.close();
            }

        } catch (Exception e) {
            log.error("error when destroy PathChildrenCache in Observer", e);
        }
    }
}
