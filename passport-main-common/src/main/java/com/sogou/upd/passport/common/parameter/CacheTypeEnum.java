package com.sogou.upd.passport.common.parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存类型，有db、cache、token
 * User: shipengzhi
 * Date: 14-7-27
 * Time: 下午6:11
 * To change this template use File | Settings | File Templates.
 */
public enum CacheTypeEnum {
    db,
    cache,
    token;

    public static List<String> CacheTypeList = new ArrayList<>();

    static {
        CacheTypeList.add("db");
        CacheTypeList.add("cache");
        CacheTypeList.add("token");
    }
}
