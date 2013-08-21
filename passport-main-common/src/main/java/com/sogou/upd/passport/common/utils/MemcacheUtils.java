package com.sogou.upd.passport.common.utils;

import com.danga.MemCached.MemCachedClient;

/**
 * memcache工具类
 * User: mayan
 * Date: 13-8-21
 * Time: 上午11:07
 * To change this template use File | Settings | File Templates.
 */
public class MemcacheUtils {
    private MemCachedClient tokenMaster;
    private MemCachedClient rTokenMaster;

    public MemCachedClient getTokenMaster() {
        return tokenMaster;
    }

    public void setTokenMaster(MemCachedClient tokenMaster) {
        this.tokenMaster = tokenMaster;
    }

    public MemCachedClient getrTokenMaster() {
        return rTokenMaster;
    }

    public void setrTokenMaster(MemCachedClient rTokenMaster) {
        this.rTokenMaster = rTokenMaster;
    }
}
