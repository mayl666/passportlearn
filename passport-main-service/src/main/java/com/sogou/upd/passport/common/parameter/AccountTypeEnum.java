package com.sogou.upd.passport.common.parameter;


import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.utils.PhoneUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 账号类型，第三方，邮箱，手机号码
 *
 * @author shipengzhi
 */
public enum AccountTypeEnum {

    UNKNOW(0), //未知
    EMAIL(1), // 邮箱
    PHONE(2), // 手机号码
    QQ(3), // QQ
    SINA(4), // Sina微博
    RENREN(5); // 人人

    // provider数字与字符串映射字典表
    private static BiMap<String, Integer> PROVIDER_MAPPING_DICT = HashBiMap.create();

    static {
        PROVIDER_MAPPING_DICT.put("email", EMAIL.getValue());
        PROVIDER_MAPPING_DICT.put("phone", PHONE.getValue());
        PROVIDER_MAPPING_DICT.put("qq", QQ.getValue());
        PROVIDER_MAPPING_DICT.put("sina", SINA.getValue());
        PROVIDER_MAPPING_DICT.put("renren", RENREN.getValue());
    }

    private int value;

    AccountTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static int getProvider(String providerStr) {
        return PROVIDER_MAPPING_DICT.get(providerStr);
    }

    public static String getProviderStr(int provider) {
        return PROVIDER_MAPPING_DICT.inverse().get(provider);
    }

    public static boolean isPhone(String account, int provider) {
        if (PhoneUtil.verifyPhoneNumberFormat(account)) {
            if (provider == PHONE.getValue() || provider == UNKNOW.getValue()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEmail(String account, int provider) {
        if (account.contains("@")) {
            if (provider == EMAIL.getValue() || provider == UNKNOW.getValue()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isConnect(int provider) {
        if (provider != PHONE.getValue() && provider != EMAIL.getValue()) {
            return true;
        } else {
            return false;
        }
    }

}
