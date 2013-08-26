package com.sogou.upd.passport.common.utils;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * memcache工具类
 * User: mayan
 * Date: 13-8-21
 * Time: 上午11:07
 * To change this template use File | Settings | File Templates.
 */
public class MemcacheUtils {
    private static Logger logger = LoggerFactory.getLogger(MemcacheUtils.class);

    private MemcachedClientBuilder builder;
    private MemcachedClient c;

    public MemcachedClientBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(MemcachedClientBuilder builder) {
        this.builder = builder;
    }

    private MemcachedClient buildMemcachedClient() throws IOException {
        return builder.build();
    }

    public void init() {
        try {
            c = buildMemcachedClient();
        }catch (Exception e) {
            logger.error(" init xmemchached client error, msg is :{}", e.getMessage());
        }
    }

    @Profiled(el = true, logger = "memcacheTimingLogger", tag = "memcache_get")
    public String get(String key) throws Exception {
        try {
            Object value =c.get(key);
            if (value != null) {
                return value.toString();
            }
            return null;
        }catch (Exception e) {
            logger.error("[memcache] get cache fail, key:" + key, e);
            return null;
        }
    }
}
