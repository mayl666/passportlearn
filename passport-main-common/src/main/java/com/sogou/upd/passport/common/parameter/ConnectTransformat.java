package com.sogou.upd.passport.common.parameter;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * 第三方平台枚举
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-1-10
 * Time: 下午4:14
 * To change this template use File | Settings | File Templates.
 */
public enum ConnectTransformat {
    qzone,
    weibo,
    mail;

    // 第三方平台字符串与枚举类型映射字典表
    private static BiMap<String, Object> CONNECT_MAPPING_DICT = HashBiMap.create();

    static {
        CONNECT_MAPPING_DICT.put("qzone", ConnectTransformat.qzone);
        CONNECT_MAPPING_DICT.put("weibo", ConnectTransformat.weibo);
        CONNECT_MAPPING_DICT.put("mail", ConnectTransformat.mail);
    }

    public static Object getConnectPlatform(String platformStr) {
        return CONNECT_MAPPING_DICT.get(platformStr);
    }
}
