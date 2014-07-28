package com.sogou.upd.passport.common.parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * 操作类型，仅支持get、hget、hgetall、expire
 * User: shipengzhi
 * Date: 14-7-27
 * Time: 下午6:11
 * To change this template use File | Settings | File Templates.
 */
public enum CacheOperEnum {
    get,
    hget,
    hgetall;

    public static List<String> CacheOperList = new ArrayList<>();

    static {
        CacheOperList.add("get");
        CacheOperList.add("hget");
        CacheOperList.add("hgetall");
    }
}
