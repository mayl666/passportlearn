package com.sogou.upd.passport.common.utils;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;

import java.io.IOException;

/**
 * memcache工具类
 * User: mayan
 * Date: 13-8-21
 * Time: 上午11:07
 * To change this template use File | Settings | File Templates.
 */
public class MemcacheUtils {
    private MemcachedClientBuilder builder;
    private MemcachedClient c;

    public MemcachedClientBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(MemcachedClientBuilder builder) {
        this.builder = builder;
    }

    public MemcachedClient buildMemcachedClient() throws IOException {
        return builder.build();
    }
}
