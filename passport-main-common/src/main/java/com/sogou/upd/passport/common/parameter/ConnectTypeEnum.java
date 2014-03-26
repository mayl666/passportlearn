package com.sogou.upd.passport.common.parameter;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Created with IntelliJ IDEA.
 * User: liuling
 * Date: 14-2-23
 * Time: 下午7:22
 * To change this template use File | Settings | File Templates.
 */
public enum ConnectTypeEnum {
    QQ(3), // QQ
    SINA(4), // Sina微博
    RENREN(5), // 人人
    TAOBAO(6), // 淘宝
    BAIDU(7);  // 百度

    // 第三方类型枚举与app_key映射字典表
    private static BiMap<Integer, String> APPKEY_MAPPING_DICT = HashBiMap.create();

    static {
        APPKEY_MAPPING_DICT.put(3, "100294784");
        APPKEY_MAPPING_DICT.put(4, "1279688155");
        APPKEY_MAPPING_DICT.put(5, "f447f857a7844bd0810d4036c2ba24b1");
        APPKEY_MAPPING_DICT.put(6, "21524658");
        APPKEY_MAPPING_DICT.put(7, "tOrfIuD5xXqBG4x9GHjmf6SW");
    }

    private int value;

    ConnectTypeEnum(int value) {
        this.value = value;
    }

    public static String getAppKey(int provider) {
        return APPKEY_MAPPING_DICT.get(provider);
    }

}
